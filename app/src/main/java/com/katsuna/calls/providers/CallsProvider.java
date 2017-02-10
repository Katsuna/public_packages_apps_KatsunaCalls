package com.katsuna.calls.providers;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.CallLog;
import android.util.Log;

import com.katsuna.calls.domain.Call;

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

        List<Call> calls = new ArrayList<>();
        String orderBy = CallLog.Calls.DATE + " DESC";

        Cursor cursor = cr.query(CallLog.Calls.CONTENT_URI, CallLogQuery._PROJECTION, null, null, orderBy);
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
}
