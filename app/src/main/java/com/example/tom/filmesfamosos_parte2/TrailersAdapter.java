package com.example.tom.filmesfamosos_parte2;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.net.URL;
import java.util.List;

/**
 * Created by Tom on 24/03/2018.
 */

public class TrailersAdapter extends RecyclerView.Adapter<TrailersAdapter.TrailersAdapterViewHolder>{

    private final TrailersAdapter.TrailersAdapterOnClickHandler mClickHandler;
    private int numTrailers = 0;
    private Context context;


    public interface TrailersAdapterOnClickHandler{
        void onClickTrailer (int adapter_position);
    }

    public TrailersAdapter(TrailersAdapter.TrailersAdapterOnClickHandler clickHandler, Context context, int numTrailers){
        mClickHandler = clickHandler;
        this.context = context;
        this.numTrailers = numTrailers;
    }




    public class TrailersAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final ImageView mImage;
        public final TextView mTextView;

        public TrailersAdapterViewHolder(View view){
            super(view);
            mImage =  view.findViewById(R.id.itemPlayButton);
            mTextView = view.findViewById(R.id.itemlabelTrailer);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapter_pos = getAdapterPosition();
            mClickHandler.onClickTrailer(adapter_pos);
        }
    }

    @Override
    public TrailersAdapter.TrailersAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        int id_grid_item = R.layout.item_trailers;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(id_grid_item, parent, shouldAttachToParentImmediately);
        TrailersAdapter.TrailersAdapterViewHolder holder = new TrailersAdapter.TrailersAdapterViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(TrailersAdapter.TrailersAdapterViewHolder holder, int position) {
        int i = position + 1;
        holder.mTextView.setText("Trailer " + i);
    }

    @Override
    public int getItemCount() {
        return numTrailers;
    }
}
