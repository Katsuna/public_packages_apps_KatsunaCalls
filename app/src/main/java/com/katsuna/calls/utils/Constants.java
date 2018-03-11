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
