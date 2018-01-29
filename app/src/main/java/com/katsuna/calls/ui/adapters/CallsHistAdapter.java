package com.katsuna.calls.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.katsuna.calls.R;
import com.katsuna.calls.domain.Call;
import com.katsuna.calls.ui.viewholders.CallsHistViewHolder;

import java.util.List;

public class CallsHistAdapter extends RecyclerView.Adapter<CallsHistViewHolder> {

    private final List<Call> mCalls;

    public CallsHistAdapter(List<Call> calls) {
        mCalls = calls;
    }

    @Override
    public int getItemCount() {
        return mCalls.size();
    }

    @Override
    public CallsHistViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.call_hist, parent, false);
        return new CallsHistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CallsHistViewHolder holder, int position) {
        Call call = mCalls.get(position);
        holder.bind(call);
    }
}
