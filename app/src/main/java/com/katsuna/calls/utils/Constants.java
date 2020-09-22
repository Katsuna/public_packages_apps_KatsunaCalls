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
package com.katsuna.calls.utils;

import static android.provider.ContactsContract.Directory.PACKAGE_NAME;

public class Constants {

    public static final int NOT_SELECTED_CALL_VALUE = -1;
    public static final String SHARED_PREFERENCES_NAME = PACKAGE_NAME + ".SHARED_PREFERENCES";
    public static final String [] SHARED_PREF_LASTCALL_HOUR_DAY = { "day1" , "day2" ,"day3", "day4", "day5", "day6", "day7"};
    public static final String SHARED_PREF_MISSEDCALLS_ALARM_HOUR = ".MISSCALLS_ALARM_HOUR";
    public static final int FIRST_MISSED_CALLS_ALARM_HOUR = 19;
    public static final int START_DAY_HOUR = 5;
    public static final int END_DAY_HOUR = 24;
    //DAYS THAT WE EXAMINE FOR USER'S LAST CALL PATTERN
    public static final int MAX_DAYS_LAST_CALL = 7;




}
