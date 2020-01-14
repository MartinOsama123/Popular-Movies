package com.example.martinosama.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Movie;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.MovieViewHolder> {

    private Cursor mCursor;
    private Context mContext;
    String title,rating,overview,posterLink,ID,date;
    ItemClickListener mClickListener;
    public FavoritesAdapter(Context context) {
        this.mContext = context;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.recyclerview_favorites, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        if (!mCursor.moveToPosition(position))
            return;
        title = mCursor.getString(mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_NAME));
        rating = mCursor.getString(mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_RATING));
        overview = mCursor.getString(mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_OVERVIEW));
        date = mCursor.getString(mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_DATE));
        ID = mCursor.getString(mCursor.getColumnIndex(MovieContract.MovieEntry._ID));
        Picasso.with(mContext).load("http://image.tmdb.org/t/p/w185"+mCursor.getString(mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER))).into(holder.movieTitle);

    }

    public Cursor swapCursor(Cursor c) {
        if (mCursor == c) {
            return null;
        }
        Cursor temp = mCursor;
        this.mCursor = c;

        if (c != null) {
            this.notifyDataSetChanged();
        }
        return temp;
    }
    @Override
    public int getItemCount() {
        if(mCursor == null)return 0;
        return mCursor.getCount();
    }

    class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener  {

        ImageView movieTitle;

        public MovieViewHolder(View itemView) {
            super(itemView);
            movieTitle = (ImageView) itemView.findViewById(R.id.poster_fav);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            movieTitle = (ImageView) view.findViewById(R.id.poster_fav);
            Log.i("onPosterClick","ONCLICK");
            mClickListener.onItemClick(view, getAdapterPosition());
        }
    }
    String getItem(int id) {
        if (!mCursor.moveToPosition(id))
            return null;
        return mCursor.getString(mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER));
    }

    void setClickListener(final ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}