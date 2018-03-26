package com.example.tom.filmesfamosos_parte2;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.example.tom.filmesfamosos_parte2.utilities.NetworkUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.List;

/**
 * Created by Tom on 23/03/2018.
 */

public class MovieServiceVideoReview extends AsyncTask<Integer, Void, Bundle> {

    private static final String TAG = MovieServiceSimple.class.getCanonicalName();
    private Context context = null;
    private List<MovieSimple> movies;
    private AsyncTaskDelegate asyncTaskDelegate;
    public static final int MODO_VIDEOS = 1;
    public static final int MODO_REVIEWS = 2;



    public MovieServiceVideoReview(Context context, AsyncTaskDelegate asyncTaskDelegate){
        this.context = context;
        this.asyncTaskDelegate = asyncTaskDelegate;
    }



    @Override
    protected Bundle doInBackground(Integer... params) {
        URL url;
        String modo;
        if(params[0] == MODO_REVIEWS){
            url = NetworkUtils.reviewsUrl(params[1]);
            modo = Integer.toString(MODO_REVIEWS);
        }else if(params[0] == MODO_VIDEOS){
            url = NetworkUtils.videosUrl(params[1]);
            modo = Integer.toString(MODO_VIDEOS);
        }else
            return null;
        String json = null;
        try {
            json = NetworkUtils.getResponseFromHttpUrl(url);
        } catch (IOException e){
            e.printStackTrace();

        }
        Log.d(TAG, "JSON = " + json);
        Bundle b = new Bundle();
        b.putString(modo, json);
        return b;
    }

    @Override
    protected void onPostExecute(Bundle response) {
        if(asyncTaskDelegate != null)
            asyncTaskDelegate.processFinish(response);
    }
}