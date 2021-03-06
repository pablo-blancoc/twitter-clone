package com.codepath.apps.restclienttemplate;

import android.content.Context;

import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.codepath.oauth.OAuthBaseClient;
import com.github.scribejava.apis.TwitterApi;
import com.github.scribejava.core.builder.api.BaseApi;

/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/scribejava/scribejava/tree/master/scribejava-apis/src/main/java/com/github/scribejava/apis
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * 
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 * 
 */
public class TwitterClient extends OAuthBaseClient {
	public static final BaseApi REST_API_INSTANCE = TwitterApi.instance();
	public static final String REST_URL = "https://api.twitter.com/1.1/";
	public static final String REST_CONSUMER_KEY = BuildConfig.CONSUMER_KEY;
	public static final String REST_CONSUMER_SECRET = BuildConfig.CONSUMER_SECRET;

	// Landing page to indicate the OAuth flow worked in case Chrome for Android 25+ blocks navigation back to the app.
	public static final String FALLBACK_URL = "https://codepath.github.io/android-rest-client-template/success.html";

	// See https://developer.chrome.com/multidevice/android/intents
	public static final String REST_CALLBACK_URL_TEMPLATE = "intent://%s#Intent;action=android.intent.action.VIEW;scheme=%s;package=%s;S.browser_fallback_url=%s;end";

	public TwitterClient(Context context) {
		super(context, REST_API_INSTANCE,
				REST_URL,
				REST_CONSUMER_KEY,
				REST_CONSUMER_SECRET,
				null,  // OAuth2 scope, null for OAuth1
				String.format(REST_CALLBACK_URL_TEMPLATE, context.getString(R.string.intent_host),
						context.getString(R.string.intent_scheme), context.getPackageName(), FALLBACK_URL));
	}

	/**
	 * Get HomeTimeline Tweets from Twitter
	 * @param handler: how to handle the response
	 */
	public void getHomeTimeline(JsonHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/home_timeline.json");
		// Can specify query string params directly or through RequestParams.
		RequestParams params = new RequestParams();
		params.put("count", "25");
		params.put("since_id", 1);
		params.put("tweet_mode", "extended");
		client.get(apiUrl, params, handler);
	}

	/**
	 * Get more HomeTimeline Tweets from Twitter
	 * @param handler: how to handle the response
	 */
	public void getMoreHomeTimeline(String id, JsonHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/home_timeline.json");
		// Can specify query string params directly or through RequestParams.
		RequestParams params = new RequestParams();
		params.put("count", "25");
		params.put("max_id", id);
		params.put("tweet_mode", "extended");
		client.get(apiUrl, params, handler);
	}

	/**
	 * Post a new Tweet using the User's account on Twitter
	 * @param content: The text to tweet
	 * @param handler: how to handle the response
	 */
	public void tweet(String content, String reply_to, JsonHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/update.json");
		// Can specify query string params directly or through RequestParams.
		RequestParams params = new RequestParams();
		params.put("status", content);
		if( reply_to != null ) {
			params.put("in_reply_to_status_id", reply_to);
		}
		client.post(apiUrl, params, "", handler);
	}

	/**
	 * Like a tweet
	 * @param id: The id from the Tweet
	 * @param handler: how to handle the response
	 */
	public void like(String id, JsonHttpResponseHandler handler) {
		String apiUrl = getApiUrl("favorites/create.json");
		// Can specify query string params directly or through RequestParams.
		RequestParams params = new RequestParams();
		params.put("id", id);
		client.post(apiUrl, params, "", handler);
	}

	/**
	 * Dislike a tweet
	 * @param id: The id from the Tweet
	 * @param handler: how to handle the response
	 */
	public void dislike(String id, JsonHttpResponseHandler handler) {
		String apiUrl = getApiUrl("favorites/destroy.json");
		// Can specify query string params directly or through RequestParams.
		RequestParams params = new RequestParams();
		params.put("id", id);
		client.post(apiUrl, params, "", handler);
	}

	/**
	 * Retweet
	 * @param id: The id from the Tweet
	 * @param handler: how to handle the response
	 */
	public void retweet(String id, JsonHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/retweet/" + id + ".json");
		// Can specify query string params directly or through RequestParams.
		RequestParams params = new RequestParams();
		params.put("id", id);
		client.post(apiUrl, params, "", handler);
	}

	/**
	 * Dislike a tweet
	 * @param id: The id from the Tweet
	 * @param handler: how to handle the response
	 */
	public void unretweet(String id, JsonHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/unretweet/" + id + ".json");
		// Can specify query string params directly or through RequestParams.
		RequestParams params = new RequestParams();
		params.put("id", id);
		client.post(apiUrl, params, "", handler);
	}

	/**
	 * searches for specific tweets
	 * @param id: The id from the Tweet
	 * @param handler: how to handle the response
	 */
	public void search(String id, String q, JsonHttpResponseHandler handler) {
		String apiUrl = getApiUrl("search/tweets.json");
		// Can specify query string params directly or through RequestParams.
		RequestParams params = new RequestParams();
		params.put("q", q);
		// params.put("max_id", id);
		params.put("tweet_mode", "extended");
		params.put("since_id", 1);
		client.get(apiUrl, params, handler);
	}

	/* 1. Define the endpoint URL with getApiUrl and pass a relative path to the endpoint
	 * 	  i.e getApiUrl("statuses/home_timeline.json");
	 * 2. Define the parameters to pass to the request (query or body)
	 *    i.e RequestParams params = new RequestParams("foo", "bar");
	 * 3. Define the request method and make a call to the client
	 *    i.e client.get(apiUrl, params, handler);
	 *    i.e client.post(apiUrl, params, handler);
	 */
}
