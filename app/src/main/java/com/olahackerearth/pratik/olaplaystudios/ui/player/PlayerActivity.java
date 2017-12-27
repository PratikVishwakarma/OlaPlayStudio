package com.olahackerearth.pratik.olaplaystudios.ui.player;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.olahackerearth.pratik.olaplaystudios.R;
import com.olahackerearth.pratik.olaplaystudios.model.SongDBModel;
import com.olahackerearth.pratik.olaplaystudios.service.PlayBackService;
import com.olahackerearth.pratik.olaplaystudios.singleton.PlaybackSingleton;
import com.olahackerearth.pratik.olaplaystudios.singleton.Player;
import com.olahackerearth.pratik.olaplaystudios.ui.HomeActivity;
import com.olahackerearth.pratik.olaplaystudios.utility.Constant;
import com.olahackerearth.pratik.olaplaystudios.utility.PlaybackController;
import com.squareup.picasso.Picasso;

public class PlayerActivity extends AppCompatActivity {

    TextView playingSong, playingArtist;
    ImageView playingSongImage, playingButton;
    SeekBar seekBar;
    PlaybackController playbackController;
    private MyPlayBackReciever myPlaybackReceiver;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        playbackController = PlaybackSingleton.getPlaybackInstance(this).playbackController;

        handler = new Handler();
//        myPlaybackReceiver = new MyPlayBackReciever();
//        registerReceiver(myPlaybackReceiver, new IntentFilter(Constant.ACTION_PLAYBACK_REQUESTED));

        initializeScreen();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
//        unregisterReceiver(myPlaybackReceiver);
    }

    public void setView(){
        if(handler != null){
            handler.removeCallbacks(runnable);
        }

        SongDBModel currentSong = Player.getPlayerInstance(getApplicationContext()).currentSong;

        if(currentSong != null) {
            playingSong.setText(currentSong.getSong());
            playingArtist.setText(currentSong.getArtists());

            Picasso.with(getApplicationContext()).
                    load(currentSong.getCoverImageFullUlr()).
                    placeholder(R.drawable.placeholder_song).
                    error(R.drawable.placeholder_song).
                    into(playingSongImage);
            showSeekBarNow();
            if (playbackController.isPlaying()) {
                playingButton.setImageResource(R.drawable.ic_pause_song);
            } else {
                playingButton.setImageResource(R.drawable.ic_play_song);
            }
            /**
             * To handel the seekBar changed Listener but not working properly
             */
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    if(b){
                        setSongPosition(i);
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
        }
    }

    public void initializeScreen(){
        playingSong = findViewById(R.id.player_textView_playing_song);
        playingArtist = findViewById(R.id.player_textView_playing_song_artist);
        playingSongImage =  findViewById(R.id.player_imageView_playing_song_image);
        playingButton = findViewById(R.id.player_imageView_playing);
        seekBar = findViewById(R.id.player_seekBar);
//        seekBar.setVisibility(View.GONE);
        setView();
    }

    /**
     * To handel the seekBar but not working properly
     */

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                int i = playbackController.getCurrentPosition() *100;
                int i1 = i  / playbackController.getDuration();
                seekBar.setProgress(i1);
            } catch (Exception ex) {

            }
            showSeekBar();
        }
    };

    public void showSeekBar(){
        handler.postDelayed(runnable, 1000);
    }

    public void showSeekBarNow(){
        handler.post(runnable);
    }

    public void setSongPosition(int i){
        int i1 = i * playbackController.getDuration();
        playbackController.seekTo(i1 / 100);
    }


    /**
     * Handling all Clicks on player
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void onClick(View v){

        switch (v.getId()){
            case R.id.player_imageView_playing:
                if(playbackController.isPlaying()){
                    playingButton.setImageResource(R.drawable.ic_play_song);
                    playbackController.pause();
                } else{
                    playingButton.setImageResource(R.drawable.ic_pause_song);
                    playbackController.start();
                }
                break;
            case R.id.player_imageView_next:
                Player.getPlayerInstance(getApplicationContext()).nextSong();
                setView();
                break;
            case R.id.player_imageView_previous:
                Player.getPlayerInstance(getApplicationContext()).previousSong();
                setView();
                break;
            case R.id.player_imageView_fast_backward:
                playbackController.seekTo(playbackController.getCurrentPosition()-10000);
                break;
            case R.id.player_imageView_fast_forward:
                playbackController.seekTo(playbackController.getCurrentPosition()+10000);
                break;
        }
    }


    public class MyPlayBackReciever extends BroadcastReceiver{

        MyPlayBackReciever(){

        }
        @Override
        public void onReceive(Context context, Intent intent) {
            setView();
        }
    }

    public class MyReadyForPlayBackReceiver extends BroadcastReceiver{

        MyReadyForPlayBackReceiver(){

        }
        @Override
        public void onReceive(Context context, Intent intent) {
            setView();
        }
    }
}
