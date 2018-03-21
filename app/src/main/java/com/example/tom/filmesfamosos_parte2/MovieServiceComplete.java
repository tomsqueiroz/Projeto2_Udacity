package com.example.tom.filmesfamosos_parte2;

import android.content.Context;
import android.os.AsyncTask;

import com.example.tom.filmesfamosos_parte2.utilities.NetworkUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;

/**
 * Created by tom on 21/03/18.
 */

public class MovieServiceComplete extends AsyncTask<Integer, Void, Movie> {

    private Context context = null;
    private AsyncTaskDelegate asyncTaskDelegate = null;


    public MovieServiceComplete(Context context, AsyncTaskDelegate asyncTaskDelegate){
        this.context = context;
        this.asyncTaskDelegate = asyncTaskDelegate;
    }
    @Override
    protected Movie doInBackground(Integer... integers) {
        URL url = NetworkUtils.Movie_URL(integers[0]);
        String json = null;
        Movie movie = null;

        try {
            json = NetworkUtils.getResponseFromHttpUrl(url);
        }catch (IOException e){e.printStackTrace();}
        try{
            movie = NetworkUtils.parse(json);
        }catch (JSONException e){e.printStackTrace();}
        return movie;
    }
    @Override
    protected void onPostExecute(Movie movie) {
        if(asyncTaskDelegate != null)
            asyncTaskDelegate.processFinish(movie);
    }
}
