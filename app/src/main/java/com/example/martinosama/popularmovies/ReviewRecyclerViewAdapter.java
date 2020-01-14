package com.example.martinosama.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;


public class ReviewRecyclerViewAdapter extends RecyclerView.Adapter<ReviewRecyclerViewAdapter.ViewHolder> {

    private List<String> author,content;
    private LayoutInflater mInflater;

    ReviewRecyclerViewAdapter(Context context, List<String> author,List<String> cotent) {
        this.mInflater = LayoutInflater.from(context);
        this.author = author;
        this.content = cotent;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recyclerview_reviews, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int i) {
        holder.autherText.setText(author.get(i));
        holder.cotentText.setText(content.get(i));
    }

    @Override
    public int getItemCount() {
        return author.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView autherText,cotentText;

        ViewHolder(View itemView) {
            super(itemView);
            autherText = (TextView) itemView.findViewById(R.id.review_Author_Text);
            cotentText  = (TextView) itemView.findViewById(R.id.review_Content_Text);
        }
    }
}