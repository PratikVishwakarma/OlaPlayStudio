package com.olahackerearth.pratik.olaplaystudios.service;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.olahackerearth.pratik.olaplaystudios.R;
import com.olahackerearth.pratik.olaplaystudios.database.DBHelper;
import com.olahackerearth.pratik.olaplaystudios.model.History;
import com.olahackerearth.pratik.olaplaystudios.model.SongDBModel;
import com.olahackerearth.pratik.olaplaystudios.utility.Constant;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;

import static kotlin.io.ConstantsKt.DEFAULT_BUFFER_SIZE;

public class DownloadService extends Service {
    public static final int ID = 5;
    private DownloadSongFromUrl downloadSongFromUrl;

    public DownloadService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null ) {
            SongDBModel songDBModel = (SongDBModel) intent.getParcelableExtra(Constant.INTENT_PASSING_SINGLE_SONG);

            Toast.makeText(getApplicationContext(), "Downloading...", Toast.LENGTH_SHORT).show();
            downloadSongFromUrl = new DownloadSongFromUrl();
            downloadSongFromUrl.execute(songDBModel);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public class DownloadSongFromUrl extends AsyncTask<SongDBModel, String, SongDBModel>{

        NotificationManager managerCompat = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder updateNotification = new NotificationCompat.Builder(getApplicationContext(), Constant.DATABASE_CONSTANT_HISTORY_DOWNLOAD)
                .setSmallIcon(R.drawable.ic_download);
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            NotificationCompat.Builder preExecuteNotification = new NotificationCompat.Builder(getApplicationContext(), Constant.DATABASE_CONSTANT_HISTORY_DOWNLOAD)
                    .setSmallIcon(R.drawable.ic_download)
                    .setContentTitle("Song Download")
                    .setContentText("Download in Progress");

            startForeground(ID, preExecuteNotification.build());
            Log.e("song downloader", "Download starting...");
        }

        @Override
        protected SongDBModel doInBackground(SongDBModel... songDBModels) {
            int count;
            if(songDBModels != null){
                SongDBModel song = songDBModels[0];
                if(song!= null){
                    URL url;
                    try {
                        url = new URL(song.getSongFullUrl());
                        File dir = new File(Environment.getExternalStorageDirectory().getPath() + File.separator + "OlaPlayStudios");
                        if (!dir.exists()) dir.mkdir();
                        File file = new File(dir.getPath() + File.separator + song.getSong()+".mp3");
                        URLConnection connection = url.openConnection();
                        if (true){
                            try {
                                file.createNewFile();
                                FileOutputStream outputStream = new FileOutputStream(file);
                                byte data[] = new byte[1024];
                                long total = 0;
                                int lengthOfFile = connection.getContentLength();
                                InputStream input = connection.getInputStream();

//                                Copy the data into sdCard
                                while ((count = input.read(data)) != -1) {
                                    total += count;
//                                    Log.e("song downloader", "in while "+ total);
                                    // publishing the progress....
                                    // After this onProgressUpdate will be called
                                    publishProgress(""+(int)((total*100)/lengthOfFile));

                                    // writing data to file
                                    outputStream.write(data, 0, count);
                                }

//                                copyWithProgress(connection.getInputStream(), outputStream, new byte[DEFAULT_BUFFER_SIZE]);
                                song.setDownloadStatus(Constant.CONSTANT_SONG_DOWNLOAD_STATUS_DOWNLOADED);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return song;
                }else{
                }
            }
            else{
            }
            return null;
        }

        @Override
        protected void onPostExecute(SongDBModel songDBModel) {
            super.onPostExecute(songDBModel);

            printLog("onPostExec", " download complete");
            DBHelper.getDBHelper(getApplicationContext()).updateSong(songDBModel);
            if(songDBModel != null) {
                History history = new History(Constant.DATABASE_CONSTANT_HISTORY_DOWNLOAD, songDBModel.id, (new Date()).getTime());
                DBHelper.getDBHelper(getApplicationContext()).addHistory(history);
            }else{
                printLog("onPostExec", " download complete with null");
            }

            stopForeground(true);
            NotificationManager managerCompat = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            if (managerCompat != null) {

                NotificationCompat.Builder preExecuteNotification = new NotificationCompat.Builder(getApplicationContext(), Constant.DATABASE_CONSTANT_HISTORY_DOWNLOAD)
                        .setSmallIcon(R.drawable.ic_download)
                        .setContentText(songDBModel.getSong())
                        .setContentTitle("Download complete ");

                managerCompat.notify(ID,preExecuteNotification.build());
            }
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            if (values != null) {
                if (managerCompat != null) {
                    updateNotification
                            .setContentTitle("Song Download")
                            .setContentText("Download in Progress")
                            .setProgress(100, Integer.parseInt(values[0]), false );
                    managerCompat.notify(ID, updateNotification.build());
                }
            }
        }

        public void printLog(String label, String message){
            Log.e(label, message);
        }
    }

}
