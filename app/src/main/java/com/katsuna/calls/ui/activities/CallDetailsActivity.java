package com.katsuna.calls.ui.activities;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.katsuna.calls.R;
import com.katsuna.calls.domain.Call;
import com.katsuna.calls.domain.CallSearchParams;
import com.katsuna.calls.providers.CallsProvider;
import com.katsuna.calls.ui.adapters.CallsHistAdapter;
import com.katsuna.calls.utils.CallInfo;
import com.katsuna.calls.utils.CallTypeAdjuster;
import com.katsuna.calls.utils.DividerItemDecorator;
import com.katsuna.commons.utils.DrawableGenerator;
import com.katsuna.commons.ui.KatsunaActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class CallDetailsActivity extends KatsunaActivity {

    private static final String TAG = "CallDetailsActivity";
    private ImageView mContactPhoto;
    private TextView mContactName;
    private TextView mContactDesc;
    private ImageView mCallTypeImage;
    private TextView mPhone;
    private TextView mCallDesc;
    private TextView mCallDuration;
    private CardView mCallContainerCard;
    private View mCallContainerInner;
    private RecyclerView mRecentCallsList;
    private View mCallHistContainerCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_details);

        initControls();
    }

    private void initControls() {
        initToolbar(R.drawable.common_ic_close_black54_24dp);

        mCallContainerCard = findViewById(R.id.call_container_card);
        mCallHistContainerCard = findViewById(R.id.call_hist_container_card);
        mCallContainerInner = findViewById(R.id.call_container_card_inner);
        mContactPhoto = findViewById(R.id.contact_photo);
        mContactPhoto = findViewById(R.id.contact_photo);
        mContactName = findViewById(R.id.contact_name);
        mContactDesc = findViewById(R.id.contact_desc);
        mCallTypeImage = findViewById(R.id.call_type_image);
        mPhone = findViewById(R.id.phone);
        mCallDesc = findViewById(R.id.call_desc);
        mCallDuration = findViewById(R.id.call_duration);
        mRecentCallsList = findViewById(R.id.recent_calls_list);
        mRecentCallsList.setHasFixedSize(true);
        mRecentCallsList.setLayoutManager(new LinearLayoutManager(this));

        RecyclerView.ItemDecoration dividerItemDecoration = new DividerItemDecorator(
                ContextCompat.getDrawable(this, R.drawable.divider));
        mRecentCallsList.addItemDecoration(dividerItemDecoration);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent i = getIntent();
        Call call = i.getParcelableExtra("call");
        bindCall(call);
    }

    private void bindCall(Call call) {
        if (call.getContact() != null) {
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

        mPhone.setText(call.getNumber());
        mCallDesc.setText(CallInfo.getDetails(this, call));

        mCallDuration.setText(CallInfo.getCallDuration(this, call));

        CallTypeAdjuster.adjustCardLayout(this, call.getType(), mCallContainerCard,
                mCallContainerInner, mUserProfileContainer.getActiveUserProfile());

        // style callTypeDrawable based on call type
        Drawable callTypeDrawable = DrawableGenerator.getCallTypeDrawable(this, call.getType(),
                mUserProfileContainer.getActiveUserProfile());
        mCallTypeImage.setImageDrawable(callTypeDrawable);


        // get last 4 calls from the same number
        List<Call> calls = new CallsProvider(this).getCalls(
                new CallSearchParams(call.getNumber(), 4, call.getId()));

        if (calls.size() == 0) {
            mCallHistContainerCard.setVisibility(View.GONE);
        } else {
            CallsHistAdapter adapter = new CallsHistAdapter(calls, mUserProfileContainer);
            mRecentCallsList.setAdapter(adapter);
        }
    }

    @Override
    protected void showPopup(boolean flag) {

    }
}
