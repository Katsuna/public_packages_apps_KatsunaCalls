/**
* Copyright (C) 2020 Manos Saratsis
*
* This file is part of Katsuna.
*
* Katsuna is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* Katsuna is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with Katsuna.  If not, see <https://www.gnu.org/licenses/>.
*/
package com.katsuna.calls.ui.adapters;

import android.support.annotation.NonNull;
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

        if (mDeleteMode) {
            viewType = CALL_NOT_SELECTED;
        } else {
            if (position == mSelectedCallPosition) {
                viewType = CALL_SELECTED;
            } else if (mSelectedCallPosition != Constants.NOT_SELECTED_CALL_VALUE) {
                viewType = CALL_GREYED;
            }
        }

        return viewType;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        boolean isRightHanded = mListener.getUserProfileContainer().isRightHanded();
        View view;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case CALL_NOT_SELECTED:
            case CALL_GREYED:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.call, parent, false);
                viewHolder = new CallViewHolder(view, mListener);
                break;
            case CALL_SELECTED:
                view = inflater.inflate(R.layout.call, parent, false);
                ViewGroup buttonsWrapper = view.findViewById(R.id.action_buttons_wrapper);
                if (isRightHanded) {
                    View buttonsView = inflater.inflate(R.layout.action_buttons_rh, buttonsWrapper,
                            false);
                    buttonsWrapper.addView(buttonsView);
                } else {
                    View buttonsView = inflater.inflate(R.layout.action_buttons_lh, buttonsWrapper,
                            false);
                    buttonsWrapper.addView(buttonsView);
                }
                viewHolder = new CallSelectedViewHolder(view, mListener);
                break;
            default:
                throw new RuntimeException("viewType not defined");
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        Call model = mFilteredCalls.get(position);
        if (model.getContact() == null) {
            model.setContact(mListener.getCallContact(model));
        }

        switch (viewHolder.getItemViewType()) {
            case CALL_NOT_SELECTED:
                CallViewHolder callViewHolder = (CallViewHolder) viewHolder;
                callViewHolder.bind(model, position, mDeleteMode);
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

    public void setDeleteMode(boolean flag) {
        mDeleteMode = flag;
    }

    public void enableDeleteMode(boolean flag) {
        mDeleteMode = flag;
        notifyDataSetChanged();
    }
}
