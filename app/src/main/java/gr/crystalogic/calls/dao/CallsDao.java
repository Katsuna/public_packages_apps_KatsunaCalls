package gr.crystalogic.calls.dao;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.CallLog;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import gr.crystalogic.calls.dao.metadata.CallColumns;
import gr.crystalogic.calls.domain.Call;

@SuppressWarnings("ResourceType")
public class CallsDao {

    private static final String TAG = "CallsDao";

    private final ContentResolver cr;

    public CallsDao(Context context) {
        cr = context.getContentResolver();
    }

    public List<Call> getCalls() {

        List<Call> calls = new ArrayList<>();

        Cursor cursor = cr.query(CallLog.Calls.CONTENT_URI, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {

                Call call = new Call();
                call.setId(cursor.getLong(cursor.getColumnIndex(CallColumns.ID)));
                call.setType(cursor.getInt(cursor.getColumnIndex(CallColumns.TYPE)));
                call.setContactId(cursor.getLong(cursor.getColumnIndex(CallColumns.CONTACT_ID)));
                call.setLookupUri(cursor.getString(cursor.getColumnIndex(CallColumns.LOOKUP_URI)));
                call.setNumber(cursor.getString(cursor.getColumnIndex(CallColumns.NUMBER)));
                call.setDate(cursor.getLong(cursor.getColumnIndex(CallColumns.DATE)));
                call.setIsRead(cursor.getInt(cursor.getColumnIndex(CallColumns.IS_READ)));
                call.setIsNew(cursor.getInt(cursor.getColumnIndex(CallColumns.NEW)));
                call.setDuration(cursor.getLong(cursor.getColumnIndex(CallColumns.DURATION)));
                calls.add(call);

                showCursor(cursor);

            } while (cursor.moveToNext());

            cursor.close();
        }

        return calls;
    }

    private void showCursor(Cursor cursor) {
        int l = cursor.getColumnCount();
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < l; i++) {
            buf.append(i).append(":");
            buf.append(cursor.getColumnName(i));
            buf.append("=");
            buf.append(cursor.getString(i));
            buf.append(" type=");
            buf.append(cursor.getType(i));
            buf.append(" | ");
        }
        Log.e(TAG, buf.toString());
    }


}
