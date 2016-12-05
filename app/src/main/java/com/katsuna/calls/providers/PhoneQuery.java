package com.katsuna.calls.providers;

import android.provider.ContactsContract.PhoneLookup;

import com.katsuna.calls.domain.Contact;

/**
 * The query to look up the {@link Contact} for a given number in the Call Log.
 */
final class PhoneQuery {
    public static final String[] _PROJECTION = new String[]{
            PhoneLookup._ID,
            PhoneLookup.DISPLAY_NAME,
            PhoneLookup.PHOTO_THUMBNAIL_URI
    };

    public static final int _ID = 0;
    public static final int DISPLAY_NAME = 1;
    public static final int PHOTO_THUMBNAIL_URI = 2;
}