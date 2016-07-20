package com.katsuna.calls.ui.viewholders;

import android.graphics.Typeface;
import android.provider.CallLog;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.katsuna.calls.R;
import com.katsuna.calls.domain.Call;
import com.katsuna.calls.ui.listeners.ICallInteractionListener;
import com.katsuna.commons.entities.Profile;
import com.katsuna.commons.entities.ProfileType;

public class CallSelectedViewHolder extends CallBaseViewHolder {

    private final ICallInteractionListener mListener;
    private final Button mCallButton;
    private final Button mMessageButton;
    private final ImageView mCreateContact;
    private final Profile mProfile;

    public CallSelectedViewHolder(View itemView, ICallInteractionListener listener, Profile profile) {
        super(itemView);
        mCallButton = (Button) itemView.findViewById(R.id.call_button);
        mMessageButton = (Button) itemView.findViewById(R.id.message_button);
        mCreateContact = (ImageView) itemView.findViewById(R.id.createContact);
        mListener = listener;
        mProfile = profile;
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
            mDisplayName.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.red));
            mDisplayName.setTypeface(null, Typeface.BOLD);
            mNumber.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.red));
            mNumber.setTypeface(null, Typeface.BOLD);
        } else {
            mDisplayName.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.white));
            mDisplayName.setTypeface(null, Typeface.NORMAL);
            mNumber.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.white));
            mNumber.setTypeface(null, Typeface.NORMAL);
        }
    }

    private void adjustProfile() {
        if (mProfile != null) {
            int photoSize = itemView.getResources().getDimensionPixelSize(R.dimen.contact_photo_size_intemediate);
            int actionButtonHeight = itemView.getResources().getDimensionPixelSize(R.dimen.action_button_height_intemediate);

            if (mProfile.getType() == ProfileType.ADVANCED.getNumVal()) {
                photoSize = itemView.getResources().getDimensionPixelSize(R.dimen.contact_photo_size_advanced);
                actionButtonHeight = itemView.getResources().getDimensionPixelSize(R.dimen.action_button_height_advanced);
            } else if (mProfile.getType() == ProfileType.SIMPLE.getNumVal()) {
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
        }
    }
}
