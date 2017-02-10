package com.katsuna.calls.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.katsuna.calls.R;
import com.katsuna.calls.domain.Call;
import com.katsuna.calls.ui.listeners.IContactResolver;
import com.katsuna.calls.ui.viewholders.CallForSelectionViewHolder;

import java.util.List;

public class CallsSelectionAdapter extends CallsAdapterBase {

    public CallsSelectionAdapter(List<Call> calls, IContactResolver listener) {
        super(listener);
        mOriginalCalls = calls;
        mFilteredCalls = calls;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.call_for_selection,
                parent, false);
        return new CallForSelectionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Call model = mFilteredCalls.get(position);
        if (model.getContact() == null) {
            model.setContact(mContactResolver.getCallContact(model));
        }

        ((CallForSelectionViewHolder) holder).bind(mFilteredCalls.get(position));
    }

    public void removeItem(Call call) {
        int filteredPosition = -1;
        for (int i = 0; i < mFilteredCalls.size(); i++) {
            if (mFilteredCalls.get(i).getId() == call.getId()) {
                filteredPosition = i;
                break;
            }
        }
        //position found
        if (filteredPosition > -1) {
            mFilteredCalls.remove(filteredPosition);
            notifyItemRemoved(filteredPosition);
        }

        //remove also from original list
        int originalListPosition = -1;
        for (int i = 0; i < mOriginalCalls.size(); i++) {
            if (mOriginalCalls.get(i).getId() == call.getId()) {
                originalListPosition = i;
                break;
            }
        }
        if (originalListPosition > -1) {
            mOriginalCalls.remove(originalListPosition);
        }
    }

    public List<Call> getModels() {
        return mFilteredCalls;
    }
}
