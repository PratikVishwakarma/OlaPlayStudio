package com.olahackerearth.pratik.olaplaystudios.ui;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.olahackerearth.pratik.olaplaystudios.R;
import com.olahackerearth.pratik.olaplaystudios.database.DBContract;
import com.olahackerearth.pratik.olaplaystudios.database.DBHelper;
import com.olahackerearth.pratik.olaplaystudios.model.SongDBModel;
import com.olahackerearth.pratik.olaplaystudios.singleton.Player;
import com.olahackerearth.pratik.olaplaystudios.ui.player.PlayerActivity;
import com.olahackerearth.pratik.olaplaystudios.utility.Constant;
import com.olahackerearth.pratik.olaplaystudios.utility.PlaybackController;
import com.olahackerearth.pratik.olaplaystudios.singleton.PlaybackSingleton;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;
    private TabLayout tabLayout;

    private TextView playingSong, playingArtist;
    private ImageView playingSongButton, playingSongCoverImage;
    private ConstraintLayout playingContainer;

    private List<SongDBModel> songList = new ArrayList<>();
    private List<SongDBModel> songDBList = new ArrayList<>();
    public List<SongDBModel> songListMain = songDBList;

    DBHelper dbHelper;

    final int STORAGE_PERMISSION_REQUEST = 2;

    MyReceiver myReceiver;
    PermissionRequestReceiver permissionRequestReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        dbHelper = DBHelper.getDBHelper(getApplicationContext());
        initializeScreen();
        fetchSongsFromDB();
        callLoadDataTask();
        myReceiver = new MyReceiver();
        registerReceiver(myReceiver, new IntentFilter(Constant.ACTION_PLAYBACK_REQUESTED));

        permissionRequestReceiver = new PermissionRequestReceiver();
        registerReceiver(permissionRequestReceiver, new IntentFilter(Constant.ACTION_PERMISSION_REQUEST));

    }

    /**
     * Handle all click on Home activity or the small player on the bottom
     */

    @Override
    public void onClick(View view) {
        PlaybackController controller = PlaybackSingleton.getPlaybackInstance(this).playbackController;
        switch (view.getId()){
            case R.id.home_constraintLayout_small_player:
                callPlayer();
                break;
            case R.id.home_imageView_playing_song_image:
                callPlayer();
                break;
            case R.id.home_textView_playing_song:
                callPlayer();
                break;
            case R.id.home_textView_playing_song_artist:
                callPlayer();
                break;
            case R.id.home_imageView_playing:
                if(controller.isPlaying()){
                    playingSongButton.setImageResource(R.drawable.ic_play_song);
                    controller.pause();
                } else{
                    playingSongButton.setImageResource(R.drawable.ic_pause_song);
                    controller.start();
                }
                break;
            case R.id.home_imageView_playing_next:
                Player.getPlayerInstance(getApplicationContext()).nextSong();
                break;
        }
    }

    /**
     * *
     * calling player activity
     * */
    public void callPlayer(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            startActivity(new Intent(getApplicationContext(), PlayerActivity.class),
                    ActivityOptionsCompat.makeSceneTransitionAnimation(HomeActivity.this,
                            new Pair<View,String>(playingSong, playingSong.getTransitionName()),
                            new Pair<View,String>(playingSongCoverImage, playingSongCoverImage.getTransitionName()),
                            new Pair<View,String>(playingArtist, playingArtist.getTransitionName()),
                            new Pair<View,String>(playingSongButton, playingSongButton.getTransitionName())
                    ).toBundle());
        }else{
            startActivity(new Intent(getApplicationContext(), PlayerActivity.class));
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        AllSongsFragment all, downloaded, liked;
        HistoryFragment history;

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return getFragmentAt(position);
        }

        private Fragment getFragmentAt(int position) {
            Fragment fragment = null;
            switch(position) {
                case 0:
                    fragment = all=all==null?AllSongsFragment.newInstance(Constant.CONSTANT_SONG_ALL_SONG, songListMain):all;
                    break;
                case 1:
                    fragment = downloaded=downloaded==null?AllSongsFragment.newInstance(Constant.CONSTANT_SONG_DOWNLOADED_SONG, songListMain):downloaded;
                    break;
                case 2:
                    fragment = liked=liked==null?AllSongsFragment.newInstance(Constant.CONSTANT_SONG_FAVORITE_SONG, songListMain):liked;
                    break;
                case 3:
                    fragment = history=history==null?HistoryFragment.newInstance():history;
                    break;
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "All Songs";
                case 1:
                    return "Download";
                case 2:
                    return "Favorite";
                case 3:
                    return "History";
            }
            return null;
        }
    }

    /**
     * Basic Initializing function
     */
    public void initializeScreen() {

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.home_container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        playingContainer =  findViewById(R.id.home_constraintLayout_small_player);
        playingSongCoverImage = findViewById(R.id.home_imageView_playing_song_image);
        playingSong = findViewById(R.id.home_textView_playing_song);
        playingArtist = findViewById(R.id.home_textView_playing_song_artist);
        playingSongButton = findViewById(R.id.home_imageView_playing);

        tabLayout = (TabLayout) findViewById(R.id.home_tabs);
        tabLayout.setupWithViewPager(mViewPager);
//        setupTabIcons();
    }

    private void setupTabIcons() {
//        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
//        tabLayout.getTabAt(0).setIcon(tabIcons[1]);
//        tabLayout.getTabAt(0).setIcon(tabIcons[2]);
    }

    /**
     * unregister the Receiver
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myReceiver);
        unregisterReceiver(permissionRequestReceiver);
    }

    /**
     * Calling Method to fetch the data from URL
     */
    public void callLoadDataTask(){
        URL url = null;
        try{
            url = new URL(builtUri().toString());
            new NLoadDataFromHTTPTask().execute(url);
        }catch (MalformedURLException e){
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * uri builder for the song data url
     */
    public Uri builtUri(){
        return  Uri.parse(Constant.URL).buildUpon().build();
    }

    /**
     * @param inputStream the data in string format
     */
    public void parseJsonFromString(String inputStream) throws JSONException {
        printLog("JSON ", "Parsing start ");
        JSONArray mainJsonArray = new JSONArray(inputStream);
//        printLog("JSONData", mainJsonArray.toString().replace("\\",""));
        parseSongDetailFromJson(mainJsonArray);
    }

    /**
     * json array of data
     * converting JSONArray into Song object
     */
    public void parseSongDetailFromJson(JSONArray mainJsonArray) throws JSONException {
        for (int i = 0; i < mainJsonArray.length(); i++) {
            JSONObject explrObject = mainJsonArray.getJSONObject(i);
            SongDBModel song = new SongDBModel(
                    explrObject.get(SongDBModel.COLUMN_SONG).toString(),
                    explrObject.get(SongDBModel.COLUMN_URL).toString(),
                    explrObject.get(SongDBModel.COLUMN_ARTISTS).toString(),
                    explrObject.get(SongDBModel.COLUMN_COVER_IMAGE).toString());
            songList.add(song);
        }

//        /**/

        for(SongDBModel song: songList) {
            for (SongDBModel songDB : songDBList) {
                if(song.getUrl().equals(songDB.getUrl())){
                    song.exists = true;
                    break;
                }
            }
        }


//        //
        if (songList.size() != 0) {
            new FetchSongFullUrl().execute(songList);
        } else {

        }
//        songListMain = new ArrayList<Song>(songList);
//        addSongs(songDBList);
//        CreateDynamicButton();
//        mAdapter.notifyDataSetChanged();
    }

    /**
     * UrlConnection handler
     */
    public String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = httpURLConnection.getInputStream();
            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        }finally {
            httpURLConnection.disconnect();
        }
    }

    /**
     * AsyncTask to load the song data from the server taking URL as input and return String as output
     */
    public class NLoadDataFromHTTPTask extends AsyncTask<URL, Void, String> {
        @Override
        protected String doInBackground(URL... params) {
            URL Url = params[0];
            printLog("http ", "AsyTsk srt : "+Url.toString());
            String postUrlResult = null;
            try{
                postUrlResult = getResponseFromHttpUrl(Url);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return postUrlResult;
        }

        @Override
        protected void onPostExecute(String s) {
            printLog("http ", "AsyncTask postEx : ");
            if(s != null && !s.equals("")){
                //Log.e("http ", s);
                try {
                    parseJsonFromString(s);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else{
                printLog("http ", "blank ");
            }
            super.onPostExecute(s);
        }
    }

    /**
     * BroadCastReceiver to handle the current music play and pause
     */
    public class MyReceiver extends BroadcastReceiver {
        public MyReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                SongDBModel songDBModel = intent.getParcelableExtra(Constant.INTENT_PASSING_SINGLE_SONG);
                Picasso.with(getApplicationContext()).
                        load(songDBModel.getCoverImageFullUlr()).
                        placeholder(R.drawable.ic_play_button).
                        error(R.drawable.ic_play_button).
                        into(playingSongCoverImage);

                playingSong.setText(songDBModel.getSong());
                playingArtist.setText(songDBModel.getArtists());
                playingSongButton.setImageResource(R.drawable.ic_pause_song);
            }
        }
    }

    public class PermissionRequestReceiver extends BroadcastReceiver {
        public PermissionRequestReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                    printToast("Permission granted ");
                } else {
                    ActivityCompat.requestPermissions(HomeActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_REQUEST);
                }
            } else {
                printToast("on receive with null intent ");
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == STORAGE_PERMISSION_REQUEST) {
            if(grantResults.length>0) {
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // task
                } else {
                    ActivityCompat.requestPermissions(HomeActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_REQUEST);
                }
            }
        }
    }

    /**
     * Add the fetched songs into the database
     */
    public void addSongs(List<SongDBModel> songList){
        for(SongDBModel song : songList){
            song.id = dbHelper.addSong(song);
        }
        dataFetchedAndInserted();
    }

    /**
     * Check and refresh the fragment after fetching from internet is done
     */
    public void dataFetchedAndInserted() {
        if(mSectionsPagerAdapter!=null) {
            if(mSectionsPagerAdapter.all!=null)
                mSectionsPagerAdapter.all.refresh();
            if(mSectionsPagerAdapter.downloaded!=null)
                mSectionsPagerAdapter.downloaded.refresh();
            if(mSectionsPagerAdapter.liked!=null)
                mSectionsPagerAdapter.liked.refresh();
        }
    }
    /**
     * Fetching full image url from short Url
     */
//    public class FetchImageFullUrl extends AsyncTask<List<SongDBModel>, Void, Void> {
//        @Override
//        protected Void doInBackground(List<SongDBModel>[] lists) {
//            if(lists != null || lists.length != 0){
//                for(SongDBModel sSong: lists[0]){
//                    if(!sSong.exists) {
////                    if(true) {
//                        final URL url;
//                        try {
//                            url = new URL(Uri.parse(sSong.getCoverImage()).buildUpon().build().toString());
//                            final HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
//                            urlConnection.setInstanceFollowRedirects(false);
//
//                            final String location = urlConnection.getHeaderField("location");
//                            sSong.setCoverImageFullUlr(location);
//
//
////                            allSongUrl.add(location);
//                        } catch (MalformedURLException e) {
//                            e.printStackTrace();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    } else {
////                        allSongUrl.add(sSong.getCoverImage());
//                    }
//                }
//            } else{
//                return null;
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
//            new FetchSongFullUrl().execute(songList);
//        }
//    }

    /**
     * Fetching full song url from short Url to download it
     */
    public class FetchSongFullUrl extends AsyncTask<List<SongDBModel>, Void, Void> {
        @Override
        protected Void doInBackground(List<SongDBModel>... lists) {
            for(SongDBModel sSong: lists[0]){
                if(!sSong.exists) {
//                if(true) {
                    final URL songUrl, imageUrl;
                    try {

                        //  get Song Full url
                        songUrl = new URL(Uri.parse(sSong.getUrl()).buildUpon().build().toString());
                        final HttpURLConnection songUrlConnection = (HttpURLConnection) songUrl.openConnection();
                        songUrlConnection.setInstanceFollowRedirects(false);
                        final String songLocation = songUrlConnection.getHeaderField("location");
                        sSong.setSongFullUrl(songLocation);

                        //  get Image Full url
                        imageUrl = new URL(Uri.parse(sSong.getCoverImage()).buildUpon().build().toString());
                        final HttpURLConnection imageUrlConnection = (HttpURLConnection) imageUrl.openConnection();
                        imageUrlConnection.setInstanceFollowRedirects(false);
                        final String imageLocation = imageUrlConnection.getHeaderField("location");
                        sSong.setCoverImageFullUlr(imageLocation);


//                        allSongUrl.add(location);
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
//                    allSongUrl.add(sSong.getUrl());
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            new FetchSongSize().execute(songList);
        }
    }

    /**
     * Fetching size of song from Url to download it
     */
    public class FetchSongSize extends AsyncTask<List<SongDBModel>, Void, Void> {
        @Override
        protected Void doInBackground(List<SongDBModel>[] lists) {
            for(SongDBModel sSong : lists[0]){
                if(!sSong.exists) {
                    URL url = null;
                    try {
                        url = new URL(sSong.getSongFullUrl());
                        URLConnection urlConnection = url.openConnection();
                        urlConnection.connect();
                        printLog("Song url",  " "+sSong.getSongFullUrl());
                        int file_size = urlConnection.getContentLength();
                        printLog("Song size ",  " "+file_size);
                        sSong.setSize(file_size);
//                    allSongSize.add(file_size);

                        MediaPlayer mp;
                        mp = new MediaPlayer();
                        mp.setDataSource(getApplicationContext(), Uri.parse(sSong.getSongFullUrl()));
                        mp.prepare();
                        printLog("Song duration ",  " "+mp.getDuration());


                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else {
//                    allSongUrl.add(sSong.getUrl());
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            addSongOtherDetails();
        }
    }

    /**
     * add the full song and image url into song objects
     */
//    public void addSongOtherDetails(List<String> imageUrlList, List<String> songUrlList, List<Integer> songSizes){
//        ArrayList<SongDBModel> songListNotInDB= new ArrayList<SongDBModel>();
//        for(int i = 0; i < imageUrlList.size(); i++){
//            Song song = songList.get(i);
//            if(song.exists) continue;
//            boolean b = checkPreDownlodedSongs(song, songUrlList.get(i), songSizes.get(i));
//            SongDBModel songDBModel;
//            if(b){
//                songDBModel = new SongDBModel(
//                        song.getSong(),
//                        song.getUrl(),
//                        song.getArtists(),
//                        song.getCoverImage(),
//                        Constant.CONSTANT_SONG_DOWNLOAD_STATUS_DOWNLOADED,
//                        imageUrlList.get(i),
//                        Constant.CONSTANT_SONG_FAVORITE_STATUS_NOT,
//                        songUrlList.get(i),
//                        songSizes.get(i)
//                );
//            } else{
//                songDBModel = new SongDBModel(
//                        song.getSong(),
//                        song.getUrl(),
//                        song.getArtists(),
//                        song.getCoverImage(),
//                        Constant.CONSTANT_SONG_DOWNLOAD_STATUS_NOT_DOWNLOADED,
//                        imageUrlList.get(i),
//                        Constant.CONSTANT_SONG_FAVORITE_STATUS_NOT,
//                        songUrlList.get(i),
//                        songSizes.get(i)
//                );
//            }
//            songDBList.add(songDBModel);
//            songListNotInDB.add(songDBModel);
//        }
//        addSongs(songListNotInDB);
//
////        mAdapter.notifyDataSetChanged();
//    }

    public void addSongOtherDetails(){
        ArrayList<SongDBModel> songListNotInDB= new ArrayList<SongDBModel>();
        for(SongDBModel song : songList){
            if(song.exists) continue;
            boolean b = checkPreDownlodedSongs(song, song.getSongFullUrl(), song.getSize());
            SongDBModel songDBModel;
            if(b){
                songDBModel = new SongDBModel(
                        song.getSong(),
                        song.getUrl(),
                        song.getArtists(),
                        song.getCoverImage(),
                        Constant.CONSTANT_SONG_DOWNLOAD_STATUS_DOWNLOADED,
                        song.getCoverImageFullUlr(),
                        Constant.CONSTANT_SONG_FAVORITE_STATUS_NOT,
                        song.getSongFullUrl(),
                        song.getSize()
                );
            } else{
                songDBModel = new SongDBModel(
                        song.getSong(),
                        song.getUrl(),
                        song.getArtists(),
                        song.getCoverImage(),
                        Constant.CONSTANT_SONG_DOWNLOAD_STATUS_NOT_DOWNLOADED,
                        song.getCoverImageFullUlr(),
                        Constant.CONSTANT_SONG_FAVORITE_STATUS_NOT,
                        song.getSongFullUrl(),
                        song.getSize()
                );
            }
            songDBList.add(songDBModel);
            songListNotInDB.add(songDBModel);
        }
        addSongs(songListNotInDB);

//        mAdapter.notifyDataSetChanged();
    }
    /**
     * Fetching songs form database and load it into List
     */
    public void fetchSongsFromDB(){
        String selectQuery = "SELECT  * FROM " + DBContract.Song.TABLE_NAME;
        SQLiteDatabase dbw = dbHelper.getWritableDatabase();
        Cursor cursor = dbw.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
//        printLog("DB len ", cursor.getCount()+"");
        if (cursor.moveToFirst()) {
            do {
                SongDBModel songDBModel = new SongDBModel(
                        cursor.getString(DBContract.Song.COLUMN_INT_SONG),
                        cursor.getString(DBContract.Song.COLUMN_INT_SONG_URL),
                        cursor.getString(DBContract.Song.COLUMN_INT_ARTISTS),
                        cursor.getString(DBContract.Song.COLUMN_INT_COVER_IMAGE_SHORT_LENGTH),
                        cursor.getString(DBContract.Song.COLUMN_INT_DOWNLOAD_STATUS),
                        cursor.getString(DBContract.Song.COLUMN_INT_COVER_IMAGE_FULL_LENGTH),
                        cursor.getString(DBContract.Song.COLUMN_INT_FAVORITE_STATUS),
                        cursor.getString(DBContract.Song.COLUMN_INT_SONG_FULL_URL),
                        cursor.getInt(DBContract.Song.COLUMN_INT_SIZE)
                );
                songDBModel.id = cursor.getLong(DBContract.Song.COLUMN_INT_ID);
                songDBList.add(songDBModel);
                songListMain = songDBList;
            } while (cursor.moveToNext());
            cursor.close();
        }
        // Check the song is all ready exist in Storage
        dataFetchedAndInserted();
    }

    /**
     * For Print logE
     */
    public boolean checkPreDownlodedSongs(SongDBModel song, String songUrl, int songSize){
        boolean Status = false;
        try {
            URL url = new URL(songUrl);
            File dir = new File(Environment.getExternalStorageDirectory().getPath() + File.separator + "OlaPlayStudios");
            if (!dir.exists()) dir.mkdir();
            File file = new File(dir.getPath() + File.separator + song.getSong()+".mp3");
            if (!file.exists()){
                printLog("File ", "Not Exist");
                Status = false;
            }else{
                long length = file.length();
                if(length == songSize){
                    printLog("File ", "Exist full length = "+length+" song size = "+songSize);
                    Status = true;
                }else{
                    printLog("File ", "Exist but incomplete  = "+length+" song size = "+songSize);
                    Status = false;
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return Status;
    }

    /**
     * For Print logE
     */
    public void printLog(String label, String message ){
        Log.e(label, message);
    }

    /**
     * For Print/Show Toast
     */
    public void printToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}
