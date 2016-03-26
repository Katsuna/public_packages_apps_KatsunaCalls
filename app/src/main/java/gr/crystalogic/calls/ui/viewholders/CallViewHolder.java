package gr.crystalogic.calls.ui.viewholders;

import android.provider.CallLog;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import gr.crystalogic.calls.R;
import gr.crystalogic.calls.domain.Call;

public class CallViewHolder extends CallBaseViewHolder {

    private final ImageView mCallTypeImage;

    public CallViewHolder(View itemView) {
        super(itemView);
        mCallTypeImage = (ImageView) itemView.findViewById(R.id.callTypeImage);
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
}
