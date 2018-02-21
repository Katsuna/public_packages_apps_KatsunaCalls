package com.katsuna.calls.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.widget.Filter;
import android.widget.Filterable;

import com.katsuna.calls.domain.Call;
import com.katsuna.calls.domain.Contact;
import com.katsuna.calls.ui.listeners.IContactResolver;
import com.katsuna.calls.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public abstract class CallsAdapterBase extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements Filterable {

    boolean mDeleteMode = false;
    final IContactResolver mContactResolver;
    private final CallFilter mFilter = new CallFilter();
    List<Call> mOriginalCalls;
    List<Call> mFilteredCalls;
    int mSelectedCallPosition = Constants.NOT_SELECTED_CALL_VALUE;

    CallsAdapterBase(IContactResolver contactResolver) {
        mContactResolver = contactResolver;
    }

    public void selectCallAtPosition(int position) {
        mSelectedCallPosition = position;
        notifyItemChanged(position);
    }

    public void deselectCall() {
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
    public int getItemCount() {
        return mFilteredCalls.size();
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    public void resetFilter() {
        mFilteredCalls = mOriginalCalls;
        notifyDataSetChanged();
    }

    public class CallFilter extends Filter {
        public void show(int callType, boolean deleteMode) {
            mDeleteMode = deleteMode;
            mFilteredCalls = new ArrayList<>();

            for (Call call : mOriginalCalls) {
                if (call.getType() == callType) {
                    mFilteredCalls.add(call);
                }
            }
            notifyDataSetChanged();
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String filterString = constraint.toString().toLowerCase();

            List<Call> filteredCalls = new ArrayList<>();

            for (Call call : mOriginalCalls) {
                Contact contact = mContactResolver.getCallContact(call);
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
