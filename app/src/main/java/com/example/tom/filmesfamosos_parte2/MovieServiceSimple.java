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
 * Created by Tom on 24/02/2018.
 */

public class MovieServiceSimple extends AsyncTask<Integer, Void, List<MovieSimple>> {

    private static final String TAG = MovieServiceSimple.class.getCanonicalName();
    private Context context = null;
    private List<MovieSimple> movies;
    private AsyncTaskDelegate asyncTaskDelegate;


    public MovieServiceSimple(Context context, AsyncTaskDelegate asyncTaskDelegate){
        this.context = context;
        this.asyncTaskDelegate = asyncTaskDelegate;
    }



    @Override
    protected List<MovieSimple> doInBackground(Integer... params) {
        URL movies_url = null;

        if(params[0] == context.getResources().getInteger(R.integer.MODO_POPULARITY))
            movies_url = NetworkUtils.allMovies_URl(NetworkUtils.sort_popularity, params[1]);
        if(params[0] == context.getResources().getInteger(R.integer.MODO_TOPRATED))
            movies_url = NetworkUtils.allMovies_URl(NetworkUtils.sort_vote_average, params[1]);

        String json = null;
        try {
            json = NetworkUtils.getResponseFromHttpUrl(movies_url);
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
        return movies;
    }

    @Override
    protected void onPostExecute(List<MovieSimple> movies_data) {

        if(asyncTaskDelegate != null)
            asyncTaskDelegate.processFinish(movies_data);
    }
}
