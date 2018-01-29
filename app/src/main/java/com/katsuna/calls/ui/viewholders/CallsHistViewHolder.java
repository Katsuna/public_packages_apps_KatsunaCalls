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
import com.katsuna.calls.utils.DrawableGenerator;

public class CallsHistViewHolder extends RecyclerView.ViewHolder {
    private final ImageView mCallTypeImage;
    private final TextView mCallDetails;
    private final TextView mCallDuration;


    public CallsHistViewHolder(View v) {
        super(v);

        mCallTypeImage = itemView.findViewById(R.id.call_type_image);
        mCallDetails = v.findViewById(R.id.call_details);
        mCallDuration = v.findViewById(R.id.call_duration);
    }


    public void bind(Call call) {
        // style callTypeDrawable based on call type
        Drawable callTypeDrawable = DrawableGenerator.getCallTypeDrawable(itemView.getContext(),
                call.getType());
        mCallTypeImage.setImageDrawable(callTypeDrawable);

        mCallDetails.setText(CallInfo.getDetails(itemView.getContext(), call));

        if (call.getType() == CallLog.Calls.MISSED_TYPE) {
            mCallDuration.setVisibility(View.GONE);
        } else {
            mCallDuration.setVisibility(View.VISIBLE);
            mCallDuration.setText(CallInfo.getCallDuration(itemView.getContext(), call));
        }

        adjustProfile();
    }

    private void adjustProfile() {


    }
}
