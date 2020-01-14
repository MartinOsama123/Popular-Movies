package com.example.martinosama.popularmovies;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Movie;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity implements TrailerRecyclerViewAdapter.ItemClickListener {
    ImageView imageView;
    TextView overviewText,dateText,ratingText;
    ImageButton starButton;
    RecyclerView recyclerViewTrailer,recyclerViewReview;
    TrailerRecyclerViewAdapter trailerAdapter;
    ReviewRecyclerViewAdapter reviewAdapter;
    ArrayList<String> links,reviews,author,content;
    Bundle extras;
    String id;
    boolean On;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        imageView = (ImageView) findViewById(R.id.thumbnail);
        overviewText = (TextView)findViewById(R.id.overviewText);
        dateText = (TextView)findViewById(R.id.releaseText);
        ratingText = (TextView)findViewById(R.id.ratingText);
        starButton = (ImageButton)findViewById(R.id.starButton);
        recyclerViewTrailer = (RecyclerView)findViewById(R.id.recyclerViewTrailer);
        recyclerViewReview = (RecyclerView)findViewById(R.id.recycleViewReview);
        recyclerViewTrailer.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,true));
        recyclerViewReview.setLayoutManager(new LinearLayoutManager(this));

        ActionBar actionBar = this.getSupportActionBar();
        if(actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);
        if (savedInstanceState != null) {
            extras = savedInstanceState.getBundle("MOVIE_BUNDLE");
            Picasso.with(imageView.getContext()).load("http://image.tmdb.org/t/p/w500" + extras.getString("LINKS")).placeholder(R.drawable.place_holder).into(imageView);
            overviewText.setText(extras.getString("OVERVIEW"));
            dateText.setText(extras.getString("DATE"));
            ratingText.setText(extras.getString("RATING") + "/10");
            this.setTitle(extras.getString("TITLE"));
            id = extras.getString("IDS");
            savedInstanceState.putBundle("MOVIE_BUNDLE",extras);
        }
        else {
            extras = getIntent().getExtras();
            Picasso.with(imageView.getContext()).load("http://image.tmdb.org/t/p/w500" + extras.getString("LINKS")).placeholder(R.drawable.place_holder).into(imageView);
            overviewText.setText(extras.getString("OVERVIEW"));
            dateText.setText(extras.getString("DATE"));
            ratingText.setText(extras.getString("RATING") + "/10");
            this.setTitle(extras.getString("TITLE"));
             id = extras.getString("IDS");
        }
        Uri uri = MovieContract.MovieEntry.CONTENT_URI;
        uri = uri.buildUpon().appendPath(String.valueOf(id)).build();
        final Cursor cursor = getContentResolver().query(uri,null,"_id",new String[]{id},null);
         On = cursor.getCount()>0;
        if(cursor != null && On){
            starButton.setImageResource(R.drawable.star_yellow);
        }
        starButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!On){
                    addNewMovie(extras.getString("TITLE"),extras.getString("RATING"),extras.getString("DATE"),extras.getString("OVERVIEW"),extras.getString("IDS"),extras.getString("LINKS"));
                    starButton.setImageResource(R.drawable.star_yellow);
                    On = true;
                }else{
                    removeMovie(Long.parseLong(extras.getString("IDS")));
                    starButton.setImageResource(R.drawable.star_white);
                    On = false;
                }
            }
        });
        new TrailerQueryTask().execute(NetworkUtils.buildUrl(id,"videos"));
        new ReveiwQueryTask().execute(NetworkUtils.buildUrl(id,"reviews"));

    }

    @Override
    public void onItemClick(View view, int i) {
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + links.get(i)));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.youtube.com/watch?v=" + links.get(i)));
        try {
            startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            startActivity(webIntent);
        }
    }

    public class TrailerQueryTask extends AsyncTask<URL, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(URL... params) {
            URL searchUrl = params[0];
            String movieResults = null;
            try {
                movieResults = NetworkUtils.getResponseFromHttpUrl(searchUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return movieResults;
        }

        @Override
        protected void onPostExecute(String movieResults) {

            if (movieResults != null && !movieResults.equals("")) {
                try {
                    links = new ArrayList<String>();
                    reviews = new ArrayList<String>();
                    JSONObject reader = new JSONObject(movieResults);
                    JSONArray jsonArray = reader.getJSONArray("results");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        links.add(jsonArray.optJSONObject(i).optString("key"));
                    }
                    trailerAdapter = new TrailerRecyclerViewAdapter(DetailActivity.this,links);
                    trailerAdapter.setClickListener(DetailActivity.this);
                    recyclerViewTrailer.setAdapter(trailerAdapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public class ReveiwQueryTask extends AsyncTask<URL, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(URL... params) {
            URL searchUrl = params[0];
            String movieResults = null;
            try {
                movieResults = NetworkUtils.getResponseFromHttpUrl(searchUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return movieResults;
        }

        @Override
        protected void onPostExecute(String movieResults) {

            if (movieResults != null && !movieResults.equals("")) {
                try {
                    author = new ArrayList<String>();
                    content = new ArrayList<String>();
                    JSONObject reader = new JSONObject(movieResults);
                    JSONArray jsonArray = reader.getJSONArray("results");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        author.add(jsonArray.optJSONObject(i).optString("author"));
                        content.add(jsonArray.optJSONObject(i).optString("content"));
                    }
                    reviewAdapter = new ReviewRecyclerViewAdapter(DetailActivity.this,author,content);
                    recyclerViewReview.setAdapter(reviewAdapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
           finish();
           return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private Uri addNewMovie(String title, String rating,String date,String overview,String id,String poster) {
        ContentValues cv = new ContentValues();
        cv.put(MovieContract.MovieEntry.COLUMN_MOVIE_DATE,date);
        cv.put(MovieContract.MovieEntry.COLUMN_MOVIE_OVERVIEW,overview);
        cv.put(MovieContract.MovieEntry.COLUMN_MOVIE_RATING,rating);
        cv.put(MovieContract.MovieEntry.COLUMN_MOVIE_NAME,title);
        cv.put(MovieContract.MovieEntry._ID,Long.parseLong(id));
        cv.put(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER,poster);
        return getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI,cv);
    }
    private int removeMovie(long id) {
        Uri uri = MovieContract.MovieEntry.CONTENT_URI;
        uri = uri.buildUpon().appendPath(String.valueOf(id)).build();
       return getContentResolver().delete(uri, null, null);
    }
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBundle("MOVIE_BUNDLE",extras);

        super.onSaveInstanceState(savedInstanceState);
    }
}
