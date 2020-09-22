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
import android.provider.CallLog;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.katsuna.calls.R;
import com.katsuna.calls.domain.Call;
import com.katsuna.calls.utils.CallInfo;
import com.katsuna.commons.utils.DrawableGenerator;
import com.katsuna.commons.entities.UserProfile;
import com.katsuna.commons.entities.UserProfileContainer;

public class CallsHistViewHolder extends RecyclerView.ViewHolder {
    private final ImageView mCallTypeImage;
    private final TextView mCallDetails;
    private final TextView mCallDuration;
    private final UserProfile mUserProfile;


    public CallsHistViewHolder(View v, UserProfileContainer userProfileContainer) {
        super(v);

        mCallTypeImage = itemView.findViewById(R.id.call_type_image);
        mCallDetails = v.findViewById(R.id.call_details);
        mCallDuration = v.findViewById(R.id.call_duration);
        mUserProfile = userProfileContainer.getActiveUserProfile();
    }


    public void bind(Call call) {
        // style callTypeDrawable based on call type
        Drawable callTypeDrawable = DrawableGenerator.getCallTypeDrawable(itemView.getContext(),
                call.getType(), mUserProfile);
        mCallTypeImage.setImageDrawable(callTypeDrawable);

        mCallDetails.setText(CallInfo.getDetails(itemView.getContext(), call));

        if (call.getType() == CallLog.Calls.MISSED_TYPE) {
            mCallDuration.setVisibility(View.GONE);
        } else {
            mCallDuration.setVisibility(View.VISIBLE);
            mCallDuration.setText(CallInfo.getCallDuration(itemView.getContext(), call));
        }

    }
}
