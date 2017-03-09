package com.katsuna.calls.domain;

@SuppressWarnings("WeakerAccess")
public class Call {

    private long id;
    private int type;
    private String number;
    private int numberPresentation;
    private String name;
    private long date;
    private long duration;
    private int isNew;
    private int isRead;
    private Contact contact;
    private boolean selected;
    private String dayInfo;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public int getNumberPresentation() {
        return numberPresentation;
    }

    public void setNumberPresentation(int numberPresentation) {
        this.numberPresentation = numberPresentation;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public int getIsNew() {
        return isNew;
    }

    public void setIsNew(int isNew) {
        this.isNew = isNew;
    }

    public int getIsRead() {
        return isRead;
    }

    public void setIsRead(int isRead) {
        this.isRead = isRead;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getDayInfo() {
        return dayInfo;
    }

    public void setDayInfo(String dayInfo) {
        this.dayInfo = dayInfo;
    }
}
