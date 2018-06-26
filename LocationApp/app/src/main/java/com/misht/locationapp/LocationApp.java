package com.misht.locationapp;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import com.misht.locationapp.DataBase;

public class LocationApp extends AppCompatActivity {
    private Button saveButton;
    private LocationManager locationManager; //An instance of LocationManager
    public double longitude;
    public double latitude;
    public static String reminder;
    private TextView longitudeTextView, latitudeTextView;
    private EditText et;
    private ProximityAlertReceiver alert;
    public static int id = 0;
    private DataBase myDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        myDatabase = new DataBase(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_app);
        longitudeTextView = (TextView) findViewById(R.id.textView3);
        latitudeTextView = (TextView) findViewById(R.id.textView2);
        et=(EditText)findViewById(R.id.editText);

        saveButton = findViewById(R.id.button);
        saveButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
                if (ActivityCompat.checkSelfPermission(LocationApp.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(LocationApp.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(LocationApp.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                            0);
                    return;
                }
                //locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
               // Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
               // if (location==null)location=locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                Location location=getLastKnownLocation(locationManager);
                longitude = location.getLongitude();
                latitude = location.getLatitude();
                Log.d("longitude", Double.toString(longitude));
                Log.d("latitude", Double.toString(latitude));
                latitudeTextView.setText(Double.toString(latitude));
                longitudeTextView.setText(Double.toString(longitude));
                //To show the notification near the place
                reminder = et.getText().toString();
                Intent intent = new Intent(getApplicationContext(), ProximityAlertReceiver.class);
                /*intent.putExtra("message", reminder);
                int notifId = id;
                id++;
                intent.putExtra("id", notifId);*/
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                locationManager.addProximityAlert(latitude, longitude, 1, -1, pendingIntent);

                Toast.makeText(getApplicationContext(), "Longitude: " + Double.toString(longitude) + "\nLatitude: " + Double.toString(latitude) + "\nReminder: " + reminder, Toast.LENGTH_SHORT).show();

                boolean isInserted = myDatabase.addNotification(Double.toString(latitude), Double.toString(longitude), reminder);

                if (isInserted == true)
                    Toast.makeText(LocationApp.this, "Data Inserted", Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(LocationApp.this, "Data not Inserted", Toast.LENGTH_LONG).show();
                }
                /*SharedPreferences.Editor editor = getSharedPreferences("MY_PREFS_NAME", MODE_PRIVATE).edit();
                editor.putString("Reminder:", reminder);
                editor.putString("Longitude:", Double.toString(longitude));
                editor.putString("Latitude:", Double.toString(latitude));
                editor.commit();

                SharedPreferences prefs = getSharedPreferences("MY_PREFS_NAME", MODE_PRIVATE);
                String reminder1 = prefs.getString("Reminder:", "");
                String longitude1 = prefs.getString("Longitude:", "");
                String latitude1 = prefs.getString("Latitude:", "");
                saveData();*/
        });

    }
    private Location getLastKnownLocation(LocationManager mLocationManager) {
        mLocationManager = (LocationManager)getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            Location l = mLocationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }

    /*public void saveData() {


        if (Double.toString(longitude) == longitude1) {
            if(Double.toString(latitude) == latitude1) {
                showNotifications(reminder1);
            }
        }
        //else {
        //    showNotifications("BLA BLA BLA");
      //  }
    //}*/


}
