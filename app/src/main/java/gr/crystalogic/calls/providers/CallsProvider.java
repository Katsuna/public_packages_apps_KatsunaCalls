package gr.crystalogic.calls.providers;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import gr.crystalogic.calls.domain.Call;
import gr.crystalogic.calls.domain.Contact;
import gr.crystalogic.calls.providers.metadata.CallColumns;

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

        LinkedHashMap<String, Contact> map = new LinkedHashMap<>();

        Cursor cursor = cr.query(CallLog.Calls.CONTENT_URI, null, null, null, orderBy);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Call call = new Call();
                call.setId(cursor.getLong(cursor.getColumnIndex(CallColumns.ID)));
                call.setType(cursor.getInt(cursor.getColumnIndex(CallColumns.TYPE)));
                call.setName(cursor.getString(cursor.getColumnIndex(CallColumns.NAME)));
                call.setNumber(cursor.getString(cursor.getColumnIndex(CallColumns.NUMBER)));
                call.setDate(cursor.getLong(cursor.getColumnIndex(CallColumns.DATE)));
                call.setIsRead(cursor.getInt(cursor.getColumnIndex(CallColumns.IS_READ)));
                call.setIsNew(cursor.getInt(cursor.getColumnIndex(CallColumns.NEW)));
                call.setDuration(cursor.getLong(cursor.getColumnIndex(CallColumns.DURATION)));

                //use map to store already found contacts for better perfomance
                Contact contact = map.get(call.getNumber());
                if (contact == null) {
                    contact = getContactByNumber(call.getNumber());
                    map.put(call.getNumber(), contact);
                }
                call.setContact(contact);
                calls.add(call);

                //showCursor(cursor);
            } while (cursor.moveToNext());

            cursor.close();
        }

        return calls;
    }

    private Contact getContactByNumber(String number) {
        Contact contact = null;

        String[] projection = {
                ContactsContract.PhoneLookup._ID,
                ContactsContract.PhoneLookup.DISPLAY_NAME,
                ContactsContract.PhoneLookup.NUMBER,
                ContactsContract.PhoneLookup.PHOTO_THUMBNAIL_URI
        };

        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));

        Cursor cursor = cr.query(uri, projection, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            contact = new Contact();
            contact.setId(cursor.getLong(cursor.getColumnIndex(ContactsContract.PhoneLookup._ID)));
            contact.setName(cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME)));
            contact.setPhotoUri(cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.PHOTO_THUMBNAIL_URI)));

            cursor.close();
        }
        return contact;
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
