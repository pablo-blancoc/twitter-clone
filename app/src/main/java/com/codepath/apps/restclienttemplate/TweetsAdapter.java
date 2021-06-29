package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder> {

    // Atrributes
    private Context context;
    private List<Tweet> tweets;

    /**
     * Constructor for the TweetsAdapter
     * @param context: The context to bind it to
     * @param tweets: The list of tweets to show. The data it contains
     */
    public TweetsAdapter(Context context, List<Tweet> tweets) {
        this.context = context;
        this.tweets = tweets;
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
     * Class for TweetsAdapter ViewHolder
     */
    public class ViewHolder extends RecyclerView.ViewHolder {

        // Attributes
        ImageView ivProfileImage;
        TextView tvBody;
        TextView tvScreenName;
        TextView tvUsername;
        TextView tvTimeAgo;

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
            Glide.with(context)
                    .load(tweet.getUser().profileImageUrl)
                    .fitCenter()
                    .circleCrop()
                    .into(this.ivProfileImage);
        }
    }

}
