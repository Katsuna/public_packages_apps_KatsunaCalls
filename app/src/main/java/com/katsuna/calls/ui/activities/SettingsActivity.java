package com.katsuna.calls.ui.activities;

import android.os.Bundle;

import com.katsuna.calls.R;
import com.katsuna.commons.ui.SettingsKatsunaActivity;

public class SettingsActivity extends SettingsKatsunaActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initControls();
    }

    private void initControls() {
        initToolbar();
        initSizeProfiles();
        initColorProfiles();
        initRightHand();
    }
}
