package gr.crystalogic.calls.ui.viewholders;

import android.view.View;
import android.widget.Button;

import gr.crystalogic.calls.R;
import gr.crystalogic.calls.domain.Call;
import gr.crystalogic.calls.ui.listeners.ICallInteractionListener;

public class CallSelectedViewHolder extends CallBaseViewHolder {

    private final ICallInteractionListener mListener;
    private final Button mCallButton;
    private final Button mMessageButton;

    public CallSelectedViewHolder(View itemView, ICallInteractionListener listener) {
        super(itemView);
        mCallButton = (Button) itemView.findViewById(R.id.callButton);
        mMessageButton = (Button) itemView.findViewById(R.id.messageButton);
        mListener = listener;
    }

    public void bind(final Call call) {
        super.bind(call);

        mCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.callContact(call);
            }
        });
        mMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.sendSMS(call);
            }
        });
    }
}
