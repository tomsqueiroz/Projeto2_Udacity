package com.example.tom.filmesfamosos_parte1;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.MalformedJsonException;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tom.filmesfamosos_parte1.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.List;

/**
 * Created by Tom on 09/02/2018.
 */

public class DetailsActivity extends AppCompatActivity {

    private ImageView poster;
    private TextView descricao;


    private ProgressBar progressBar;
    private Context context;

    private String MODO = "modo anterior";
    private int MODO_ANTERIOR;




    private static final String TAG = DetailsActivity.class.getCanonicalName();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        poster = (ImageView) findViewById(R.id.poster);
        descricao = (TextView)  findViewById(R.id.descricao);


        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        context = this;
        Intent i = getIntent();
        Integer id = i.getIntExtra(Intent.EXTRA_TEXT,1);
        MODO_ANTERIOR = i.getIntExtra(MODO, 1);
        loadMovie(id);
    }

    //COMO UNICO ITEM DE MENU NESSA ACTIVITY EH O BACK BUTTON
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if(id == android.R.id.home){
            Intent intent = new Intent();
            intent.putExtra(MODO, MODO_ANTERIOR);
            setResult(0, intent);
            finish();

        }
        return true;
    }

    public void loadMovie(int id){

        if(NetworkUtils.connection_ok(getApplicationContext())){
            new FetchMovieData().execute(id);
        }else{
            Toast toast = Toast.makeText(getApplicationContext(), "Não conectado a internet", Toast.LENGTH_SHORT);
            toast.show();
            progressBar.setVisibility(View.INVISIBLE);
        }
    }


    public class FetchMovieData extends AsyncTask<Integer, Void, Movie> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Movie doInBackground(Integer... params) {
            URL url = NetworkUtils.Movie_URL(params[0]);
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

            URL poster_url = NetworkUtils.posterUrl(movie.getPoster_path());
            Picasso.with(context).load(poster_url.toString()).into(poster);
            progressBar.setVisibility(View.INVISIBLE);

            descricao.setText("\nTítulo: " + movie.getTitle());
           descricao.append("\nTítulo Original: " + movie.getOriginal_title());
            descricao.append("\nData de lançamento: " + movie.getRelease_date());
            descricao.append("\nPopularidade: " + movie.getPopularity());
            descricao.append("\nVote Count: " + movie.getVote_count());
            descricao.append("\nGêneros: ");

           for(int i = 0; i < movie.getGenre_names().length; ++i){
               descricao.append(movie.getGenre_names()[i] + " ");
           }
            descricao.append("\nSinopse: " + movie.getOverview());
        }
    }

}
