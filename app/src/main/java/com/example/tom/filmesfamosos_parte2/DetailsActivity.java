package com.example.tom.filmesfamosos_parte2;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;

import com.example.tom.filmesfamosos_parte2.data.MovieContentProvider;
import com.example.tom.filmesfamosos_parte2.data.MovieContract;
import com.example.tom.filmesfamosos_parte2.databinding.ActivityDetailsBinding;
import com.example.tom.filmesfamosos_parte2.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.net.URL;
import java.util.List;

import static com.example.tom.filmesfamosos_parte2.data.MovieContract.MovieEntry.COLUMN_DESCRIPTION;
import static com.example.tom.filmesfamosos_parte2.data.MovieContract.MovieEntry.COLUMN_MOVIE_ID;
import static com.example.tom.filmesfamosos_parte2.data.MovieContract.MovieEntry.COLUMN_POSTERPATH;
import static com.example.tom.filmesfamosos_parte2.data.MovieContract.MovieEntry.COLUMN_RATE;
import static com.example.tom.filmesfamosos_parte2.data.MovieContract.MovieEntry.COLUMN_TITLE;
import static com.example.tom.filmesfamosos_parte2.data.MovieContract.MovieEntry.COLUMN_YEAR;


/**
 * Created by Tom on 09/02/2018.
 */

public class DetailsActivity extends AppCompatActivity implements AsyncTaskDelegate, TrailersAdapter.TrailersAdapterOnClickHandler{

    private Context context;
    private String MODO = "modo anterior";
    private int MODO_ANTERIOR;
    private Movie movie = null;
    private static final String TAG = DetailsActivity.class.getCanonicalName();

    private ActivityDetailsBinding mDetailsBinding;
    private MovieContentProvider mContentProvider;
    private RecyclerView mRecyclerView;
    private List<URL> youtubeURLS = null;


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
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_trailers);
    }

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

    public void loadMovie(int id) {
        Cursor cursor = queryMovie(id);
        if (cursor != null && cursor.getCount() != 0) {
            clearActivity();
            mDetailsBinding.progressBar.setVisibility(View.INVISIBLE);
            showMovie(cursor);
        } else if (NetworkUtils.connection_ok(getApplicationContext())) {
            mDetailsBinding.botaoAddFavorito.setBackgroundColor(getResources().getColor(R.color.colorPrimaryLight));
            mDetailsBinding.botaoAddFavorito.setTextColor(getResources().getColor(R.color.colorPrimary));
            clearActivity();
            mDetailsBinding.progressBar.setVisibility(View.VISIBLE);
            new MovieServiceComplete(context, this).execute(id);
        }
    }

    public Cursor queryMovie(int id){

        Cursor cursor = getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI, null,
                COLUMN_MOVIE_ID + "=?",
                new String[]{Integer.toString(id)},
                null,
                null);

        return cursor;
    }

    public void showMovie(Cursor cursor){
        cursor.moveToFirst();
        for(int i = 0; i < cursor.getCount(); ++i){
            int movieId = cursor.getInt(cursor.getColumnIndex(COLUMN_MOVIE_ID));
            String movieTitle = cursor.getString(cursor.getColumnIndex(COLUMN_TITLE));
            String moviePosterPath = cursor.getString(cursor.getColumnIndex(COLUMN_POSTERPATH));
            String movieYear = Integer.toString(cursor.getInt(cursor.getColumnIndex(COLUMN_YEAR)));
            int movieRate = cursor.getInt(cursor.getColumnIndex(COLUMN_RATE));
            String movieDescription = cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION));
            Movie mov = new Movie(0, movieId, true, movieRate, movieTitle, 0, moviePosterPath, null, null, null, null, false, movieDescription, movieYear);
            cursor.moveToNext();
            processFinish(mov);
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

        if(output instanceof Movie) {
            movie = (Movie) output;
            URL url = NetworkUtils.posterUrl(movie.getPoster_path());
            showActivity();
            Picasso.with(context).load(url.toString()).into(mDetailsBinding.poster);
            mDetailsBinding.filmeId.setText(Integer.toString(movie.getId()));
            mDetailsBinding.progressBar.setVisibility(View.INVISIBLE);
            mDetailsBinding.tituloFilme.setText(movie.getTitle());
            mDetailsBinding.anoFilme.setText(movie.getRelease_date());
            mDetailsBinding.notaFilme.setText(movie.getVote_average() + "/10");
            mDetailsBinding.botaoAddFavorito.setVisibility(View.VISIBLE);
            mDetailsBinding.descricaoFilme.setText(movie.getOverview());
            mDetailsBinding.viewLight.setVisibility(View.VISIBLE);
            mDetailsBinding.labelTrailers.setVisibility(View.VISIBLE);
            new MovieServiceVideoReview(this, this).execute(new Integer[]{MovieServiceVideoReview.MODO_REVIEWS, movie.getId()});
            new MovieServiceVideoReview(this, this).execute(new Integer[]{MovieServiceVideoReview.MODO_VIDEOS, movie.getId()});
        }else if(output instanceof Bundle){
            if(((Bundle) output).getString(Integer.toString(MovieServiceVideoReview.MODO_VIDEOS)) != null){
                try {
                    youtubeURLS = NetworkUtils.parseVideoURLS(((Bundle)output).getString(Integer.toString(MovieServiceVideoReview.MODO_VIDEOS)));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                LinearLayoutManager layoutManager = new LinearLayoutManager(this);
                mRecyclerView.setLayoutManager(layoutManager);
                mRecyclerView.setHasFixedSize(true);
                TrailersAdapter trailersAdapter = new TrailersAdapter(this, this, youtubeURLS.size());
                mRecyclerView.setAdapter(trailersAdapter);
            }
        }
    }

    public void onClickFavorito(View view){
        Cursor cursor = queryMovie(Integer.parseInt(mDetailsBinding.filmeId.getText().toString()));
        if (cursor != null && cursor.getCount() != 0) {
            mDetailsBinding.botaoAddFavorito.setTextColor(getResources().getColor(R.color.colorPrimary));
            mDetailsBinding.botaoAddFavorito.setBackgroundColor(getResources().getColor(R.color.colorPrimaryLight));
            getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI, MovieContract.MovieEntry.COLUMN_MOVIE_ID+"=?",new String[]{mDetailsBinding.filmeId.getText().toString()});
        }else{
            mDetailsBinding.botaoAddFavorito.setTextColor(getResources().getColor(R.color.colorPrimaryLight));
            mDetailsBinding.botaoAddFavorito.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            ContentValues values = new ContentValues();
            if(movie != null){
                values.put(COLUMN_MOVIE_ID, movie.getId());
                values.put(MovieContract.MovieEntry.COLUMN_TITLE, movie.getTitle());
                values.put(MovieContract.MovieEntry.COLUMN_YEAR, movie.getRelease_date());
                values.put(MovieContract.MovieEntry.COLUMN_RATE, movie.getVote_average());
                values.put(MovieContract.MovieEntry.COLUMN_DESCRIPTION, movie.getOverview());
                values.put(MovieContract.MovieEntry.COLUMN_LENGTH, "x");
                values.put(MovieContract.MovieEntry.COLUMN_GENRE, "x");
                values.put(MovieContract.MovieEntry.COLUMN_POSTERPATH, movie.getPoster_path());
                getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, values);
            }
        }
    }

    @Override
    public void onClickTrailer(int adapter_position) {
        if(youtubeURLS != null){
            Intent implicitIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(youtubeURLS.get(adapter_position).toString()));
            startActivity(implicitIntent);
        }

    }
}
