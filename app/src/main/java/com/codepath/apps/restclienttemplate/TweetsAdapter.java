package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder> {

    // Constants
    public static final String LIKED_COLOR = "#e0245e";
    public static final String RETWEET_COLOR = "#17bf63";

    // Atrributes
    private Context context;
    private List<Tweet> tweets;
    private Drawable LIKED;
    private Drawable RETWEETED;

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
        public void bind(Tweet tweet) {
            this.tvBody.setText(tweet.getBody());
            this.tvScreenName.setText(tweet.getUser().name);
            this.tvUsername.setText(String.format("@%s", tweet.getUser().username));
            this.tvTimeAgo.setText(tweet.getCreatedAt());
            this.likeCount.setText(String.valueOf(tweet.likes));
            this.retweetCount.setText(String.valueOf(tweet.retweets));

            // Tweet is liked
            if( tweet.isLiked ) {
                this.like.setImageDrawable(LIKED);
                this.like.setColorFilter(Integer.parseInt(LIKED_COLOR));
            }

            // Tweet is retweeted
            if( tweet.isRetweeted ) {
                this.retweet.setImageDrawable(RETWEETED);
                this.retweet.setColorFilter(Integer.parseInt(RETWEET_COLOR));
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
        }
    }

}
