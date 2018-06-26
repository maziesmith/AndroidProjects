package com.misht.prototype;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class NewPayment extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_payment);
    }

    /** Called when the user taps the ACCEPT button */
    public void sendMessage2(View view) {
        // Do something in response to button
        Intent intent;
        intent = new Intent(this, Account.class);
        startActivity(intent);
    }
}
