package com.misht.prototype;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class Account extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
    }

    /** Called when the user taps the LOGIN button */
    public void sendMessage1(View view) {
        // Do something in response to button
        Intent intent = new Intent(this, NewPayment.class);
        startActivity(intent);
    }
}
