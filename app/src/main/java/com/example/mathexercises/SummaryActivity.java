package com.example.mathexercises;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class SummaryActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);
        int scores = getIntent().getIntExtra("Scores", 0);
        int questionAsked = getIntent().getIntExtra("ASKED", 0);
        int questionsAnsweredRight = getIntent().getIntExtra("RIGHTS", 0);
        String congratulationText = questionAsked != 0 &&
                (float) questionsAnsweredRight / questionAsked > 0.5 ? "יפה מאוד!" : "כדאי לתרגל עוד קצת";
        ((TextView) findViewById(R.id.summ)).setText(congratulationText + "\nהניקוד שצברת הוא: " + scores + " נקודות");
        String answered = questionsAnsweredRight == 1 ? "ענית על תשובה נכונה אחת\nמתוך " + questionAsked : "\n ענית על " + questionsAnsweredRight + " תשובות נכונות\nמתוך " + questionAsked;
        ((TextView) findViewById(R.id.questions_answered)).setText(answered);

        ((GradientDrawable) findViewById(R.id.exit_bttn).getBackground()).setColor(Color.parseColor("#FFDD88"));
        ((GradientDrawable) findViewById(R.id.return_bttn).getBackground()).setColor(Color.parseColor("#FFDD88"));
    }

    @Override
    public void onBackPressed() {
        returnToWelcome(null);
    }

    public void exit(View view) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        SummaryActivity.this.finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    public void returnToWelcome(View view) {
        Intent welcomeIntent = new Intent(SummaryActivity.this, WelcomeScreen.class);
        SummaryActivity.this.startActivity(welcomeIntent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        SummaryActivity.this.finish();
    }

}