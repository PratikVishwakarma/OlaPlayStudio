package com.olahackerearth.pratik.olaplaystudios.utility;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;
import android.widget.MediaController;

import com.olahackerearth.pratik.olaplaystudios.singleton.PlaybackSingleton;

/**
 * Created by pratik on 19/12/17.
 */

public class PlaybackController implements MediaController.MediaPlayerControl {

    
    Context context;
    public PlaybackController(Context context) {
        this.context = context;
    }

    MediaPlayer getMediaPlayer() {
        return PlaybackSingleton.getPlaybackInstance(context).mediaPlayer;
    }
    @Override
    public void start() {
        try {
            getMediaPlayer().start();

//            seekTo(getDuration()-5000);

            //
        }
        catch (Exception e) {
            Log.e("MediaPlayer", ""+e);
            e.printStackTrace();
        }
    }

    @Override
    public void pause() {
        try {
            getMediaPlayer().pause();
            //
        }
        catch (Exception e) {
            Log.e("MediaPlayer", ""+e);
            e.printStackTrace();
        }
    }

    @Override
    public int getDuration() {
        try {
            return getMediaPlayer().getDuration();
        }
        catch (Exception e) {
            Log.e("MediaPlayer", ""+e);
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public int getCurrentPosition() {
        try {
            return getMediaPlayer().getCurrentPosition();
        }
        catch (Exception e) {
            Log.e("MediaPlayer", ""+e);
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public void seekTo(int i) {
        try {
            getMediaPlayer().seekTo(i);
        }
        catch (Exception e) {
            Log.e("MediaPlayer", ""+e);
            e.printStackTrace();

        }
    }

    @Override
    public boolean isPlaying() {
        try {
            return getMediaPlayer().isPlaying();
        }
        catch (Exception e) {
            Log.e("MediaPlayer", ""+e);
            e.printStackTrace();
            return false;
        }

    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        try {
            return getMediaPlayer().getAudioSessionId();
        }
        catch (Exception e) {
            Log.e("MediaPlayer", ""+e);
            e.printStackTrace();
            return 0;
        }
    }
}
