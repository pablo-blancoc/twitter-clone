package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class TimelineActivity extends AppCompatActivity {

    public static final String TAG = "TimelineActivity";
    private TwitterClient client;
    RecyclerView rvTweets;
    TweetsAdapter adapter;
    List<Tweet> tweets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

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

        // Fill our timeline with tweets
        this.populateHomeTimeline();
    }

    /**
     * Gets timeline's tweets by making a GET request from the client to Twitter's API
     */
    private void populateHomeTimeline() {
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.d(TAG, "onSuccess!" + json.toString());
                JSONArray jsonArray = json.jsonArray;
                try {
                    tweets.addAll(Tweet.fromJsonArray(jsonArray));
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    Log.e(TAG, "JSON exception on populateHomeTimeline", e);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "Error while getting timeline: " + response, throwable);
            }
        });
    }
}