package com.olahackerearth.pratik.olaplaystudios.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import com.olahackerearth.pratik.olaplaystudios.database.DBHelper;
import com.olahackerearth.pratik.olaplaystudios.model.History;
import com.olahackerearth.pratik.olaplaystudios.model.SongDBModel;
import com.olahackerearth.pratik.olaplaystudios.singleton.Player;
import com.olahackerearth.pratik.olaplaystudios.utility.Constant;
import com.olahackerearth.pratik.olaplaystudios.singleton.PlaybackSingleton;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import static android.media.CamcorderProfile.get;

public class PlayBackService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {

    private String status;
    private SongDBModel song;

    public PlayBackService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    PlaybackSingleton getPlayback() {
        return PlaybackSingleton.getPlaybackInstance(getApplicationContext());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent !=null){

            song = intent.getParcelableExtra(Constant.INTENT_PASSING_SINGLE_SONG);

            MediaPlayer player = getPlayback().mediaPlayer;


            Intent playIntent = new Intent(Constant.ACTION_PLAYBACK_STARTED);
            playIntent.putExtra(Constant.INTENT_PASSING_SINGLE_SONG, song);
            sendBroadcast(playIntent);


            player.setOnPreparedListener(this);
            player.setOnCompletionListener(this);

            playSong(song, player);

        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        getPlayback().playbackController.start();
        History history = new History(
                Constant.DATABASE_CONSTANT_HISTORY_PLAY,
                song.getId(),
                new Date().getTime()
        );
        DBHelper.getDBHelper(getApplicationContext()).addHistory(history);
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        SongDBModel songDBModel = Player.nextSong();
        playSong(songDBModel, mediaPlayer);
    }

    public void playSong(SongDBModel song , MediaPlayer player){
        try {
            if(player != null){ player.reset(); }
            if(song.getDownloadStatus().equals(Constant.CONSTANT_SONG_DOWNLOAD_STATUS_DOWNLOADED)){
                File dir = new File(Environment.getExternalStorageDirectory().getPath() + File.separator + "OlaPlayStudios");
                File file = new File(dir.getPath() + File.separator + song.getSong()+".mp3");
                Uri uri = Uri.fromFile(file);
                Log.e("playback service ", " "+uri);
                player.setDataSource(getApplicationContext(), uri);
            } else{
                player.setDataSource(getApplicationContext(), Uri.parse(song.getSongFullUrl()).buildUpon().build());
            }
            player.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
