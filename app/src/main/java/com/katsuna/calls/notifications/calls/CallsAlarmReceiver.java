package com.katsuna.calls.notifications.calls;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.katsuna.calls.notifications.sms.SmsContentProviderHandler;

import java.util.Calendar;

import static com.katsuna.calls.notifications.calls.CheckMissedCallsService.MISSED_CALLS_SET_ALARM;
import static com.katsuna.calls.utils.Constants.FIRST_MISSED_CALLS_ALARM_HOUR;
import static com.katsuna.calls.utils.Constants.SHARED_PREFERENCES_NAME;
import static com.katsuna.calls.utils.Constants.SHARED_PREF_MISSEDCALLS_ALARM_HOUR;


public class CallsAlarmReceiver extends BroadcastReceiver {
    public static final int REQUEST_CODE_MISSEDCALLS = 11111;
    public static final String MISSED_CALLS = "gr.katsuna.services.calls.MISSED_CALLS";
    private static final String MISSED_CALLS_HOUR_ALARM = "gr.katsuna.services.calls.MISSED_CALLS_HOUR_ALARM";
    private static final CallsContentProviderHandler callsHandler =  new CallsContentProviderHandler();
    private static final SmsContentProviderHandler smsHandler = new SmsContentProviderHandler();
    private static final CallsUtils callUtils = new CallsUtils();

    @Override
    public void onReceive(Context context, Intent intent) {
        callsHandler.readCallLogs(context);
        if(intent.getAction().equals(MISSED_CALLS)){
            Intent inService = new Intent(context,CheckMissedCallsService.class);
            inService.setAction(CheckMissedCallsService.MISSED_CALLS_NOTIFY);
            context.startService(inService);



        }
        else if( intent.getAction().equals(MISSED_CALLS_HOUR_ALARM)){
            Intent inService = new Intent(context,CheckMissedCallsService.class);
            inService.setAction(MISSED_CALLS_SET_ALARM);
        //    System.out.println("im setting up allarm 2nd");
            context.startService(inService);
          //  setMissedCalledAlarm(context);

        }
    }



    public void setMissedCalledAlarm(Context context){
        Intent myIntent =  new Intent(context, CallsAlarmReceiver.class);
        myIntent.putExtra("action",this.MISSED_CALLS);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, this.REQUEST_CODE_MISSEDCALLS,
                myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager =  (AlarmManager)context.getSystemService(context.ALARM_SERVICE);

        SharedPreferences myPrefs = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        int hour = myPrefs.getInt(SHARED_PREF_MISSEDCALLS_ALARM_HOUR, 0);
        Calendar calendar = Calendar.getInstance();

        if (hour == 0 ) {
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, FIRST_MISSED_CALLS_ALARM_HOUR);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 0);
        }
        else
        {
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 0);
        }

        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    public void setFindMissedCallAlarmHour(Context context){
        Intent myIntent =  new Intent(context, CallsAlarmReceiver.class);
        myIntent.putExtra("action",this.MISSED_CALLS_HOUR_ALARM);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, this.REQUEST_CODE_MISSEDCALLS,
                myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager =  (AlarmManager)context.getSystemService(context.ALARM_SERVICE);
        SharedPreferences myPrefs = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
//        int hour = myPrefs.getInt(SHARED_PREF_MISSEDCALLS_ALARM_HOUR, 0);
        Calendar calendar = Calendar.getInstance();


            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 0);

        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }


    public void setAlarm(Context context)
    {
        AlarmManager am =( AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

        Intent i = new Intent(context, CallsAlarmReceiver.class);
        i.setAction(MISSED_CALLS_SET_ALARM);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);

        Calendar calendar = Calendar.getInstance();

        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 0);

        am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 1000 * 60 * 24* 1, pi); //Once a day at 23.59.

        Intent inService = new Intent(context,CheckMissedCallsService.class);
        inService.setAction(MISSED_CALLS_SET_ALARM);
        context.startService(inService);
     //   System.out.println("im setting up alarm 2nd");
    }

    public void cancelAlarm(Context context)
    {
        Intent intent = new Intent(context, CallsAlarmReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }
}
