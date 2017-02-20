package com.katsuna.calls.ui.viewholders;

import android.graphics.Typeface;
import android.provider.CallLog;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.katsuna.calls.R;
import com.katsuna.calls.domain.Call;
import com.katsuna.calls.ui.listeners.ICallInteractionListener;
import com.katsuna.commons.entities.ColorProfile;
import com.katsuna.commons.entities.ColorProfileKey;
import com.katsuna.commons.entities.ProfileType;
import com.katsuna.commons.entities.UserProfileContainer;
import com.katsuna.commons.utils.ColorCalc;
import com.katsuna.commons.utils.Shape;

public class CallSelectedViewHolder extends CallBaseViewHolder {

    private final ICallInteractionListener mListener;
    private final Button mCallButton;
    private final Button mMessageButton;
    private final ImageButton mCreateContact;
    private final UserProfileContainer mUserProfileContainer;
    private final View mCallSelectedContainer;

    public CallSelectedViewHolder(View itemView, ICallInteractionListener listener) {
        super(itemView);
        mCallButton = (Button) itemView.findViewById(R.id.call_button);
        mMessageButton = (Button) itemView.findViewById(R.id.message_button);
        mCreateContact = (ImageButton) itemView.findViewById(R.id.createContact);
        mCallSelectedContainer = itemView.findViewById(R.id.call_selected_container);
        mListener = listener;
        mUserProfileContainer = mListener.getUserProfileContainer();
        adjustProfile();
    }

    public void bind(final Call call) {
        super.bind(call);

        mCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.callContact(call);
            }
        });
        mMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.sendSMS(call);
            }
        });

        mCreateContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.createContact(call);
            }
        });

        if (call.getContact() == null) {
            mCreateContact.setVisibility(View.VISIBLE);
        } else {
            mCreateContact.setVisibility(View.GONE);
        }
    }

    @Override
    void adjustDisplayForNameAndNumber(Call call) {
        if (call.getType() == CallLog.Calls.MISSED_TYPE) {
            int textColor = ColorCalc.getColor(itemView.getContext(), ColorProfileKey.ACCENT1_COLOR,
                    mUserProfileContainer.getColorProfile());
            mDisplayName.setTextColor(textColor);
            mDisplayName.setTypeface(null, Typeface.BOLD);
            mNumber.setTypeface(null, Typeface.BOLD);
        } else {
            mDisplayName.setTextColor(ContextCompat.getColor(itemView.getContext(),
                    R.color.common_black));
            mDisplayName.setTypeface(null, Typeface.NORMAL);
            mNumber.setTypeface(null, Typeface.NORMAL);
        }
    }

    private void adjustProfile() {
        ProfileType sizeProfile = mUserProfileContainer.getOpticalSizeProfile();

        //default values
        int photoSize = itemView.getResources().getDimensionPixelSize(R.dimen.contact_photo_size_intemediate);
        int actionButtonHeight = itemView.getResources().getDimensionPixelSize(R.dimen.action_button_height_intemediate);

        if (sizeProfile == ProfileType.ADVANCED) {
            photoSize = itemView.getResources().getDimensionPixelSize(R.dimen.contact_photo_size_advanced);
            actionButtonHeight = itemView.getResources().getDimensionPixelSize(R.dimen.action_button_height_advanced);
        } else if (sizeProfile == ProfileType.SIMPLE) {
            photoSize = itemView.getResources().getDimensionPixelSize(R.dimen.contact_photo_size_simple);
            actionButtonHeight = itemView.getResources().getDimensionPixelSize(R.dimen.action_button_height_simple);
        }
        ViewGroup.LayoutParams layoutParams = mPhoto.getLayoutParams();
        layoutParams.height = photoSize;
        layoutParams.width = photoSize;
        mPhoto.setLayoutParams(layoutParams);

        ViewGroup.LayoutParams callButtonParams = mCallButton.getLayoutParams();
        callButtonParams.height = actionButtonHeight;

        ViewGroup.LayoutParams messageButtonParams = mMessageButton.getLayoutParams();
        messageButtonParams.height = actionButtonHeight;

        mCallButton.setLayoutParams(callButtonParams);
        mMessageButton.setLayoutParams(messageButtonParams);

        adjustColorProfile();
    }

    private void adjustColorProfile() {
        ColorProfile colorProfile = mUserProfileContainer.getColorProfile();
        // set action buttons background color
        int color1 = ColorCalc.getColor(itemView.getContext(),
                ColorProfileKey.ACCENT1_COLOR, colorProfile);
        Shape.setRoundedBackground(mCallButton, color1);

        int color2 = ColorCalc.getColor(itemView.getContext(), ColorProfileKey.ACCENT2_COLOR,
                colorProfile);
        Shape.setRoundedBackground(mMessageButton, color2);

        // set background color
        int bgColor = ColorCalc.getColor(itemView.getContext(), ColorProfileKey.POP_UP_COLOR,
                colorProfile);
        mCallSelectedContainer.setBackgroundColor(bgColor);
        mCreateContact.setBackgroundColor(bgColor);
    }
}
