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
import com.katsuna.calls.ui.listeners.IContactProvider;
import com.katsuna.calls.ui.viewholders.CallSelectedViewHolder;
import com.katsuna.calls.ui.viewholders.CallViewHolder;
import com.katsuna.calls.utils.Constants;
import com.katsuna.commons.entities.Profile;

import java.util.ArrayList;
import java.util.List;

public class CallsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements Filterable {

    private static final int CALL_NOT_SELECTED = 1;
    private static final int CALL_SELECTED = 2;
    private final View.OnClickListener mOnClickListener;
    private final ICallInteractionListener mListener;
    private final Profile mProfile;
    private List<Call> mOriginalCalls = null;
    private List<Call> mFilteredCalls = null;
    private int mSelectedCallPosition = Constants.NOT_SELECTED_CALL_VALUE;
    private final IContactProvider mContactProvider;
    private final CallFilter mFilter = new CallFilter();

    public CallsAdapter(List<Call> models, View.OnClickListener onClickListener,
                        ICallInteractionListener listener, int selectedCallPosition,
                        Profile profile, IContactProvider contactProvider) {
        mOriginalCalls = models;
        mFilteredCalls = models;
        mOnClickListener = onClickListener;
        mListener = listener;
        mSelectedCallPosition = selectedCallPosition;
        mProfile = profile;
        mContactProvider = contactProvider;
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
                viewHolder = new CallViewHolder(view, mProfile);
                view.setOnClickListener(mOnClickListener);
                break;
            case CALL_SELECTED:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.call_selected, parent, false);
                viewHolder = new CallSelectedViewHolder(view, mListener, mProfile);
                break;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        Call model = mFilteredCalls.get(position);
        if (model.getContact() == null) {
            model.setContact(mContactProvider.getCallContact(model));
        }

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
        return mFilteredCalls.size();
    }

    public void selectCallAtPosition(int position) {
        mSelectedCallPosition = position;
        notifyItemChanged(position);
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
                Contact contact = mContactProvider.getCallContact(call);
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
