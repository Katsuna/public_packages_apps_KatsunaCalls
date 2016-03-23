package gr.crystalogic.calls.ui.viewholders;

import android.net.Uri;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import gr.crystalogic.calls.R;
import gr.crystalogic.calls.domain.Call;

public class CallViewHolder extends RecyclerView.ViewHolder {

    private final ImageView mPhoto;
    private final TextView mDisplayName;
    private final TextView mNumber;
    private final TextView mDateTime;
    private final TextView mCallType;

    public CallViewHolder(View itemView) {
        super(itemView);
        mPhoto = (ImageView) itemView.findViewById(R.id.photo);
        mDisplayName = (TextView) itemView.findViewById(R.id.displayName);
        mNumber = (TextView) itemView.findViewById(R.id.number);
        mCallType = (TextView) itemView.findViewById(R.id.callType);
        mDateTime = (TextView) itemView.findViewById(R.id.dateTime);
    }

    public void bind(Call call) {

        Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, String.valueOf(call.getContactId()));

        Picasso.with(itemView.getContext())
                .load(uri)
                .fit()
                .into(mPhoto);

        if (TextUtils.isEmpty(call.getName())) {
            mDisplayName.setText(R.string.unknown);
        } else {
            mDisplayName.setText(call.getName());
        }

        mNumber.setText(call.getNumber());

        if (call.getType() == CallLog.Calls.INCOMING_TYPE) {
            mCallType.setText("INCOMING");
        } else if (call.getType() == CallLog.Calls.OUTGOING_TYPE) {
            mCallType.setText("OUTGOING");
        }
        if (call.getType() == CallLog.Calls.MISSED_TYPE) {
            mCallType.setText("MISSED");
        }

        mDateTime.setText(call.getDateFormatted());
    }
}
