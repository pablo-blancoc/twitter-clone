package com.codepath.apps.restclienttemplate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.jetbrains.annotations.NotNull;
import org.parceler.Parcels;

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import okhttp3.Headers;

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder> {

    // CONSTANTS
    public static final int REQUEST_CODE = 24;

    // Atrributes
    private TwitterClient client;
    private Context context;
    private List<Tweet> tweets;
    private Drawable LIKED;
    private Drawable RETWEETED;
    private Drawable NOT_LIKED;
    private Drawable NOT_RETWEETED;

    /**
     * Constructor for the TweetsAdapter
     * @param context: The context to bind it to
     * @param tweets: The list of tweets to show. The data it contains
     */
    public TweetsAdapter(Context context, List<Tweet> tweets) {
        this.context = context;
        this.tweets = tweets;
        LIKED = ContextCompat.getDrawable(context, R.drawable.ic_vector_heart);
        RETWEETED = ContextCompat.getDrawable(context, R.drawable.ic_vector_retweet);
        NOT_LIKED = ContextCompat.getDrawable(context, R.drawable.ic_vector_heart_stroke);
        NOT_RETWEETED = ContextCompat.getDrawable(context, R.drawable.ic_vector_retweet_stroke);

         this.client = TwitterApp.getRestClient(context);
    }

    /**
     * For each tweet, inflate a layout
     * @param parent: The parent ViewGroup to inflate the layout to
     * @param viewType: Type of view to inflate
     * @return TweetsAdapter.ViewHolder
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tweet, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Get data from our Tweets array and bind it into a ViewHolder
     * @param holder: The ViewHolder data should bind to
     * @param position: The position to grab data from
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Tweet tweet = this.tweets.get(position);

        holder.bind(tweet);
    }

    /**
     * Returns the number of tweets
     * @return int
     */
    @Override
    public int getItemCount() {
        return this.tweets.size();
    }

    /**
     * Clean all elements within the RecyclerView.Adapter
     */
    public void clear() {
        this.tweets.clear();
        notifyDataSetChanged();
    }

    /**
     * Add a list of Tweets to Adapter data
     * @param list: List of Tweets to add
     */
    public void addAll(List<Tweet> list) {
        this.tweets.addAll(list);
        notifyDataSetChanged();
    }


    /**
     * Class for TweetsAdapter ViewHolder
     */
    public class ViewHolder extends RecyclerView.ViewHolder {

        // Attributes
        ImageView ivProfileImage;
        TextView tvBody;
        TextView tvScreenName;
        TextView tvUsername;
        TextView tvTimeAgo;
        ImageView ivImage;
        TextView likeCount;
        TextView retweetCount;

        // Buttons
        ImageView like;
        ImageView retweet;
        ImageView comment;
        ImageView share;


        /**
         * Constructor for the ViewHolder
         * @param itemView: A View of a tweet
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // Find references on .xml
            this.ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
            this.tvBody = itemView.findViewById(R.id.tvBody);
            this.tvScreenName = itemView.findViewById(R.id.tvScreenName);
            this.tvUsername = itemView.findViewById(R.id.tvUsername);
            this.tvTimeAgo = itemView.findViewById(R.id.tvTimeAgo);
            this.ivImage = itemView.findViewById(R.id.ivImage);
            this.likeCount = itemView.findViewById(R.id.likeCount);
            this.retweetCount = itemView.findViewById(R.id.retweetCount);
            this.like = itemView.findViewById(R.id.like);
            this.retweet = itemView.findViewById(R.id.retweet);
            this.comment = itemView.findViewById(R.id.comment);
            this.share = itemView.findViewById(R.id.share);
        }

        /**
         * Bind data to the ViewHolder object
         * @param tweet: The tweet to bind to the View
         */
        public void bind(final Tweet tweet) {
            this.tvBody.setText(tweet.getBody());
            this.tvScreenName.setText(tweet.getUser().name);
            this.tvUsername.setText(String.format("@%s", tweet.getUser().username));
            this.tvTimeAgo.setText(tweet.getCreatedAt());
            this.likeCount.setText(String.valueOf(tweet.likes));
            this.retweetCount.setText(String.valueOf(tweet.retweets));

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


            Glide.with(context)
                    .load(tweet.getUser().profileImageUrl)
                    .fitCenter()
                    .circleCrop()
                    .into(this.ivProfileImage);
            if( tweet.getMedia().length() > 1 ) {
                this.ivImage.setVisibility(View.VISIBLE);
                Glide.with(context)
                        .load(tweet.getMedia())
                        .fitCenter()
                        .transform(new RoundedCornersTransformation(30, 10))
                        .into(this.ivImage);
            } else {
                this.ivImage.setVisibility(View.GONE);
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
                    Intent intent = new Intent(v.getContext(), ComposeActivity.class);
                    intent.putExtra("id", id);
                    intent.putExtra("username", name);
                    context.startActivity(intent);
                }
            });

        }

    }

}
