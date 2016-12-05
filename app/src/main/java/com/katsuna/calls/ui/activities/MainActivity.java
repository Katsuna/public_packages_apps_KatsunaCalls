package com.katsuna.calls.ui.activities;

import android.Manifest;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.CallLog;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.katsuna.calls.R;
import com.katsuna.calls.domain.Call;
import com.katsuna.calls.domain.Contact;
import com.katsuna.calls.providers.CallsProvider;
import com.katsuna.calls.providers.ContactInfoHelper;
import com.katsuna.calls.ui.adapters.CallsAdapter;
import com.katsuna.calls.ui.listeners.ICallInteractionListener;
import com.katsuna.calls.ui.listeners.IContactProvider;
import com.katsuna.calls.utils.Constants;
import com.katsuna.calls.utils.Device;
import com.katsuna.commons.KatsunaConstants;
import com.katsuna.commons.entities.Profile;
import com.katsuna.commons.entities.ProfileType;
import com.katsuna.commons.utils.ProfileReader;

import java.util.LinkedHashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements IContactProvider {

    private static final int REQUEST_CODE_ASK_CALL_PERMISSION = 1;
    private static final int REQUEST_CODE_ASK_READ_CALL_LOG_PERMISSION = 2;
    private static final int CREATE_CONTACT_REQUEST = 3;

    private RecyclerView mRecyclerView;
    private CallsAdapter mAdapter;
    private SearchView mSearchView;
    private TextView mNoResultsView;
    private List<Call> mModels;
    private DrawerLayout mDrawerLayout;
    private Profile mProfile;
    private LinkedHashMap<String, Contact> mContactCache;
    private ContactInfoHelper mContactInfoHelper;
    private int mSelectedCallPosition = Constants.NOT_SELECTED_CALL_VALUE;
    private LinkedHashMap<String, Boolean> mContactSearchedMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabContacts);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Device.openApp(MainActivity.this, Constants.CONTACTS_APP)) {
                    showContactsAppInstallationDialog();
                }
            }
        });

        FloatingActionButton fabDial = (FloatingActionButton) findViewById(R.id.fabDial);
        assert fabDial != null;
        fabDial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
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
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.common_navigation_drawer_open, R.string.common_navigation_drawer_close);
        assert drawer != null;
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        initControls();
        setupDrawerLayout();

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
        setupProfile();
        loadCalls();
    }

    private void setupProfile() {
        Profile freshProfileFromContentProvider = ProfileReader.getProfile(this);
        Profile profileFromPreferences = getProfileFromPreferences();
        if (freshProfileFromContentProvider == null) {
            setSelectedProfile(profileFromPreferences);
        } else {
            if (profileFromPreferences.getType() == ProfileType.AUTO.getNumVal()) {
                setSelectedProfile(freshProfileFromContentProvider);
            } else {
                setSelectedProfile(profileFromPreferences);
            }
        }
    }

    private void initControls() {
        mRecyclerView = (RecyclerView) findViewById(R.id.calls_list);
        mNoResultsView = (TextView) findViewById(R.id.no_results);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
    }

    private void loadCalls() {
        String[] permissions = new String[]{Manifest.permission.READ_CALL_LOG, Manifest.permission.READ_CONTACTS};
        if (!Device.hasAllPermissions(this, permissions)) {
            Device.requestPermissions(this, permissions, REQUEST_CODE_ASK_READ_CALL_LOG_PERMISSION);
            return;
        }

        CallsProvider callsProvider = new CallsProvider(this);
        mModels = callsProvider.getCalls();
        mAdapter = new CallsAdapter(mModels, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = mRecyclerView.getChildAdapterPosition(v);
                mAdapter.selectCallAtPosition(position);
                centerOnCall(position);
            }
        }, new ICallInteractionListener() {
            @Override
            public void callContact(Call call) {
                if (call.getNumberPresentation() != CallLog.Calls.PRESENTATION_ALLOWED) {
                    showMessageForHiddenNumber();
                    return;
                }

                MainActivity.this.callContact(call.getNumber());
            }

            @Override
            public void sendSMS(Call call) {
                if (call.getNumberPresentation() != CallLog.Calls.PRESENTATION_ALLOWED) {
                    showMessageForHiddenNumber();
                    return;
                }

                MainActivity.this.sendSMS(call.getNumber());
            }

            @Override
            public void createContact(Call call) {
                if (call.getNumberPresentation() != CallLog.Calls.PRESENTATION_ALLOWED) {
                    showMessageForHiddenNumber();
                    return;
                }

                mSelectedCallPosition = Constants.NOT_SELECTED_CALL_VALUE;

                Intent i = new Intent(Constants.CREATE_CONTACT_ACTION);
                i.putExtra("number", call.getNumber());

                PackageManager packageManager = getPackageManager();
                List activities = packageManager.queryIntentActivities(i, PackageManager.MATCH_DEFAULT_ONLY);
                boolean isIntentSafe = activities.size() > 0;

                if (isIntentSafe) {
                    startActivityForResult(i, CREATE_CONTACT_REQUEST);
                } else {
                    showContactsAppInstallationDialog();
                }
            }
        }, mSelectedCallPosition, mProfile, this);

        mRecyclerView.setAdapter(mAdapter);
        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                showNoResultsView();
            }
        });

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CREATE_CONTACT_REQUEST) {
            if (resultCode == RESULT_OK) {
                //noinspection ConstantConditions
                String number = data.getExtras().get("number").toString();
                for (int i = 0; i < mModels.size(); i++) {
                    if (mModels.get(i).getNumber().equals(number)) {
                        mSelectedCallPosition = i;
                        break;
                    }
                }
            }
        }
    }

    private void centerOnCall(int position) {
        Resources r = getResources();
        float offset = (mRecyclerView.getHeight() / 2) - TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 275, r.getDisplayMetrics()) / 2;
        int offsetInt = Math.round(offset);

        ((LinearLayoutManager) mRecyclerView.getLayoutManager()).scrollToPositionWithOffset(position, offsetInt);
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

    private boolean setSelectedProfile(Profile profile) {
        boolean profileChanged = false;
        if (mProfile == null) {
            mProfile = profile;
            profileChanged = true;
        } else {
            if (mProfile.getType() != profile.getType()) {
                profileChanged = true;
                mProfile.setType(profile.getType());
            }
        }
        return profileChanged;
    }

    private Profile getProfileFromPreferences() {
        Profile profile = new Profile();
        int profileType = PreferenceManager.getDefaultSharedPreferences(this)
                .getInt(KatsunaConstants.PROFILE_KEY, ProfileType.INTERMEDIATE.getNumVal());

        profile.setType(profileType);

        return profile;
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
}
