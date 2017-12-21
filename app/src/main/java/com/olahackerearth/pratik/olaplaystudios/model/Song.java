package com.olahackerearth.pratik.olaplaystudios.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by pratik on 20/12/17.
 */

public class Song implements Parcelable{

    public final static String COLUMN_SONG= "song";
    public final static String COLUMN_URL = "url";
    public final static String COLUMN_ARTISTS = "artists";
    public final static String COLUMN_COVER_IMAGE= "cover_image";

    private String song, url, artists, coverImage;

    public boolean exists = false;

    public Song() {
    }

    public Song(String song, String url, String artists, String coverImage) {
        this.song = song;
        this.url = url;
        this.artists = artists;
        this.coverImage = coverImage;
    }

    protected Song(Parcel in) {
        song = in.readString();
        url = in.readString();
        artists = in.readString();
        coverImage = in.readString();
    }

    public static final Creator<Song> CREATOR = new Creator<Song>() {
        @Override
        public Song createFromParcel(Parcel in) {
            return new Song(in);
        }

        @Override
        public Song[] newArray(int size) {
            return new Song[size];
        }
    };

    public String getSong() {
        return song;
    }

    public String getUrl() {
        return url;
    }

    public String getArtists() {
        return artists;
    }

    public String getCoverImage() {
        return coverImage;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(song);
        parcel.writeString(url);
        parcel.writeString(artists);
        parcel.writeString(coverImage);
    }
}
