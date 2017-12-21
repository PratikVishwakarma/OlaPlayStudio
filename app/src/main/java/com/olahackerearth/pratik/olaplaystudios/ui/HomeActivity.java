package com.olahackerearth.pratik.olaplaystudios.ui;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.olahackerearth.pratik.olaplaystudios.MainActivity;
import com.olahackerearth.pratik.olaplaystudios.R;
import com.olahackerearth.pratik.olaplaystudios.adapter.SongAdapter;
import com.olahackerearth.pratik.olaplaystudios.database.DBContract;
import com.olahackerearth.pratik.olaplaystudios.database.DBHelper;
import com.olahackerearth.pratik.olaplaystudios.model.Song;
import com.olahackerearth.pratik.olaplaystudios.model.SongDBModel;
import com.olahackerearth.pratik.olaplaystudios.ui.player.PlayerActivity;
import com.olahackerearth.pratik.olaplaystudios.utility.Constant;
import com.olahackerearth.pratik.olaplaystudios.utility.PlaybackController;
import com.olahackerearth.pratik.olaplaystudios.singleton.PlaybackSingleton;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
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

    private List<SongDBModel> songDBList = new ArrayList<>();
    private List<Song> songList = new ArrayList<>();
    public List<SongDBModel> songListMain = songDBList;

    private List<String> imageFullUrlList= new ArrayList<>();

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
        registerReceiver(myReceiver, new IntentFilter(Constant.ACTION_PLAYBACK_STARTED));

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
//                case 0:
//                    return "Home";
//                case 1:
//                    return "Download";
//                case 2:
//                    return "Favorite";
//                case 3:
//                    return "History";
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
            Song post = new Song(explrObject.get(Song.COLUMN_SONG).toString(),
                    explrObject.get(Song.COLUMN_URL).toString(),
                    explrObject.get(Song.COLUMN_ARTISTS).toString(),
                    explrObject.get(Song.COLUMN_COVER_IMAGE).toString());
            songList.add(post);
        }

        /**/

        for(Song song: songList) {
            for (SongDBModel songDB : songDBList) {
                if(song.getUrl().equals(songDB.getUrl())){
                    song.exists=true;
                    break;
                }
            }
        }


        //
        if (songList.size() != 0) {
            new FetchImageFullUrl().execute(songList);
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
        setupTabIcons();
    }
    /**
     * Fetching full image url from short Url
     */
    public class FetchImageFullUrl extends AsyncTask<List<Song>, Void, List<String>> {
        @Override
        protected List<String> doInBackground(List<Song>[] lists) {
            List<String> allSongUrl = new ArrayList<String>();
            if(lists != null || lists.length != 0){
                for(Song sSong: lists[0]){
                    if(!sSong.exists) {
                        final URL url;
                        try {
                            url = new URL(Uri.parse(sSong.getCoverImage()).buildUpon().build().toString());
                            final HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                            urlConnection.setInstanceFollowRedirects(false);

                            final String location = urlConnection.getHeaderField("location");
                            allSongUrl.add(location);
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        allSongUrl.add(sSong.getCoverImage());
                    }
                }
                return allSongUrl;
            } else{
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<String> strings) {
            super.onPostExecute(strings);

            imageFullUrlList = strings;
            new FetchSongFullUrl().execute(songList);
            return;
        }
    }

    /**
     * Fetching full song url from short Url to download it
     */
    public class FetchSongFullUrl extends AsyncTask<List<Song>, Void, List<String>> {
        @Override
        protected List<String> doInBackground(List<Song>[] lists) {
            List<String> allSongUrl = new ArrayList<String>();
            for(Song sSong: lists[0]){
                if(!sSong.exists) {
                    final URL url;
                    try {
                        url = new URL(Uri.parse(sSong.getUrl()).buildUpon().build().toString());
                        final HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                        urlConnection.setInstanceFollowRedirects(false);

                        final String location = urlConnection.getHeaderField("location");
                        allSongUrl.add(location);
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    allSongUrl.add(sSong.getUrl());
                }
            }
            return allSongUrl;
        }

        @Override
        protected void onPostExecute(List<String> strings) {
            super.onPostExecute(strings);
            addImageFullUrl(imageFullUrlList, strings);
            return;
        }
    }

    /**
     * add the full song and image url into song objects
     */
    public void addImageFullUrl(List<String> imageUrlList, List<String> songUrlList){
        ArrayList<SongDBModel> songListNotInDB= new ArrayList<SongDBModel>();
        for(int i = 0; i < imageUrlList.size(); i++){
            Song song = songList.get(i);
            if(song.exists) continue;
            SongDBModel songDBModel = new SongDBModel(
                    song.getSong(),
                    song.getUrl(),
                    song.getArtists(),
                    song.getCoverImage(),
                    Constant.CONSTANT_SONG_DOWNLOAD_STATUS_NOT_DOWNLOADED,
                    imageUrlList.get(i),
                    Constant.CONSTANT_SONG_FAVORITE_STATUS_NOT,
                    songUrlList.get(i)
            );
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
                        cursor.getString(DBContract.Song.COLUMN_INT_SONG_FULL_URL)
                );
                songDBModel.id = cursor.getLong(DBContract.Song.COLUMN_INT_ID);
                songDBList.add(songDBModel);
                songListMain=songDBList;
            } while (cursor.moveToNext());
            cursor.close();
        }
        dataFetchedAndInserted();
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
