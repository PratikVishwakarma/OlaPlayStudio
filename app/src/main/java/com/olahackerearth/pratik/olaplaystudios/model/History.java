package com.olahackerearth.pratik.olaplaystudios.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by pratik on 17/12/17.
 */

public class History implements Parcelable{

    String type;
    long songId, timeStamp;
    public long id = 0;

    public History() {
    }

    public History(String type, long songId, long timeStamp) {
        this.type = type;
        this.songId = songId;
        this.timeStamp = timeStamp;
    }

    protected History(Parcel in) {
        type = in.readString();
        songId = in.readLong();
        timeStamp = in.readLong();
        id = in.readLong();
    }

    public static final Creator<History> CREATOR = new Creator<History>() {
        @Override
        public History createFromParcel(Parcel in) {
            return new History(in);
        }

        @Override
        public History[] newArray(int size) {
            return new History[size];
        }
    };

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getSongId() {
        return songId;
    }

    public void setSongId(long songId) {
        this.songId = songId;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public long getId() {
        return id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(type);
        parcel.writeLong(songId);
        parcel.writeLong(timeStamp);
        parcel.writeLong(id);
    }
}
