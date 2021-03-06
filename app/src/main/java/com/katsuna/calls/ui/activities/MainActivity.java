/**
* Copyright (C) 2020 Manos Saratsis
*
* This file is part of Katsuna.
*
* Katsuna is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* Katsuna is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with Katsuna.  If not, see <https://www.gnu.org/licenses/>.
*/
package com.katsuna.calls.ui.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.telephony.PhoneNumberUtils;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.katsuna.calls.BuildConfig;
import com.katsuna.calls.R;
import com.katsuna.calls.domain.Call;
import com.katsuna.calls.domain.Contact;
import com.katsuna.calls.notifications.calls.CallsAlarmReceiver;
import com.katsuna.calls.notifications.sms.SmsAlarmReceiver;
import com.katsuna.calls.providers.CallsProvider;
import com.katsuna.calls.providers.ContactInfoHelper;
import com.katsuna.calls.ui.adapters.CallsAdapter;
import com.katsuna.calls.ui.adapters.CallsAdapterBase;
import com.katsuna.calls.ui.listeners.ICallInteractionListener;
import com.katsuna.calls.utils.DayInfoFormatter;
import com.katsuna.calls.utils.Device;
import com.katsuna.calls.utils.TelecomUtils;
import com.katsuna.commons.controls.KatsunaNavigationView;
import com.katsuna.commons.entities.KatsunaConstants;
import com.katsuna.commons.entities.UserProfile;
import com.katsuna.commons.entities.UserProfileContainer;
import com.katsuna.commons.ui.SearchBarActivity;
import com.katsuna.commons.utils.BrowserUtils;
import com.katsuna.commons.utils.KatsunaAlertBuilder;
import com.katsuna.commons.utils.KatsunaUtils;
import com.katsuna.commons.utils.KeyboardUtils;

import java.util.LinkedHashMap;
import java.util.List;

import static com.katsuna.commons.utils.Constants.ADD_TO_CONTACT_ACTION;
import static com.katsuna.commons.utils.Constants.ADD_TO_CONTACT_ACTION_NUMBER;
import static com.katsuna.commons.utils.Constants.CREATE_CONTACT_ACTION;
import static com.katsuna.commons.utils.Constants.EDIT_CONTACT_ACTION;
import static com.katsuna.commons.utils.Constants.KATSUNA_PRIVACY_URL;
import static com.katsuna.commons.utils.Constants.KATSUNA_TERMS_OF_USE;

public class MainActivity extends SearchBarActivity implements
        ICallInteractionListener {

    private static final int REQUEST_CODE_ASK_CALL_PERMISSION = 1;
    private static final int REQUEST_CODE_ASK_READ_CALL_LOG_PERMISSION = 2;
    private static final int CREATE_CONTACT_REQUEST = 3;
    private static final int EDIT_CONTACT_REQUEST = 4;
    private static final int ADD_TO_CONTACT_REQUEST = 5;
    private static final String TAG = "MainActivity";

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
    private boolean mDontAskForPermissions;
    private boolean mDeleteModeOn = false;
    private boolean mCallTypeFilteringOn = false;
    private boolean mSearchModeOn;
    private AlertDialog mDialog;
    private View mFabsTopContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            setupCallsLogReceiver();
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {

            setupSmsLogReceiver();
        }
        initControls();

        mContactCache = new LinkedHashMap<>();
        mContactSearchedMap = new LinkedHashMap<>();
        mContactInfoHelper = new ContactInfoHelper(this);
    }

    private void showContactsAppInstallationDialog() {
        KatsunaAlertBuilder builder = new KatsunaAlertBuilder(this);
        String appName = getString(R.string.common_katsuna_contacts_app);
        String title = getString(R.string.common_missing_app, appName);
        builder.setTitle(title);
        builder.setView(R.layout.common_katsuna_alert);
        builder.setUserProfile(mUserProfileContainer.getActiveUserProfile());
        builder.setOkListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KatsunaUtils.goToGooglePlay(MainActivity.this,
                        KatsunaUtils.KATSUNA_CONTACTS_PACKAGE);
            }
        });

        mDialog = builder.create();
        mDialog.show();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        assert drawer != null;
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (mDeleteModeOn) {
            enableDeleteMode(false);
        } else if (mCallTypeFilteringOn) {
            if (mAdapter != null) {
                mAdapter.resetFilter();
                mCallTypeFilteringOn = false;
            }
        } else if (mItemSelected) {
            deselectItem();
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

            // mark miss called as read
            CallsProvider callsProvider = new CallsProvider(this);
            callsProvider.markMissedCallsAsRead();

            // clear missed calls notifications
            if (TelecomUtils.hasModifyPhoneStatePermission(this)) {
                TelecomUtils.cancelMissedCallsNotification(this);
            }
        }
        mDeleteModeOn = false;
        mCallTypeFilteringOn = false;

        Intent intent = getIntent();
        handleIntent(intent);
    }

    @Override
    protected void showPopup(boolean show) {
        if (show) {
            //don't show popup if menu drawer is open or a call is selected or delete mode is enabled.
            if (!mDrawerLayout.isDrawerOpen(GravityCompat.START)
                    && !mItemSelected
                    && !dialogActive()
                    && !mDeleteModeOn
                    && !mSearchModeOn) {
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
        }
    }

    private boolean dialogActive() {
        return mDialog != null && mDialog.isShowing();
    }

    private void initControls() {
        mRecyclerView = findViewById(R.id.calls_list);
        mRecyclerView.setItemAnimator(null);
        mNoResultsView = findViewById(R.id.no_results);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mLastTouchTimestamp = System.currentTimeMillis();
        initPopupActionHandler();
        initDeselectionActionHandler();

        initToolbar();
        initDrawer();
        initFabs();

        mPopupFrame = findViewById(R.id.popup_frame);
        mPopupFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup(false);
            }
        });

    }

    private void initDrawer() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, mToolbar, R.string.common_navigation_drawer_open,
                R.string.common_navigation_drawer_close);
        assert drawer != null;
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        setupDrawerLayout();
    }

    private void initFabs() {
        mFabsTopContainer = findViewById(R.id.fabs_top_container);
        mFabContainer = findViewById(R.id.fab_container);

        mButtonsContainer1 = findViewById(R.id.dial_buttons_container);
        mPopupButton1 = findViewById(R.id.dial_button);
        mPopupButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dial();
            }
        });

        mFab1 = findViewById(R.id.fabDial);
        mFab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                dial();
            }
        });

        mButtonsContainer2 = findViewById(R.id.search_buttons_container);
        mPopupButton2 = findViewById(R.id.search_button);
        mPopupButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openContactsApp();
            }
        });

        mFab2 = findViewById(R.id.fabContacts);
        mFab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openContactsApp();
            }
        });
    }

    private void openContactsApp() {
        String targetPackage = KatsunaUtils.KATSUNA_CONTACTS_PACKAGE;
        if (BuildConfig.BUILD_TYPE.equals(KatsunaUtils.BUILD_TYPE_STAGING)) {
            targetPackage = KatsunaUtils.KATSUNA_CONTACTS_STAGING_PACKAGE;
        }

        if (!Device.openApp(MainActivity.this, targetPackage)) {
            showContactsAppInstallationDialog();
        }
    }

    private void dial() {
        dial("");
    }

    private void dial(String number) {
        KatsunaAlertBuilder builder = new KatsunaAlertBuilder(this);
        builder.setTitle(getString(R.string.common_dial_instruction));
        builder.setView(R.layout.common_katsuna_alert);
        builder.setText(number);
        builder.setTextVisibility(View.VISIBLE);
        builder.setTextInputType(InputType.TYPE_CLASS_PHONE);
        builder.setUserProfile(mUserProfileContainer.getActiveUserProfile());
        builder.setOkListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        builder.setTextSelected(new KatsunaAlertBuilder.KatsunaAlertText() {
            @Override
            public void textSelected(String number) {
                if (!TextUtils.isEmpty(number)) {
                    callContact(number);
                }
            }
        });

        mDialog = builder.create();
        mDialog.show();
    }

    private boolean loadCalls() {
        if (!readContactsPermissionGranted()) {
            return false;
        }

        CallsProvider callsProvider = new CallsProvider(this);
        List<Call> mModels = callsProvider.getCalls();

        DayInfoFormatter.calculateDateInfo(this, mModels);

        mAdapter = new CallsAdapter(mModels, this, this);
        mAdapter.setDeleteMode(mDeleteModeOn);

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

    private boolean readContactsPermissionGranted() {
        boolean output;
        String[] permissions = new String[]{Manifest.permission.READ_CALL_LOG, Manifest.permission.READ_CONTACTS};
        if (!Device.hasAllPermissions(this, permissions)) {
            if (mDontAskForPermissions) {
                Toast.makeText(MainActivity.this, R.string.common_go_to_settings_permissions,
                        Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE_ASK_READ_CALL_LOG_PERMISSION);
            }
            output = false;
        } else {
            output = true;
        }

        return output;
    }

    private void showMessageForHiddenNumber() {
        Toast.makeText(this, R.string.hidden_number, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_READ_CALL_LOG_PERMISSION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "read contacts and calls permissions granted");
                    loadCalls();
                } else if (!shouldShowRequestPermissionRationale(permissions[0]) ||
                        !shouldShowRequestPermissionRationale(permissions[1])) {
                    Log.d(TAG, "read contacts or calls permissions set to never ask again");
                    mDontAskForPermissions = true;
                } else {
                    Log.d(TAG, "read contacts permission denied");
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

    private void sendSMS(String number, String name) {
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", number, null));
        i.putExtra(KatsunaConstants.EXTRA_DISPLAY_NAME, name);
        startActivity(i);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        // Assumes current activity is the searchable activity
        if (searchManager != null) {
            mSearchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
            mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    if (mItemSelected) {
                        deselectItem();
                    }
                    search(newText);
                    return false;
                }
            });
            mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
                @Override
                public boolean onClose() {
                    if (mAdapter != null) {
                        mAdapter.resetFilter();
                    }
                    return false;
                }
            });

            mSearchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    mSearchModeOn = hasFocus;
                    if (hasFocus) {
                        mFabsTopContainer.setVisibility(View.GONE);
                        showPopup(false);
                    } else {
                        refreshLastTouchTimestamp();
                        mFabsTopContainer.setVisibility(View.VISIBLE);
                    }
                }
            });
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.calls_action_selection:
                displayPopupWindow(mToolbar);
                break;
        }
        return true;
    }

    private void displayPopupWindow(View anchorView) {
        final PopupWindow popup = new PopupWindow(this);
        View layout = getLayoutInflater().inflate(R.layout.calls_actions_menu, null);

        TextView missedCallsSelector = layout.findViewById(R.id.missed_calls_menu_item);
        missedCallsSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterByCallType(CallLog.Calls.MISSED_TYPE);
                popup.dismiss();
            }
        });

        TextView incomingCallsSelector = layout.findViewById(R.id.incoming_calls_menu_item);
        incomingCallsSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterByCallType(CallLog.Calls.INCOMING_TYPE);
                popup.dismiss();
            }
        });

        TextView outgoingCallsSelector = layout.findViewById(R.id.outgoing_calls_menu_item);
        outgoingCallsSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterByCallType(CallLog.Calls.OUTGOING_TYPE);
                popup.dismiss();
            }
        });

        TextView allCallsSelector = layout.findViewById(R.id.all_calls_menu_item);
        allCallsSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdapter.resetFilter();
                mCallTypeFilteringOn = false;
                popup.dismiss();
            }
        });

        TextView deleteCallsItem = layout.findViewById(R.id.delete_calls_menu_item);
        deleteCallsItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableDeleteMode(!mDeleteModeOn);
                popup.dismiss();
            }
        });

        popup.setContentView(layout);

        // Closes the popup window when touch outside of it - when looses focus
        popup.setOutsideTouchable(true);
        popup.setFocusable(true);
        popup.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        int margin = getResources().getDimensionPixelSize(R.dimen.common_4dp);

        popup.showAtLocation(anchorView, Gravity.TOP | Gravity.END, margin, margin);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    private void handleIntent(Intent intent) {
        String action = intent.getAction();
        if (Intent.ACTION_VIEW.equals(action) || Intent.ACTION_DIAL.equals(action)) {
            String number = PhoneNumberUtils.getNumberFromIntent(intent, this);
            dial(number);
        } else if (Intent.ACTION_SEARCH.equals(action)) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            mSearchView.setQuery(query, false);
        }
    }

    private void search(String query) {
        if (mAdapter == null) return;
        if (TextUtils.isEmpty(query)) {
            mAdapter.resetFilter();
        } else {
            mAdapter.getFilter().filter(query);
        }
    }

    private void filterByCallType(int callType) {
        if (mAdapter == null) return;

        deselectItem();

        tintFabs(mDeleteModeOn);
        adjustFabPosition(!mDeleteModeOn);

        CallsAdapterBase.CallFilter filter = (CallsAdapterBase.CallFilter) mAdapter.getFilter();
        filter.show(callType, mDeleteModeOn);

        mCallTypeFilteringOn = true;
    }

    private void enableDeleteMode(boolean flag) {
        if (mAdapter == null) return;

        deselectItem();

        mDeleteModeOn = flag;
        mAdapter.enableDeleteMode(flag);

        tintFabs(flag);
        adjustFabPosition(!flag);
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
        KatsunaNavigationView mKatsunaNavigationView = findViewById(R.id.katsuna_navigation_view);
        mKatsunaNavigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                        mDrawerLayout.closeDrawers();

                        switch (menuItem.getItemId()) {
                            case R.id.drawer_settings:
                                if (readContactsPermissionGranted()) {
                                    startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                                }
                                break;
                            case R.id.drawer_info:
                                startActivity(new Intent(MainActivity.this, InfoActivity.class));
                                break;
                            case R.id.drawer_privacy:
                                BrowserUtils.openUrl(MainActivity.this, KATSUNA_PRIVACY_URL);
                                break;
                            case R.id.drawer_terms:
                                BrowserUtils.openUrl(MainActivity.this, KATSUNA_TERMS_OF_USE);
                                break;
                        }

                        return true;
                    }
                });
        mKatsunaNavigationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.closeDrawers();
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
        mDrawerLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.common_grey50));
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

        KeyboardUtils.hideKeyboard(this);

        mDrawerLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.common_black07));
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

        String name = null;
        Contact contact = call.getContact();
        if (contact != null) {
            name = contact.getName();
        }

        sendSMS(call.getNumber(), name);
    }

    @Override
    public void createContact(Call call) {
        if (call.getNumberPresentation() != CallLog.Calls.PRESENTATION_ALLOWED) {
            showMessageForHiddenNumber();
            return;
        }

        Intent i = new Intent(CREATE_CONTACT_ACTION);
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
    public void addToContact(Call call) {
        Intent i = new Intent(ADD_TO_CONTACT_ACTION);
        i.putExtra(ADD_TO_CONTACT_ACTION_NUMBER, call.getNumber());

        PackageManager packageManager = getPackageManager();
        List activities = packageManager.queryIntentActivities(i, PackageManager.MATCH_DEFAULT_ONLY);
        boolean isIntentSafe = activities.size() > 0;

        if (isIntentSafe) {
            startActivityForResult(i, ADD_TO_CONTACT_REQUEST);
            mCallNumberFocus = call.getNumber();
            //clear it from cache to enable fetching the fresh contact
            mContactSearchedMap.remove(mCallNumberFocus);
        } else {
            showContactsAppInstallationDialog();
        }
    }

    @Override
    public void editContact(Call call) {
        Intent i = new Intent(EDIT_CONTACT_ACTION);
        i.putExtra("number", call.getNumber());
        i.putExtra("contactId", call.getContact().getId());

        PackageManager packageManager = getPackageManager();
        List activities = packageManager.queryIntentActivities(i, PackageManager.MATCH_DEFAULT_ONLY);
        boolean isIntentSafe = activities.size() > 0;

        if (isIntentSafe) {
            startActivityForResult(i, EDIT_CONTACT_REQUEST);
            mCallNumberFocus = call.getNumber();
            //clear it from cache to enable fetching the fresh contact
            mContactSearchedMap.remove(mCallNumberFocus);
        } else {
            showContactsAppInstallationDialog();
        }
    }

    @Override
    public void showCallDetails(Call call) {
        Intent i = new Intent(this, CallDetailsActivity.class);
        i.putExtra("call", call);
        startActivity(i);
    }

    @Override
    public void deleteCall(final Call call) {
        if (!checkPermission()) {
            return;
        }

        KatsunaAlertBuilder builder = new KatsunaAlertBuilder(this);
        builder.setTitle(getString(R.string.delete_calls));
        builder.setMessage(getString(R.string.delete_call_approval));
        builder.setView(R.layout.common_katsuna_alert);
        builder.setUserProfile(mUserProfileContainer.getActiveUserProfile());
        builder.setOkListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CallsProvider callsProvider = new CallsProvider(MainActivity.this);
                callsProvider.deleteCall(call);
                loadCalls();
                Toast.makeText(MainActivity.this, R.string.calls_deleted, Toast.LENGTH_LONG)
                        .show();
            }
        });
        mDialog = builder.create();
        mDialog.show();
    }

    private boolean checkPermission() {
        if (!Device.hasPermission(this, Manifest.permission.WRITE_CALL_LOG)) {
            Device.requestPermission(this, Manifest.permission.WRITE_CALL_LOG, 0);
            return false;
        }
        return true;
    }


    private void setupCallsLogReceiver() {

        CallsAlarmReceiver alarm = new CallsAlarmReceiver();
        alarm.setAlarm(this);
    }

    private void setupSmsLogReceiver() {

        SmsAlarmReceiver alarm = new SmsAlarmReceiver();
        alarm.setAlarm(this);
    }

    @Override
    public void selectItemByStartingLetter(String s) {
        // no search bar so nothing to be done here
    }

    @Override
    public UserProfileContainer getUserProfileContainer() {
        return mUserProfileContainer;
    }

    @Override
    public UserProfile getUserProfile() {
        return mUserProfileContainer.getActiveUserProfile();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == EDIT_CONTACT_REQUEST || requestCode == CREATE_CONTACT_REQUEST
                || requestCode == ADD_TO_CONTACT_REQUEST) {
            mContactCache.clear();
            mContactSearchedMap.clear();
            loadCalls();
        }
    }
}
