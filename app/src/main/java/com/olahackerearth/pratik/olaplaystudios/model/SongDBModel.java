package com.olahackerearth.pratik.olaplaystudios.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by pratik on 17/12/17.
 */

public class SongDBModel implements Parcelable{

    private String song, url, artists, coverImage, downloadStatus, coverImageFullUlr, favorite, songFullUrl;

    public long id = 0;

    public SongDBModel() {
    }

    public SongDBModel(String song, String url, String artists, String coverImage, String downloadStatus,
                       String coverImageFullUlr, String favorite, String songFullUrl) {
        this.song = song;
        this.url = url;
        this.artists = artists;
        this.coverImage = coverImage;
        this.downloadStatus = downloadStatus;
        this.coverImageFullUlr = coverImageFullUlr;
        this.favorite= favorite;
        this.songFullUrl= songFullUrl;
    }

    protected SongDBModel(Parcel in) {
        id = in.readLong();
        song = in.readString();
        url = in.readString();
        artists = in.readString();
        coverImage = in.readString();
        downloadStatus = in.readString();
        coverImageFullUlr = in.readString();
        favorite = in.readString();
        songFullUrl = in.readString();
    }

    public static final Creator<SongDBModel> CREATOR = new Creator<SongDBModel>() {
        @Override
        public SongDBModel createFromParcel(Parcel in) {
            return new SongDBModel(in);
        }

        @Override
        public SongDBModel[] newArray(int size) {
            return new SongDBModel[size];
        }
    };

    public String getSong() {
        return song;
    }

    public void setSong(String song) {
        this.song = song;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getArtists() {
        return artists;
    }

    public void setArtists(String artists) {
        this.artists = artists;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public String getDownloadStatus() {
        return downloadStatus;
    }

    public long getId() {
        return id;
    }

    public void setDownloadStatus(String downloadStatus) {
        this.downloadStatus = downloadStatus;
    }

    public String getCoverImageFullUlr() {
        return coverImageFullUlr;
    }

    public void setCoverImageFullUlr(String coverImageFullUlr) {
        this.coverImageFullUlr = coverImageFullUlr;
    }

    public String getFavorite() {
        return favorite;
    }

    public void setFavorite(String favorite) {
        this.favorite = favorite;
    }

    public String getSongFullUrl() {
        return songFullUrl;
    }

    public void setSongFullUrl(String songFullUrl) {
        this.songFullUrl = songFullUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(id);
        parcel.writeString(song);
        parcel.writeString(url);
        parcel.writeString(artists);
        parcel.writeString(coverImage);
        parcel.writeString(downloadStatus);
        parcel.writeString(coverImageFullUlr);
        parcel.writeString(favorite);
        parcel.writeString(songFullUrl);
    }
}
