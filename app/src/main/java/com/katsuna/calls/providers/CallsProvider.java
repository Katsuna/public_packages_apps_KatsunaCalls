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
package com.katsuna.calls.providers;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.CallLog;
import android.util.Log;

import com.katsuna.calls.domain.Call;
import com.katsuna.calls.domain.CallSearchParams;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("ResourceType")
public class CallsProvider {

    private static final String TAG = "CallsProvider";

    private final ContentResolver cr;

    public CallsProvider(Context context) {
        cr = context.getContentResolver();
    }

    public List<Call> getCalls() {
        return getCalls(null);
    }

    public List<Call> getCalls(CallSearchParams searchParams) {
        List<Call> calls = new ArrayList<>();
        String where = " 1=1 ";
        String[] whereArgs = null;
        String limit = " ";
        if (searchParams != null) {
            List<String> whereArgsList = new ArrayList<>();
            if (searchParams.number != null) {
                where += " AND " + CallLog.Calls.NUMBER + " = ?";
                whereArgsList.add(searchParams.number);
            }

            if (searchParams.idToExclude != null) {
                where += " AND " + CallLog.Calls._ID + " != ? ";
                whereArgsList.add(String.valueOf(searchParams.idToExclude));
            }
            if (whereArgsList.size() > 0) {
                whereArgs = whereArgsList.toArray(new String[0]);
            }
            if (searchParams.limit != null) {
                limit += " LIMIT " + searchParams.limit;
            }
        }

        String orderBy = CallLog.Calls.DATE + " DESC";

        Cursor cursor = cr.query(CallLog.Calls.CONTENT_URI, CallLogQuery._PROJECTION, where,
                whereArgs, orderBy + limit);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Call call = new Call();
                call.setId(cursor.getLong(CallLogQuery.ID));
                call.setType(cursor.getInt(CallLogQuery.TYPE));
                call.setName(cursor.getString(CallLogQuery.CACHED_NAME));
                call.setNumber(cursor.getString(CallLogQuery.NUMBER));
                call.setNumberPresentation(cursor.getInt(CallLogQuery.NUMBER_PRESENTATION));
                call.setIsRead(cursor.getInt(CallLogQuery.IS_READ));
                call.setDate(cursor.getLong(CallLogQuery.DATE));
                call.setIsNew(cursor.getInt(CallLogQuery.NEW));
                call.setDuration(cursor.getLong(CallLogQuery.DURATION));
                calls.add(call);
            } while (cursor.moveToNext());

            cursor.close();
        }

        return calls;
    }

    public void deleteCall(Call call) {
        ArrayList<ContentProviderOperation> ops = new ArrayList<>();

        String where = CallLog.Calls._ID + " = ? ";
        String[] params = new String[]{String.valueOf(call.getId())};

        ops.add(ContentProviderOperation.newDelete(CallLog.Calls.CONTENT_URI)
                .withSelection(where, params)
                .build());

        try {
            cr.applyBatch(CallLog.AUTHORITY, ops);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    /** Updates all missed calls to mark them as read. */
    public void markMissedCallsAsRead() {
        ArrayList<ContentProviderOperation> ops = new ArrayList<>();

        ops.add(ContentProviderOperation.newUpdate(CallLog.Calls.CONTENT_URI)
                .withSelection(getUnreadMissedCallsQuery(), null)
                .withValue(CallLog.Calls.IS_READ, "1")
                .build());

        try {
            cr.applyBatch(CallLog.AUTHORITY, ops);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }


    /**
     * @return Query string to get all unread missed calls.
     */
    private String getUnreadMissedCallsQuery() {
        return CallLog.Calls.IS_READ + " = 0 OR " + CallLog.Calls.IS_READ + " IS NULL" +
                " AND " +
                CallLog.Calls.TYPE + " = " + CallLog.Calls.MISSED_TYPE;
    }

}