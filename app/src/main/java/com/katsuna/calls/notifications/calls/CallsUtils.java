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
package com.katsuna.calls.notifications.calls;

import android.content.Context;
import android.content.SharedPreferences;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.katsuna.calls.utils.Constants.MAX_DAYS_LAST_CALL;
import static com.katsuna.calls.utils.Constants.SHARED_PREFERENCES_NAME;
import static com.katsuna.calls.utils.Constants.SHARED_PREF_LASTCALL_HOUR_DAY;


public class CallsUtils {

    public CallsUtils(){

    }

    public String findAlarmHour(Context context){
        int hourMean = 0;
        String alarmHour = null;
        int s = 0;
        SharedPreferences myPrefs = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        //int hour = myPrefs.getInt(SHARED_PREF_MISSEDCALLS_ALARM_HOUR, 0);
        for (int i = 0; i < 7; i++){
            if(myPrefs.contains(SHARED_PREF_LASTCALL_HOUR_DAY[i])){
                //hourMean =
            }
            else{
                break;
            }
        }
       return alarmHour;

    }

    public void storelastCallHour(Context context, CallLogModel lastCall){
        SharedPreferences myPrefs = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = myPrefs.edit();

        boolean isFullWeek = true;
        for (int i = 0; i < 7; i++) {

            if (!myPrefs.contains(SHARED_PREF_LASTCALL_HOUR_DAY[i])) {
                //insert day for the first time
                editor.putLong(SHARED_PREF_LASTCALL_HOUR_DAY[i], lastCall.getDatetimemillis());
                isFullWeek = false;
                break;
            }
        }

        if( isFullWeek){
            for (int i= 0; i< MAX_DAYS_LAST_CALL -1; i++){
                long temp = myPrefs.getLong(SHARED_PREF_LASTCALL_HOUR_DAY[i+1],0);
                editor.putLong(SHARED_PREF_LASTCALL_HOUR_DAY[i],myPrefs.getLong(SHARED_PREF_LASTCALL_HOUR_DAY[i+1],0));
            }
            editor.putLong(SHARED_PREF_LASTCALL_HOUR_DAY[MAX_DAYS_LAST_CALL-1],lastCall.getDatetimemillis());
        }
    }

    public float findNormalDistribution(List<CallLogModel> weekCalls){
        ArrayList<Float> weekHours = convertCallWeekToFloat(weekCalls);
        Float meanHour = findMeanHour(weekHours);
        ArrayList<Float> tempArray = new ArrayList<>();
        float sum =0;
        for (Float hour: weekHours){
            double temp =0;
            if(!hour.equals(meanHour)){
               temp =  power((hour - meanHour),2);

            }
            else {
                temp =  power((hour),2);

            }
            tempArray.add((float) temp);
            sum += temp;
        }
        float tempAverage = sum / weekHours.size();

        float stdDeviation = (float) Math.sqrt( tempAverage);


        return stdDeviation;
    }

    public Float findMeanHour(List<Float> weekHours){
        float totalSeconds = 0;
        for (Float hour : weekHours) {
            totalSeconds += hour;
        }
        float averageSeconds = totalSeconds / weekHours.size();
        return averageSeconds;

    }

    public float power(final float base, final int power) {
        float result = 1;
        for( int i = 0; i < power; i++ ) {
            result *= base;
        }
        return result;
    }

    public ArrayList<Float> convertCallWeekToFloat(List<CallLogModel> weekCalls){
        ArrayList<Float> weekHours =  new ArrayList<>();
        for (CallLogModel call : weekCalls) {
            SimpleDateFormat parseFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
            SimpleDateFormat printFormat = new SimpleDateFormat("HH:mm");
            Date time = null;
            try {
                time = parseFormat.parse(call.getDate());

                weekHours.add(hoursToFloat(printFormat.format(time)));

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return  weekHours;
    }


    public static float hoursToFloat(String tmpHours) throws NumberFormatException {
        float result = 0;
        tmpHours = tmpHours.trim();

        // Try converting to float first
        try
        {
            result = new Float(tmpHours);
        }
        catch(NumberFormatException nfe)
        {
            // OK so that didn't work.  Did they use a colon?
            if(tmpHours.contains(":"))
            {
                int hours = 0;
                int minutes = 0;
                int locationOfColon = tmpHours.indexOf(":");
                String[] temp = tmpHours.split(":");
                try {
                    hours = new Integer(Integer.parseInt(temp[0]));
                    minutes = new Integer(Integer.parseInt(temp[1]));
                //    System.out.println("im in here:"+minutes);
                }
                catch(NumberFormatException nfe2) {
                    //need to do something here if they are still formatted wrong.
                    //perhaps throw the exception to the user to the UI to force the user
                    //to put in a correct value.
                    throw nfe2;
                }

                //add in partial hours (ie minutes if minutes are greater than zero.
                if(minutes > 0) {
                    result = minutes / 60;
                }

                //now add in the full number of hours.
                result += hours;
            }
        }

        return result;
    }

}
