package com.example.martinosama.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity implements MyRecyclerViewAdapter.ItemClickListener {

    MyRecyclerViewAdapter adapter;
    RecyclerView recyclerView;
    ArrayList<String> links, title, overview, rating, date, ID;
    ProgressBar mLoadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = (RecyclerView) findViewById(R.id.rvNumbers);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb);
        recyclerView.setLayoutManager(new GridLayoutManager(this,calculateNoOfColumns(this)));
        if (savedInstanceState != null) {
            Log.i("SAVEDIN","ENTERED\n");
            links = savedInstanceState.getStringArrayList("MOVIE_LINKS");
            date = savedInstanceState.getStringArrayList("MOVIE_DATE");
            rating = savedInstanceState.getStringArrayList("MOVIE_RATING");
            overview = savedInstanceState.getStringArrayList("MOVIE_OVERVIEW");
            title = savedInstanceState.getStringArrayList("MOVIE_TITLE");
            ID = savedInstanceState.getStringArrayList("MOVIE_ID");
            adapter = new MyRecyclerViewAdapter(this, links);
            adapter.setClickListener(this);
            recyclerView.setAdapter(adapter);
        } else {
            new MovieQueryTask().execute(NetworkUtils.buildUrl("popular"));

        }

    }

    public void callJSON(URL url) {
        new MovieQueryTask().execute(url);

    }

    @Override
    public void onItemClick(View view, int i) {
        Intent intent = new Intent(this, DetailActivity.class);
        Bundle extras = new Bundle();
        extras.putString("OVERVIEW", overview.get(i));
        extras.putString("DATE", date.get(i));
        extras.putString("RATING", rating.get(i));
        extras.putString("TITLE", title.get(i));
        extras.putString("LINKS", links.get(i));
        extras.putString("IDS", ID.get(i));
        intent.putExtras(extras);
        startActivity(intent);
    }

    public class MovieQueryTask extends AsyncTask<URL, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
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
            links = new ArrayList<String>();
            overview = new ArrayList<String>();
            rating = new ArrayList<String>();
            title = new ArrayList<String>();
            date = new ArrayList<String>();
            ID = new ArrayList<String>();
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (movieResults != null && !movieResults.equals("")) {
                try {
                    JSONObject reader = new JSONObject(movieResults);
                    Log.i("LINKS", reader.toString());
                    JSONArray jsonArray = reader.getJSONArray("results");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        links.add(jsonArray.optJSONObject(i).optString("poster_path"));
                        date.add(jsonArray.optJSONObject(i).optString("release_date"));
                        overview.add(jsonArray.optJSONObject(i).optString("overview"));
                        rating.add(jsonArray.optJSONObject(i).optString("vote_average"));
                        title.add(jsonArray.optJSONObject(i).optString("original_title"));
                        ID.add(jsonArray.optJSONObject(i).optString("id"));
                    }
                    adapter = new MyRecyclerViewAdapter(MainActivity.this, links);
                    adapter.setClickListener(MainActivity.this);
                    recyclerView.setAdapter(adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemThatWasClickedId = item.getItemId();
        if (itemThatWasClickedId == R.id.popularButton) {
            callJSON(NetworkUtils.buildUrl("popular"));
            return true;
        }
        if (itemThatWasClickedId == R.id.ratingButton) {
            callJSON(NetworkUtils.buildUrl("top_rated"));
            return true;
        }
        if (itemThatWasClickedId == R.id.favoriteBtn) {
            Intent intent = new Intent(this, FavoritesActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putStringArrayList("MOVIE_LINKS",links);
        savedInstanceState.putStringArrayList("MOVIE_DATE",date);
        savedInstanceState.putStringArrayList("MOVIE_RATING",rating);
        savedInstanceState.putStringArrayList("MOVIE_OVERVIEW",overview);
        savedInstanceState.putStringArrayList("MOVIE_ID",ID);
        savedInstanceState.putStringArrayList("MOVIE_TITLE",title);
        super.onSaveInstanceState(savedInstanceState);
    }
    public static int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int scalingFactor = 200;
        int noOfColumns = (int) (dpWidth / scalingFactor);
        if(noOfColumns < 2)
            noOfColumns = 2;
        return noOfColumns;
    }
}


