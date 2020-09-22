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

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;

import com.katsuna.commons.notifications.Notifications;
import com.katsuna.calls.notifications.sms.SmsContentProviderHandler;

import java.util.Calendar;
import java.util.List;
import java.util.Map;


public class CheckMissedCallsService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_FOO = "com.katsuna.services.calls.action.FOO";
    private static final String ACTION_BAZ = "com.katsuna.services.calls.action.BAZ";

    // TODO: Rename parameters
    public static final String MISSED_CALLS_NOTIFY = "com.katsuna.services.calls.extra.missedCalls";

    public static final String MISSED_CALLS_SET_ALARM = "com.katsuna.services.calls.extra.missedCallsAlarm";
    private static final String EXTRA_PARAM2 = "com.katsuna.services.calls.extra.PARAM2";

    private static final CallsContentProviderHandler callsHandler =  new CallsContentProviderHandler();
    private static final SmsContentProviderHandler smsHandler = new SmsContentProviderHandler();
    private static final CallsUtils callUtils = new CallsUtils();



    public CheckMissedCallsService() {
        super("CheckMissedCallsService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method


    @Override
    protected void onHandleIntent(Intent intent) {

        callsHandler.readCallLogs(getApplicationContext());
        if (intent != null) {
            if(intent.getAction().equals(MISSED_CALLS_NOTIFY)){
                callsHandler.findLastDayCalls(getApplicationContext());
                Map<String, Long> sentMessages = smsHandler.findLastDaySMS(getApplicationContext());

                List<CallLogModel> unansweredCalls = callsHandler.findUnansweredCalls(sentMessages);

                for(CallLogModel call : unansweredCalls){
                    Notifications.callNotification(getApplicationContext(),"You have an unaswered Call from: ",call.getNumber());
                }
         //       System.out.println("Notification for unaswered calls should be triggered");

            }
            else if(intent.getAction().equals(MISSED_CALLS_SET_ALARM)){
                List<CallLogModel> lastWeekCalls = callsHandler.findLastWeekCalls(getApplicationContext());

                float alarmHour = callUtils.findNormalDistribution(lastWeekCalls);
           //     Log.d("ALARM SET","Alarm set at hour in float:"+alarmHour);
          //      System.out.println("Alarm set at hour in float:"+alarmHour);

                setMissedCalledAlarm(alarmHour);
            }
        }
    }
    public void setMissedCalledAlarm(float hour){
        Intent myIntent =  new Intent(getApplicationContext(), CallsAlarmReceiver.class);
        myIntent.setAction(CallsAlarmReceiver.MISSED_CALLS);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), CallsAlarmReceiver.REQUEST_CODE_MISSEDCALLS,
                myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager =  (AlarmManager)getApplicationContext().getSystemService(getApplicationContext().ALARM_SERVICE);

        Calendar calendar = Calendar.getInstance();

        int h = (int) hour;
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, h);
        calendar.set(Calendar.MINUTE, (int) (60 * (hour - h)));
        calendar.set(Calendar.SECOND, 0);
      //  System.out.println("the hour of alarm that is set is h:"+h+"mins:"+(int) (60 * (hour - h)));
//        calendar.set(Calendar.HOUR_OF_DAY, 13);
//        calendar.set(Calendar.MINUTE, 30);
//        calendar.set(Calendar.SECOND, 0);


        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }
}
