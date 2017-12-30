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
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.olahackerearth.pratik.olaplaystudios.R;
import com.olahackerearth.pratik.olaplaystudios.model.SongDBModel;
import com.olahackerearth.pratik.olaplaystudios.service.PlayBackService;
import com.olahackerearth.pratik.olaplaystudios.singleton.PlaybackSingleton;
import com.olahackerearth.pratik.olaplaystudios.singleton.Player;
import com.olahackerearth.pratik.olaplaystudios.ui.HomeActivity;
import com.olahackerearth.pratik.olaplaystudios.utility.Constant;
import com.olahackerearth.pratik.olaplaystudios.utility.PlaybackController;
import com.olahackerearth.pratik.olaplaystudios.utility.SimpleGestureFilter;
import com.squareup.picasso.Picasso;

public class PlayerActivity extends AppCompatActivity implements SimpleGestureFilter.SimpleGestureListener {

    TextView playingSong, playingArtist, playingTiming;
    ImageView playingSongImage, playingButton, loopSong, loopList;
    SeekBar seekBar;
    PlaybackController playbackController;
    private MyPlayBackReciever myPlaybackReceiver;
    private MyReadyForPlayBackReceiver myReadyForPlayBackReceiver;
    private Handler handler;

    private SimpleGestureFilter detector;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        playbackController = PlaybackSingleton.getPlaybackInstance(this).playbackController;

        handler = new Handler();
        myPlaybackReceiver = new MyPlayBackReciever();
        registerReceiver(myPlaybackReceiver, new IntentFilter(Constant.ACTION_PLAYBACK_REQUESTED));
        myReadyForPlayBackReceiver = new MyReadyForPlayBackReceiver();
        registerReceiver(myReadyForPlayBackReceiver, new IntentFilter(Constant.ACTION_READY_FOR_PLAYBACK));

        // Detect touched area
        detector = new SimpleGestureFilter(this,this);

        initializeScreen();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myReadyForPlayBackReceiver);
        unregisterReceiver(myPlaybackReceiver);
    }

    public void setView(){

        SongDBModel currentSong = Player.getPlayerInstance(getApplicationContext()).currentSong;

        if(currentSong != null) {
            playingSong.setText(currentSong.getSong());
            playingArtist.setText(currentSong.getArtists());

            Picasso.with(getApplicationContext()).
                    load(currentSong.getCoverImageFullUlr()).
                    placeholder(R.drawable.placeholder_song).
                    error(R.drawable.placeholder_song).
                    into(playingSongImage);
        }
        setPlayPauseButton();
        setLoopList();
        setLoopSong();
        setSeekBarView();
    }

    public void initializeScreen(){
        playingSong = findViewById(R.id.player_textView_playing_song);
        playingArtist = findViewById(R.id.player_textView_playing_song_artist);
        playingTiming = findViewById(R.id.player_textView_song_time);
        playingSongImage =  findViewById(R.id.player_imageView_playing_song_image);
        playingButton = findViewById(R.id.player_imageView_playing);
        playingButton = findViewById(R.id.player_imageView_playing);
        loopList = findViewById(R.id.player_imageview_loop_list);
        loopSong= findViewById(R.id.player_imageview_loop_song);
        seekBar = findViewById(R.id.player_seekBar);
//        seekBar.setVisibility(View.GONE);
        setView();
    }

    public void setPlayPauseButton(){
        if (playbackController.isPlaying()) {
            playingButton.setImageResource(R.drawable.ic_pause_song);
        } else {
            playingButton.setImageResource(R.drawable.ic_play_current);
        }
    }

    public void setLoopSong(){
        if (Player.getPlayerInstance(getApplicationContext()).loopSong) {
            loopSong.setImageResource(R.drawable.ic_logo_single_hover);
        } else {
            loopSong.setImageResource(R.drawable.ic_logo_single);
        }
    }

    public void setLoopList(){
        if (Player.getPlayerInstance(getApplicationContext()).loopList) {
            loopList.setImageResource(R.drawable.ic_logo_list_hover);
        } else {
            loopList.setImageResource(R.drawable.ic_logo_list);
        }
    }

    public void setSeekBarView(){
        if(handler != null){
            handler.removeCallbacks(runnable);
        }
        setPlayPauseButton();

        /**
         * To handel the seekBar changed Listener but not working properly
         */
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(b){
                    setSongPosition(i);
                    playingTiming.setText(getSongPosition(i));
                }
                playingTiming.setText(getSongPosition(i));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        showSeekBarNow();
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
            } catch (Exception ex) {}
            showSeekBar();
        }
    };

    public void showSeekBar(){
        handler.postDelayed(runnable, 400);
    }

    public void showSeekBarNow(){
        handler.post(runnable);
    }

    public void setSongPosition(int i){
        int i1 = i * playbackController.getDuration();
        playbackController.seekTo(i1 / 2000);
    }
    public String getSongPosition(int i){
        String time = null;
        int i1 = i * playbackController.getDuration();
        long i2 = i1 / 100000;
//        Log.e("Time ", i2+"");
        if(i2 > 60){
            time = i2 / 60+":"+i2 % 60;
        }else{
            time = "0:"+i2;
        }
//        Log.e("Time ", time);
        return time;
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
//                setView();
                if(handler != null){
                    handler.removeCallbacks(runnable);
                }
                break;
            case R.id.player_imageView_previous:
                Player.getPlayerInstance(getApplicationContext()).previousSong();
//                setView();
                if(handler != null){
                    handler.removeCallbacks(runnable);
                }
                break;
            case R.id.player_imageView_fast_backward:
                playbackController.seekTo(playbackController.getCurrentPosition()-10000);
                break;
            case R.id.player_imageView_fast_forward:
                playbackController.seekTo(playbackController.getCurrentPosition()+10000);
                break;
            case R.id.player_imageview_loop_list:
                Player.getPlayerInstance(getApplicationContext()).toggleSongList();
                setLoopList();
                break;
            case R.id.player_imageview_loop_song:
                Player.getPlayerInstance(getApplicationContext()).toggleSingleSong();
                setLoopSong();
                break;
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent me){
        // Call onTouchEvent of SimpleGestureFilter class
        this.detector.onTouchEvent(me);
        return super.dispatchTouchEvent(me);
    }
    @Override
    public void onSwipe(int direction) {
        String str = "";

        switch (direction) {

            case SimpleGestureFilter.SWIPE_RIGHT : str = "Swipe Right";
                Player.getPlayerInstance(getApplicationContext()).nextSong();
//                setView();
                if(handler != null){
                    handler.removeCallbacks(runnable);
                }
                break;
            case SimpleGestureFilter.SWIPE_LEFT:
                str = "Swipe Left";
                Player.getPlayerInstance(getApplicationContext()).previousSong();
//                setView();
                if(handler != null){
                    handler.removeCallbacks(runnable);
                }
                break;
            case SimpleGestureFilter.SWIPE_DOWN :  str = "Swipe Down";
                break;
            case SimpleGestureFilter.SWIPE_UP :    str = "Swipe Up";
                break;

        }
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDoubleTap() {
        Toast.makeText(this, "Double Tap", Toast.LENGTH_SHORT).show();
    }


    public class MyPlayBackReciever extends BroadcastReceiver{

        MyPlayBackReciever(){
            initializeScreen();
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
            setSeekBarView();
        }
    }
}
