package com.katsuna.calls.notifications.calls;


import java.util.Collection;

public class CallLogModel {

    /*Name of the Contact*/
    private String name;
    /*Contact Number*/
    private String number;
    /*Duration of the call*/
    private String duration;
    /*Date of the call*/
    private String date;

    private long datetimemillis;

    public CallLogModel() {

    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getDatetimemillis() {
        return datetimemillis;
    }

    public void setDatetimemillis(long datetimemillis) {
        this.datetimemillis = datetimemillis;
    }

    /*Constructor*/
    public CallLogModel(String name, String number, String duration, String date, long datetimemillis) {
        this.name = name;
        this.number = number;
        this.duration = duration;
        this.date = date;
    }

    /*Contact Name*/
    public String getName() {
        return name;
    }

    /*Contact Number*/
    public String getNumber() {
        return number;
    }

    /*Duration of call*/
    public String getDuration() {
        return duration;
    }

    /*Date of call*/
    public String getDate() {
        return date;
    }


}