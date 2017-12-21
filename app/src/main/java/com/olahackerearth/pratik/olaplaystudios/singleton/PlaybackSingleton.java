package com.olahackerearth.pratik.olaplaystudios.singleton;

import android.content.Context;
import android.media.MediaPlayer;

import com.olahackerearth.pratik.olaplaystudios.utility.PlaybackController;

/**
 * Created by pratik on 20/12/17.
 */

public class PlaybackSingleton {

    private static PlaybackSingleton playbackSingleton;

    public Context mContext;
    public static PlaybackSingleton getPlaybackInstance(Context context){
        if(playbackSingleton == null){
            playbackSingleton = new PlaybackSingleton(context.getApplicationContext());
        }
        return playbackSingleton;
    }

    private PlaybackSingleton(Context mContext) {
        this.mContext = mContext;
        this.mediaPlayer = new MediaPlayer();
        this.playbackController = new PlaybackController(mContext);
    }

    public MediaPlayer mediaPlayer;
    public PlaybackController playbackController;

    ///

}
