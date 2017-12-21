package com.olahackerearth.pratik.olaplaystudios.database;

import android.provider.BaseColumns;

/**
 * Created by pratik on 20/12/17.
 */

public class DBContract {

    public static final class Song implements BaseColumns {
        public static final String TABLE_NAME = "Songs";
        public static final String COLUMN_SONG = "song";
        public static final String COLUMN_SONG_URL = "url";
        public static final String COLUMN_ARTISTS = "artists";
        public static final String COLUMN_COVER_IMAGE_FULL_LENGTH = "cover_image_full_length";
        public static final String COLUMN_COVER_IMAGE_SHORT_LENGTH = "cover_image_short_length";
        public static final String COLUMN_DOWNLOAD_STATUS = "download_status";
        public static final String COLUMN_SONG_FULL_URL = "song_full_url";
        public static final String COLUMN_FAVORITE_STATUS = "favorite_status";


        public static final int COLUMN_INT_ID = 0;
        public static final int COLUMN_INT_SONG = 1;
        public static final int COLUMN_INT_SONG_URL = 2;
        public static final int COLUMN_INT_ARTISTS = 3;
        public static final int COLUMN_INT_DOWNLOAD_STATUS = 4;
        public static final int COLUMN_INT_COVER_IMAGE_FULL_LENGTH = 5;
        public static final int COLUMN_INT_COVER_IMAGE_SHORT_LENGTH = 6;
        public static final int COLUMN_INT_SONG_FULL_URL = 7;
        public static final int COLUMN_INT_FAVORITE_STATUS  = 8;
    }
    public static final class History implements BaseColumns {
        public static final String TABLE_NAME = "History";
        public static final String COLUMN_SONG_ID = "songId";
        public static final String COLUMN_TYPE = "type";
        public static final String COLUMN_TIME_STAMP = "timeStamp";

        public static final int COLUMN_INT_ID = 0;
        public static final int COLUMN_INT_SONG_ID = 1;
        public static final int COLUMN_INT_TYPE = 2;
        public static final int COLUMN_INT_TIME_STAMP = 3;
    }
}
