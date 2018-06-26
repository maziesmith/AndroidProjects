package com.misht.metaldetector;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class metalDetector extends AppCompatActivity implements SensorEventListener {
    private TextView value; //Create an instance of textView
    private SensorManager sensorManager; //Create an instance of sensorManager
    public static DecimalFormat DECIMAL_FORMATTER;
    final int MAGNETOMETER_THRESHOLD = 120; //We set the threshold of the magnetometer in 120
    private MediaPlayer mPlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_metal_detector);
        //We assign value the textView of the screen
        value = (TextView) findViewById(R.id.textView);
        //We will use this in order to format the value of the magnitude
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setDecimalSeparator('.');
        DECIMAL_FORMATTER = new DecimalFormat("#.000", symbols);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
    }

    @Override
    protected void onPause() {
        //We use this function in order to stop the sensor when the app is not active
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        //We use it in order to register changes in the magnetometer
        super.onResume();
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        //We use it in order to know the values of the magnetometer and calculate the magnitude. Then, we set the magnitude in the textView so we can see it in the screen
        //We also make that if this magnitude is greater than 120, the phone sounds with pacman song
        if(sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];
            double magnitude = Math.sqrt((x * x) + (y * y) + (z * z));

            //In order to set text in the textView
            value.setText(DECIMAL_FORMATTER.format(magnitude) + " \u00B5Tesla");
            if(magnitude > MAGNETOMETER_THRESHOLD) {
                if (mPlay == null) {
                    mPlay = MediaPlayer.create(this, R.raw.pacman);
                }
                else {
                    mPlay.start();
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
