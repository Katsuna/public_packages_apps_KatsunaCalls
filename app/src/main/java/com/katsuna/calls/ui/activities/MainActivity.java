package com.katsuna.calls.ui.activities;

import android.Manifest;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.katsuna.calls.R;
import com.katsuna.calls.domain.Call;
import com.katsuna.calls.domain.Contact;
import com.katsuna.calls.providers.CallsProvider;
import com.katsuna.calls.providers.ContactInfoHelper;
import com.katsuna.calls.ui.adapters.CallsAdapter;
import com.katsuna.calls.ui.listeners.ICallInteractionListener;
import com.katsuna.calls.utils.Constants;
import com.katsuna.calls.utils.Device;
import com.katsuna.commons.entities.UserProfileContainer;
import com.katsuna.commons.ui.SearchBarActivity;
import com.katsuna.commons.utils.KatsunaAlertBuilder;

import java.util.LinkedHashMap;
import java.util.List;

public class MainActivity extends SearchBarActivity implements
        ICallInteractionListener {

    private static final int REQUEST_CODE_ASK_CALL_PERMISSION = 1;
    private static final int REQUEST_CODE_ASK_READ_CALL_LOG_PERMISSION = 2;
    private static final int CREATE_CONTACT_REQUEST = 3;

    private RecyclerView mRecyclerView;
    private CallsAdapter mAdapter;
    private SearchView mSearchView;
    private TextView mNoResultsView;
    private DrawerLayout mDrawerLayout;
    private LinkedHashMap<String, Contact> mContactCache;
    private ContactInfoHelper mContactInfoHelper;
    private LinkedHashMap<String, Boolean> mContactSearchedMap;
    private FrameLayout mPopupFrame;
    private String mCallNumberFocus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initControls();

        mContactCache = new LinkedHashMap<>();
        mContactSearchedMap = new LinkedHashMap<>();
        mContactInfoHelper = new ContactInfoHelper(this);
    }

    private void showContactsAppInstallationDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
        alert.setTitle(R.string.missing_app);
        alert.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Device.goToMarket(MainActivity.this, Constants.CONTACTS_APP);
            }
        });
        alert.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //Put actions for CANCEL button here, or leave in blank
            }
        });
        alert.show();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        assert drawer != null;
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean callsLoaded = loadCalls();

        if (callsLoaded) {
            showPopup(false);
            if (mCallNumberFocus != null && !mCallNumberFocus.isEmpty()) {
                int position = mAdapter.getPositionByNumber(mCallNumberFocus);
                focusCall(position);
                mCallNumberFocus = null;
            } else {
                deselectItem();
            }
        }
    }

    @Override
    protected void showPopup(boolean show) {
        if (show) {
            //don't show popup if menu drawer is open or a call is selected.
            if (!mDrawerLayout.isDrawerOpen(GravityCompat.START) && !mItemSelected) {
                mPopupFrame.setVisibility(View.VISIBLE);
                mPopupButton1.setVisibility(View.VISIBLE);
                mPopupButton2.setVisibility(View.VISIBLE);
                mPopupVisible = true;
            }
        } else {
            mPopupFrame.setVisibility(View.GONE);
            mPopupButton1.setVisibility(View.GONE);
            mPopupButton2.setVisibility(View.GONE);
            mPopupVisible = false;
            mLastTouchTimestamp = System.currentTimeMillis();
        }
    }

    private void initControls() {
        mRecyclerView = (RecyclerView) findViewById(R.id.calls_list);
        mRecyclerView.setItemAnimator(null);
        mNoResultsView = (TextView) findViewById(R.id.no_results);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mLastTouchTimestamp = System.currentTimeMillis();
        initPopupActionHandler();
        initDeselectionActionHandler();

        initToolbar();
        initDrawer();
        initFabs();

        mPopupFrame = (FrameLayout) findViewById(R.id.popup_frame);
        mPopupFrame.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                showPopup(false);
                return true;
            }
        });

    }

    private void initDrawer() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, mToolbar, R.string.common_navigation_drawer_open,
                R.string.common_navigation_drawer_close);
        assert drawer != null;
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        setupDrawerLayout();
    }

    private void initFabs() {
        mFabContainer = (LinearLayout) findViewById(R.id.fab_container);

        mButtonsContainer1 = (LinearLayout) findViewById(R.id.search_buttons_container);
        mPopupButton1 = (Button) findViewById(R.id.search_button);
        mPopupButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openContactsApp();
            }
        });

        mFab1 = (FloatingActionButton) findViewById(R.id.fabContacts);
        mFab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openContactsApp();
            }
        });

        mButtonsContainer2 = (LinearLayout) findViewById(R.id.dial_buttons_container);
        mPopupButton2 = (Button) findViewById(R.id.dial_button);
        mPopupButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dial();
            }
        });

        mFab2 = (FloatingActionButton) findViewById(R.id.fabDial);
        mFab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                dial();
            }
        });
    }

    private void openContactsApp() {
        if (!Device.openApp(MainActivity.this, Constants.CONTACTS_APP)) {
            showContactsAppInstallationDialog();
        }
    }

    private void dial() {
        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
        alert.setTitle(R.string.common_select_phone_number);
        final EditText input = new EditText(MainActivity.this);
        input.setInputType(InputType.TYPE_CLASS_PHONE);
        alert.setView(input);
        alert.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String number = input.getText().toString();
                if (!TextUtils.isEmpty(number)) {
                    callContact(number);
                }
            }
        });
        alert.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //Put actions for CANCEL button here, or leave in blank
            }
        });
        final AlertDialog dialog = alert.show();

        //focus on input
        input.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    Window window = dialog.getWindow();
                    if (window != null)
                        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });
    }

    private boolean loadCalls() {
        String[] permissions = new String[]{Manifest.permission.READ_CALL_LOG, Manifest.permission.READ_CONTACTS};
        if (!Device.hasAllPermissions(this, permissions)) {
            Device.requestPermissions(this, permissions, REQUEST_CODE_ASK_READ_CALL_LOG_PERMISSION);
            return false;
        }

        CallsProvider callsProvider = new CallsProvider(this);
        List<Call> mModels = callsProvider.getCalls();
        mAdapter = new CallsAdapter(mModels, this, this);

        mRecyclerView.setAdapter(mAdapter);
        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                showNoResultsView();
            }
        });
        showNoResultsView();

        return true;
    }

    private void showMessageForHiddenNumber() {
        Toast.makeText(this, R.string.hidden_number, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_READ_CALL_LOG_PERMISSION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    loadCalls();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void callContact(String number) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CODE_ASK_CALL_PERMISSION);
            return;
        }

        Intent i = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
        startActivity(i);
    }

    private void sendSMS(String number) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", number, null)));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mSearchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        // Assumes current activity is the searchable activity
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                search(newText);
                return false;
            }
        });
        mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                mAdapter.resetFilter();
                return false;
            }
        });


        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            mSearchView.setQuery(query, false);
        }
    }

    private void search(String query) {
        if (TextUtils.isEmpty(query)) {
            mAdapter.resetFilter();
        } else {
            mAdapter.getFilter().filter(query);
        }
    }

    private void showNoResultsView() {
        if (mAdapter.getItemCount() > 0) {
            mNoResultsView.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        } else {
            mNoResultsView.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        }
    }

    private void setupDrawerLayout() {
        NavigationView view = (NavigationView) findViewById(R.id.nav_view);
        assert view != null;
        view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                mDrawerLayout.closeDrawers();

                switch (menuItem.getItemId()) {
                    case R.id.drawer_settings:
                        startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                        break;
                    case R.id.drawer_help:
                        break;
                    case R.id.drawer_info:
                        break;
                }

                return true;
            }
        });
    }

    @Override
    public Contact getCallContact(Call call) {
        if (call.getContact() != null) {
            return call.getContact();
        }

        Contact contact = null;
        // search for contact if number presentation is allowed
        if (call.getNumberPresentation() == CallLog.Calls.PRESENTATION_ALLOWED) {
            //search from cache
            contact = mContactCache.get(call.getNumber());
            if (contact != null) return contact;

            // if not found in cache check if content provider is already asked
            if (mContactSearchedMap.get(call.getNumber()) != null &&
                    mContactSearchedMap.get(call.getNumber())) {
                return null;
            }

            // last resort. Check with mContactInfoHelper
            contact = mContactInfoHelper.getContactFromNumber(call.getNumber());
            mContactCache.put(call.getNumber(), contact);
            mContactSearchedMap.put(call.getNumber(), true);
        }
        return contact;
    }

    @Override
    public void selectCall(int position) {
        if (mItemSelected) {
            deselectItem();
        } else {
            focusOnCall(position, getCenter());
        }
    }

    @Override
    protected void deselectItem() {
        mItemSelected = false;
        mAdapter.deselectCall();
        tintFabs(false);
        adjustFabPosition(true);
        refreshLastTouchTimestamp();
    }

    private int getCenter() {
        return (mRecyclerView.getHeight() / 2) - 170;
    }

    private void scrollToPositionWithOffset(int position, int offset) {
        ((LinearLayoutManager) mRecyclerView.getLayoutManager())
                .scrollToPositionWithOffset(position, offset);
    }

    @Override
    public void focusCall(int position) {
        focusOnCall(position, getCenter());
    }

    private void focusOnCall(int position, int offset) {
        mAdapter.selectCallAtPosition(position);
        scrollToPositionWithOffset(position, offset);

        tintFabs(true);

        adjustFabPosition(false);
        mItemSelected = true;
        refreshLastSelectionTimestamp();
    }

    @Override
    public void callContact(Call call) {
        if (call.getNumberPresentation() != CallLog.Calls.PRESENTATION_ALLOWED) {
            showMessageForHiddenNumber();
            return;
        }
        callContact(call.getNumber());
    }

    @Override
    public void sendSMS(Call call) {
        if (call.getNumberPresentation() != CallLog.Calls.PRESENTATION_ALLOWED) {
            showMessageForHiddenNumber();
            return;
        }

        sendSMS(call.getNumber());
    }

    @Override
    public void createContact(Call call) {
        if (call.getNumberPresentation() != CallLog.Calls.PRESENTATION_ALLOWED) {
            showMessageForHiddenNumber();
            return;
        }

        Intent i = new Intent(Constants.CREATE_CONTACT_ACTION);
        i.putExtra("number", call.getNumber());

        PackageManager packageManager = getPackageManager();
        List activities = packageManager.queryIntentActivities(i, PackageManager.MATCH_DEFAULT_ONLY);
        boolean isIntentSafe = activities.size() > 0;

        if (isIntentSafe) {
            startActivityForResult(i, CREATE_CONTACT_REQUEST);
            mCallNumberFocus = call.getNumber();
            //clear it from cache to enable fetching the fresh contact
            mContactSearchedMap.remove(mCallNumberFocus);
        } else {
            showContactsAppInstallationDialog();
        }
    }

    @Override
    public void deleteCall(final Call call) {
        if (!checkPermission()) {
            return;
        }

        KatsunaAlertBuilder builder = new KatsunaAlertBuilder(this);
        builder.setTitle(R.string.delete_calls);
        builder.setMessage(R.string.delete_call_approval);
        builder.setView(R.layout.common_katsuna_alert);
        builder.setUserProfileContainer(mUserProfileContainer);
        builder.setOkListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CallsProvider callsProvider = new CallsProvider(MainActivity.this);
                callsProvider.deleteCall(call);
                mAdapter.removeItem(call);
                Toast.makeText(MainActivity.this, R.string.calls_deleted, Toast.LENGTH_LONG)
                        .show();
            }
        });
        builder.create().show();
    }

    private boolean checkPermission() {
        if (!Device.hasPermission(this, Manifest.permission.WRITE_CALL_LOG)) {
            Device.requestPermission(this, Manifest.permission.WRITE_CALL_LOG, 0);
            return false;
        }
        return true;
    }

    @Override
    public void selectItemByStartingLetter(String s) {
        // no search bar so nothing to be done here
    }

    @Override
    public UserProfileContainer getUserProfileContainer() {
        return mUserProfileContainer;
    }
}
