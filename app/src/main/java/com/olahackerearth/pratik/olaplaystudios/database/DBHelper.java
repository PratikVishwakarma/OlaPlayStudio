package com.olahackerearth.pratik.olaplaystudios.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.olahackerearth.pratik.olaplaystudios.model.History;
import com.olahackerearth.pratik.olaplaystudios.model.SongDBModel;
import com.olahackerearth.pratik.olaplaystudios.singleton.PlaybackSingleton;

/**
 * Created by pratik on 20/12/17.
 */

public class DBHelper extends SQLiteOpenHelper {


    public static final String DATABASE_NAME = "newPlayStudioDb.db";
//    public static final String DATABASE_NAME = "olaPlayStudioDb.db";
    public static final int DATABASE_VERSION = 3;

    DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    private static DBHelper instance;

    public static DBHelper getDBHelper(Context context){
        return instance = instance==null?new DBHelper(context.getApplicationContext()):instance;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_SONG_TABLE = "  CREATE TABLE " +
                DBContract.Song.TABLE_NAME + "( " +
                DBContract.Song._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                DBContract.Song.COLUMN_SONG + " TEXT," +
                DBContract.Song.COLUMN_SONG_URL + " TEXT," +
                DBContract.Song.COLUMN_ARTISTS + " TEXT," +
                DBContract.Song.COLUMN_DOWNLOAD_STATUS+ " TEXT," +
                DBContract.Song.COLUMN_COVER_IMAGE_FULL_LENGTH + " TEXT," +
                DBContract.Song.COLUMN_COVER_IMAGE_SHORT_LENGTH+ " TEXT," +
                DBContract.Song.COLUMN_SONG_FULL_URL+ " TEXT," +
                DBContract.Song.COLUMN_FAVORITE_STATUS+ " TEXT" +
                DBContract.Song.COLUMN_SIZE+ " NUMERIC" +
                ");";
        final String SQL_CREATE_HISTORY_TABLE = "  CREATE TABLE " +
                DBContract.History.TABLE_NAME + "( " +
                DBContract.History._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                DBContract.History.COLUMN_SONG_ID + " LONG," +
                DBContract.History.COLUMN_TYPE+ " TEXT," +
                DBContract.History.COLUMN_TIME_STAMP+ " LONG" +
                ");";
        sqLiteDatabase.execSQL(SQL_CREATE_SONG_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_HISTORY_TABLE );
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+ DBContract.Song.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+ DBContract.History.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    ContentValues getContentValues(SongDBModel song) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DBContract.Song.COLUMN_SONG, song.getSong());
        contentValue.put(DBContract.Song.COLUMN_SONG_URL, song.getUrl());
        contentValue.put(DBContract.Song.COLUMN_ARTISTS, song.getArtists());
        contentValue.put(DBContract.Song.COLUMN_DOWNLOAD_STATUS, song.getDownloadStatus());
        contentValue.put(DBContract.Song.COLUMN_COVER_IMAGE_FULL_LENGTH, song.getCoverImageFullUlr());
        contentValue.put(DBContract.Song.COLUMN_COVER_IMAGE_SHORT_LENGTH, song.getCoverImage());
        contentValue.put(DBContract.Song.COLUMN_SONG_FULL_URL, song.getSongFullUrl());
        contentValue.put(DBContract.Song.COLUMN_FAVORITE_STATUS, song.getFavorite());
        return contentValue;
    }

    /**
     * *
     * Updating song on DataBase
     * */

    public long updateSong(SongDBModel song) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValue = getContentValues(song);
        long id = db.update(DBContract.Song.TABLE_NAME, contentValue,DBContract.Song._ID+"="+song.getId(),null);
        db.close(); // Closing database connection
        return id;
    }

    /**
     * *
     * Adding the Song into DataBase
     * */
    public long addSong(SongDBModel song) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValue = getContentValues(song);
        long id = db.insert(DBContract.Song.TABLE_NAME, null, contentValue);
        db.close(); // Closing database connection
        return id;
    }
    /**
     * *
     * Adding the history into DataBase
     * */
    public long addHistory(History history) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValue = getContentValuesHistory(history);
        long id = db.insert(DBContract.History.TABLE_NAME, null, contentValue);
        db.close(); // Closing database connection
        return id;
    }

    ContentValues getContentValuesHistory(History history) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DBContract.History.COLUMN_SONG_ID, history.getSongId());
        contentValue.put(DBContract.History.COLUMN_TYPE, history.getType());
        contentValue.put(DBContract.History.COLUMN_TIME_STAMP, history.getTimeStamp());
        return contentValue;
    }


    public SongDBModel fetchSongNameById(int id){
        SQLiteDatabase dbw = this.getWritableDatabase();
        SongDBModel songDBModel = null;
        Cursor cursor =dbw.query(DBContract.Song.TABLE_NAME,new String[]{"*"},
                "_id="+id, null, null,null, null);

        if (cursor.moveToFirst()) {
                songDBModel = new SongDBModel(
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
            }
            cursor.close();
        return songDBModel;
    }

}
