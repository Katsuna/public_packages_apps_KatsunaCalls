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