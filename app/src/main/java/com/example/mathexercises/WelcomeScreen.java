package com.example.mathexercises;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

/**
 * welcome screen activity. in this activity the user choose which type of quiz to show up
 */
public class WelcomeScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);
        resetButtonsColors();
    }

    /**
     * make sure the buttons not modified - resetting them
     */
    private void resetButtonsColors() {
        ((GradientDrawable) findViewById(R.id.rand_button).getBackground()).setColor(Color.parseColor("#FFDD88"));
        ((GradientDrawable) findViewById(R.id.custom_button).getBackground()).setColor(Color.parseColor("#FFDD88"));
    }

    /**
     * in case the user chosen is random questions (not subject filtered)
     *
     * @param view random btn
     */
    public void goToRandom(View view) {
        Intent intent = new Intent(this, QuestionActivity.class);
        startActivity(intent);
        WelcomeScreen.this.finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    /**
     * in case the user chosen is custom questions (subject filtered)
     *
     * @param view custom btn
     */
    public void goToCustomizing(View view) {
        Intent intent = new Intent(this, Customizing_Questions.class);
        startActivity(intent);
        WelcomeScreen.this.finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    boolean backPressed = false;

    /**
     * overriding back btn action
     */
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
            }, 3500);  //if the user don't click the second time in that delay period the backPressed state reset.
            Toast.makeText(WelcomeScreen.this, "לחץ שוב על מנת לצאת", Toast.LENGTH_SHORT).show();
        }
    }

}