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
