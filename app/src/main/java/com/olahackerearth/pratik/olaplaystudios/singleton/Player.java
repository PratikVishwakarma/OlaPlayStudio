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
    public boolean loopSong = false;
    public boolean loopList = true;


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

    public final void nextSong(){
        if(currentSong != null){
            int i = playlist.indexOf(currentSong);
            Log.e("Next song in ", i+"");
            if(loopSong){
                play();
            }else{
                if( (i+1) == playlist.size()){
                    if(loopList){
                        currentSong = playlist.get(0);
                    }else{
                        currentSong = playlist.get(0);
                        new PlaybackController(mContext).pause();
                    }
                } else{
                    currentSong = playlist.get(i+1);
                }
                play();
            }
        }else{

        }
    }

    public final void previousSong(){
        if(currentSong != null){
            int i = playlist.indexOf(currentSong);
            Log.e("Pre song in ", i+"");
            if( i == 0){
                currentSong = playlist.get(0);
            } else{
                currentSong = playlist.get(i-1);
            }
            play();
        }else{

        }
    }

    public void toggleSingleSong(){
        if(loopSong){
            loopSong = false;
        }else{
            loopSong = true;
        }
    }

    public void toggleSongList(){
        if(loopList){
            loopList = false;
        }else{
            loopList = true;
        }
    }

}