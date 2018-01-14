package com.katsuna.calls.notifications.sms;


import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SmsContentProviderHandler {

    //Context context;
    List<SmsLogModel> lstSms = new ArrayList<SmsLogModel>();


    public SmsContentProviderHandler(){
       // this.context = context;
    }

    public List<SmsLogModel> getAllSms(Context context) {
        SmsLogModel objSms = new SmsLogModel();
        Uri message = Uri.parse("content://sms/");
        ContentResolver cr = context.getContentResolver();

        Cursor c = cr.query(message, null, null, null, null);
        int totalSMS = c.getCount();

        if (c.moveToFirst()) {
            for (int i = 0; i < totalSMS; i++) {

                objSms = new SmsLogModel();
                objSms.setId(c.getString(c.getColumnIndexOrThrow("_id")));
                objSms.setAddress(c.getString(c
                        .getColumnIndexOrThrow("address")));
                objSms.setMsg(c.getString(c.getColumnIndexOrThrow("body")));
                objSms.setReadState(c.getString(c.getColumnIndex("read")));
                objSms.setTime(c.getString(c.getColumnIndexOrThrow("date")));
                objSms.setDeliverStatus(c.getString(c.getColumnIndexOrThrow("delivery_status")));
                if (c.getString(c.getColumnIndexOrThrow("type")).contains("1")) {
                    objSms.setFolderName("inbox");
                } else {
                    objSms.setFolderName("sent");
                }
            //    System.out.println("--SMS:--"+objSms.getMsg());
                lstSms.add(objSms);
                c.moveToNext();
            }
        }
        // else {
        // throw new RuntimeException("You have no SMS");
        // }
        c.close();
    //    System.out.println("--SMS handler called--");

        return lstSms;
    }
    public  Map<String,Long> findLastDaySMS(Context context) {
        Map<String,Long> lastDaySms =  new HashMap<>();
        SmsLogModel objSms = new SmsLogModel();
        Uri message = Uri.parse("content://sms/");
        ContentResolver cr = context.getContentResolver();
        long date = new Date(System.currentTimeMillis() - 24 * 3600 * 1000).getTime();
        Cursor c = context.getContentResolver().query(message, null,"date" + ">?",new String[]{""+date},"date DESC");

        int totalSMS = c.getCount();

        if (c.moveToFirst()) {
            for (int i = 0; i < totalSMS; i++) {

                objSms = new SmsLogModel();
                objSms.setId(c.getString(c.getColumnIndexOrThrow("_id")));
                objSms.setAddress(c.getString(c
                        .getColumnIndexOrThrow("address")));
                objSms.setMsg(c.getString(c.getColumnIndexOrThrow("body")));
                objSms.setReadState(c.getString(c.getColumnIndex("read")));
                objSms.setTime(c.getString(c.getColumnIndexOrThrow("date")));
             //   objSms.setDeliverStatus(c.getString(c.getColumnIndexOrThrow("delivery_status")));
                if (c.getString(c.getColumnIndexOrThrow("type")).contains("1")) {
                    objSms.setFolderName("inbox");
                } else {
                    objSms.setFolderName("sent");

                    lastDaySms.put(objSms.getAddress(), Long.parseLong(objSms.getTime()));

                }
           //     System.out.println("--SMS:--"+objSms.getMsg());
                lstSms.add(objSms);
                c.moveToNext();


            }
        }
        // else {
        // throw new RuntimeException("You have no SMS");
        // }
        c.close();
     //   System.out.println("--SMS handler called--");

        return lastDaySms;
    }

}
