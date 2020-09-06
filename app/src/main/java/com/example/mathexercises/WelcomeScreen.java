package com.example.mathexercises;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

public class WelcomeScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);
        resetButtonsColors();
    }

    private void resetButtonsColors() {
        ((GradientDrawable) findViewById(R.id.rand_button).getBackground()).setColor(Color.parseColor("#FFDD88"));
        ((GradientDrawable) findViewById(R.id.custom_button).getBackground()).setColor(Color.parseColor("#FFDD88"));
    }

    public void goToRandom(View view) {
        Intent intent = new Intent(this, QuestionActivity.class);
        startActivity(intent);
        WelcomeScreen.this.finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    public void goToCustomizing(View view) {
        Intent intent = new Intent(this, Customizing_Questions.class);
        startActivity(intent);
        WelcomeScreen.this.finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    boolean backPressed = false;

    @Override
    public void onBackPressed() {
        if (backPressed) {
            super.onBackPressed();
        } else {
            backPressed = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    backPressed = false;
                }
            }, 3500);
            Toast.makeText(WelcomeScreen.this, "לחץ שוב על מנת לצאת", Toast.LENGTH_SHORT).show();
        }
    }

}