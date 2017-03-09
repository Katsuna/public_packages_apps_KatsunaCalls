package com.katsuna.calls.utils;

import android.content.Context;
import android.text.format.DateUtils;

import com.katsuna.calls.R;
import com.katsuna.calls.domain.Call;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Interval;

import java.util.List;

public class DayInfoFormatter {

    public static void calculateDateInfo(Context context, List<Call> calls) {
        String prevDayInfo = "";

        for (Call call : calls) {
            String dayInfo = getDayInfo(context, call.getDate());
            if (!prevDayInfo.equals(dayInfo)) {
                call.setDayInfo(dayInfo);
                prevDayInfo = dayInfo;
            }
        }
    }

    private static String getDayInfo(Context context, long date) {
        DateTime dateTime = new DateTime(date);

        Interval todayInterval = new Interval(DateTime.now().withTimeAtStartOfDay(), Days.ONE);
        Interval yesterdayInterval = new Interval(
                DateTime.now().withTimeAtStartOfDay().minusDays(1), Days.ONE);

        String output;
        if (todayInterval.contains(dateTime) || yesterdayInterval.contains(dateTime)) {
            output = DateUtils.getRelativeTimeSpanString(date, System.currentTimeMillis(),
                    DateUtils.DAY_IN_MILLIS).toString();
        } else {
            output = context.getResources().getString(R.string.day_info_older);
        }

        return output;
    }

}
