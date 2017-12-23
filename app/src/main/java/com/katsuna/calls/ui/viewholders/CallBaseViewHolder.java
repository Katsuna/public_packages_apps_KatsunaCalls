package com.katsuna.calls.ui.viewholders;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.provider.CallLog;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.katsuna.calls.R;
import com.katsuna.calls.domain.Call;
import com.katsuna.calls.ui.listeners.ICallInteractionListener;
import com.katsuna.calls.utils.DrawableGenerator;
import com.katsuna.commons.entities.UserProfileContainer;
import com.katsuna.commons.utils.DateFormatter;

abstract class CallBaseViewHolder extends RecyclerView.ViewHolder {

    final ICallInteractionListener mListener;
    final UserProfileContainer mUserProfileContainer;
    final TextView mDisplayName;
    final TextView mContactDesc;
    private final View mDayContainer;
    private final TextView mDayInfo;
    private final TextView mCallDetails;
    private final CardView mCallContainer;
    private final RelativeLayout mCallContainerInner;
    private final ImageView mCallTypeImage;

    CallBaseViewHolder(View itemView, ICallInteractionListener listener) {
        super(itemView);
        mDisplayName = itemView.findViewById(R.id.display_name);
        mContactDesc = itemView.findViewById(R.id.contact_desc);
        mDayContainer = itemView.findViewById(R.id.day_info_container);
        mDayInfo = itemView.findViewById(R.id.day_info);
        mListener = listener;
        mUserProfileContainer = mListener.getUserProfileContainer();

        mCallTypeImage = itemView.findViewById(R.id.call_type_image);
        mCallDetails = itemView.findViewById(R.id.call_details);
        mCallContainer = itemView.findViewById(R.id.call_container_card);
        mCallContainerInner = itemView.findViewById(R.id.call_container_card_inner);
    }

    void bind(Call call) {

        // calc call details
        String callDetails = "";
        if (call.getType() == CallLog.Calls.INCOMING_TYPE) {
            callDetails = itemView.getResources().getString(R.string.incoming_call);
        } else if (call.getType() == CallLog.Calls.OUTGOING_TYPE) {
            callDetails = itemView.getResources().getString(R.string.outgoing_call);
        } else if (call.getType() == CallLog.Calls.MISSED_TYPE) {
            callDetails = itemView.getResources().getString(R.string.missed_call);
        }
        // add date
        callDetails += ", " + DateFormatter.format(call.getDate());
        mCallDetails.setText(callDetails);


        if (mDayContainer != null) {
            if (TextUtils.isEmpty(call.getDayInfo())) {
                mDayContainer.setVisibility(View.GONE);
            } else {
                mDayContainer.setVisibility(View.VISIBLE);
            }
            mDayInfo.setText(call.getDayInfo());
        }

        adjustColorBasedOnCallType(call.getType());
    }

    void adjustProfile() {
/*        SizeProfile sizeProfile = mUserProfileContainer.getOpticalSizeProfile();

        // display name
        OpticalParams nameOpticalParams = SizeCalc.getOpticalParams(SizeProfileKey.TITLE,
                sizeProfile);
        SizeAdjuster.adjustText(itemView.getContext(), mDisplayName, nameOpticalParams);

        // display name
        OpticalParams contactDescOpticalParams = SizeCalc.getOpticalParams(SizeProfileKey.BODY_2,
                sizeProfile);
        SizeAdjuster.adjustText(itemView.getContext(), mContactDesc, contactDescOpticalParams);

        // date
        OpticalParams dateOpticalParams = SizeCalc.getOpticalParams(SizeProfileKey.BODY_1,
                sizeProfile);
        SizeAdjuster.adjustText(itemView.getContext(), mCallDetails, dateOpticalParams);*/

    }

    private void adjustColorBasedOnCallType(int callType) {

        // calc colors
        int cardColor = 0;
        int cardColorAlpha = 0;

        if (callType == CallLog.Calls.INCOMING_TYPE) {
            cardColor = R.color.priority_two;
            cardColorAlpha = R.color.priority_two_tone_one;
        } else if (callType == CallLog.Calls.OUTGOING_TYPE) {
            cardColor = R.color.priority_one;
            cardColorAlpha = R.color.priority_one_tone_one;
        } else if (callType == CallLog.Calls.MISSED_TYPE) {
            cardColor = R.color.priority_three;
            cardColorAlpha = R.color.priority_three_tone_one;
        }

        // set colors
        if (cardColor != 0) {
            mCallContainer.setCardBackgroundColor(ColorStateList.valueOf(
                    ContextCompat.getColor(itemView.getContext(), cardColor)));
            mCallContainerInner.setBackgroundColor(
                    ContextCompat.getColor(itemView.getContext(), cardColorAlpha));
        }

        // style callTypeDrawable based on call type
        Drawable callTypeDrawable = DrawableGenerator.getCallTypeDrawable(itemView.getContext(),
                callType);
        mCallTypeImage.setImageDrawable(callTypeDrawable);
    }

}
