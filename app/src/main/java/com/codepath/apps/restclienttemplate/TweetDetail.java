package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.EndlessRecyclerViewScrollListener;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import okhttp3.Headers;

public class TweetDetail extends AppCompatActivity {

    // CONSTANTS
    Drawable LIKED;
    Drawable RETWEETED;
    Drawable NOT_LIKED;
    Drawable NOT_RETWEETED;

    // Attributes
    ImageView ivProfileImage;
    TextView tvBody;
    TextView tvScreenName;
    TextView tvUsername;
    TextView tvTimeAgo;
    ImageView ivImage;
    TextView likeCount;
    TextView retweetCount;
    TextView replyTo;
    TextView replyToTitle;
    TwitterClient client;
    String max_id;
    List<Tweet> replies;
    RecyclerView rvReplies;
    TweetsAdapter adapter;

    Tweet tweet;
    Toolbar toolbar;

    // Buttons
    ImageView like;
    ImageView retweet;
    ImageView comment;
    ImageView share;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_detail);

        // Get tweet
        this.tweet = Parcels.unwrap(getIntent().getParcelableExtra("tweet"));
        this.max_id = tweet.id;

        // Setup Toolbar
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        this.toolbar.setTitle("");
        setSupportActionBar(this.toolbar);

        // Drawables
         this.LIKED= ContextCompat.getDrawable(this, R.drawable.ic_vector_heart);
         this.RETWEETED= ContextCompat.getDrawable(this, R.drawable.ic_vector_retweet);
         this.NOT_LIKED= ContextCompat.getDrawable(this, R.drawable.ic_vector_heart_stroke);
         this.NOT_RETWEETED= ContextCompat.getDrawable(this, R.drawable.ic_vector_retweet_stroke);

        // Find references on .xml
        this.ivProfileImage = findViewById(R.id.ivProfileImage);
        this.tvBody = findViewById(R.id.tvBody);
        this.tvScreenName = findViewById(R.id.tvScreenName);
        this.tvUsername = findViewById(R.id.tvUsername);
        this.tvTimeAgo = findViewById(R.id.tvTimeAgo);
        this.ivImage = findViewById(R.id.ivImage);
        this.likeCount = findViewById(R.id.likeCount);
        this.retweetCount = findViewById(R.id.retweetCount);
        this.like = findViewById(R.id.like);
        this.retweet = findViewById(R.id.retweet);
        this.comment = findViewById(R.id.comment);
        this.share = findViewById(R.id.share);
        this.replyTo = findViewById(R.id.tvReplyTo);
        this.replyToTitle = findViewById(R.id.tvReplyToTitle);
        this.rvReplies = findViewById(R.id.rvReplies);

        this.client = TwitterApp.getRestClient(this);

        this.tvBody.setText(tweet.getBody());
        this.tvScreenName.setText(tweet.getUser().name);
        this.tvUsername.setText(String.format("@%s", tweet.getUser().username));
        this.tvTimeAgo.setText(tweet.getCreatedAt() + " ago");
        this.likeCount.setText(String.valueOf(tweet.likes));
        this.retweetCount.setText(String.valueOf(tweet.retweets));


        // RecyclerView for replies
        // Init the list of tweets and adapter
        this.replies = new ArrayList<>();
        this.adapter = new TweetsAdapter(this, this.replies);

        // RecyclerView setup (Layout Manager and set TweetsAdapter)
        this.rvReplies.setLayoutManager(new LinearLayoutManager(this));
        this.rvReplies.setAdapter(this.adapter);


        // Tweet is liked
        if( tweet.isLiked ) {
            this.like.setImageDrawable(LIKED);
            this.like.setColorFilter(Color.argb(255, 224, 36, 94));
        } else {
            this.like.setImageDrawable(NOT_LIKED);
            this.like.setColorFilter(null);
        }

        // Tweet is retweeted
        if( tweet.isRetweeted ) {
            this.retweet.setImageDrawable(RETWEETED);
            this.like.setColorFilter(Color.argb(255, 23, 191, 99));
        } else {
            this.retweet.setImageDrawable(NOT_RETWEETED);
            this.retweet.setColorFilter(null);
        }


        Glide.with(this)
                .load(tweet.getUser().profileImageUrl)
                .fitCenter()
                .circleCrop()
                .into(this.ivProfileImage);
        if( tweet.getMedia().length() > 1 ) {
            this.ivImage.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(tweet.getMedia())
                    .fitCenter()
                    .transform(new RoundedCornersTransformation(30, 10))
                    .into(this.ivImage);
        } else {
            this.ivImage.setVisibility(View.GONE);
        }

        // Set replyTo
        if(!tweet.inReplyToUsername.equals("null")) {
            this.replyTo.setVisibility(View.VISIBLE);
            this.replyToTitle.setVisibility(View.VISIBLE);
            this.replyTo.setText("@" + tweet.inReplyToUsername);
        } else {
            this.replyTo.setVisibility(View.GONE);
            this.replyToTitle.setVisibility(View.GONE);
        }

        /**
         * Like or unlike a tweet
         */
        this.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tweet.isLiked) {
                    // If tweet is liked then dislike it
                    client.dislike(tweet.id, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            tweet.isLiked = false;
                            tweet.likes -= 1;
                            likeCount.setText(String.valueOf(tweet.likes));
                            like.setImageDrawable(NOT_LIKED);
                            like.setColorFilter(null);
                        }

                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                            Log.e("TweetsAdapter", "Not disliked: " + response, throwable);
                        }
                    });
                } else {
                    // If tweet is not liked then like it
                    client.like(tweet.id, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            tweet.isLiked = true;
                            tweet.likes += 1;
                            likeCount.setText(String.valueOf(tweet.likes));
                            like.setImageDrawable(LIKED);
                            like.setColorFilter(Color.argb(255, 224, 36, 94));
                        }

                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                            Log.e("TweetsAdapter", "Not liked: " + response, throwable);
                        }
                    });
                }
            }
        });

        /**
         * Retweet or unretweet a tweet
         */
        this.retweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tweet.isRetweeted) {
                    // If tweet is retweeted then undo
                    client.unretweet(tweet.id, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            tweet.isRetweeted = false;
                            tweet.retweets -= 1;
                            retweetCount.setText(String.valueOf(tweet.retweets));
                            retweet.setImageDrawable(NOT_RETWEETED);
                            retweet.setColorFilter(null);
                        }

                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                            Log.e("TweetsAdapter", "Not retweeted: " + response, throwable);
                        }
                    });
                } else {
                    // If tweet is not retweeted then retweeted it
                    client.retweet(tweet.id, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            tweet.isRetweeted = true;
                            tweet.retweets += 1;
                            retweetCount.setText(String.valueOf(tweet.retweets));
                            retweet.setImageDrawable(RETWEETED);
                            retweet.setColorFilter(Color.argb(255, 23, 191, 99));
                        }

                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                            Log.e("TweetsAdapter", "Not liked: " + response, throwable);
                        }
                    });
                }
            }
        });

        /**
         * When the comment button is pressed open compose to write a new comment
         */
        comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = tweet.id;
                String name = tweet.user.username;
                Intent intent = new Intent(TweetDetail.this, ComposeActivity.class);
                intent.putExtra("id", id);
                intent.putExtra("username", name);
                startActivity(intent);
            }
        });


        // Get comments
        client.search(max_id, "to:" + tweet.user.username, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.d("XXX", String.valueOf(json));
                JSONObject object = json.jsonObject;
                try {
                    List<Tweet> _replies = Tweet.fromSearch(object);
                    for(int i=0; i<_replies.size(); i++) {
                        Log.d("XXX", _replies.get(i).inReplyToTweet);
                        Log.d("XXX", tweet.id);
                        if(_replies.get(i).inReplyToTweet.equals(tweet.id)) {
                            replies.add(_replies.get(i));
                        }
                    }
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    Log.e("DetailActivity", "JSON exception on populateHomeTimeline", e);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e("DetailActivity", "Request exception: " + response, throwable);
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
}