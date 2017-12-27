package com.olahackerearth.pratik.olaplaystudios.utility;

/**
 * Created by pratik on 17/12/17.
 */

public class Constant {

    public static final String URL = "http://starlord.hackerearth.com/studio";
    public static final String SHARED_PREFRENCE_SONGS_DATA = "songsData";

    // async task
    // to fetch redirected url

    /*
 startActivity(Intent(applicationContext, PlayerActivity::class.java),
                                ActivityOptionsCompat.makeSceneTransitionAnimation(this@MainActivity,
                                        Pair(playPauseButton, playPauseButton?.transitionName),
                                        Pair(titleTextView, titleTextView?.transitionName),
                                        Pair(artistTextView, artistTextView?.transitionName),
                                        Pair(artImageView, artImageView?.transitionName),
                                        Pair(playerBottomBar, "card"),
                                        Pair(container, "container")
                                ).toBundle())
}
    ///
    /// */

    public static final String INTENT_PASSING_SINGLE_SONG  = "singleSong";
    public static final String INTENT_PASSING_SINGLE_SONG_POSITION  = "singleSongPosition";
    public static final String INTENT_PASSING_FULL_SONG_LIST  = "songList";
    public static final String INTENT_PASSING_SINGLE_SONG_URL  = "songUrl";
    public static final String INTENT_PASSING_SINGLE_SONG_NAME_URL  = "songNameUrl";
    public static final String INTENT_PASSING_SINGLE_SONG_STATUS = "currentSongStatus";
    public static final String INTENT_PASSING_PERMISSION_ASKING = "permissionAsking";

    public static final String CONSTANT_SINGLE_SONG_STATUS_PLAY = "play";
    public static final String CONSTANT_SINGLE_SONG_STATUS_PAUSE = "pause";
    public static final String CONSTANT_SINGLE_SONG_STATUS_RESUME = "resume";
    public static final String CONSTANT_SINGLE_REQUEST_FOR_PERMISSION = "permissionRequest";

    public static final String CONSTANT_SONG_DOWNLOAD_STATUS_DOWNLOADED = "downloaded";
    public static final String CONSTANT_SONG_DOWNLOAD_STATUS_NOT_DOWNLOADED = "notDownloaded";

    public static final String CONSTANT_SONG_ALL_SONG = "allSong";
    public static final String CONSTANT_SONG_DOWNLOADED_SONG = "downloadedSong";
    public static final String CONSTANT_SONG_FAVORITE_SONG = "favoriteSong";

    public static final String CONSTANT_SONG_FAVORITE_STATUS_NOT = "notFavorite";
    public static final String CONSTANT_SONG_FAVORITE_STATUS_YES = "favorite";

    public static final String ACTION_PLAYBACK_REQUESTED = "playbackRequested";
    public static final String ACTION_READY_FOR_PLAYBACK = "playbackReadyForPlayback";
    public static final String ACTION_PERMISSION_REQUEST = "permissionRequest";


    public static final String DATABASE_CONSTANT_HISTORY_TYPE_ADD_TO_FAVORITE = "addToFavorite";
    public static final String DATABASE_CONSTANT_HISTORY_TYPE_REMOVE_FROM_FAVORITE = "removeFromFavorite";
    public static final String DATABASE_CONSTANT_HISTORY_DOWNLOAD = "download";
    public static final String DATABASE_CONSTANT_HISTORY_PLAY = "play";


}
