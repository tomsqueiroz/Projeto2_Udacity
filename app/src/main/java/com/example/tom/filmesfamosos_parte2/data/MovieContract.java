package com.example.tom.filmesfamosos_parte2.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by tom on 20/03/18.
 */

public class MovieContract {

    public static final String CONTENT_AUTHORITY = "com.example.tom.filmesfamosos_parte2";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_MOVIE = "movie";

    public static final class MovieEntry implements BaseColumns{

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_MOVIE)
                .build();

        public static final String TABLE_NAME = "movie";

        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_TITLE = "movie_title";
        public static final String COLUMN_YEAR = "movie_year";
        public static final String COLUMN_LENGTH = "movie_length";
        public static final String COLUMN_RATE = "movie_rate";
        public static final String COLUMN_DESCRIPTION = "movie_description";
        public static final String COLUMN_GENRE = "movie_genre";
        public static final String COLUMN_POSTERPATH = "movie_posterpath";

    }

}
