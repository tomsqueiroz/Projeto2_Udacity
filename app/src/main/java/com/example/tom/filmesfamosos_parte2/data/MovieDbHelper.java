package com.example.tom.filmesfamosos_parte2.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by tom on 20/03/18.
 */

public class MovieDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "movie.db";
    private static final int DATABASE_VERSION = 1;

    public MovieDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_WEATHER_TABLE =

                "CREATE TABLE " + MovieContract.MovieEntry.TABLE_NAME + " (" +

                        MovieContract.MovieEntry._ID               + " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                        MovieContract.MovieEntry.COLUMN_MOVIE_ID       + " INTEGER NOT NULL, "                 +

                        MovieContract.MovieEntry.COLUMN_TITLE + " TEXT,"                  +

                        MovieContract.MovieEntry.COLUMN_YEAR   + " INTEGER NOT NULL, "                    +
                        MovieContract.MovieEntry.COLUMN_LENGTH   + " INTEGER NOT NULL, "                    +

                        MovieContract.MovieEntry.COLUMN_RATE   + " REAL NOT NULL, "                    +
                        MovieContract.MovieEntry.COLUMN_DESCRIPTION   + " TEXT NOT NULL, "                    +

                        MovieContract.MovieEntry.COLUMN_GENRE + " TEXT,"            +

                        " UNIQUE (" + MovieContract.MovieEntry.COLUMN_MOVIE_ID + ") ON CONFLICT REPLACE);";

        sqLiteDatabase.execSQL(SQL_CREATE_WEATHER_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
