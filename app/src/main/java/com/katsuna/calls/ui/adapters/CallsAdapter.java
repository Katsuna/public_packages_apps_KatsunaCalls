package com.katsuna.calls.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filterable;

import com.katsuna.calls.R;
import com.katsuna.calls.domain.Call;
import com.katsuna.calls.ui.listeners.ICallInteractionListener;
import com.katsuna.calls.ui.listeners.IContactResolver;
import com.katsuna.calls.ui.viewholders.CallSelectedViewHolder;
import com.katsuna.calls.ui.viewholders.CallViewHolder;
import com.katsuna.calls.utils.Constants;

import java.util.List;

public class CallsAdapter extends CallsAdapterBase implements Filterable {

    private static final int CALL_NOT_SELECTED = 1;
    private static final int CALL_SELECTED = 2;
    private static final int CALL_GREYED = 3;
    private final ICallInteractionListener mListener;

    public CallsAdapter(List<Call> models, ICallInteractionListener listener,
                        IContactResolver contactResolver) {
        super(contactResolver);
        mOriginalCalls = models;
        mFilteredCalls = models;
        mListener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        int viewType = CALL_NOT_SELECTED;
        if (position == mSelectedCallPosition) {
            viewType = CALL_SELECTED;
        } else if (mSelectedCallPosition != Constants.NOT_SELECTED_CALL_VALUE) {
            viewType = CALL_GREYED;
        }

        return viewType;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        switch (viewType) {
            case CALL_NOT_SELECTED:
            case CALL_GREYED:
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.call, parent, false);
                viewHolder = new CallViewHolder(view, mListener);
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
        Call model = mFilteredCalls.get(position);
        if (model.getContact() == null) {
            model.setContact(mListener.getCallContact(model));
        }

        switch (viewHolder.getItemViewType()) {
            case CALL_NOT_SELECTED:
                CallViewHolder callViewHolder = (CallViewHolder) viewHolder;
                callViewHolder.bind(model, position);
                break;
            case CALL_GREYED:
                CallViewHolder callGreyedViewHolder = (CallViewHolder) viewHolder;
                callGreyedViewHolder.bindGreyed(model, position);
                break;
            case CALL_SELECTED:
                CallSelectedViewHolder callSelectedViewHolder = (CallSelectedViewHolder) viewHolder;
                callSelectedViewHolder.bind(model);
                break;
        }
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


}
