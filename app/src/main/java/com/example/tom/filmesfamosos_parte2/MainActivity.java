package com.example.tom.filmesfamosos_parte2;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
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
    private GridLayoutManager layoutManager;
    private MoviesAdapter mMoviesAdapter;
    private ProgressBar progressBar;
    private static final String TAG = MainActivity.class.getCanonicalName();
    private int PAGINA_ATUAL;
    private ArrayList<String> urls;
    private ArrayList<Integer> ids;
    private int MODO_POPULARITY = 0;
    private int MODO_TOPRATED = 1;
    private int MODO_FAVORITOS = 2;
    private int MODO_ATUAL = MODO_TOPRATED;
    private int ITEM_ATUAL = 0;
    private String MODO = "modo anterior";
    private static final String MODO_ATUAL_CALLBACK_STRING = "modoatual_callback";
    private static final String PAGINA_ATUAL_CALLBACK_STRING = "paginaatual_callback";
    private static final String ITEM_ATUAL_CALLBACK_STRING = "itematual_callback";
    private static final String LIST_URLS_ATUAL_CALLBACK_STRING = "urlatual_callback";
    private static final String LIST_IDS_ATUAL_CALLBACK_STRING = "idatual_callback";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar = (ProgressBar) findViewById(R.id.main_progressBar);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_main);
        layoutManager = new GridLayoutManager(this, 3);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mMoviesAdapter = new MoviesAdapter(this, this);
        mRecyclerView.setAdapter(mMoviesAdapter);
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
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(MODO_ATUAL_CALLBACK_STRING)) {
                MODO_ATUAL = savedInstanceState.getInt(MODO_ATUAL_CALLBACK_STRING);
                PAGINA_ATUAL = savedInstanceState.getInt(PAGINA_ATUAL_CALLBACK_STRING);
                ITEM_ATUAL = savedInstanceState.getInt(ITEM_ATUAL_CALLBACK_STRING);
                urls = savedInstanceState.getStringArrayList(LIST_URLS_ATUAL_CALLBACK_STRING);
                ids = savedInstanceState.getIntegerArrayList(LIST_IDS_ATUAL_CALLBACK_STRING);
            }
        }else{
            MODO_ATUAL = MODO_POPULARITY;
            PAGINA_ATUAL = getResources().getInteger(R.integer.PAGINA_INICIO);
            ITEM_ATUAL = 0;
            urls = new ArrayList<>();
            ids = new ArrayList<>();
        }
        loadMoviesData(PAGINA_ATUAL, MODO_ATUAL);
    }
    public void loadMoviesData(int inicio, int mode){

        if(NetworkUtils.connection_ok(getApplicationContext()) && mode != MODO_FAVORITOS){
            mMoviesAdapter.setImages(urls, ids);
            progressBar.setVisibility(View.VISIBLE);
            new MovieServiceSimple(this, this).execute(mode, inicio);
            PAGINA_ATUAL = inicio;
            mRecyclerView.setVisibility(View.VISIBLE);
        }else if(mode == MODO_FAVORITOS){
            Toast toast = Toast.makeText(getApplicationContext(), "Apenas favoritos", Toast.LENGTH_SHORT);
            toast.show();
            queryContentProvider();
            PAGINA_ATUAL = inicio;
            progressBar.setVisibility(View.INVISIBLE);
        }else if(!NetworkUtils.connection_ok(getApplicationContext())){
            Toast toast = Toast.makeText(getApplicationContext(), "Sem Conexão à Internet", Toast.LENGTH_SHORT);
            toast.show();
            progressBar.setVisibility(View.INVISIBLE);
            mRecyclerView.setVisibility(View.INVISIBLE);
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
                urls.add(posterPath.toString());
                ids.add(cursor.getInt(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID)));
                cursor.moveToNext();
            }
        }
        cursor.close();
        if(urls.size() > 0 && ids.size() > 0){
            mMoviesAdapter.setImages(urls, ids);
        }else{
            mRecyclerView.setVisibility(View.INVISIBLE);
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
                urls.add(NetworkUtils.posterUrl(movies_data.get(i).getPost_path()).toString());
                ids.add(movies_data.get(i).getId());
            }
            progressBar.setVisibility(View.INVISIBLE);
            mMoviesAdapter.setImages(urls, ids);
            if(ITEM_ATUAL != -1 && ITEM_ATUAL != 0){
                layoutManager.scrollToPosition(ITEM_ATUAL);
                ITEM_ATUAL = -1;
            }
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
            urls.clear();
            ids.clear();
            PAGINA_ATUAL = getResources().getInteger(R.integer.PAGINA_INICIO);
            MODO_ATUAL = MODO_FAVORITOS;
            loadMoviesData(PAGINA_ATUAL, MODO_ATUAL);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(MODO_ATUAL_CALLBACK_STRING, MODO_ATUAL);
        outState.putInt(PAGINA_ATUAL_CALLBACK_STRING, PAGINA_ATUAL);
        outState.putInt(ITEM_ATUAL_CALLBACK_STRING, layoutManager.findFirstCompletelyVisibleItemPosition());
        outState.putStringArrayList(LIST_URLS_ATUAL_CALLBACK_STRING, urls);
        outState.putIntegerArrayList(LIST_IDS_ATUAL_CALLBACK_STRING, ids);
    }


}
