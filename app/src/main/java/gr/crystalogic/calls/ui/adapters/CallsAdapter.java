package gr.crystalogic.calls.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import gr.crystalogic.calls.R;
import gr.crystalogic.calls.domain.Call;
import gr.crystalogic.calls.ui.viewholders.CallViewHolder;

public class CallsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<Call> mModels;

    private final View.OnClickListener mOnClickListener;
    private final View.OnLongClickListener mOnLongClickListener;

    public CallsAdapter(List<Call> models, View.OnClickListener onClickListener, View.OnLongClickListener onLongClickListener) {
        mModels = models;
        mOnClickListener = onClickListener;
        mOnLongClickListener = onLongClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.call, parent, false);
        view.setOnClickListener(mOnClickListener);
        view.setOnLongClickListener(mOnLongClickListener);
        return new CallViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Call model = mModels.get(position);
        ((CallViewHolder) holder).bind(model);
    }

    @Override
    public int getItemCount() {
        return mModels.size();
    }
}
