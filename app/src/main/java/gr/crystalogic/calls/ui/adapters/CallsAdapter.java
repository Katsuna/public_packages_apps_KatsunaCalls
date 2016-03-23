package gr.crystalogic.calls.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import gr.crystalogic.calls.R;
import gr.crystalogic.calls.domain.Call;
import gr.crystalogic.calls.ui.listeners.ICallInteractionListener;
import gr.crystalogic.calls.ui.viewholders.CallSelectedViewHolder;
import gr.crystalogic.calls.ui.viewholders.CallViewHolder;

public class CallsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int CALL_NOT_SELECTED = 1;
    private static final int CALL_SELECTED = 2;

    private final List<Call> mModels;

    private int mSelectedContactPosition = -1;
    private final View.OnClickListener mOnClickListener;
    private ICallInteractionListener mListener;

    @Override
    public int getItemViewType(int position) {
        int viewType = CALL_NOT_SELECTED;
        if (position == mSelectedContactPosition) {
            viewType = CALL_SELECTED;
        }
        return viewType;
    }

    public CallsAdapter(List<Call> models, View.OnClickListener onClickListener, ICallInteractionListener listener) {
        mModels = models;
        mOnClickListener = onClickListener;
        mListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        switch (viewType) {
            case CALL_NOT_SELECTED:
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.call, parent, false);
                viewHolder = new CallViewHolder(view);
                view.setOnClickListener(mOnClickListener);
                break;
            case CALL_SELECTED:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.call_selected, parent, false);
                viewHolder = new CallSelectedViewHolder(view, mListener);
                break;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        Call model = mModels.get(position);

        switch (viewHolder.getItemViewType()) {
            case CALL_NOT_SELECTED:
                CallViewHolder callViewHolder = (CallViewHolder) viewHolder;
                callViewHolder.bind(model);
                break;

            case CALL_SELECTED:
                CallSelectedViewHolder callSelectedViewHolder = (CallSelectedViewHolder) viewHolder;
                callSelectedViewHolder.bind(model);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mModels.size();
    }

    public void selectCallAtPosition(int position) {
        mSelectedContactPosition = position;
        notifyItemChanged(position);
    }
}
