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

import com.katsuna.calls.R;
import com.katsuna.calls.domain.Call;
import com.katsuna.calls.ui.viewholders.CallsHistViewHolder;
import com.katsuna.commons.entities.UserProfileContainer;

import java.util.List;

public class CallsHistAdapter extends RecyclerView.Adapter<CallsHistViewHolder> {

    private final List<Call> mCalls;
    private final UserProfileContainer mUserProfileContainer;

    public CallsHistAdapter(List<Call> calls, UserProfileContainer userProfileContainer) {
        mCalls = calls;
        mUserProfileContainer = userProfileContainer;
    }

    @Override
    public int getItemCount() {
        return mCalls.size();
    }

    @NonNull
    @Override
    public CallsHistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.call_hist, parent, false);
        return new CallsHistViewHolder(view, mUserProfileContainer);
    }

    @Override
    public void onBindViewHolder(@NonNull CallsHistViewHolder holder, int position) {
        Call call = mCalls.get(position);
        holder.bind(call);
    }
}
