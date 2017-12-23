package com.katsuna.calls.ui.viewholders;

import android.content.res.Resources;
import android.provider.CallLog;
import android.view.View;

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
        this.bind(call, position);
        if (mOpacityLayer != null) {
            mOpacityLayer.setVisibility(View.VISIBLE);
        }
    }

    public void bind(Call call, final int position) {
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

        //
/*        SizeProfile sizeProfile = mUserProfileContainer.getOpticalSizeProfile();
        int size = itemView.getResources()
                .getDimensionPixelSize(R.dimen.common_icon_h_intermediate);
        if (sizeProfile == SizeProfile.SIMPLE) {
            size = itemView.getResources().getDimensionPixelSize(R.dimen.common_icon_h_simple);
        }

        if (call.getType() == CallLog.Calls.MISSED_TYPE) {
            size = itemView.getResources()
                    .getDimensionPixelSize(R.dimen.call_type_circle_size);
        }

        ViewGroup.LayoutParams layoutParams = mCallTypeImage.getLayoutParams();
        layoutParams.height = size;
        layoutParams.width = size;
        mCallTypeImage.setLayoutParams(layoutParams);*/

        adjustProfile();
    }
}
