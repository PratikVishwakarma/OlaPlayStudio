package com.olahackerearth.pratik.olaplaystudios.singleton;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.util.Log;

import com.olahackerearth.pratik.olaplaystudios.model.SongDBModel;
import com.olahackerearth.pratik.olaplaystudios.service.PlayBackService;
import com.olahackerearth.pratik.olaplaystudios.utility.Constant;
import com.olahackerearth.pratik.olaplaystudios.utility.PlaybackController;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pratik on 21/12/17.
 */

public class Player {

    public final ArrayList<SongDBModel> playlist = new ArrayList<>();
    public SongDBModel currentSong;

    private static Player player;
    public Context mContext;
//    public static boolean loopSong = false;

    public static Player getPlayerInstance(Context context){
        if(player == null){
            player = new Player(context.getApplicationContext());
        }
        return player;
    }

    private Player(Context mContext) {
        this.mContext = mContext;
    }

    public final ArrayList<SongDBModel> play(List<SongDBModel> list){
        playlist.clear();
        playlist.addAll(list);
        return playlist;
    }

    public final void play(List<SongDBModel> list, SongDBModel song){
        playlist.clear();
        playlist.addAll(list);
        play(song);
    }


    public final SongDBModel play(SongDBModel song){
        currentSong = song;
        return play();
    }

    public final SongDBModel play(){
        Intent playIntent = new Intent(mContext, PlayBackService.class).setAction(Constant.ACTION_PLAYBACK_REQUESTED);
        mContext.startService(playIntent);
        return currentSong;
    }
//    public final static void setLoop(boolean value){
//        loopSong = value;
//    }

    public final void nextSong(){
        int i = playlist.indexOf(currentSong);
        Log.e("Next song in ", i+"");
        if( (i+1) == playlist.size()){
            currentSong = playlist.get(0);
        } else{
            currentSong = playlist.get(i+1);
        }
        play();
    }

    public final void previousSong(){
        int i = playlist.indexOf(currentSong);
        Log.e("Pre song in ", i+"");
        if( i == 0){
            currentSong = playlist.get(0);
        } else{
            currentSong = playlist.get(i-1);
        }
        play();
    }

}