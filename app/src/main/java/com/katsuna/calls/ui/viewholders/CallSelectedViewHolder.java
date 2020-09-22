/**
* Copyright (C) 2020 Manos Saratsis
*
* This file is part of Katsuna.
*
* Katsuna is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* Katsuna is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with Katsuna.  If not, see <https://www.gnu.org/licenses/>.
*/
package com.katsuna.calls.ui.viewholders;

import android.content.res.Resources;
import android.provider.CallLog;
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
import com.katsuna.commons.utils.ColorAdjusterV2;
import com.katsuna.commons.utils.SizeAdjuster;
import com.katsuna.commons.utils.SizeCalcV2;

public class CallSelectedViewHolder extends CallBaseViewHolder {

    private final Button mCallButton;
    private final View mCallButtonsContainer;
    private final Button mMessageButton;
    private final TextView mMoreText;
    private final View mMoreActionsContainer;
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

        mCreateContactText.setOnClickListener(new View.OnClickListener() {
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

        mEditContactText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.editContact(call);
            }
        });

        mCallHistoryText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.showCallDetails(call);
            }
        });

        if (call.getContact() == null) {
            mCreateContactText.setVisibility(View.VISIBLE);
            mAddToExistingText.setVisibility(View.VISIBLE);
            mCallHistoryText.setVisibility(View.GONE);
            mEditContactText.setVisibility(View.GONE);
        } else {
            mCallHistoryText.setVisibility(View.VISIBLE);
            mEditContactText.setVisibility(View.VISIBLE);
            mCreateContactText.setVisibility(View.GONE);
            mAddToExistingText.setVisibility(View.GONE);
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
        ColorAdjusterV2.adjustButtons(itemView.getContext(),
                mUserProfileContainer.getActiveUserProfile(),
                mCallButton, mMessageButton, mMoreText);
    }
}
