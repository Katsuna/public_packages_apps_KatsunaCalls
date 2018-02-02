package com.katsuna.calls.ui.viewholders;

import android.content.Context;
import android.content.res.Resources;
import android.provider.CallLog;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.katsuna.calls.R;
import com.katsuna.calls.domain.Call;
import com.katsuna.calls.domain.Contact;
import com.katsuna.calls.ui.listeners.ICallInteractionListener;
import com.katsuna.commons.entities.OpticalParams;
import com.katsuna.commons.entities.SizeProfile;
import com.katsuna.commons.entities.SizeProfileKeyV2;
import com.katsuna.commons.utils.Shape;
import com.katsuna.commons.utils.SizeAdjuster;
import com.katsuna.commons.utils.SizeCalcV2;

public class CallSelectedViewHolder extends CallBaseViewHolder {

    private final Button mCallButton;
    private final View mCallButtonsContainer;
    private final Button mMessageButton;
    private final TextView mMoreText;
    private final View mMoreActionsContainer;
    private final View mCreateContactContainer;
    private final View mAddToExistingContainer;
    private final View mCallHistoryContainer;
    private final View mEditContactContainer;
    private final TextView mCreateContactText;
    private final TextView mAddToExistingText;
    private final TextView mEditContactText;
    private final TextView mCallHistoryText;

    public CallSelectedViewHolder(View itemView, ICallInteractionListener listener) {
        super(itemView, listener);
        mCallButtonsContainer = itemView.findViewById(R.id.call_buttons_container);
        mCallButton = itemView.findViewById(R.id.button_call);
        mMessageButton = itemView.findViewById(R.id.button_message);
        mMoreText = itemView.findViewById(R.id.txt_more);
        mMoreActionsContainer = itemView.findViewById(R.id.more_actions_container);
        mCreateContactContainer = itemView.findViewById(R.id.create_contact_container);
        mAddToExistingContainer = itemView.findViewById(R.id.add_to_existing_contact_container);
        mCallHistoryContainer = itemView.findViewById(R.id.call_history_container);
        mEditContactContainer = itemView.findViewById(R.id.edit_contact_container);
        mCreateContactText = itemView.findViewById(R.id.create_contact_text);
        mAddToExistingText = itemView.findViewById(R.id.add_to_existing_contact_text);
        mCallHistoryText = itemView.findViewById(R.id.call_history_text);
        mEditContactText = itemView.findViewById(R.id.edit_contact_text);
    }

    public void bind(final Call call) {
        super.bind(call);

        // bind display name
        Resources res = itemView.getResources();
        // calc name
        String name;

        if (call.getContact() == null) {
            name = res.getString(R.string.unknown);

        } else {
            name = call.getContact().getName();
        }
        mDisplayName.setText(name);

        if (call.getNumberPresentation() == CallLog.Calls.PRESENTATION_ALLOWED) {
            String description = call.getNumber();
            Contact contact = call.getContact();
            if (contact != null) {
                if (!TextUtils.isEmpty(contact.getDescription())) {
                    description = contact.getDescription();
                }
            }

            mContactDesc.setText(description);
            mContactDesc.setVisibility(View.VISIBLE);
        }

        mCallButtonsContainer.setVisibility(View.VISIBLE);

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

        mMoreText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMoreActionsContainer.getVisibility() == View.VISIBLE) {
                    expandMoreActions(false);
                } else {
                    expandMoreActions(true);
                }
            }
        });

        // by default more actions are hidden
        expandMoreActions(false);

        mCreateContactContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.createContact(call);
            }
        });

        mAddToExistingText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.addToContact(call);
            }
        });

        mEditContactContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.editContact(call);
            }
        });

        mCallHistoryContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.showCallDetails(call);
            }
        });

        if (call.getContact() == null) {
            mCreateContactContainer.setVisibility(View.VISIBLE);
            mAddToExistingContainer.setVisibility(View.VISIBLE);
            mCallHistoryContainer.setVisibility(View.GONE);
            mEditContactContainer.setVisibility(View.GONE);
        } else {
            mCallHistoryContainer.setVisibility(View.VISIBLE);
            mEditContactContainer.setVisibility(View.VISIBLE);
            mCreateContactContainer.setVisibility(View.GONE);
            mAddToExistingContainer.setVisibility(View.GONE);
        }

        adjustProfile();
    }

    private void expandMoreActions(boolean flag) {
        if (flag) {
            mMoreActionsContainer.setVisibility(View.VISIBLE);
            mMoreText.setText(R.string.common_less);
        } else {
            mMoreActionsContainer.setVisibility(View.GONE);
            mMoreText.setText(R.string.common_more);
        }
    }

    void adjustProfile() {
        super.adjustProfile();
        SizeProfile sizeProfile = mUserProfileContainer.getOpticalSizeProfile();

        // adjust buttons
        OpticalParams opticalParams = SizeCalcV2.getOpticalParams(SizeProfileKeyV2.BUTTON,
                sizeProfile);
        SizeAdjuster.adjustText(itemView.getContext(), mCallButton, opticalParams);
        SizeAdjuster.adjustText(itemView.getContext(), mMessageButton, opticalParams);

        // more text
        opticalParams = SizeCalcV2.getOpticalParams(SizeProfileKeyV2.BUTTON,
                sizeProfile);
        SizeAdjuster.adjustText(itemView.getContext(), mMoreText, opticalParams);

        // level_2 action text
        opticalParams = SizeCalcV2.getOpticalParams(SizeProfileKeyV2.SUBHEADING_2,
                sizeProfile);
        SizeAdjuster.adjustText(itemView.getContext(), mCreateContactText, opticalParams);
        SizeAdjuster.adjustText(itemView.getContext(), mAddToExistingText, opticalParams);
        SizeAdjuster.adjustText(itemView.getContext(), mCallHistoryText, opticalParams);
        SizeAdjuster.adjustText(itemView.getContext(), mEditContactText, opticalParams);

        adjustColorProfile();
    }

    private void adjustColorProfile() {
        adjustPrimaryButton(itemView.getContext(), mCallButton);
        adjustSecondaryButton(itemView.getContext(), mMessageButton);
    }

    private void adjustPrimaryButton(Context context, Button button) {
        int color1 = ContextCompat.getColor(context, R.color.buttons_color);
        Shape.setRoundedBackground(button, color1);
    }

    private void adjustSecondaryButton(Context context, Button button) {
        int color1 = ContextCompat.getColor(context, R.color.buttons_color);
        int white = ContextCompat.getColor(context, R.color.common_white);
        Shape.setRoundedBorder(button, color1, white);
    }

}
