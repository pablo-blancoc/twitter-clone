package com.codepath.apps.restclienttemplate.models;

import android.util.Log;

import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Parcel
public class Tweet {

    // Constants
    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;
    private static final String TAG = "Tweet";

    // Attributes
    public String id;
    public String body;
    public String createdAt;
    public User user;
    public Media media;
    public int likes;
    public int retweets;
    public int comments;
    public boolean isLiked;
    public boolean isRetweeted;
    public String inReplyToTweet;
    public String inReplyToUsername;


    /**
     * Empty constructor for Parcel
     */
    public Tweet() {
    }

    /**
     * Create a Tweet from a JSONObject that Twitter API responded
     * @param jsonObject: The JSONObject to turn to a Tweet
     * @return Tweet
     * @throws JSONException: Key not found on object
     */
    public static Tweet fromJson(JSONObject jsonObject) throws JSONException {
        Tweet tweet = new Tweet();
        try {
            tweet.body = jsonObject.getString("full_text");
        } catch (JSONException e) {
            tweet.body = jsonObject.getString("text");
        }
        tweet.createdAt = jsonObject.getString("created_at");
        tweet.user = User.fromJSON(jsonObject.getJSONObject("user"));
        tweet.likes = jsonObject.getInt("favorite_count");
        tweet.retweets = jsonObject.getInt("retweet_count");
        tweet.id = jsonObject.getString("id");
        tweet.isLiked = jsonObject.getBoolean("favorited");
        tweet.isRetweeted = jsonObject.getBoolean("retweeted");
        tweet.inReplyToTweet = jsonObject.getString("in_reply_to_status_id");
        tweet.inReplyToUsername = jsonObject.getString("in_reply_to_screen_name");


        JSONObject entities = jsonObject.getJSONObject("entities");
        try {
            JSONArray jsonArray = entities.getJSONArray("media");
            JSONObject object = jsonArray.getJSONObject(0);
            tweet.media = Media.fromJsonObject(object);
        } catch (JSONException e) {
            Log.e(TAG, "No media to retrieve");
            // e.printStackTrace();
        }

        return tweet;
    }

    /**
     * Creates a list of tweets out of a list of JSONObjects that Twitter's API responded
     * @param jsonArray: The JSONArray to turn into a list of Tweets
     * @return: List<Tweet>
     */
    public static List<Tweet> fromJsonArray(JSONArray jsonArray) throws JSONException {
        List<Tweet> tweets = new ArrayList<>();
        for(int i=0; i< jsonArray.length(); i++) {
            tweets.add(fromJson(jsonArray.getJSONObject(i)));
        }
        return tweets;
    }

    public static List<Tweet> fromSearch(JSONObject object) throws JSONException {
        List<Tweet> tweets = new ArrayList<>();
        JSONArray jsonArray = object.getJSONArray("statuses");
        if(jsonArray == null){
            return tweets;
        }

        for(int i=0; i< jsonArray.length(); i++) {
            tweets.add(fromJson(jsonArray.getJSONObject(i)));
        }
        return tweets;
    }

    /**
     * Getter for Tweet's body
     * @return String
     */
    public String getBody() {
        return body;
    }

    /**
     * Getter for Tweet's createdAt
     * @return String
     */
    public String getCreatedAt() {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        try {
            long time = sf.parse(this.createdAt).getTime();
            long now = System.currentTimeMillis();

            final long diff = now - time;
            if (diff < MINUTE_MILLIS) {
                return "?? 1m";
            } else if (diff < 2 * MINUTE_MILLIS) {
                return "?? 1m";
            } else if (diff < 50 * MINUTE_MILLIS) {
                return "?? " + diff / MINUTE_MILLIS + "m";
            } else if (diff < 90 * MINUTE_MILLIS) {
                return "?? " + "1h";
            } else if (diff < 24 * HOUR_MILLIS) {
                return "?? " + diff / HOUR_MILLIS + "h";
            } else if (diff < 48 * HOUR_MILLIS) {
                return "?? " + "1d";
            } else {
                return diff / DAY_MILLIS + "d";
            }
        } catch (ParseException e) {
            Log.i(TAG, "getRelativeTimeAgo failed");
            e.printStackTrace();
        }

        return "";
    }

    /**
     * Getter for Tweet's user
     * @return User
     */
    public User getUser() {
        return user;
    }

    /**
     * Getter for Tweet's media
     * @return User
     */
    public String getMedia() {
        try {
            if (this.media.type.equals("photo")) {
                return this.media.imagePath;
            } else {
                return "";
            }
        } catch (NullPointerException e) {
            return "";
        }
    }

}
