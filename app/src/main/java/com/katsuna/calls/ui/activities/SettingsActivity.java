package com.katsuna.calls.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

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

        Button deleteButton = (Button) findViewById(R.id.deleteContacts);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this, SelectCallsActivity.class));
            }
        });
    }
}
