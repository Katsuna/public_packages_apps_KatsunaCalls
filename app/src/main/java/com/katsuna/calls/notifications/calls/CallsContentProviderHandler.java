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

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.CallLog;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.telecom.Call;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.katsuna.calls.utils.Constants.END_DAY_HOUR;
import static com.katsuna.calls.utils.Constants.START_DAY_HOUR;


public class CallsContentProviderHandler {

    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 0;
    ArrayList<CallLogModel> outgoingList = new ArrayList<CallLogModel>();
    ArrayList<CallLogModel> incomingList = new ArrayList<CallLogModel>();
    ArrayList<CallLogModel> missedcallList = new ArrayList<CallLogModel>();
    ArrayList<CallLogModel> weeksCallActivity = new ArrayList<CallLogModel>();

    HashMap<String, Long> outgoingCalls = new HashMap<>();
    HashMap<String, Long> missedCalls = new HashMap<>();

    DateFormat format;


    public ArrayList<CallLogModel> getOutgoingList() {
        return outgoingList;
    }

    public void setOutgoingList(ArrayList<CallLogModel> outgoingList) {
        this.outgoingList = outgoingList;
    }

    public ArrayList<CallLogModel> getIncomingList() {
        return incomingList;
    }

    public void setIncomingList(ArrayList<CallLogModel> incomingList) {
        this.incomingList = incomingList;
    }

    public ArrayList<CallLogModel> getMissedcallList() {
        return missedcallList;
    }

    public void setMissedcallList(ArrayList<CallLogModel> missedcallList) {
        this.missedcallList = missedcallList;
    }

    public CallsContentProviderHandler(){
        format = new SimpleDateFormat("dd/mm/yyyy hh:mm:ss");

    }

    public void readCallLogs(Context context) {
        missedcallList.clear();
        incomingList.clear();
        outgoingList.clear();

        if ( ContextCompat.checkSelfPermission( context, Manifest.permission.READ_CALL_LOG ) != PackageManager.PERMISSION_GRANTED ) {

          //  System.out.println("PERMISSION ERROR READ CALL LOG");
        }

        	/*Query Call Log Content Provider*/

        Cursor callLogCursor = context.getContentResolver().query(android.provider.CallLog.Calls.CONTENT_URI, null, null, null, android.provider.CallLog.Calls.DEFAULT_SORT_ORDER);

	/*Check if cursor is not null*/
        if (callLogCursor != null) {

	/*Loop through the cursor*/
            while (callLogCursor.moveToNext()) {

		/*Get ID of call*/
                String id = callLogCursor.getString(callLogCursor.getColumnIndex(CallLog.Calls._ID));
		
		/*Get Contact Name*/
                String name = callLogCursor.getString(callLogCursor.getColumnIndex(CallLog.Calls.CACHED_NAME));
		
		/*Get Contact Cache Number*/
                String cacheNumber = callLogCursor.getString(callLogCursor.getColumnIndex(CallLog.Calls.CACHED_NUMBER_LABEL));
		
		/*Get Contact Number*/
                String number = callLogCursor.getString(callLogCursor.getColumnIndex(CallLog.Calls.NUMBER));
		
		/*Get Date and time information*/
                long dateTimeMillis = callLogCursor.getLong(callLogCursor.getColumnIndex(CallLog.Calls.DATE));
                long durationMillis = callLogCursor.getLong(callLogCursor.getColumnIndex(CallLog.Calls.DURATION));
		
		/*Get Call Type*/
                int callType = callLogCursor.getInt(callLogCursor.getColumnIndex(CallLog.Calls.TYPE));

                String duration = String.valueOf(durationMillis * 1000);

                String dateString = getDateTime(dateTimeMillis,"dd/mm/yyyy");

                if (cacheNumber == null)
                    cacheNumber = number;

                if (name == null)
                    name = "No Name";

		/*Create Model Object*/
                CallLogModel callLog = new CallLogModel(name, cacheNumber, duration, dateString, dateTimeMillis);

		/*Add it into respective ArrayList*/
                if (callType == CallLog.Calls.OUTGOING_TYPE) {
                    outgoingList.add(callLog);
                } else if (callType == CallLog.Calls.INCOMING_TYPE) {
                    incomingList.add(callLog);
                } else if (callType == CallLog.Calls.MISSED_TYPE) {
                    missedcallList.add(callLog);

                }
            }
	
	/*Close the cursor*/
            callLogCursor.close();
        }

    }

    public void findLastDayCalls(Context context){

        String strOrder = android.provider.CallLog.Calls.DATE + " DESC";
        Calendar calendar = Calendar.getInstance();

        calendar.add(Calendar.DAY_OF_YEAR, -1);
        String fromDate = String.valueOf(calendar.getTimeInMillis());
        String[] whereValue = {fromDate};

        if ( ContextCompat.checkSelfPermission( context, Manifest.permission.READ_CALL_LOG ) != PackageManager.PERMISSION_GRANTED ) {

         //   System.out.println("PERMISSION ERROR READ CALL LOG");
        }
       // Cursor callLogCursor = context.getContentResolver().query(android.provider.CallLog.Calls.CONTENT_URI, null, null, null, CallLog.Calls.DEFAULT_SORT_ORDER);


        Cursor callLogCursor =  context.getContentResolver().query(android.provider.CallLog.Calls.CONTENT_URI, null, android.provider.CallLog.Calls.DATE+" >= ?", whereValue, strOrder);
//                context.getContentResolver().query(
//                android.provider.CallLog.Calls.CONTENT_URI,
//                null, null, null,
//                android.provider.CallLog.Calls.DATE + " DESC");
                //context.getContentResolver().query(CallLog.Calls.CONTENT_URI, null, android.provider.CallLog.Calls.DATE + " BETWEEN ? AND ?", whereValue,strOrder1);
        // loop through cursor
        if (callLogCursor != null) {
            while (callLogCursor.moveToNext()) {

		/*Get ID of call*/
                String id = callLogCursor.getString(callLogCursor.getColumnIndex(CallLog.Calls._ID));

		/*Get Contact Name*/
                String name = callLogCursor.getString(callLogCursor.getColumnIndex(CallLog.Calls.CACHED_NAME));

		/*Get Contact Cache Number*/
                String cacheNumber = callLogCursor.getString(callLogCursor.getColumnIndex(CallLog.Calls.CACHED_NUMBER_LABEL));

		/*Get Contact Number*/
                String number = callLogCursor.getString(callLogCursor.getColumnIndex(CallLog.Calls.NUMBER));

		/*Get Date and time information*/
                long dateTimeMillis = callLogCursor.getLong(callLogCursor.getColumnIndex(CallLog.Calls.DATE));
                long durationMillis = callLogCursor.getLong(callLogCursor.getColumnIndex(CallLog.Calls.DURATION));

		/*Get Call Type*/
                int callType = callLogCursor.getInt(callLogCursor.getColumnIndex(CallLog.Calls.TYPE));

                String duration = String.valueOf(durationMillis * 1000);

                String dateString = getDateTime(dateTimeMillis, "dd/mm/yyyy hh:mm:ss");


            //    System.out.println("the date is :" + dateString);
                if (cacheNumber == null)
                    cacheNumber = number;

                if (name == null)
                    name = "No Name";

		/*Create Model Object*/
                CallLogModel callLog = new CallLogModel(name, cacheNumber, duration, dateString, dateTimeMillis);

		/*Add it into respective ArrayList*/
                if (callType == CallLog.Calls.OUTGOING_TYPE) {
                    if (outgoingCalls.containsKey(callLog.getNumber())) {
                        if (dateTimeMillis > outgoingCalls.get(callLog.getNumber())) {
                            outgoingCalls.put(callLog.getNumber(), dateTimeMillis);
                        }
                    } else {
                        outgoingCalls.put(callLog.getNumber(), dateTimeMillis);

                    }
                    outgoingList.add(callLog);
                } else if (callType == CallLog.Calls.INCOMING_TYPE) {
                    incomingList.add(callLog);
                } else if (callType == CallLog.Calls.MISSED_TYPE) {
                    if (missedCalls.containsKey(callLog.getNumber())) {
                        if (dateTimeMillis > missedCalls.get(callLog.getNumber())) {
                            missedCalls.put(callLog.getNumber(), dateTimeMillis);
                        }
                    } else {
                        missedCalls.put(callLog.getNumber(), dateTimeMillis);

                    }
                    missedcallList.add(callLog);
                  //  System.out.println("Missed call:" + callLog.getNumber());
                }
            }
        }



    }



    public ArrayList<CallLogModel> findLastWeekCalls(Context context){
        ArrayList<CallLogModel> weekCalls = new ArrayList<>();
        String strOrder = android.provider.CallLog.Calls.DATE + " DESC";

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -7);

        String fromDate = String.valueOf(calendar.getTimeInMillis());
        String[] whereValue = {fromDate};

        if ( ContextCompat.checkSelfPermission( context, Manifest.permission.READ_CALL_LOG ) != PackageManager.PERMISSION_GRANTED ) {

         //   System.out.println("PERMISSION ERROR READ CALL LOG");
        }
      //  Cursor callLogCursor = context.getContentResolver().query(android.provider.CallLog.Calls.CONTENT_URI, null, null, null, android.provider.CallLog.Calls.DEFAULT_SORT_ORDER);


        //Cursor cur = cr.query(callUri, null, android.provider.CallLog.Calls.DATE+" BETWEEN ? AND ?", whereValue, strOrder);

        Cursor callLogCursor = context.getContentResolver().query(android.provider.CallLog.Calls.CONTENT_URI, null, android.provider.CallLog.Calls.DATE+" >= ?", whereValue, strOrder);
        // loop through cursor
        while (callLogCursor.moveToNext()) {

		/*Get ID of call*/
            String id = callLogCursor.getString(callLogCursor.getColumnIndex(CallLog.Calls._ID));

		/*Get Contact Name*/
            String name = callLogCursor.getString(callLogCursor.getColumnIndex(CallLog.Calls.CACHED_NAME));

		/*Get Contact Cache Number*/
            String cacheNumber = callLogCursor.getString(callLogCursor.getColumnIndex(CallLog.Calls.CACHED_NUMBER_LABEL));

		/*Get Contact Number*/
            String number = callLogCursor.getString(callLogCursor.getColumnIndex(CallLog.Calls.NUMBER));

		/*Get Date and time information*/
            long dateTimeMillis = callLogCursor.getLong(callLogCursor.getColumnIndex(CallLog.Calls.DATE));
            long durationMillis = callLogCursor.getLong(callLogCursor.getColumnIndex(CallLog.Calls.DURATION));

		/*Get Call Type*/
            int callType = callLogCursor.getInt(callLogCursor.getColumnIndex(CallLog.Calls.TYPE));

            String duration = String.valueOf(durationMillis * 1000);

            String dateString = getDateTime(dateTimeMillis, "dd/mm/yyyy hh:mm:ss");
       //     System.out.println("the date is :"+dateString);
            if (cacheNumber == null)
                cacheNumber = number;

            if (name == null)
                name = "No Name";

		/*Create Model Object*/
            CallLogModel callLog = new CallLogModel(name, cacheNumber, duration, dateString, dateTimeMillis);

		/*Add it into respective ArrayList*/
            if (callType != CallLog.Calls.MISSED_TYPE) {

                try {
                    Date date = format.parse(dateString);
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(date);
                    int hours = cal.get(Calendar.HOUR_OF_DAY);
                    if( hours >= START_DAY_HOUR && hours <=END_DAY_HOUR){
                   //     System.out.println(callLog.getNumber()+"date"+callLog.getDate());
                        weekCalls.add(callLog);
                    }


                } catch (ParseException e) {
                    e.printStackTrace();
                }


            }
        }

        return weekCalls;
    }

    public CallLogModel findLastOutgoingCall(){
        if(!outgoingList.isEmpty() && !incomingList.isEmpty()){
            if ( outgoingList.get(outgoingList.size()-1).getDatetimemillis() < incomingList.get(incomingList.size()-1).getDatetimemillis()){
                return incomingList.get(incomingList.size()-1);
            }
            else {
                outgoingList.get(outgoingList.size()-1);
            }
        }
        else if( outgoingList.isEmpty() && !incomingList.isEmpty()){
            return incomingList.get(incomingList.size()-1);
        }
        else if (!outgoingList.isEmpty() && incomingList.isEmpty()){
            return outgoingList.get(outgoingList.size()-1);
        }
        return null;
    }

    public List<CallLogModel> findUnansweredCalls(Map<String, Long> sentMessages){

        List<CallLogModel> unansweredCalls = new ArrayList<>();
        List<CallLogModel> tempCalls = new ArrayList<>();
        List<String> tempUnanswered = new ArrayList<>();
        Set<CallLogModel> unansweredCallsSet = new HashSet<>();

        Iterator it = missedCalls.entrySet().iterator();
        while (it.hasNext()) {

            Map.Entry pair = (Map.Entry)it.next();
            if(!outgoingCalls.containsKey(pair.getKey()) && !sentMessages.containsKey(pair.getKey())){
                    tempUnanswered.add((String) pair.getKey());
            }
            else{
                if( (Long) pair.getValue() > outgoingCalls.get(pair.getKey()))
                {
                    if(sentMessages.isEmpty())
                        tempUnanswered.add((String) pair.getKey());
                    else if(Long.parseLong((String) pair.getValue()) > sentMessages.get(pair.getKey()))
                        tempUnanswered.add((String) pair.getKey());
                }
            }

           // System.out.println(pair.getKey() + " = " + pair.getValue());
            it.remove(); // avoids a ConcurrentModificationException
        }

        for (String unanswered: tempUnanswered){
                for (CallLogModel missedCall : missedcallList) {

                    if (missedCall.getNumber().equals(unanswered) && containsNumber(unansweredCalls,unanswered)==false) {
                        unansweredCalls.add(missedCall);
                     //   System.out.println("call:"+missedCall.getNumber()+"flag:"+containsNumber(unansweredCalls,unanswered));
                    }
                   // missedcallList.remove(missedCall);
                }

        }
        return unansweredCalls;
    }


    public static boolean containsNumber(Collection<CallLogModel> c, String number) {
        for(CallLogModel o : c) {
            if(o != null && o.getNumber().equals(number)) {
                return true;
            }
        }
        return false;
    }


    private String getDateTime(long milliSeconds, String dateFormat)
    {
        // Create a DateFormatter object for displaying date in specified format.
        DateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }
}
