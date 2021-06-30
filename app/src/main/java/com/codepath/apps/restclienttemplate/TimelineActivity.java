package com.codepath.apps.restclienttemplate;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class TimelineActivity extends AppCompatActivity {

    public static final String TAG = "TimelineActivity";
    public static final int REQUEST_CODE = 24;
    private TwitterClient client;
    private Toolbar toolbar;
    private SwipeRefreshLayout swipeContainer;
    MenuItem loading;
    RecyclerView rvTweets;
    TweetsAdapter adapter;
    List<Tweet> tweets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        // Setup Toolbar
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        this.toolbar.setTitle("");
        setSupportActionBar(this.toolbar);

        // Setup SwipeContainer
        this.swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        this.swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshTimeline();
            }
        });

        // Create an instance of TwitterClient
        client = TwitterApp.getRestClient(this);

        // Find the RecyclerView that will have the tweets
        this.rvTweets = findViewById(R.id.rvTweets);

        // Init the list of tweets and adapter
        this.tweets = new ArrayList<>();
        this.adapter = new TweetsAdapter(this, this.tweets);

        // RecyclerView setup (Layout Manager and set TweetsAdapter)
        this.rvTweets.setLayoutManager(new LinearLayoutManager(this));
        this.rvTweets.setAdapter(this.adapter);
    }

    /**
     * Gets timeline's tweets by making a GET request from the client to Twitter's API
     */
    private void populateHomeTimeline() {

        this.loading.setVisible(true);

        client.getHomeTimeline(new JsonHttpResponseHandler() {
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                JSONArray jsonArray = json.jsonArray;
                try {
                    tweets.addAll(Tweet.fromJsonArray(jsonArray));
                    adapter.notifyDataSetChanged();
                    loading.setVisible(false);
                } catch (JSONException e) {
                    Log.e(TAG, "JSON exception on populateHomeTimeline", e);
                    e.printStackTrace();
                    loading.setVisible(false);
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "Error while getting timeline: " + response, throwable);
                loading.setVisible(false);
            }
        });
    }

    /**
     * Refreshes twitter feed
     */
    private void refreshTimeline() {
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                JSONArray jsonArray = json.jsonArray;
                try {
                    // Refresh all TweetAdapter data
                    adapter.clear();
                    adapter.notifyDataSetChanged();
                    adapter.addAll(Tweet.fromJsonArray(jsonArray));
                    swipeContainer.setRefreshing(false);
                } catch (JSONException e) {
                    Log.e(TAG, "JSON exception while refreshing", e);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "Error while getting timeline: " + response, throwable);
            }
        });
    }

    /**
     * Inflates the menu to display the menu options on an xml file
     * @param menu: Menu to inflate
     * @return boolean
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu
        getMenuInflater().inflate(R.menu.timeline_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        this.loading = menu.findItem(R.id.progressBar);

        // Fill our timeline with tweets after progress-bar has been found
        this.populateHomeTimeline();

        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * Logs out of the app
     */
    private void onLogout() {
        // forget who's logged in
        this.client.clearAccessToken();

        // navigate backwards to Login screen
        finish();
    }

    /**
     * Check which option of the menu was selected
     * @param item: item selected
     * @return boolean
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.logout) {
            // logout selected
            this.onLogout();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Create a new intent to change to ComposeActivity and tweet something
     */
    private void onCompose() {
        Intent intent = new Intent(TimelineActivity.this, ComposeActivity.class);
        startActivityForResult(intent, REQUEST_CODE);
    }

    /**
     * Call onCompose() method to avoid code repetition and change to ComposeActivity and tweet something
     * @param view: from what view was it called
     */
    public void onCompose(View view) {
    this.onCompose();
    }

    /**
     * Method called when an Activity has finished successfully and has returned some data
     * @param requestCode: custom code to identify the activity
     * @param resultCode: android's code to know if activity has finished successfully
     * @param data: the data returned from activity
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if( requestCode == REQUEST_CODE && resultCode == RESULT_OK ) {
            // Get Tweet object from intent
            Tweet tweet = Parcels.unwrap(data.getParcelableExtra("tweet"));

            // Update RecyclerView with new Tweet (update data and notify adapter)
            tweets.add(0, tweet);
            adapter.notifyItemInserted(0);
            rvTweets.smoothScrollToPosition(0);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}

