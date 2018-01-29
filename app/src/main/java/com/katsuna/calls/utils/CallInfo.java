package com.katsuna.calls.utils;

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