package com.olahackerearth.pratik.olaplaystudios.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.olahackerearth.pratik.olaplaystudios.database.DBHelper;
import com.olahackerearth.pratik.olaplaystudios.model.SongDBModel;
import com.olahackerearth.pratik.olaplaystudios.singleton.Player;
import com.olahackerearth.pratik.olaplaystudios.utility.Constant;
import com.olahackerearth.pratik.olaplaystudios.singleton.PlaybackSingleton;
import com.olahackerearth.pratik.olaplaystudios.utility.PlaybackController;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemException;

import static android.media.CamcorderProfile.get;
import static android.media.MediaPlayer.MEDIA_ERROR_IO;
import static android.media.MediaPlayer.MEDIA_ERROR_MALFORMED;
import static android.media.MediaPlayer.MEDIA_ERROR_SERVER_DIED;
import static android.media.MediaPlayer.MEDIA_ERROR_TIMED_OUT;
import static android.media.MediaPlayer.MEDIA_ERROR_UNKNOWN;
import static android.media.MediaPlayer.MEDIA_ERROR_UNSUPPORTED;

public class PlayBackService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, AudioManager.OnAudioFocusChangeListener, MediaPlayer.OnErrorListener {

    private AudioAttributes mPlaybackAttributes;
    private AudioFocusRequest mFocusRequest;
    private Handler mMyHandler;

    public PlayBackService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    PlaybackSingleton getPlayback() {
        return PlaybackSingleton.getPlaybackInstance(getApplicationContext());
    }

    AudioManager mAudioManager;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent !=null){

            if(intent.getAction() != null && intent.getAction().equals(Constant.ACTION_PLAYBACK_REQUESTED)){
                mMyHandler = new Handler();

                getPlayback().mediaPlayer.setOnPreparedListener(this);


                getPlayback().mediaPlayer.setOnCompletionListener(this);
                getPlayback().mediaPlayer.setOnErrorListener(this);

                Intent playIntent = new Intent(Constant.ACTION_PLAYBACK_REQUESTED);
                playIntent.putExtra(Constant.INTENT_PASSING_SINGLE_SONG, Player.getPlayerInstance(getApplicationContext()).currentSong);
                sendBroadcast(playIntent);

                int res;

                // initialization of the audio attributes and focus request
                mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mPlaybackAttributes = new AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                            .build();
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    mFocusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                            .setAudioAttributes(mPlaybackAttributes)
                            .setAcceptsDelayedFocusGain(true)
                            .setWillPauseWhenDucked(true)
                            .setOnAudioFocusChangeListener(this, mMyHandler)
                            .build();
                    res = mAudioManager.requestAudioFocus(mFocusRequest);
                } else{
                    res = mAudioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);

                }

                if(res == AudioManager.AUDIOFOCUS_REQUEST_GRANTED){
//                    printToast("Request is Granted");
                    playSong();
                } else if(res == AudioManager.AUDIOFOCUS_REQUEST_FAILED){
                    printToast("Request is Failed");
                } else{
                    printToast("Request is Delayed");
                }
            }

        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {

        getPlayback().playbackController.start();
//        Toast.makeText(getApplicationContext()," music is active", Toast.LENGTH_SHORT).show();

        // Sending Broadcast for ready to playback
        Intent readyToPlayIntent = new Intent(Constant.ACTION_READY_FOR_PLAYBACK);
        sendBroadcast(readyToPlayIntent);
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {

        Log.e("onCompletion", "called");
        Player.getPlayerInstance(getApplicationContext()).nextSong();
    }


    public void playSong(){
        try {
            SongDBModel song = Player.getPlayerInstance(getApplicationContext()).currentSong;
            MediaPlayer player = getPlayback().mediaPlayer;
            if(player != null){ player.reset(); }
            if(song.getDownloadStatus().equals(Constant.CONSTANT_SONG_DOWNLOAD_STATUS_DOWNLOADED)){
                File dir = new File(Environment.getExternalStorageDirectory().getPath() + File.separator + "OlaPlayStudios");
                File file = new File(dir.getPath() + File.separator + song.getSong()+".mp3");
                if (file.exists()){
                    Uri uri = Uri.fromFile(file);
//                      Log.e("playback service ", " "+uri);
                    try {
                        player.setDataSource(getApplicationContext(), uri);

                    } catch (Exception io){
                        io.printStackTrace();
                    }
                } else{
                    printToast("Fill not exist");
                    song.setDownloadStatus(Constant.CONSTANT_SONG_DOWNLOAD_STATUS_NOT_DOWNLOADED);
                    DBHelper.getDBHelper(getApplicationContext()).updateSong(song);
                }

            } else{
                if(!isNetworkAvailable()){
                    printToast("Network Error");
                }else{

                }
                printLog("URI", Uri.parse(song.getSongFullUrl()).buildUpon().build()+" ");
                printLog("Normal URI", song.getSongFullUrl()+" ");
                player.setDataSource(getApplicationContext(), Uri.parse(song.getSongFullUrl()).buildUpon().build());
            }
            player.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
//        printToast("In Audio focus change");
        if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT){
            printToast("In Audio focus Transient");
            getPlayback().playbackController.pause();
        } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
            getPlayback().playbackController.pause();
            printToast("In Audio focus Loss");
        } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
            getPlayback().playbackController.start();
//            printToast("In Audio focus Gain");
        } else{
            if(new PlaybackController(getApplicationContext()).isPlaying()){

            }
//            getPlayback().playbackController.start();
//            printToast("In Audio focus Else");
        }
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {

        if(i == MEDIA_ERROR_UNKNOWN){
            printLog("onError MediaPlay", "Media error unknown");
        } else if(i == MEDIA_ERROR_SERVER_DIED){
            printLog("onError MediaPlay", "Media error server died");
        }

        switch (i1){
            case MEDIA_ERROR_IO:
                printLog("onError MediaPlay", "Media error IO");
                break;
            case MEDIA_ERROR_MALFORMED:
                printLog("onError MediaPlay", "MEDIA_ERROR_MALFORMED");
                break;
            case MEDIA_ERROR_UNSUPPORTED:
                printLog("onError MediaPlay", "MEDIA_ERROR_UNSUPPORTED");
                break;
            case MEDIA_ERROR_TIMED_OUT:
                printLog("onError MediaPlay", "MEDIA_ERROR_TIMED_OUT");
                break;
            default:
                printLog("onError MediaPlay", "MEDIA_ERROR_SYSTEM");
                break;
        }
        return true;
    }

    private void printToast(String message){
        Toast.makeText(getApplicationContext(),message, Toast.LENGTH_SHORT).show();
    }
    private void printLog(String label, String message){
        Log.e(label, message);
    }
}
