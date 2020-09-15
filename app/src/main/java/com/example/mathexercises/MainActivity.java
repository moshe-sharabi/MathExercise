package com.example.mathexercises;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

/**
 * first activity - shows logo and loading features
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        NextActivity();
    }

    public void NextActivity() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent welcomeIntent = new Intent(MainActivity.this, WelcomeScreen.class);
                MainActivity.this.startActivity(welcomeIntent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                MainActivity.this.finish();
            }
        }, 500);

    }
}