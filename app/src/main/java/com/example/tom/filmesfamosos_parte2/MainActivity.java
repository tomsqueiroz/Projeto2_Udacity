package com.example.tom.filmesfamosos_parte2;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.tom.filmesfamosos_parte2.data.MovieContract;
import com.example.tom.filmesfamosos_parte2.utilities.NetworkUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MoviesAdapter.Movies_AdapterOnClickHandler, AsyncTaskDelegate {

    private RecyclerView mRecyclerView;
    private MoviesAdapter mMoviesAdapter;
    private ProgressBar progressBar;
    private static final String TAG = MainActivity.class.getCanonicalName();
    private int PAGINA_ATUAL;
    private List<URL> urls;
    private List<Integer> ids;
    private int MODO_POPULARITY = 0;
    private int MODO_TOPRATED = 1;
    private int MODO_ATUAL = MODO_TOPRATED;
    private String MODO = "modo anterior";





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        urls = new ArrayList<>();
        ids = new ArrayList<>();
        progressBar = (ProgressBar) findViewById(R.id.main_progressBar);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_main);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mMoviesAdapter = new MoviesAdapter(this, this);
        mRecyclerView.setAdapter(mMoviesAdapter);
        MODO_ATUAL = MODO_POPULARITY;
        loadMoviesData(getResources().getInteger(R.integer.PAGINA_INICIO), MODO_ATUAL);
        PAGINA_ATUAL = getResources().getInteger(R.integer.PAGINA_INICIO);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(1)) {
                    PAGINA_ATUAL++;
                    loadMoviesData(PAGINA_ATUAL, MODO_ATUAL);
                }
            }
        });
    }

    // mode == 3 significa que é pra acessar favoritos
    public void loadMoviesData(int inicio, int mode){

        if(NetworkUtils.connection_ok(getApplicationContext()) && mode != 3){
            mMoviesAdapter.setImages(urls, ids);
            progressBar.setVisibility(View.VISIBLE);
            new MovieServiceSimple(this, this).execute(mode, inicio);
            PAGINA_ATUAL = inicio;

        }else{
            Toast toast = Toast.makeText(getApplicationContext(), "Sem internet, apenas favoritos", Toast.LENGTH_SHORT);
            toast.show();
            queryContentProvider();
            PAGINA_ATUAL = inicio;
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    private void queryContentProvider(){
        urls.clear();
        ids.clear();
        Cursor cursor = getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI, null,
                null,
                null,
                null,
                null);

        if(cursor != null){
            cursor.moveToFirst();
            for(int i = 0; i < cursor.getCount(); ++i){
                URL posterPath = NetworkUtils.posterUrl(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTERPATH)));
                urls.add(posterPath);
                ids.add(cursor.getInt(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID)));
            }
        }
        cursor.close();
        if(urls.size() > 0 && ids.size() > 0){
            mMoviesAdapter.setImages(urls, ids);
        }
    }


    @Override
    public void onClick(int adapter_pos, Integer id) {
        Context context = this;
        Class destination = DetailsActivity.class;
        Intent intent = new Intent(context,destination);
        intent.putExtra(Intent.EXTRA_TEXT, id);
        intent.putExtra(MODO, MODO_ATUAL);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        urls.clear();
        ids.clear();
        MODO_ATUAL = data.getIntExtra(MODO, 1);
        PAGINA_ATUAL = getResources().getInteger(R.integer.PAGINA_INICIO);
        loadMoviesData(PAGINA_ATUAL, MODO_ATUAL);
    }

    @Override
    public void processFinish(Object output) {

        if(output != null) {

            List<MovieSimple> movies_data = (List<MovieSimple>) output;

            for (int i = 0; i < movies_data.size(); ++i) {
                urls.add(NetworkUtils.posterUrl(movies_data.get(i).getPost_path()));
                ids.add(movies_data.get(i).getId());
            }
            progressBar.setVisibility(View.INVISIBLE);
            mMoviesAdapter.setImages(urls, ids);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.movie_order, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();


        if (id == R.id.action_votes) {
            urls.clear();
            ids.clear();
            MODO_ATUAL = MODO_TOPRATED;
            PAGINA_ATUAL = getResources().getInteger(R.integer.PAGINA_INICIO);
            loadMoviesData(PAGINA_ATUAL, MODO_ATUAL);
            return true;
        }else if (id == R.id.action_popularity) {
            urls.clear();
            ids.clear();
            MODO_ATUAL = MODO_POPULARITY;
            PAGINA_ATUAL = getResources().getInteger(R.integer.PAGINA_INICIO);
            loadMoviesData(PAGINA_ATUAL, MODO_ATUAL);
            return true;
        }else if(id == R.id.action_refresh){
            urls.clear();
            ids.clear();
            PAGINA_ATUAL = getResources().getInteger(R.integer.PAGINA_INICIO);
            loadMoviesData(PAGINA_ATUAL, MODO_ATUAL);
        }else if(id == R.id.action_favorites){


        }
        return super.onOptionsItemSelected(item);
    }


}
