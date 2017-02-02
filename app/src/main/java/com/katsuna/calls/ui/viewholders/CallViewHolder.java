package com.katsuna.calls.ui.viewholders;

import android.graphics.Typeface;
import android.provider.CallLog;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.katsuna.calls.R;
import com.katsuna.calls.domain.Call;
import com.katsuna.calls.ui.listeners.ICallInteractionListener;
import com.katsuna.commons.entities.ProfileType;
import com.katsuna.commons.entities.UserProfileContainer;

public class CallViewHolder extends CallBaseViewHolder {

    private final ImageView mCallTypeImage;
    private final ICallInteractionListener mListener;
    private final UserProfileContainer mUserProfileContainer;
    private final View mCallContainer;
    private final View mOpacityLayer;

    public CallViewHolder(View itemView, ICallInteractionListener listener) {
        super(itemView);
        mCallTypeImage = (ImageView) itemView.findViewById(R.id.callTypeImage);
        mCallContainer = itemView.findViewById(R.id.call_container);
        mOpacityLayer = itemView.findViewById(R.id.opacity_layer);
        mListener = listener;
        mUserProfileContainer = mListener.getUserProfileContainer();
        adjustProfile();
    }

    public void bindGreyed(Call call, final int position) {
        bind(call, position);
        if (mOpacityLayer != null) {
            mOpacityLayer.setVisibility(View.VISIBLE);
        }
    }

    public void bind(Call call, final int position) {
        super.bind(call);

        if (call.getType() == CallLog.Calls.INCOMING_TYPE) {
            mCallTypeImage.setImageResource(R.drawable.ic_call_received_black_24dp);
        } else if (call.getType() == CallLog.Calls.OUTGOING_TYPE) {
            mCallTypeImage.setImageResource(R.drawable.ic_call_made_black_24dp);
        } else if (call.getType() == CallLog.Calls.MISSED_TYPE) {
            mCallTypeImage.setImageResource(R.drawable.ic_call_missed_red_500_24dp);
        } else {
            mCallTypeImage.setImageBitmap(null);
        }

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
    }

    @Override
    void adjustDisplayForNameAndNumber(Call call) {
        if (call.getType() == CallLog.Calls.MISSED_TYPE) {
            mDisplayName.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.red));
            mNumber.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.red));
            mNumber.setTypeface(null, Typeface.BOLD);
        } else {
            mDisplayName.setTextColor(ContextCompat.getColor(itemView.getContext(),
                    R.color.common_black));
            mNumber.setTextColor(ContextCompat.getColor(itemView.getContext(),
                    R.color.common_black));
            mNumber.setTypeface(null, Typeface.NORMAL);
        }
    }

    private void adjustProfile() {
        ProfileType sizeProfile = mUserProfileContainer.getOpticalSizeProfile();
        int size = itemView.getResources().getDimensionPixelSize(R.dimen.contact_photo_size_intemediate);
        if (sizeProfile == ProfileType.ADVANCED) {
            size = itemView.getResources().getDimensionPixelSize(R.dimen.contact_photo_size_advanced);
        } else if (sizeProfile == ProfileType.SIMPLE) {
            size = itemView.getResources().getDimensionPixelSize(R.dimen.contact_photo_size_simple);
        }
        ViewGroup.LayoutParams layoutParams = mPhoto.getLayoutParams();
        layoutParams.height = size;
        layoutParams.width = size;
        mPhoto.setLayoutParams(layoutParams);
    }
}
