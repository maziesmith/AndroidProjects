package com.misht.twitterapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

/**
 * Created by Omen23 on 27/02/2018.
 */

public class LoginTwitter extends AppCompatActivity{

    private SharedPreferences preferences;
    private Intent twitterUtils;
    private Button login;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        twitterUtils = new Intent(this, TwitterApp.class);

        if(preferences.contains(TwitterUtils.TWITTER_OAUTH_KEY) && preferences.contains(TwitterUtils.TWITTER_OAUTH_SECRET)) {
            Intent tweet = new Intent(this, TwitterApp.class);
            startActivity(tweet);
            finish();
        }
        else { //This is used the first time that the user login to the app
           /*  setContentView(R.layout.activity_twitter_app);
            login = findViewById(R.id.button);
           login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getApplicationContext(), "Mish is here", Toast.LENGTH_SHORT).show();
                    twitterUtils.setAction(TwitterUtils.ACTION_TWITTER_AUTHENTICATE);
                    startService(twitterUtils);
                }
            });*/
            twitterUtils.setAction(TwitterUtils.ACTION_TWITTER_AUTHENTICATE);
            startService(twitterUtils);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Uri twitterData = intent.getData();
        if (twitterData != null && twitterData.toString().startsWith(TwitterUtils.TWITTER_APP_CALLBACK)) {
            twitterUtils.setAction(TwitterUtils.ACTION_TWITTER_ACCESS);
            twitterUtils.putExtra(TwitterUtils.EXTRA_OAUTH_DATA, twitterData.getQueryParameter("oauth_verifier"));
            startService(twitterUtils);
        }
    }
}
