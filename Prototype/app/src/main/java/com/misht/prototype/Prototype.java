package com.misht.prototype;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class Prototype extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prototype);
    }

    /** Called when the user taps the LOGIN button */
    public void sendMessage(View view) {
        // Do something in response to button
        Intent intent;
        intent = new Intent(this, Account.class);
        startActivity(intent);
    }
}
