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

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.provider.CallLog;

import com.katsuna.calls.R;
import com.katsuna.calls.domain.Call;
import com.katsuna.commons.utils.DateFormatter;

public class CallInfo {

    public static String getDetails(Context context, Call call) {
        String callDetails = "";
        if (call.getType() == CallLog.Calls.INCOMING_TYPE) {
            callDetails = context.getResources().getString(R.string.incoming_call);
        } else if (call.getType() == CallLog.Calls.OUTGOING_TYPE) {
            callDetails = context.getResources().getString(R.string.outgoing_call);
        } else if (call.getType() == CallLog.Calls.MISSED_TYPE) {
            callDetails = context.getResources().getString(R.string.missed_call);
        }
        // add date
        callDetails += ", " + DateFormatter.format(call.getDate());

        return callDetails;
    }

    @SuppressLint("DefaultLocale")
    public static String getCallDuration(Context context, Call call) {
        String output;

        long hours = call.getDuration() / 3600;
        long minutes = (call.getDuration() % 3600) / 60;
        long seconds = call.getDuration() % 60;

        Resources res = context.getResources();

        String callDuration = res.getString(R.string.call_duration);
        String secStr = res.getString(R.string.seconds);
        String minStr = res.getString(R.string.minutes);

        if (hours == 0 && minutes == 0) {
            output = String.format("%s: %d %s", callDuration, seconds, secStr);
        } else if (hours == 0)  {
            output = String.format("%s: %02d:%02d %s", callDuration, minutes, seconds, minStr);
        } else {
            output = String.format("%s: %02d:%02d:%02d", callDuration, hours, minutes, seconds);
        }

        return output;
    }


}