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
import android.view.View;
import android.widget.FrameLayout;

import com.katsuna.calls.R;
import com.katsuna.calls.domain.Call;
import com.katsuna.calls.ui.listeners.ICallInteractionListener;

public class CallViewHolder extends CallBaseViewHolder {

    private final View mCallContainer;
    private final View mOpacityLayer;

    public CallViewHolder(View itemView, ICallInteractionListener listener) {
        super(itemView, listener);
        mCallContainer = itemView.findViewById(R.id.call_container_card);
        mOpacityLayer = itemView.findViewById(R.id.opacity_layer);
    }

    public void bindGreyed(Call call, final int position) {
        this.bind(call, position, false);
        if (mOpacityLayer != null) {
            mOpacityLayer.setVisibility(View.VISIBLE);
        }
    }

    public void bind(final Call call, final int position, boolean deleteMode) {
        super.bind(call);

        // bind display name
        Resources res = itemView.getResources();
        // calc name
        String name;
        if (call.getContact() == null) {
            name = res.getString(R.string.unknown);
            if (call.getNumberPresentation() == CallLog.Calls.PRESENTATION_ALLOWED) {
                name += ": " + call.getNumber();
            }

        } else {
            name = call.getContact().getName();
        }
        mDisplayName.setText(name);


        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mCallContainer.getLayoutParams();
        if (deleteMode) {
            int marginEnd = res.getDimensionPixelSize(R.dimen.delete_button_container_margin);
            params.setMarginEnd(marginEnd);
            mDeleteCallCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.deleteCall(call);
                }
            });

            mCallContainer.setOnClickListener(null);
            mDisplayName.setOnClickListener(null);
        } else {
            params.setMarginEnd(0);

            mCallContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.selectCall(position);
                }
            });

            // direct focus on non selected contact if photo or name is clicked
            View.OnClickListener focusContact = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.focusCall(position);
                }
            };
            mDisplayName.setOnClickListener(focusContact);

            if (mOpacityLayer != null) {
                mOpacityLayer.setVisibility(View.INVISIBLE);
            }
        }

        adjustProfile();
    }
}
