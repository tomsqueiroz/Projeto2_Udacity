package com.example.tom.filmesfamosos_parte2;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.tom.filmesfamosos_parte2.utilities.NetworkUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.List;

/**
 * Created by Tom on 23/03/2018.
 */

public class MovieServiceVideoURL extends AsyncTask<Integer, Void, String> {

    private static final String TAG = MovieServiceSimple.class.getCanonicalName();
    private Context context = null;
    private List<MovieSimple> movies;
    private AsyncTaskDelegate asyncTaskDelegate;


    public MovieServiceVideoURL(Context context, AsyncTaskDelegate asyncTaskDelegate){
        this.context = context;
        this.asyncTaskDelegate = asyncTaskDelegate;
    }



    @Override
    protected String doInBackground(Integer... params) {


        URL videosUrl = NetworkUtils.videosUrl(params[0]);


        String json = null;
        try {
            json = NetworkUtils.getResponseFromHttpUrl(videosUrl);
        } catch (IOException e){
            e.printStackTrace();

        }
        Log.d(TAG, "JSON = " + json);
        movies = null;
        try{
            movies = NetworkUtils.parseSimple(json);
        }catch (JSONException e){
            e.printStackTrace();
        }
        return json;
    }

    @Override
    protected void onPostExecute(String response) {
        if(asyncTaskDelegate != null)
            asyncTaskDelegate.processFinish(response);
    }
}