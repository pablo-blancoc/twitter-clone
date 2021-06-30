package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.parceler.Parcels;

import okhttp3.Headers;

public class ComposeActivity extends AppCompatActivity {

    // Constants
    public static final int MAX_TWEET_LENGTH = 280;
    public static final String TAG = "ComposeActivity";

    // Attributes
    EditText etCompose;
    Button btnTweet;
    Button btnCancel;
    TwitterClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        // Create client
        this.client = TwitterApp.getRestClient(this);

        // Find elements on View
        this.etCompose = findViewById(R.id.etCompose);
        this.btnTweet = findViewById(R.id.btnTweet);
        this.btnCancel = findViewById(R.id.btnCancel);

        /**
         * OnClickListener to publish tweet
         */
        btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                // Hide keayboard
                InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(),0);

                String content = etCompose.getText().toString();
                if(content.isEmpty()) {
                    Snackbar.make(v, "Tweet cannot be empty", Snackbar.LENGTH_SHORT).show();
                    return;
                } else if( content.length() > MAX_TWEET_LENGTH ) {
                    Snackbar.make(v, "Tweet too long", Snackbar.LENGTH_SHORT).show();
                    return;
                } else {
                    // Make API call to publish Tweet
                    client.tweet(content, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            Log.i(TAG, "Tweet published");
                            try {
                                // Return new tweet to HomeTimeline
                                Tweet tweet = Tweet.fromJson(json.jsonObject);
                                Intent intent = new Intent();
                                intent.putExtra("tweet", Parcels.wrap(tweet));
                                setResult(RESULT_OK, intent);
                                finish();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                            Log.e(TAG, "Failure publishing tweet: " + response, throwable);
                            Snackbar.make(v, "Error publishing tweet", Snackbar.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        /**
         * OnClickListener to cancel and go back
         */
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etCompose.setText("");
                finish();
            }
        });

    }
}