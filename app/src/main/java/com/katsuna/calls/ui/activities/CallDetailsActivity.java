package com.katsuna.calls.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.katsuna.calls.R;
import com.katsuna.calls.domain.Call;
import com.katsuna.commons.ui.KatsunaActivity;
import com.squareup.picasso.Picasso;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class CallDetailsActivity extends KatsunaActivity {

    private static final String TAG = "CallDetailsActivity";
    private ImageView mContactPhoto;
    private TextView mContactName;
    private TextView mContactDesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_details);

        initControls();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent i = getIntent();
        Call call = i.getParcelableExtra("call");

        if (call.getContact() != null) {
            //mContactPhoto.setImageBitmap(null);
            //mContactName.setText(R.string.unknown);
            if (call.getContact().getPhotoUri() == null) {
                mContactPhoto.setImageDrawable(getDrawable(R.drawable.ic_no_photo));
                int sizeInPixel = getResources().getDimensionPixelSize(R.dimen.common_20dp);
                mContactPhoto.getLayoutParams().width = sizeInPixel;
                mContactPhoto.getLayoutParams().height = sizeInPixel;
            } else {
                Picasso.with(this)
                        .load(call.getContact().getPhotoUri())
                        .transform(new CropCircleTransformation())
                        .into(mContactPhoto);
            }

            mContactName.setText(call.getContact().getName());
            mContactDesc.setText(call.getContact().getDescription());
        }

    }

    private void initControls() {
        initToolbar();

        mContactPhoto = findViewById(R.id.contact_photo);
        mContactName = findViewById(R.id.contact_name);
        mContactDesc = findViewById(R.id.contact_desc);
    }

    @Override
    protected void showPopup(boolean flag) {

    }
}
