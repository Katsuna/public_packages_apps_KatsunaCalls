package com.katsuna.calls.ui.viewholders;

import android.graphics.drawable.Drawable;
import android.provider.CallLog;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.katsuna.calls.R;
import com.katsuna.calls.domain.Call;
import com.katsuna.calls.ui.listeners.ICallInteractionListener;
import com.katsuna.calls.utils.DrawableGenerator;
import com.katsuna.commons.entities.SizeProfile;

public class CallViewHolder extends CallBaseViewHolder {

    private final ImageView mCallTypeImage;
    private final View mCallContainer;
    private final View mOpacityLayer;

    public CallViewHolder(View itemView, ICallInteractionListener listener) {
        super(itemView, listener);
        mCallTypeImage = (ImageView) itemView.findViewById(R.id.callTypeImage);
        mCallContainer = itemView.findViewById(R.id.call_container);
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

        // style based on call type
        Drawable callTypeDrawable = null;
        if (call.getType() == CallLog.Calls.INCOMING_TYPE) {
            callTypeDrawable = ContextCompat.getDrawable(itemView.getContext(),
                    R.drawable.ic_call_received_black_24dp);
        } else if (call.getType() == CallLog.Calls.OUTGOING_TYPE) {
            callTypeDrawable = ContextCompat.getDrawable(itemView.getContext(),
                    R.drawable.ic_call_made_black_24dp);
        } else if (call.getType() == CallLog.Calls.MISSED_TYPE) {
            callTypeDrawable = DrawableGenerator.getMissedCallDrawable(itemView.getContext(),
                    mUserProfileContainer.getActiveUserProfile());
        }
        mCallTypeImage.setImageDrawable(callTypeDrawable);

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
        mPhoto.setOnClickListener(focusContact);
        mDisplayName.setOnClickListener(focusContact);

        if (mOpacityLayer != null) {
            mOpacityLayer.setVisibility(View.INVISIBLE);
        }

        //
        SizeProfile sizeProfile = mUserProfileContainer.getOpticalSizeProfile();
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
        mCallTypeImage.setLayoutParams(layoutParams);

        adjustProfile();
        adjustDisplayForNameAndNumber(call);
    }
}
