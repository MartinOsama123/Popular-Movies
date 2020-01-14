package com.example.martinosama.popularmovies;

import android.content.Context;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.List;

public class TrailerRecyclerViewAdapter extends RecyclerView.Adapter<TrailerRecyclerViewAdapter.ViewHolder> {

    private List<String> mYoutubeLinks;
    private LayoutInflater mInflater;
    private Context mContext;
    public ItemClickListener mClickListener;

    TrailerRecyclerViewAdapter(Context context, List<String> posterLink) {
        this.mInflater = LayoutInflater.from(context);
        this.mContext = context;
        this.mYoutubeLinks = posterLink;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recyclerview_trailers, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int i) {
        Picasso.with(mContext).load("http://img.youtube.com/vi/"+mYoutubeLinks.get(i)+"/0.jpg").into(holder.imageView);
    }
    @Override
    public int getItemCount() {
        return mYoutubeLinks.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
       ImageView imageView;

        ViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.MediaPreview);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            imageView = (ImageView) view.findViewById(R.id.MediaPreview);
             mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    String getItem(int id) {
        return mYoutubeLinks.get(id);
    }

    void setClickListener(final ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}