package com.katsuna.calls.ui.activities;

import android.Manifest;
import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.provider.CallLog;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.katsuna.calls.R;
import com.katsuna.calls.domain.Call;
import com.katsuna.calls.domain.Contact;
import com.katsuna.calls.providers.CallsProvider;
import com.katsuna.calls.providers.ContactInfoHelper;
import com.katsuna.calls.ui.adapters.CallsSelectionAdapter;
import com.katsuna.calls.ui.listeners.IContactResolver;
import com.katsuna.calls.utils.Device;
import com.katsuna.commons.ui.KatsunaActivity;
import com.katsuna.commons.utils.KatsunaAlertBuilder;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class SelectCallsActivity extends KatsunaActivity implements IContactResolver {

    private RecyclerView mRecyclerView;
    private TextView mNoResultsView;
    private CallsSelectionAdapter mAdapter;

    private LinkedHashMap<String, Contact> mContactCache;
    private ContactInfoHelper mContactInfoHelper;
    private LinkedHashMap<String, Boolean> mContactSearchedMap;
    private List<Call> mModels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_calls);

        initControls();

        mContactCache = new LinkedHashMap<>();
        mContactSearchedMap = new LinkedHashMap<>();
        mContactInfoHelper = new ContactInfoHelper(this);

        loadCalls();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView mSearchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
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


    private void search(String query) {
        if (TextUtils.isEmpty(query)) {
            mAdapter.resetFilter();
        } else {
            mAdapter.getFilter().filter(query);
        }
    }

    private boolean checkPermission() {
        if (!Device.hasPermission(this, Manifest.permission.WRITE_CALL_LOG)) {
            Device.requestPermission(this, Manifest.permission.WRITE_CALL_LOG, 0);
            return false;
        }
        return true;
    }

    private void initControls() {
        initToolbar();

        mFab1 = (FloatingActionButton) findViewById(R.id.fab);
        mFab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final List<Call> selectedCalls = getSelectedCalls();
                if (selectedCalls.size() > 0) {
                    deleteCalls(selectedCalls);
                } else {
                    Toast.makeText(SelectCallsActivity.this, R.string.no_calls_selected,
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.calls_list);
        mNoResultsView = (TextView) findViewById(R.id.no_results);
    }

    private void deleteCalls(final List<Call> selectedCalls) {
        if (!checkPermission()) {
            return;
        }

        KatsunaAlertBuilder builder = new KatsunaAlertBuilder(this);
        builder.setTitle(R.string.delete_calls);
        builder.setMessage(R.string.delete_calls_approval);
        builder.setView(R.layout.common_katsuna_alert);
        builder.setUserProfileContainer(mUserProfileContainer);
        builder.setOkListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CallsProvider callsProvider = new CallsProvider(SelectCallsActivity.this);
                for (Call call : selectedCalls) {
                    callsProvider.deleteCall(call);
                    mAdapter.removeItem(call);
                }
                Toast.makeText(SelectCallsActivity.this, R.string.calls_deleted, Toast.LENGTH_LONG)
                        .show();
            }
        });
        builder.create().show();
    }

    private List<Call> getSelectedCalls() {
        List<Call> output = new ArrayList<>();

        if (mModels != null) {
            for (Call model : mAdapter.getModels()) {
                if (model.isSelected()) {
                    output.add(model);
                }
            }
        }

        return output;
    }

    @Override
    protected void showPopup(boolean b) {
        // no op
    }

    private void loadCalls() {
        //get contacts from device
        CallsProvider callsProvider = new CallsProvider(this);
        mModels = callsProvider.getCalls();

        mAdapter = new CallsSelectionAdapter(mModels, this);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                showNoResultsView();
            }
        });
        showNoResultsView();
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
