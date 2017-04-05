package com.katsuna.calls.ui.viewholders;

import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.katsuna.calls.R;
import com.katsuna.calls.domain.Call;
import com.katsuna.calls.ui.listeners.ICallInteractionListener;
import com.katsuna.commons.entities.ColorProfile;
import com.katsuna.commons.entities.ColorProfileKey;
import com.katsuna.commons.entities.OpticalParams;
import com.katsuna.commons.entities.SizeProfile;
import com.katsuna.commons.entities.SizeProfileKey;
import com.katsuna.commons.utils.ColorCalc;
import com.katsuna.commons.utils.Shape;
import com.katsuna.commons.utils.SizeAdjuster;
import com.katsuna.commons.utils.SizeCalc;

public class CallSelectedViewHolder extends CallBaseViewHolder {

    private final Button mCallButton;
    private final View mCallButtonContainer;
    private final View mMessageButtonContainer;
    private final Button mMessageButton;
    private final ImageButton mCreateContact;
    private final ImageButton mDeleteCallButton;
    private final View mCallSelectedContainer;

    public CallSelectedViewHolder(View itemView, ICallInteractionListener listener) {
        super(itemView, listener);
        mCallButton = (Button) itemView.findViewById(R.id.call_button);
        mCallButtonContainer = itemView.findViewById(R.id.call_button_container);
        mMessageButton = (Button) itemView.findViewById(R.id.message_button);
        mMessageButtonContainer = itemView.findViewById(R.id.message_button_container);
        mCreateContact = (ImageButton) itemView.findViewById(R.id.createContact);
        mCallSelectedContainer = itemView.findViewById(R.id.call_selected_container);
        mDeleteCallButton = (ImageButton) itemView.findViewById(R.id.delete_call_button);
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
        mCallButtonContainer.setOnClickListener(new View.OnClickListener() {
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
        mMessageButtonContainer.setOnClickListener(new View.OnClickListener() {
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

        mDeleteCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.deleteCall(call);
            }
        });

        adjustProfile();
        adjustDisplayForNameAndNumber(call);
    }

    void adjustProfile() {
        super.adjustProfile();
        SizeProfile sizeProfile = mUserProfileContainer.getOpticalSizeProfile();

        // adjust buttons
        OpticalParams opticalParams = SizeCalc.getOpticalParams(SizeProfileKey.ACTION_BUTTON,
                sizeProfile);
        SizeAdjuster.adjustText(itemView.getContext(), mCallButton, opticalParams);
        SizeAdjuster.adjustText(itemView.getContext(), mMessageButton, opticalParams);

        SizeAdjuster.adjustButtonContainer(itemView.getContext(), mCallButtonContainer,
                opticalParams);
        SizeAdjuster.adjustButtonContainer(itemView.getContext(), mMessageButtonContainer,
                opticalParams);

        adjustColorProfile();
    }

    private void adjustColorProfile() {
        ColorProfile colorProfile = mUserProfileContainer.getColorProfile();
        // set action buttons background color
        int color1 = ColorCalc.getColor(itemView.getContext(),
                ColorProfileKey.ACCENT1_COLOR, colorProfile);
        Shape.setRoundedBackground(mCallButtonContainer, color1);

        int color2 = ColorCalc.getColor(itemView.getContext(), ColorProfileKey.ACCENT2_COLOR,
                colorProfile);
        Shape.setRoundedBackground(mMessageButtonContainer, color2);

        // set background color
        int bgColor = ColorCalc.getColor(itemView.getContext(), ColorProfileKey.POP_UP_COLOR,
                colorProfile);
        mCallSelectedContainer.setBackgroundColor(bgColor);
        mCreateContact.setBackgroundColor(bgColor);
    }
}
