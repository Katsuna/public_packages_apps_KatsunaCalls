package com.katsuna.calls.ui.viewholders;

import android.graphics.Typeface;
import android.provider.CallLog;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.katsuna.calls.R;
import com.katsuna.calls.domain.Call;
import com.katsuna.calls.ui.listeners.ICallInteractionListener;
import com.katsuna.commons.entities.OpticalParams;
import com.katsuna.commons.entities.SizeProfile;
import com.katsuna.commons.entities.SizeProfileKey;
import com.katsuna.commons.entities.UserProfileContainer;
import com.katsuna.commons.utils.DateFormatter;
import com.katsuna.commons.utils.SizeAdjuster;
import com.katsuna.commons.utils.SizeCalc;
import com.squareup.picasso.Picasso;

abstract class CallBaseViewHolder extends RecyclerView.ViewHolder {

    private final TextView mDateTime;
    final ICallInteractionListener mListener;
    final UserProfileContainer mUserProfileContainer;
    final ImageView mPhoto;
    final TextView mDisplayName;
    private final TextView mNumber;
    private final View mDayContainer;
    private final TextView mDayInfo;

    CallBaseViewHolder(View itemView, ICallInteractionListener listener) {
        super(itemView);
        mPhoto = (ImageView) itemView.findViewById(R.id.photo);
        mDisplayName = (TextView) itemView.findViewById(R.id.displayName);
        mNumber = (TextView) itemView.findViewById(R.id.number);
        mDateTime = (TextView) itemView.findViewById(R.id.dateTime);
        mDayContainer = itemView.findViewById(R.id.day_info_container);
        mDayInfo = (TextView) itemView.findViewById(R.id.day_info);
        mListener = listener;
        mUserProfileContainer = mListener.getUserProfileContainer();
    }

    void bind(Call call) {

        if (call.getContact() == null) {
            mPhoto.setImageBitmap(null);
            mDisplayName.setText(R.string.unknown);
        } else {
            Picasso.with(itemView.getContext())
                    .load(call.getContact().getPhotoUri())
                    .fit()
                    .into(mPhoto);

            mDisplayName.setText(call.getContact().getName());
        }

        if (call.getNumberPresentation() == CallLog.Calls.PRESENTATION_ALLOWED) {
            mNumber.setText(call.getNumber());
        }

        mDateTime.setText(DateFormatter.format(call.getDate()));
        if (mDayContainer != null) {
            if (TextUtils.isEmpty(call.getDayInfo())) {
                mDayContainer.setVisibility(View.GONE);
            } else {
                mDayContainer.setVisibility(View.VISIBLE);
            }
            mDayInfo.setText(call.getDayInfo());
        }
    }

    void adjustProfile() {
        SizeProfile sizeProfile = mUserProfileContainer.getOpticalSizeProfile();
        int size = itemView.getResources()
                .getDimensionPixelSize(R.dimen.common_contact_photo_size_intemediate);
        if (sizeProfile == SizeProfile.ADVANCED) {
            size = itemView.getResources()
                    .getDimensionPixelSize(R.dimen.common_contact_photo_size_advanced);
        } else if (sizeProfile == SizeProfile.SIMPLE) {
            size = itemView.getResources()
                    .getDimensionPixelSize(R.dimen.common_contact_photo_size_simple);
        }
        ViewGroup.LayoutParams layoutParams = mPhoto.getLayoutParams();
        layoutParams.height = size;
        layoutParams.width = size;
        mPhoto.setLayoutParams(layoutParams);

        // display name
        OpticalParams nameOpticalParams = SizeCalc.getOpticalParams(SizeProfileKey.TITLE,
                sizeProfile);
        SizeAdjuster.adjustText(itemView.getContext(), mDisplayName, nameOpticalParams);

        // number
        OpticalParams numberOpticalParams = SizeCalc.getOpticalParams(SizeProfileKey.SUBHEADER,
                sizeProfile);
        SizeAdjuster.adjustText(itemView.getContext(), mNumber, numberOpticalParams);

        // date
        OpticalParams dateOpticalParams = SizeCalc.getOpticalParams(SizeProfileKey.BODY_1,
                sizeProfile);
        SizeAdjuster.adjustText(itemView.getContext(), mDateTime, dateOpticalParams);
    }

}
