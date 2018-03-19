package com.example.tom.filmesfamosos_parte1.utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;

import com.example.tom.filmesfamosos_parte1.Movie;
import com.example.tom.filmesfamosos_parte1.MovieSimple;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Tom on 16/02/2018.
 */

public class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getCanonicalName();

    private static final String MOVIES_BASE_URL = "https://api.themoviedb.org/3/discover/movie";
    private static final String POSTER_BASE_URL = "http://image.tmdb.org/t/p/w342/";
    private static final String MOVIE_BASE_URl = "https://api.themoviedb.org/3/movie";





   /****************YOUR API KEY HERE**************************/
    private static String APIKEY = "YOUR API KEY HERE";


    private static String APIKEY_PARAM = "api_key";
    private static String LANGUAGE_PARAM = "language";
    private static String language = "en-US";
    private static String SORTBY_PARAM = "sort_by";
    private static String PAGE_PARAM = "page";

    public static String sort_popularity = "popularity.desc";
    public static String sort_vote_average = "vote_average.desc";


    private static String JSON_response;

    private static String MOVIE_VOTECOUNT = "vote_count";
    private static String MOVIE_ID = "id";
    private static String MOVIE_VIDEO = "video";
    private static String MOVIE_VOTEAVERAGE = "vote_average";
    private static String MOVIE_TITLE = "title";
    private static String MOVIE_POPULARITY = "popularity";
    private static String MOVIE_POSTERPATH = "poster_path";
    private static String MOVIE_ORIGINALLANGUAGE = "original_language";
    private static String MOVIE_ORIGINALTITLE = "original_title";
    private static String MOVIE_GENRE = "genres";
    private static String MOVIE_GENRENAME = "name";
    private static String MOVIE_BACKDROPPATH = "backdrop_path";
    private static String MOVIE_ADULT = "adult";
    private static String MOVIE_OVERVIEW = "overview";
    private static String MOVIE_RELEASEDATE = "release_date";



    public static URL Movie_URL(int id){
        Uri builtUri = Uri.parse(MOVIE_BASE_URl).buildUpon()
                .appendPath(Integer.toString(id))
                .appendQueryParameter(APIKEY_PARAM, APIKEY)
                .build();
        URL url = null;
        try{
            url = new URL(builtUri.toString());
        }catch(MalformedURLException e){
            e.printStackTrace();
        }
        return url;
    }




    public static URL allMovies_URl(String sort_by, int pagenumber){

        Uri builtUri = Uri.parse(MOVIES_BASE_URL).buildUpon()
                .appendQueryParameter(APIKEY_PARAM, APIKEY)
                .appendQueryParameter(LANGUAGE_PARAM, language)
                .appendQueryParameter(SORTBY_PARAM, sort_by)
                .appendQueryParameter(PAGE_PARAM, Integer.toString(pagenumber))
                .build();
        URL url = null;
        try{
            url = new URL(builtUri.toString());
        }catch (MalformedURLException e){
            e.printStackTrace();
        }
        Log.d(TAG, "URL = " + url);
        return url;



    }


    public static URL posterUrl(String poster){
        Uri builtUri = Uri.parse(POSTER_BASE_URL + poster).buildUpon().build();
        URL url = null;
        try{
            url = new URL(builtUri.toString());

        }catch (MalformedURLException e){
            e.printStackTrace();

        }
        return url;
    }










    public static String getResponseFromHttpUrl (URL url) throws IOException{

        HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
        try{

            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner (in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if(hasInput){
                JSON_response = scanner.next();
                return JSON_response;
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }


    public static List<MovieSimple> parseSimple(String json) throws JSONException{


        JSONObject moviesJson = new JSONObject(json);
        List<MovieSimple> movies = new ArrayList<>();
        final String RESULTS_LIST = "results";
        final String STATUS_CODE = "status_code";

        final int INVALIDKEY_CODE = 7;
        final int RESOURCENOTFOUND = 34;

        if(moviesJson.has(STATUS_CODE)){

            int errorCode = moviesJson.getInt(STATUS_CODE);

            switch(errorCode){

                case INVALIDKEY_CODE:
                    Log.d(TAG, "Invalid Key");
                    return null;

                case RESOURCENOTFOUND:
                    Log.d(TAG, "Resource not found");
                    return null;

                default:
                    break;
            }
        }
        JSONArray movieArray = moviesJson.getJSONArray(RESULTS_LIST);
        int id;
        String poster_path;
        for(int i = 0; i < movieArray.length(); ++i){
            JSONObject movieObject = movieArray.getJSONObject(i);
            id = movieObject.getInt(MOVIE_ID);
            poster_path = movieObject.getString(MOVIE_POSTERPATH);
            MovieSimple movie = new MovieSimple(id, poster_path);
            movies.add(movie);
        }
        return movies;
    }



    public static Movie parse(String json) throws JSONException {

        JSONObject movieJson = new JSONObject(json);
        final String RESULTS_LIST = "results";
        final String STATUS_CODE = "status_code";
        final int INVALIDKEY_CODE = 7;
        final int RESOURCENOTFOUND = 34;
        if(movieJson.has(STATUS_CODE)){
            int errorCode = movieJson.getInt(STATUS_CODE);
            switch(errorCode){
                case INVALIDKEY_CODE:
                    Log.d(TAG, "Invalid Key");
                    return null;
                case RESOURCENOTFOUND:
                    Log.d(TAG, "Resource not found");
                    return null;
                default:
                    break;
            }
        }
        int vote_count;
        int id;
        boolean video;
        int vote_average;
        String title;
        int popularity;
        String poster_path;
        String original_language;
        String original_title;
        String[] genre_names;
        String backdrop_path;
        boolean adult;
        String overview;
        String release_date;


        vote_count = movieJson.optInt(MOVIE_VOTECOUNT);
        id = movieJson.optInt(MOVIE_ID);
        video = movieJson.optBoolean(MOVIE_VIDEO);
        vote_average = movieJson.optInt(MOVIE_VOTEAVERAGE);
        title = movieJson.optString(MOVIE_TITLE);
        popularity = movieJson.optInt(MOVIE_POPULARITY);
        poster_path = movieJson.optString(MOVIE_POSTERPATH);
        original_language = movieJson.optString(MOVIE_ORIGINALLANGUAGE);
        original_title = movieJson.optString(MOVIE_ORIGINALTITLE);
        backdrop_path = movieJson.optString(MOVIE_BACKDROPPATH);
        adult = movieJson.optBoolean(MOVIE_ADULT);
        overview = movieJson.optString(MOVIE_OVERVIEW);
        release_date = movieJson.optString(MOVIE_RELEASEDATE);

        JSONArray genre_array = movieJson.optJSONArray(MOVIE_GENRE);
        genre_names = new String[genre_array.length()];

        for(int x = 0; x < genre_array.length(); ++x){

            JSONObject object = genre_array.optJSONObject(x);
            genre_names[x] = object.optString(MOVIE_GENRENAME);

        }
        Movie movie = new Movie(vote_count,id,video,vote_average,title,popularity,poster_path,original_language,
                                    original_title, genre_names, backdrop_path, adult, overview, release_date);
        return movie;
    }


    public static boolean connection_ok(Context context){

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if(networkInfo != null && networkInfo.isConnectedOrConnecting())
            return true;
        else
            return false;
    }
}
