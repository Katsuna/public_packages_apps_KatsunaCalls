package com.katsuna.calls.providers;

import android.provider.CallLog.Calls;

/**
 * The query for the call log table.
 */
final class CallLogQuery {

    public static final String[] _PROJECTION = new String[] {
            Calls._ID,
            Calls.TYPE,
            Calls.CACHED_NAME,
            Calls.NUMBER,
            Calls.NUMBER_PRESENTATION,
            Calls.IS_READ,
            Calls.DATE,
            Calls.NEW,
            Calls.DURATION
    };

    public static final int ID = 0;
    public static final int TYPE = 1;
    public static final int CACHED_NAME = 2;
    public static final int NUMBER = 3;
    public static final int NUMBER_PRESENTATION = 4;
    public static final int IS_READ = 5;
    public static final int DATE = 6;
    public static final int NEW = 7;
    public static final int DURATION = 8;
}