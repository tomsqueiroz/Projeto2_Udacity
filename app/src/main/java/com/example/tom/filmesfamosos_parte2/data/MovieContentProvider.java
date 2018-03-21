package com.example.tom.filmesfamosos_parte2.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;


/**
 * Created by tom on 20/03/18.
 */

public class MovieContentProvider extends ContentProvider {

    public static final int CODE_MOVIE = 100;

    private static final UriMatcher URI_MATCHER = buildUriMatcher();

    private static MovieDbHelper mMovieDbHelper;


    public static UriMatcher buildUriMatcher(){

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;
        matcher.addURI(authority, MovieContract.PATH_MOVIE, CODE_MOVIE);
        return matcher;
    }


    @Override
    public boolean onCreate() {
        mMovieDbHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String s, @Nullable String[] strings1, @Nullable String s1) {

        Cursor cursor;

        switch(URI_MATCHER.match(uri)){

            case CODE_MOVIE: {
                cursor = mMovieDbHelper.getReadableDatabase().query(
                        MovieContract.MovieEntry.TABLE_NAME,
                        strings,
                        s,
                        strings1,
                        null,
                        null,
                        s1);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {

        final SQLiteDatabase db = mMovieDbHelper.getWritableDatabase();
        Uri returnUri;

        switch(URI_MATCHER.match(uri)){

            case CODE_MOVIE: {

                long id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, contentValues);
                if(id > 0){
                    returnUri = ContentUris.withAppendedId(MovieContract.MovieEntry.CONTENT_URI, id);
                }else{
                    throw new SQLException("Failed to insert row into: "  + uri);
                }
                break;
            }default:
                throw new UnsupportedOperationException("Unknown uri: "  + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {

        int numberRowsDeleted;

        if(s == null)
            s = "1";

        switch(URI_MATCHER.match(uri)){

            case CODE_MOVIE: {

                numberRowsDeleted = mMovieDbHelper.getWritableDatabase().delete(
                        MovieContract.MovieEntry.TABLE_NAME,
                        s,
                        strings);

                break;
            }default:
                throw new UnsupportedOperationException("Unknown Uri:" + uri);
        }

        if(numberRowsDeleted != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return numberRowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }


    @Override
    @TargetApi(11)
    public void shutdown() {
        mMovieDbHelper.close();
        super.shutdown();
    }
}
