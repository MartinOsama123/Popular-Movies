package com.example.martinosama.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Martin Osama on 5/4/2018.
 */

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {

    private List<String> mPosterLink;
    private LayoutInflater mInflater;
    public ItemClickListener mClickListener;

    MyRecyclerViewAdapter(Context context,List<String> posterLink) {
        this.mInflater = LayoutInflater.from(context);
        this.mPosterLink = posterLink;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recyclerview_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int i) {
        Picasso.with(holder.mImageView.getContext()).load("http://image.tmdb.org/t/p/w185"+mPosterLink.get(i)).into(holder.mImageView);
    }

    @Override
    public int getItemCount() {
        return mPosterLink.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView mImageView;

        ViewHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.moviePoster);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mImageView = (ImageView) view.findViewById(R.id.moviePoster);
             mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    String getItem(int id) {
        return mPosterLink.get(id);
    }

    void setClickListener(final ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}