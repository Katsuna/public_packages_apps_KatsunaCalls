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
import gr.crystalogic.calls.utils.Constants;

public class CallsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int CALL_NOT_SELECTED = 1;
    private static final int CALL_SELECTED = 2;

    private final List<Call> mModels;

    private int mSelectedCallPosition = Constants.NOT_SELECTED_CALL_VALUE;
    private final View.OnClickListener mOnClickListener;
    private final ICallInteractionListener mListener;

    @Override
    public int getItemViewType(int position) {
        int viewType = CALL_NOT_SELECTED;
        if (position == mSelectedCallPosition) {
            viewType = CALL_SELECTED;
        }
        return viewType;
    }

    public CallsAdapter(List<Call> models, View.OnClickListener onClickListener, ICallInteractionListener listener, int selectedCallPosition) {
        mModels = models;
        mOnClickListener = onClickListener;
        mListener = listener;
        mSelectedCallPosition = selectedCallPosition;
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
        mSelectedCallPosition = position;
        notifyItemChanged(position);
    }

    public void animateTo(List<Call> models) {
        applyAndAnimateRemovals(models);
        applyAndAnimateAdditions(models);
        applyAndAnimateMovedItems(models);
    }

    private void applyAndAnimateRemovals(List<Call> newModels) {
        for (int i = mModels.size() - 1; i >= 0; i--) {
            final Call model = mModels.get(i);
            if (!newModels.contains(model)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<Call> newModels) {
        for (int i = 0, count = newModels.size(); i < count; i++) {
            final Call model = newModels.get(i);
            if (!mModels.contains(model)) {
                addItem(i, model);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<Call> newModels) {
        for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
            final Call model = newModels.get(toPosition);
            final int fromPosition = mModels.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

    private void removeItem(int position) {
        mModels.remove(position);
        notifyItemRemoved(position);
    }

    private void addItem(int position, Call model) {
        mModels.add(position, model);
        notifyItemInserted(position);
    }

    private void moveItem(int fromPosition, int toPosition) {
        final Call model = mModels.remove(fromPosition);
        mModels.add(toPosition, model);
        notifyItemMoved(fromPosition, toPosition);
    }

}
