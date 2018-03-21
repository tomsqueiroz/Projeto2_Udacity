package com.example.tom.filmesfamosos_parte2;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tom.filmesfamosos_parte2.data.MovieContentProvider;
import com.example.tom.filmesfamosos_parte2.data.MovieContract;
import com.example.tom.filmesfamosos_parte2.databinding.ActivityDetailsBinding;
import com.example.tom.filmesfamosos_parte2.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.net.URL;

import static com.example.tom.filmesfamosos_parte2.data.MovieContract.MovieEntry.COLUMN_MOVIE_ID;


/**
 * Created by Tom on 09/02/2018.
 */

public class DetailsActivity extends AppCompatActivity implements AsyncTaskDelegate{

    private Context context;
    private String MODO = "modo anterior";
    private int MODO_ANTERIOR;
    private Movie movie = null;
    private static final String TAG = DetailsActivity.class.getCanonicalName();

    private ActivityDetailsBinding mDetailsBinding;
    private MovieContentProvider mContentProvider;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDetailsBinding = DataBindingUtil.setContentView(this, R.layout.activity_details);
        context = this;
        Intent i = getIntent();
        Integer id = i.getIntExtra(Intent.EXTRA_TEXT,1);
        MODO_ANTERIOR = i.getIntExtra(MODO, 1);
        loadMovie(id);
        mContentProvider = new MovieContentProvider();

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
            clearActivity();
            mDetailsBinding.progressBar.setVisibility(View.VISIBLE);
            new MovieServiceComplete(context, this).execute(id);
        }else{
            Toast toast = Toast.makeText(getApplicationContext(), "Não conectado a internet", Toast.LENGTH_SHORT);
            toast.show();
            mDetailsBinding.progressBar.setVisibility(View.INVISIBLE);
        }
    }

    public void clearActivity(){
        mDetailsBinding.tituloFilme.setVisibility(View.INVISIBLE);
        mDetailsBinding.anoFilme.setVisibility(View.INVISIBLE);
        mDetailsBinding.tempoFilme.setVisibility(View.INVISIBLE);
        mDetailsBinding.notaFilme.setVisibility(View.INVISIBLE);
        mDetailsBinding.botaoAddFavorito.setVisibility(View.INVISIBLE);
        mDetailsBinding.descricaoFilme.setVisibility(View.INVISIBLE);
        mDetailsBinding.viewLight.setVisibility(View.INVISIBLE);
        mDetailsBinding.labelTrailers.setVisibility(View.INVISIBLE);
        mDetailsBinding.rvTrailers.setVisibility(View.INVISIBLE);
        mDetailsBinding.poster.setVisibility(View.INVISIBLE);
    }
    public void showActivity(){
        mDetailsBinding.tituloFilme.setVisibility(View.VISIBLE);
        mDetailsBinding.anoFilme.setVisibility(View.VISIBLE);
        mDetailsBinding.tempoFilme.setVisibility(View.VISIBLE);
        mDetailsBinding.notaFilme.setVisibility(View.VISIBLE);
        mDetailsBinding.botaoAddFavorito.setVisibility(View.VISIBLE);
        mDetailsBinding.descricaoFilme.setVisibility(View.VISIBLE);
        mDetailsBinding.viewLight.setVisibility(View.VISIBLE);
        mDetailsBinding.labelTrailers.setVisibility(View.VISIBLE);
        mDetailsBinding.rvTrailers.setVisibility(View.VISIBLE);
        mDetailsBinding.poster.setVisibility(View.VISIBLE);
    }

    @Override
    public void processFinish(Object output) {
        movie = (Movie) output;
        URL url = NetworkUtils.posterUrl(movie.getPoster_path());
        showActivity();
        Picasso.with(context).load(url.toString()).into(mDetailsBinding.poster);
        mDetailsBinding.progressBar.setVisibility(View.INVISIBLE);
        mDetailsBinding.tituloFilme.setText(movie.getTitle());
        mDetailsBinding.anoFilme.setText(movie.getRelease_date());
        mDetailsBinding.notaFilme.setText(movie.getVote_average() + "/10");
        mDetailsBinding.botaoAddFavorito.setVisibility(View.VISIBLE);
        mDetailsBinding.descricaoFilme.setText(movie.getOverview());
        mDetailsBinding.viewLight.setVisibility(View.VISIBLE);
        mDetailsBinding.labelTrailers.setVisibility(View.VISIBLE);
    }

    public void onClick(View view){

        ContentValues values = new ContentValues();

        if(movie != null){

            values.put(COLUMN_MOVIE_ID, movie.getId());
            values.put(MovieContract.MovieEntry.COLUMN_TITLE, movie.getTitle());
            values.put(MovieContract.MovieEntry.COLUMN_YEAR, movie.getRelease_date());
            values.put(MovieContract.MovieEntry.COLUMN_RATE, movie.getVote_average());
            values.put(MovieContract.MovieEntry.COLUMN_DESCRIPTION, movie.getOverview());
            values.put(MovieContract.MovieEntry.COLUMN_LENGTH, "x");
            values.put(MovieContract.MovieEntry.COLUMN_GENRE, "x");
            getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, values);
        }





    }



}
