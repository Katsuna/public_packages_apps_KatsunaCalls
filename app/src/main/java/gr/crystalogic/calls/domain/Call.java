package gr.crystalogic.calls.domain;

import android.text.TextUtils;

import org.joda.time.DateTime;

public class Call {

    private long id;
    private int type;
    private String number;
    private String name;
    private long date;
    private long duration;
    private int isNew;
    private int isRead;
    private long contactId;
    private String lookupUri;

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

    public long getContactId() {
        return contactId;
    }

    public void setContactId(long contactId) {
        this.contactId = contactId;
    }

    public String getLookupUri() {
        return lookupUri;
    }

    public void setLookupUri(String lookupUri) {
        this.lookupUri = lookupUri;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        if (TextUtils.isEmpty(name))  {
            return number;
        } else {
            return name;
        }
    }

    public String getDateFormatted() {
        DateTime dateTime = new DateTime(date);
        return  dateTime.toString("HH:mm EEEE d-M-yy");
    }
}
