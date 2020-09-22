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

import android.graphics.drawable.Drawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.katsuna.calls.R;
import com.katsuna.calls.domain.Call;
import com.katsuna.calls.ui.listeners.ICallInteractionListener;
import com.katsuna.calls.utils.CallInfo;
import com.katsuna.calls.utils.CallTypeAdjuster;
import com.katsuna.commons.utils.DrawableGenerator;
import com.katsuna.commons.entities.OpticalParams;
import com.katsuna.commons.entities.SizeProfile;
import com.katsuna.commons.entities.SizeProfileKeyV2;
import com.katsuna.commons.entities.UserProfileContainer;
import com.katsuna.commons.utils.SizeAdjuster;
import com.katsuna.commons.utils.SizeCalcV2;

abstract class CallBaseViewHolder extends RecyclerView.ViewHolder {

    final ICallInteractionListener mListener;
    final UserProfileContainer mUserProfileContainer;
    final TextView mDisplayName;
    final TextView mContactDesc;
    final View mDeleteCallCard;
    private final TextView mCallDetails;
    private final CardView mCallContainer;
    private final RelativeLayout mCallContainerInner;
    private final ImageView mCallTypeImage;

    CallBaseViewHolder(View itemView, ICallInteractionListener listener) {
        super(itemView);
        mDisplayName = itemView.findViewById(R.id.display_name);
        mContactDesc = itemView.findViewById(R.id.contact_desc);
        mListener = listener;
        mUserProfileContainer = mListener.getUserProfileContainer();

        mCallTypeImage = itemView.findViewById(R.id.call_type_image);
        mCallDetails = itemView.findViewById(R.id.call_details);
        mCallContainer = itemView.findViewById(R.id.call_container_card);
        mCallContainerInner = itemView.findViewById(R.id.call_container_card_inner);
        mDeleteCallCard = itemView.findViewById(R.id.delete_call_card);
    }

    void bind(Call call) {

        mCallDetails.setText(CallInfo.getDetails(itemView.getContext(), call));

        adjustColorBasedOnCallType(call.getType());
    }

    void adjustProfile() {

        SizeProfile sizeProfile = mUserProfileContainer.getOpticalSizeProfile();

        // item type icon
        OpticalParams opticalParams = SizeCalcV2.getOpticalParams(SizeProfileKeyV2.ICON_1,
                sizeProfile);
        SizeAdjuster.adjustIcon(itemView.getContext(), mCallTypeImage, opticalParams);

        // date
        opticalParams = SizeCalcV2.getOpticalParams(SizeProfileKeyV2.BODY_1, sizeProfile);
        SizeAdjuster.adjustText(itemView.getContext(), mCallDetails, opticalParams);

        // display name
        opticalParams = SizeCalcV2.getOpticalParams(SizeProfileKeyV2.TITLE, sizeProfile);
        SizeAdjuster.adjustText(itemView.getContext(), mDisplayName, opticalParams);

        // contact description
        opticalParams = SizeCalcV2.getOpticalParams(SizeProfileKeyV2.SUBHEADING_1, sizeProfile);
        SizeAdjuster.adjustText(itemView.getContext(), mContactDesc, opticalParams);
    }

    private void adjustColorBasedOnCallType(int callType) {

        CallTypeAdjuster.adjustCardLayout(itemView.getContext(), callType, mCallContainer,
                mCallContainerInner, mListener.getUserProfileContainer().getActiveUserProfile());

        // style callTypeDrawable based on call type
        Drawable callTypeDrawable = DrawableGenerator.getCallTypeDrawable(itemView.getContext(),
                callType, mUserProfileContainer.getActiveUserProfile());
        mCallTypeImage.setImageDrawable(callTypeDrawable);
    }

}
