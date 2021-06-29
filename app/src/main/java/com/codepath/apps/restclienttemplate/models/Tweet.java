package com.codepath.apps.restclienttemplate.models;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Tweet {

    // Attributes
    public String body;
    public String createdAt;
    public User user;

    /**
     * Create a Tweet from a JSONObject that Twitter API responded
     * @param jsonObject: The JSONObject to turn to a Tweet
     * @return Tweet
     * @throws JSONException: Key not found on object
     */
    public static Tweet fromJson(JSONObject jsonObject) throws JSONException {
        Tweet tweet = new Tweet();
        tweet.body = jsonObject.getString("text");
        tweet.createdAt = jsonObject.getString("created_at");
        tweet.user = User.fromJSON(jsonObject.getJSONObject("user"));
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
        return createdAt;
    }

    /**
     * Getter for Tweet's user
     * @return User
     */
    public User getUser() {
        return user;
    }
}
