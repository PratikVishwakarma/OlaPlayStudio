package com.olahackerearth.pratik.olaplaystudios.ui.player;

import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.olahackerearth.pratik.olaplaystudios.R;
import com.olahackerearth.pratik.olaplaystudios.model.SongDBModel;
import com.olahackerearth.pratik.olaplaystudios.service.PlayBackService;
import com.olahackerearth.pratik.olaplaystudios.singleton.PlaybackSingleton;
import com.olahackerearth.pratik.olaplaystudios.singleton.Player;
import com.olahackerearth.pratik.olaplaystudios.utility.Constant;
import com.olahackerearth.pratik.olaplaystudios.utility.PlaybackController;
import com.squareup.picasso.Picasso;

public class PlayerActivity extends AppCompatActivity {

    TextView playingSong, playingArtist;
    ImageView playingSongImage, playingButton;
    SeekBar seekBar;
    SongDBModel currentSong ;
    PlaybackController playbackController;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        currentSong = Player.currentSong;
        playbackController = PlaybackSingleton.getPlaybackInstance(this).playbackController;
        initializeScreen();
    }
    public void setView(){
//        if(handler != null){
//            handler.removeCallbacks(runnable);
//        }
        if(currentSong != null) {
            playingSong.setText(currentSong.getSong());
            playingArtist.setText(currentSong.getArtists());

            Picasso.with(getApplicationContext()).
                    load(currentSong.getCoverImageFullUlr()).
                    placeholder(R.drawable.placeholder_song).
                    error(R.drawable.placeholder_song).
                    into(playingSongImage);
//            showSeekBarNow();
            if (playbackController.isPlaying()) {
                playingButton.setImageResource(R.drawable.ic_pause_song);
            } else {
                playingButton.setImageResource(R.drawable.ic_play_song);
            }
            /**
             * To handel the seekBar changed Listener but not working properly
             */
//            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//                @Override
//                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
//                    if(b){
//                        setSongPosition(i);
//                    }
//                }
//
//                @Override
//                public void onStartTrackingTouch(SeekBar seekBar) {
//
//                }
//
//                @Override
//                public void onStopTrackingTouch(SeekBar seekBar) {
//
//                }
//            });
        }
    }

    public void initializeScreen(){
        playingSong = findViewById(R.id.player_textView_playing_song);
        playingArtist = findViewById(R.id.player_textView_playing_song_artist);
        playingSongImage =  findViewById(R.id.player_imageView_playing_song_image);
        playingButton = findViewById(R.id.player_imageView_playing);
        seekBar = findViewById(R.id.player_seekBar);
        seekBar.setVisibility(View.GONE);
        setView();
    }

    /**
     * To handel the seekBar but not working properly
     */

//    Runnable runnable = new Runnable() {
//        @Override
//        public void run() {
//            try {
//                int i = playbackController.getCurrentPosition() *100;
//                int i1 = i  / playbackController.getDuration();
//                seekBar.setProgress(i1);
//            } catch (Exception ex) {
//
//            }
//            showSeekBar();
//        }
//    };
//
//    public void showSeekBar(){
//        handler.postDelayed(runnable, 1000);
//    }
//
//    public void showSeekBarNow(){
//        handler.post(runnable);
//    }
//
//    public void setSongPosition(int i){
//        int i1 = i * playbackController.getDuration();
//        playbackController.seekTo(i1 / 100);
//    }


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
                currentSong = Player.nextSong();
                playSong(currentSong);
                setView();
                break;

            case R.id.player_imageView_previous:
                currentSong = Player.previousSong();
                playSong(currentSong);
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

    /**
     * Play song by playback service
     */
    public void playSong(SongDBModel songDBModel) {
        Intent playIntent = new Intent(getApplicationContext(), PlayBackService.class);
        playIntent.putExtra(Constant.INTENT_PASSING_SINGLE_SONG, songDBModel);
        getApplicationContext().startService(playIntent);
    }
}
