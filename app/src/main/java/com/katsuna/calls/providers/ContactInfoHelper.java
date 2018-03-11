package com.katsuna.calls.providers;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import com.katsuna.calls.domain.Contact;
import com.katsuna.commons.domain.Description;
import com.katsuna.commons.providers.ContactProvider;

import java.util.List;

/**
 * Utility class to look up the contact information for a given number.
 */

public class ContactInfoHelper {
    private final Context mContext;

    public ContactInfoHelper(Context context) {
        mContext = context;
    }

    public Contact getContactFromNumber(String number) {
        Contact contact = null;

        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(number));

        Cursor cursor = mContext.getContentResolver().query(
                uri, PhoneQuery._PROJECTION, null, null, null);

        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    contact = new Contact();
                    contact.setId(cursor.getLong(PhoneQuery._ID));
                    contact.setName(cursor.getString(PhoneQuery.DISPLAY_NAME));
                    contact.setPhotoUri(cursor.getString(PhoneQuery.PHOTO_THUMBNAIL_URI));

                    ContactProvider contactProvider = new ContactProvider(mContext);
                    List<Description> descriptions = contactProvider.getDescriptions(contact.getId());
                    if (descriptions.size() > 0) {
                        contact.setDescription(descriptions.get(0).getDescription());
                    }
                }
            } finally {
                cursor.close();
            }
        }
        return contact;
    }
}