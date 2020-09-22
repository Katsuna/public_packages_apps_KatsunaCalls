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
package com.katsuna.calls.domain;

import android.os.Parcel;
import android.os.Parcelable;

@SuppressWarnings("WeakerAccess")
public class Call implements Parcelable {

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeInt(this.type);
        dest.writeString(this.number);
        dest.writeInt(this.numberPresentation);
        dest.writeString(this.name);
        dest.writeLong(this.date);
        dest.writeLong(this.duration);
        dest.writeInt(this.isNew);
        dest.writeInt(this.isRead);
        dest.writeParcelable(this.contact, flags);
        dest.writeByte(this.selected ? (byte) 1 : (byte) 0);
        dest.writeString(this.dayInfo);
    }

    public Call() {
    }

    protected Call(Parcel in) {
        this.id = in.readLong();
        this.type = in.readInt();
        this.number = in.readString();
        this.numberPresentation = in.readInt();
        this.name = in.readString();
        this.date = in.readLong();
        this.duration = in.readLong();
        this.isNew = in.readInt();
        this.isRead = in.readInt();
        this.contact = in.readParcelable(Contact.class.getClassLoader());
        this.selected = in.readByte() != 0;
        this.dayInfo = in.readString();
    }

    public static final Parcelable.Creator<Call> CREATOR = new Parcelable.Creator<Call>() {
        @Override
        public Call createFromParcel(Parcel source) {
            return new Call(source);
        }

        @Override
        public Call[] newArray(int size) {
            return new Call[size];
        }
    };
}
