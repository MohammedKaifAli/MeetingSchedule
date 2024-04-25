package com.example.asd;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class Meeting implements Parcelable {
    private String id;
    private String title;
    private Date dateTime;
    private boolean isCancelled;

    public Meeting(String id, String title, Date dateTime) {
        this.id = id;
        this.title = title;
        this.dateTime = dateTime;
    }

    protected Meeting(Parcel in) {
        id = in.readString();
        title = in.readString();
        dateTime = new Date(in.readLong());
    }

    public static final Creator<Meeting> CREATOR = new Creator<Meeting>() {
        @Override
        public Meeting createFromParcel(Parcel in) {
            return new Meeting(in);
        }

        @Override
        public Meeting[] newArray(int size) {
            return new Meeting[size];
        }
    };
    public boolean isCancelled() {
        return isCancelled;
    }
    public void setCancelled(boolean cancelled) {
        isCancelled = cancelled;
    }
    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Date getDateTime() {
        return dateTime;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeLong(dateTime.getTime());
    }
}