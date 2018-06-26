package com.misht.weatherapp;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class Weather extends AppCompatActivity {

    private Button buttonWeather; //Create an instance of button
    private TextView cityField, detailsField, currentTemperatureField, humidity_field, pressure_field, weatherIcon, updatedField; //Create instances of textViews
    private Typeface weatherFont;
    private String city, description, temperature, humidity, pressure, updatedOn, iconText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_app);

        cityField = (TextView)findViewById(R.id.city_field);
        updatedField = (TextView)findViewById(R.id.updated_field);
        detailsField = (TextView)findViewById(R.id.details_field);
        currentTemperatureField = (TextView)findViewById(R.id.current_temperature_field);
        humidity_field = (TextView)findViewById(R.id.humidity_field);
        pressure_field = (TextView)findViewById(R.id.pressure_field);
        weatherIcon = (TextView)findViewById(R.id.weather_icon);
        weatherFont = Typeface.createFromAsset(getAssets(), "fonts/weathericons-regular-webfont.ttf");
        weatherIcon.setTypeface(weatherFont);

        //To make the app load and show the weather when the user clicks the button
        buttonWeather = findViewById(R.id.button);
        buttonWeather.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Timer timer= new Timer();
                timer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        WeatherLoader loadWeather = new WeatherLoader();
                        //I pass as parameters the coordenates of OULU, Finland
                        loadWeather.execute("65.0121", "25.4651");
                    }
                }, 0, 1000*60);
            }
        });
    }

    //This method load the weather data
    public static JSONObject loadWeatherData(String lat, String lon)  {
       try { //Http connection on the url
           URL url = new URL(String.format("http://api.openweathermap.org/data/2.5/weather?lat=%s&lon=%s&units=metric", lat, lon));
           HttpURLConnection connection = (HttpURLConnection) url.openConnection();
           connection.addRequestProperty("x-api-key", "93cd9aea674fed950230062027be47ba");
           BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
           StringBuffer json = new StringBuffer(1024);
           String tmp = "";
           while((tmp = reader.readLine()) != null)
               json.append(tmp).append("/n");
           reader.close();
           JSONObject entry = new JSONObject(json.toString());
           if(entry.getInt("cod") != 200) {
               return null;
           }
           return entry;
       } catch(Exception e){
           return null;
       }
    }

    //To read and show the data
    public void readData(JSONObject weatherData){
        try {
            if(weatherData != null) {
                JSONObject details = weatherData.getJSONArray("weather").getJSONObject(0);
                JSONObject main = weatherData.getJSONObject("main");
                DateFormat df = DateFormat.getDateTimeInstance();

                city = weatherData.getString("name").toUpperCase(Locale.US) + ", " + weatherData.getJSONObject("sys").getString("country");
                description = details.getString("description").toUpperCase(Locale.US);
                temperature = (int)main.getDouble("temp") + "º";
                humidity = main.getString("humidity") + "%";
                pressure = main.getString("pressure") + "hPa";
                updatedOn = df.format(new Date());
                iconText = setWeatherIcon(details.getInt("id"), weatherData.getJSONObject("sys").getLong("sunrise") * 1000, weatherData.getJSONObject("sys").getLong("sunset") * 1000);
                getSupportActionBar().hide();
                cityField.setText(city);
                updatedField.setText(updatedOn);
                detailsField.setText(description);
                currentTemperatureField.setText(temperature);
                humidity_field.setText("Humidity: "+ humidity);
                pressure_field.setText("Pressure: "+ pressure);
                weatherIcon.setText(Html.fromHtml(iconText));
            }
        }
        catch(JSONException e){

        }
    }

    //In order to set the weather icon
    public static String setWeatherIcon(int actualId, long sunrise, long sunset){
        int id = actualId / 100;
        String icon = "";
        if(actualId == 800){
            long currentTime = new Date().getTime();
            if(currentTime>=sunrise && currentTime<sunset) {
                icon = "&#xf00d;";
            } else {
                icon = "&#xf02e;";
            }
        } else {
            switch(id) {
                case 2 : icon = "&#xf01e;";
                    break;
                case 3 : icon = "&#xf01c;";
                    break;
                case 7 : icon = "&#xf014;";
                    break;
                case 8 : icon = "&#xf013;";
                    break;
                case 6 : icon = "&#xf01b;";
                    break;
                case 5 : icon = "&#xf019;";
                    break;
            }
        }
        return icon;
    }

    @Override
    //We call checkStorage on the onResume in order to check that data has storaged fine
    protected void onResume() {
        super.onResume();
        checkStorage();
    }
    /*This method show notifications
        I have made some changes as create a channel because on my android version 8.0, the other way doesn't work
        I use target because it only going to apply the changes when it is necessary
        */
    @TargetApi(Build.VERSION_CODES.O)
    public void showNotifications(){
        Intent intent = new Intent(this, Weather.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, (int)System.currentTimeMillis(), intent, 0);

        String CHANNEL_ID = "my_channel_01";
        CharSequence name = "Mishell";
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);

        //Build notification
        Notification noti = new Notification.Builder(this)
                .setContentTitle(city)
                .setContentText(temperature)
                .setSmallIcon(R.drawable.ic_wb_sunny_black_24dp)
                .setContentIntent(pIntent)
                .setChannelId(CHANNEL_ID)
                .build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(mChannel);
        noti.flags |= Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(0, noti);
    }

    //This method save the data in preference
    public void saveWeatherData() {
        SharedPreferences.Editor editor = getSharedPreferences("MY_PREFS_NAME", MODE_PRIVATE).edit();
        editor.putString("City:", city);
        editor.putString("Description:", description);
        editor.putString("Temperature:", temperature);
        editor.putString("Humidity:", humidity);
        editor.putString("Pressure:", pressure);
        editor.putString("Date:", updatedOn);
        editor.putString("Icon:", iconText);
        editor.commit();
    }

    //This method is created in order to check the storage of the data
    public void checkStorage() {
        SharedPreferences prefs = getSharedPreferences("MY_PREFS_NAME", MODE_PRIVATE);
        city = prefs.getString("City:", "");
        description = prefs.getString("Description:", "");
        temperature = prefs.getString("Temperature:", "");
        humidity = prefs.getString("Humidity:", "");
        pressure = prefs.getString("Pressure:", "");
        updatedOn = prefs.getString("Date:", "");
        iconText = prefs.getString("Icon:", "");

        if(city != null) {
            cityField.setText(city);
        }
        if(updatedOn != null) {
            updatedField.setText(updatedOn);
        }
        if(description != null) {
            detailsField.setText(description);
        }
        if(temperature != null) {
            currentTemperatureField.setText(temperature);
        }
        if(humidity != null) {
            humidity_field.setText("Humidity: "+ humidity);
        }
        if(pressure != null) {
            pressure_field.setText("Pressure: "+ pressure);
        }
        if(iconText != null) {
            weatherIcon.setText(Html.fromHtml(iconText));
        }
    }

    public class WeatherLoader extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... params) {
            JSONObject jsonWeather = null;
            try {
                jsonWeather = loadWeatherData(params[0], params[1]);
            } catch (Exception e) {
                Log.d("Error", "Cannot process JSON results", e);
            }
            return jsonWeather;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            readData(jsonObject);
            showNotifications();
            saveWeatherData();
        }
    }
}
