package com.misht.twitterapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterApp extends AppCompatActivity {

    private SharedPreferences preferences;
    private Twitter twitter;

    @Override
    protected void onResume() {
        super.onResume();

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        //We have authenticated
       if (preferences.contains(TwitterUtils.TWITTER_OAUTH_KEY) && preferences.contains(TwitterUtils.TWITTER_OAUTH_SECRET)) {
           setContentView(R.layout.activity_list_tweets);
            final LinearLayout list_tweets = (LinearLayout) findViewById(R.id.list_tweets);
            final ProgressBar progressBar = (ProgressBar)findViewById(R.id.progressBar);

           new Thread(new Runnable() {
               @Override
               public void run() {
                   try {
                       List<Status> tweets = getTwitter().getHomeTimeline();
                       for (final Status s : tweets) {
                           Log.d("tweet", s.toString());
                           runOnUiThread(new Runnable() {
                               @Override
                               public void run() {
                                   View tweekLayout = LayoutInflater.from(TwitterApp.this).inflate(R.layout.activity_tweet_view, null);
                                   ImageView img = (ImageView) tweekLayout.findViewById(R.id.tweet_picture);
                                   //Ion.with(img).load(s.getUser().getBiggerProfileImageURL());
                                   TextView text = (TextView) tweekLayout.findViewById(R.id.tweet_text);
                                   text.setText(s.getText());
                                   list_tweets.addView(tweekLayout);
                               }
                           });
                       }
                       //Done loading
                       runOnUiThread(new Runnable() {
                           @Override
                           public void run() {
                               progressBar.setVisibility(View.INVISIBLE);
                           }
                       });
                   } catch (TwitterException e) {
                       e.printStackTrace();
                   }
               }
           }).start();
       }
       else {
           Intent login = new Intent(this, LoginTwitter.class);
           startActivity(login);
           finish();
       }
    }

    //To load a twitter object with credentials
    private Twitter getTwitter() {
        if (twitter != null) return twitter;

        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setOAuthConsumerKey(TwitterUtils.TWITTER_APP_KEY)
                .setOAuthConsumerSecret(TwitterUtils.TWITTER_APP_SECRET)
                .setOAuthAccessToken(preferences.getString(TwitterUtils.TWITTER_OAUTH_KEY, ""))
                .setOAuthAccessTokenSecret(preferences.getString(TwitterUtils.TWITTER_OAUTH_SECRET, ""));

        TwitterFactory tf = new TwitterFactory(cb.build());
        twitter = tf.getInstance();
        return twitter;
    }
}


