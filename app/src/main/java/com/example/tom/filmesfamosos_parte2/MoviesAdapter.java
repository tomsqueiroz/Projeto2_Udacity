package com.example.tom.filmesfamosos_parte2;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.List;

/**
 * Created by Tom on 09/02/2018.
 */

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.Movies_AdapterViewHolder>{

    private final Movies_AdapterOnClickHandler mClickHandler;
    private List<URL> urls;
    private List<Integer> ids;
    private Context context;


    public interface Movies_AdapterOnClickHandler{
        void onClick (int adapter_position, Integer id);
    }

    public MoviesAdapter(Movies_AdapterOnClickHandler clickHandler, Context context){
        mClickHandler = clickHandler;
        this.context = context;
    }

    public class Movies_AdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final ImageView mImage;
        public final ProgressBar mProgbar;
        public Movies_AdapterViewHolder(View view){
            super(view);
            mImage =  view.findViewById(R.id.item_imageview);
            mProgbar =  view.findViewById(R.id.item_progressbar);
            view.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {
            int adapter_pos = getAdapterPosition();
            mClickHandler.onClick(adapter_pos, ids.get(adapter_pos));
        }
    }

    @Override
    public Movies_AdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        int id_grid_item = R.layout.item_movies_grid;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(id_grid_item, parent, shouldAttachToParentImmediately);
        Movies_AdapterViewHolder holder = new Movies_AdapterViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(Movies_AdapterViewHolder holder, int position) {
        URL url = urls.get(position);
        Picasso.with(context).load(url.toString()).into(holder.mImage);
        holder.mProgbar.setVisibility(View.INVISIBLE);
    }

    @Override
    public int getItemCount() {
        if(urls == null) return 0;
        return urls.size();
    }


    public void setImages(List<URL> urls, List<Integer> ids){
        this.urls = urls;
        this.ids = ids;
        notifyDataSetChanged();
    }
}
