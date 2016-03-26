package gr.crystalogic.calls.ui.viewholders;

import android.provider.CallLog;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import gr.crystalogic.calls.R;
import gr.crystalogic.calls.domain.Call;
import gr.crystalogic.calls.utils.DateFormatter;

public class CallBaseViewHolder extends RecyclerView.ViewHolder {

    private final ImageView mPhoto;
    private final TextView mDisplayName;
    private final TextView mNumber;
    private final TextView mDateTime;

    public CallBaseViewHolder(View itemView) {
        super(itemView);
        mPhoto = (ImageView) itemView.findViewById(R.id.photo);
        mDisplayName = (TextView) itemView.findViewById(R.id.displayName);
        mNumber = (TextView) itemView.findViewById(R.id.number);
        mDateTime = (TextView) itemView.findViewById(R.id.dateTime);
    }

    public void bind(Call call) {

        if (call.getContact() == null) {
            mPhoto.setImageBitmap(null);
            mDisplayName.setText(R.string.unknown);
        } else {
            Picasso.with(itemView.getContext())
                    .load(call.getContact().getPhotoUri())
                    .fit()
                    .into(mPhoto);

            mDisplayName.setText(call.getContact().getName());
        }

        mNumber.setText(call.getNumber());
        mDateTime.setText(DateFormatter.format(itemView.getContext(), call.getDate()));

        if (call.getType() == CallLog.Calls.MISSED_TYPE) {
            mDisplayName.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.red));
            mNumber.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.red));
        } else {
            mDisplayName.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.gray));
            mNumber.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.gray));
        }

    }
}
