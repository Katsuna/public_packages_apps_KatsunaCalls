package gr.crystalogic.calls.ui.viewholders;

import android.graphics.Typeface;
import android.provider.CallLog;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import gr.crystalogic.calls.R;
import gr.crystalogic.calls.domain.Call;
import gr.crystalogic.commons.entities.Profile;
import gr.crystalogic.commons.entities.ProfileType;

public class CallViewHolder extends CallBaseViewHolder {

    private final ImageView mCallTypeImage;
    private final Profile mProfile;

    public CallViewHolder(View itemView, Profile profile) {
        super(itemView);
        mCallTypeImage = (ImageView) itemView.findViewById(R.id.callTypeImage);
        mProfile = profile;
        adjustProfile();
    }

    public void bind(Call call) {
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
    }

    @Override
    void adjustDisplayForNameAndNumber(Call call) {
        if (call.getType() == CallLog.Calls.MISSED_TYPE) {
            mDisplayName.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.red));
            mDisplayName.setTypeface(null, Typeface.BOLD);
            mNumber.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.red));
            mNumber.setTypeface(null, Typeface.BOLD);
        } else {
            mDisplayName.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.black));
            mDisplayName.setTypeface(null, Typeface.NORMAL);
            mNumber.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.black));
            mNumber.setTypeface(null, Typeface.NORMAL);
        }
    }

    private void adjustProfile() {
        if (mProfile != null) {
            int size = itemView.getResources().getDimensionPixelSize(R.dimen.contact_photo_size_intemediate);
            if (mProfile.getType() == ProfileType.ADVANCED.getNumVal()) {
                size = itemView.getResources().getDimensionPixelSize(R.dimen.contact_photo_size_advanced);
            } else if (mProfile.getType() == ProfileType.SIMPLE.getNumVal()) {
                size = itemView.getResources().getDimensionPixelSize(R.dimen.contact_photo_size_simple);
            }
            ViewGroup.LayoutParams layoutParams = mPhoto.getLayoutParams();
            layoutParams.height = size;
            layoutParams.width = size;
            mPhoto.setLayoutParams(layoutParams);
        }
    }
}
