package com.katsuna.calls.ui.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.katsuna.calls.R;
import com.katsuna.commons.KatsunaConstants;
import com.katsuna.commons.entities.ProfileType;
import com.katsuna.commons.utils.SettingsManager;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initToolbar();
        initControls();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initControls() {
        Spinner mProfileTypes = (Spinner) findViewById(R.id.profiles);
        int profileSetting = SettingsManager.readSetting(this, KatsunaConstants.PROFILE_KEY, ProfileType.INTERMEDIATE.getNumVal());
        mProfileTypes.setSelection(profileSetting);
        mProfileTypes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                SettingsManager.setSetting(SettingsActivity.this, KatsunaConstants.PROFILE_KEY, i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
}
