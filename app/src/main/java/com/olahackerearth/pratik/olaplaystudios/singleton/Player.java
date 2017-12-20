package com.olahackerearth.pratik.olaplaystudios.singleton;

import com.olahackerearth.pratik.olaplaystudios.model.SongDBModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pratik on 21/12/17.
 */

public class Player {

    public final static ArrayList<SongDBModel> playlist = new ArrayList<>();
    public static SongDBModel currentSong;

    public final static ArrayList<SongDBModel> play(List<SongDBModel> list){
        playlist.clear();
        playlist.addAll(list);
        return playlist;
    }

    public final static void play(List<SongDBModel> list, SongDBModel song){
        playlist.clear();
        playlist.addAll(list);
        currentSong = song;
    }


    public final static SongDBModel play(SongDBModel song){
        currentSong = song;
        return currentSong;
    }

    public final static SongDBModel nextSong(){
        int i = playlist.indexOf(currentSong);
        if( (i+1) == playlist.size()){
            return currentSong = playlist.get(0);
        } else{
            return currentSong = playlist.get(i+1);
        }
    }

    public final static SongDBModel previousSong(){
        int i = playlist.indexOf(currentSong);
        if( i == 0){
            return currentSong = playlist.get(0);
        } else{
            return currentSong = playlist.get(i-1);
        }
    }

}