package com.katsuna.calls.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import com.katsuna.calls.R;
import com.katsuna.calls.domain.Call;
import com.katsuna.calls.domain.Contact;
import com.katsuna.calls.ui.listeners.ICallInteractionListener;
import com.katsuna.calls.ui.viewholders.CallSelectedViewHolder;
import com.katsuna.calls.ui.viewholders.CallViewHolder;
import com.katsuna.calls.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class CallsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements Filterable {

    private static final int CALL_NOT_SELECTED = 1;
    private static final int CALL_SELECTED = 2;
    private final ICallInteractionListener mListener;
    private List<Call> mOriginalCalls = null;
    private List<Call> mFilteredCalls = null;
    private int mSelectedCallPosition = Constants.NOT_SELECTED_CALL_VALUE;
    private final CallFilter mFilter = new CallFilter();

    public CallsAdapter(List<Call> models, ICallInteractionListener listener) {
        mOriginalCalls = models;
        mFilteredCalls = models;
        mListener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        int viewType = CALL_NOT_SELECTED;
        if (position == mSelectedCallPosition) {
            viewType = CALL_SELECTED;
        }
        return viewType;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        switch (viewType) {
            case CALL_NOT_SELECTED:
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

            case CALL_SELECTED:
                CallSelectedViewHolder callSelectedViewHolder = (CallSelectedViewHolder) viewHolder;
                callSelectedViewHolder.bind(model);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mFilteredCalls.size();
    }

    public void selectCallAtPosition(int position) {
        mSelectedCallPosition = position;
        notifyItemChanged(position);
    }

    public void deselectContact() {
        selectCallAtPosition(Constants.NOT_SELECTED_CALL_VALUE);
    }

    public int getPositionByNumber(String number) {
        int position = Constants.NOT_SELECTED_CALL_VALUE;
        for (int i = 0; i < mFilteredCalls.size(); i++) {
            //don't focus on premium contacts
            Call call = mFilteredCalls.get(i);
            if (call.getNumber().equals(number)) {
                position = i;
                break;
            }
        }
        return position;
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    public void resetFilter() {
        mFilteredCalls = mOriginalCalls;
        notifyDataSetChanged();
    }

    private class CallFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String filterString = constraint.toString().toLowerCase();

            List<Call> filteredCalls = new ArrayList<>();

            for (Call call : mOriginalCalls) {
                Contact contact = mListener.getCallContact(call);
                String text;
                if (contact == null) {
                    text = call.getNumber();
                } else {
                    text = contact.getName().toLowerCase();
                }

                if (text.contains(filterString) || call.getNumber().contains(filterString)) {
                    filteredCalls.add(call);
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredCalls;
            results.count = filteredCalls.size();

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mFilteredCalls = (ArrayList<Call>) results.values;
            notifyDataSetChanged();
        }
    }
}
