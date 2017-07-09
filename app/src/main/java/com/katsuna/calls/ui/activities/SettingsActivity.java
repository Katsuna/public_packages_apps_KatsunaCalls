package com.katsuna.calls.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;

import com.katsuna.calls.R;
import com.katsuna.commons.entities.ColorProfile;
import com.katsuna.commons.entities.SizeProfile;
import com.katsuna.commons.ui.SettingsKatsunaActivity;
import com.katsuna.commons.utils.ColorAdjuster;
import com.katsuna.commons.utils.ProfileReader;
import com.katsuna.commons.utils.SizeAdjuster;
import com.katsuna.commons.utils.ViewUtils;

public class SettingsActivity extends SettingsKatsunaActivity {

    private Button mDeleteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initControls();
    }

    @Override
    protected void onResume() {
        super.onResume();

        applyProfiles();
        loadProfiles();
        applyColorProfile(mUserProfileContainer.getColorProfile());
    }

    @Override
    protected void applyColorProfile(ColorProfile colorProfile) {
        ColorProfile profile = colorProfile;
        if (colorProfile == ColorProfile.AUTO) {
            profile = ProfileReader.getUserProfileFromKatsunaServices(this).colorProfile;
        }
        ColorAdjuster.adjustButtons(this, profile, mDeleteButton, null);
    }

    @Override
    protected void applySizeProfile(SizeProfile sizeProfile) {
        ViewGroup topViewGroup = (ViewGroup) findViewById(android.R.id.content);
        SizeAdjuster.applySizeProfile(this, topViewGroup, sizeProfile);
    }

    private void initControls() {
        initToolbar();
        initAppSettings();
        mScrollViewContainer = (ScrollView) findViewById(R.id.scroll_view_container);

        mDeleteButton = (Button) findViewById(R.id.deleteContacts);
        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this, SelectCallsActivity.class));
            }
        });
    }
}
