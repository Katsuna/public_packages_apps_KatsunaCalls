package com.katsuna.calls.ui.viewholders;

import android.graphics.drawable.Drawable;
import android.provider.CallLog;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.katsuna.calls.R;
import com.katsuna.calls.domain.Call;
import com.katsuna.calls.ui.listeners.ICallInteractionListener;
import com.katsuna.calls.utils.DrawableGenerator;

public class CallForSelectionViewHolder extends CallBaseViewHolder {

    private final ImageView mCallTypeImage;
    private final CheckBox mCheckBox;

    public CallForSelectionViewHolder(View itemView, ICallInteractionListener listener) {
        super(itemView, listener);

        mCallTypeImage = itemView.findViewById(R.id.call_type_image);
        mCheckBox = itemView.findViewById(R.id.checkbox);
        adjustProfile();
    }

    public void bind(final Call call) {
        super.bind(call);

        if (call.getType() == CallLog.Calls.INCOMING_TYPE) {
            mCallTypeImage.setImageResource(R.drawable.ic_call_received_black_24dp);
        } else if (call.getType() == CallLog.Calls.OUTGOING_TYPE) {
            mCallTypeImage.setImageResource(R.drawable.ic_call_made_black_24dp);
        } else if (call.getType() == CallLog.Calls.MISSED_TYPE) {
            Drawable drawable = DrawableGenerator.getMissedCallDrawable(itemView.getContext(),
                    mUserProfileContainer.getActiveUserProfile());
            mCallTypeImage.setImageDrawable(drawable);
        } else {
            mCallTypeImage.setImageBitmap(null);
        }

        mCheckBox.setChecked(call.isSelected());
        mCheckBox.setChecked(call.isSelected());
        mCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean newValue = !call.isSelected();
                call.setSelected(newValue);
                mCheckBox.setChecked(newValue);
            }
        });
    }
}
