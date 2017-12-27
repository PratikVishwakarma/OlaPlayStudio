package com.olahackerearth.pratik.olaplaystudios.service

import android.content.Context
import android.os.AsyncTask
import android.os.Environment
import android.util.Log
import com.olahackerearth.pratik.olaplaystudios.database.DBHelper
import com.olahackerearth.pratik.olaplaystudios.model.History
import com.olahackerearth.pratik.olaplaystudios.model.SongDBModel
import com.olahackerearth.pratik.olaplaystudios.utility.Constant
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.net.URL
import java.util.*

/**
 * Created by pratik on 20/12/17.
 */

class SongDownloader(val context: Context) : AsyncTask<SongDBModel, String, SongDBModel>() {

    override fun onPreExecute() {
        super.onPreExecute()
        Log.e("songdownloader", "Download starting...")
    }

    override fun doInBackground(vararg params: SongDBModel?): SongDBModel? {
        if (params.isNotEmpty()) {
            val song = params[0]
            if (song != null) {
                val url = URL(song.songFullUrl)
                val dir = File(Environment.getExternalStorageDirectory().path + File.separator + "OlaPlayStudios")
                if (!dir.exists()) dir.mkdir()
                val file = File(dir.path + File.separator + "${song.song}.mp3")
                if (!file.exists()) file.createNewFile()
                val connection = url.openConnection()
                val outputStream = FileOutputStream(file)
                copyWithProgress(connection.getInputStream(), outputStream, ByteArray(DEFAULT_BUFFER_SIZE))
                song.downloadStatus = Constant.CONSTANT_SONG_DOWNLOAD_STATUS_DOWNLOADED
                return song
            } else {
                return null
            }
        } else {
//            Log.e("songdownloader", "Params empty")
            return null
        }
    }

    override fun onProgressUpdate(vararg values: String?) {
        super.onProgressUpdate(*values)
            if (values.isNotEmpty()) { }
    }

    override fun onPostExecute(result: SongDBModel?) {
        super.onPostExecute(result)
        DBHelper.getDBHelper(context).updateSong(result)
        val result = result
        if(result!=null) {
            /**
             * Adding into history table
             */

            val history = History(
                    Constant.DATABASE_CONSTANT_HISTORY_DOWNLOAD,
                    result.id,
                    Date().time
            )
            DBHelper.getDBHelper(context).addHistory(history)
        }
    }

    /**
     * Copy the data into the sdCard
     */

    fun copyWithProgress(`in`: InputStream, out: OutputStream, buffer: ByteArray): Long {
        var read = 0L
        var n: Int
        n = `in`.read(buffer)
        while (n > 0) {
            out.write(buffer, 0, n)
            read += n.toLong()
            publishProgress(read.toString())
            n = `in`.read(buffer)
        }
        out.flush()
        `in`.close()
        out.close()

        return read
    }

}