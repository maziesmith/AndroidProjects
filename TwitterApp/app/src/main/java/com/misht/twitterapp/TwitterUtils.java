package com.misht.twitterapp;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.Browser;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

public class TwitterUtils extends Service {

   /* public final static String TWITTER_APP_KEY = "3S0Dtg4M9E31I2Mjma8p3CfaU";
    public final static String TWITTER_APP_SECRET = "Wq9ODvNCWXFgOvJBZ8AHJQve1pajWKu2EfJgIlFjXdYos3Ol1r";
    public final static String TWITTER_APP_CALLBACK = "";*/

    public final static String TWITTER_APP_KEY = "X066QGTM81XTUrI5Cjs2ybS5t";
    public final static String TWITTER_APP_SECRET = "qjjDBIxhZWvu6QAFDRubBgEJ8iayox5FRMeysyHyClqGeVgHg6";
    public final static String TWITTER_APP_CALLBACK = "tweetit://";
    public final static String TWITTER_OAUTH_KEY = "oauth_key";
    public final static String TWITTER_OAUTH_SECRET = "oauth_secret";
    public static final String ACTION_TWITTER_AUTHENTICATE = "ACTION_TWITTER_AUTHENTICATE";
    public static final String ACTION_TWITTER_ACCESS = "ACTION_TWITTER_ACCESS";
    public static final String EXTRA_OAUTH_DATA = "EXTRA_OAUTH_DATA";
    public static Twitter twitter;
    public static RequestToken authRequest;

    private SharedPreferences preferences;
    @Override
    public void onCreate() {
        super.onCreate();

        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        twitter = TwitterFactory.getSingleton();
        twitter.setOAuthConsumer(TWITTER_APP_KEY, TWITTER_APP_SECRET);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int id) {
        if(intent == null || intent.getAction() == null) {
            return super.onStartCommand(intent, flags, id);
        }

        if(intent.getAction().equalsIgnoreCase(ACTION_TWITTER_AUTHENTICATE)) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {

                        authRequest = twitter.getOAuthRequestToken(TWITTER_APP_CALLBACK);
                        Log.d("authenticate", authRequest.getAuthenticationURL());
                        Intent auth = new Intent(Intent.ACTION_VIEW);
                        auth.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        auth.setData(Uri.parse(authRequest.getAuthenticationURL()));
                        auth.putExtra(Browser.EXTRA_APPLICATION_ID, getPackageName());
                        startActivity(auth);
                    } catch (TwitterException e) {
                        Toast.makeText(getApplicationContext(), "Unable to login.", Toast.LENGTH_SHORT).show();
                    }
                }
            }).start();
        }

        if (intent.getAction().equalsIgnoreCase(ACTION_TWITTER_ACCESS)) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        AccessToken accessToken = twitter.getOAuthAccessToken(authRequest, intent.getStringExtra(EXTRA_OAUTH_DATA));
                        Log.d("access token", accessToken.getToken());
                        preferences.edit()
                                .putString(TWITTER_OAUTH_KEY, accessToken.getToken())
                                .putString(TWITTER_OAUTH_SECRET, accessToken.getTokenSecret())
                                .apply();

                        Toast.makeText(getApplicationContext(), "Login sucessfully!", Toast.LENGTH_SHORT).show();

                        //Open the main UI
                        Intent tweetit = new Intent(getApplicationContext(), TwitterApp.class);
                        tweetit.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(tweetit);

                    } catch (TwitterException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
        return super.onStartCommand(intent, flags, id);
    }
}
