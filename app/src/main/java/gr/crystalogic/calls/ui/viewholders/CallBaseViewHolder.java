package gr.crystalogic.calls.ui.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import gr.crystalogic.calls.R;
import gr.crystalogic.calls.domain.Call;
import gr.crystalogic.calls.utils.DateFormatter;

abstract class CallBaseViewHolder extends RecyclerView.ViewHolder {

    final ImageView mPhoto;
    final TextView mDisplayName;
    final TextView mNumber;
    private final TextView mDateTime;

    CallBaseViewHolder(View itemView) {
        super(itemView);
        mPhoto = (ImageView) itemView.findViewById(R.id.photo);
        mDisplayName = (TextView) itemView.findViewById(R.id.displayName);
        mNumber = (TextView) itemView.findViewById(R.id.number);
        mDateTime = (TextView) itemView.findViewById(R.id.dateTime);
    }

    void bind(Call call) {

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

        adjustDisplayForNameAndNumber(call);
    }

    abstract void adjustDisplayForNameAndNumber(Call call);
}
