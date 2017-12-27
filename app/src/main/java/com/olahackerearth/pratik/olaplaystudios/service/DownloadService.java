package com.olahackerearth.pratik.olaplaystudios.service;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.IBinder;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

import com.olahackerearth.pratik.olaplaystudios.database.DBHelper;
import com.olahackerearth.pratik.olaplaystudios.model.History;
import com.olahackerearth.pratik.olaplaystudios.model.Song;
import com.olahackerearth.pratik.olaplaystudios.model.SongDBModel;
import com.olahackerearth.pratik.olaplaystudios.utility.Constant;

import java.io.ByteArrayInputStream;
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
            new DownloadSongFromUrl().execute(songDBModel);
//            final SongDownloader songDownloader = new SongDownloader(getApplicationContext());
//            songDownloader.execute(songDBModel);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public class DownloadSongFromUrl extends AsyncTask<SongDBModel, String, SongDBModel>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
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
                        if (!file.exists()){
                            try {
                                file.createNewFile();
                                FileOutputStream outputStream = new FileOutputStream(file);
                                byte data[] = new byte[1024];
                                long total = 0;
                                int lenghtOfFile = connection.getContentLength();
                                InputStream input = connection.getInputStream();

                                while ((count = input.read(data)) != -1) {
                                    total += count;
                                    Log.e("song downloader", "in while "+ total);
                                    // publishing the progress....
                                    // After this onProgressUpdate will be called
                                    publishProgress(""+(int)((total*100)/lenghtOfFile));

                                    // writing data to file
                                    outputStream.write(data, 0, count);
                                }

                                copyWithProgress(connection.getInputStream(), outputStream, new byte[DEFAULT_BUFFER_SIZE]);
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
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(SongDBModel songDBModel) {
            super.onPostExecute(songDBModel);
            DBHelper.getDBHelper(getApplicationContext()).updateSong(songDBModel);
            if(songDBModel != null) {
                History history = new History("download", songDBModel.id, (new Date()).getTime());
                DBHelper.getDBHelper(getApplicationContext()).addHistory(history);
            }
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            if (values != null) { }
        }

        /**
         * Copy the data into the sdCard
         */

//        private copyWithProgress(`in`: InputStream, out: OutputStream, buffer: ByteArray): Long {
//            var read = 0L;
//            var n: Int
//            n = `in`.read(buffer)
//            while (n > 0) {
//                out.write(buffer, 0, n)
//                read += n.toLong()
//                publishProgress(read.toString())
//                n = `in`.read(buffer)
//            }
//            out.flush()
//        `in`.close()
//            out.close()
//
//            return read
//        }
        private Long copyWithProgress(InputStream in, OutputStream out, byte[] buffer){
            Long read = 0L;
            int n;
            try {
                n = in.read(buffer);
                while (n > 0) {
                    out.write(buffer, 0, n);
                    read += Long.parseLong(n+"");
                    publishProgress(read.toString());
                    n = in.read(buffer);
                    out.flush();
                    in.close();
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return read;
        }

        public void printLog(String label, String message){
            Log.e(label, message);
        }
    }


}
