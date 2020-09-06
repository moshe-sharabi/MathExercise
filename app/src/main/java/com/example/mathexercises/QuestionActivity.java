package com.example.mathexercises;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.webkit.WebSettings;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;

public class QuestionActivity extends AppCompatActivity {
    int questionAsked = 1;
    int questionsAnsweredRight = 0;
    ArrayList<Question> questions;
    Question question;
    int[] answers = {R.id.button_up_start, R.id.button_up_end, R.id.button_down_start, R.id.button_down_end};
    int scores = 0;
    int answered_wrong = -1;
    boolean freeze = false;
    private SharedPreferences mPrefs;
    boolean popup;
    MathView[] mathViews;
    AudioPlayer ap = new AudioPlayer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        SharedPreferences mPrefs = getSharedPreferences("abc",MODE_PRIVATE);
        popup = mPrefs.getBoolean("popup", true);
        AssetManager assetManager = getApplicationContext().getAssets();
        String subjects = null;
        try {
            subjects = getIntent().getExtras().getString("SUBJECTS");
        } catch (NullPointerException e) {
        }
        try {
            InputStream is = assetManager.open("Questions/questions.txt");
            if (subjects != null) {
                questions = new QuestionsGetter(is, subjects).getQuestions();
            } else {
                questions = new QuestionsGetter(is).getQuestions();
            }
        } catch (Exception e) {
        }


        Collections.shuffle(questions);
        question = questions.remove(0);
        viewsLoad();
        setOnTouch();
        resetButtonsColors();
        setTextSize();
        setNewQuestion(question);
        if (popup)popupMessage();
    }

    private void popupMessage() {
        Rect displayRectangle = new Rect();
        Window window = this.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this, R.style.popup);
        alertDialogBuilder
                .setTitle("ברוך הבא לשאלון במתמטיקה")
                .setMessage("לפניך 10 שאלות\n" +
                        "ענית נכון - קיבלת 10 נק'. \n" +
                        "טעית ואז ענית נכון - קיבלת 5 נק'.\n" +
                        "טעית פעמיים - ממשיכים לשאלה הבאה.\n" +
                        "בהצלחה!")
                .setCancelable(false)
                .setPositiveButton("אל תציג שוב", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
//TODO
                        SharedPreferences mPrefs = getSharedPreferences("abc",MODE_PRIVATE);
                        SharedPreferences.Editor ed = mPrefs.edit();
                        popup = false;
                        ed.putBoolean("popup", false);
                        ed.commit();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("אוקיי סבבה", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                })
                .show().getWindow().setLayout((int) (displayRectangle.width() *
                0.9f), (int) (displayRectangle.height() * 0.36f));
    }

    private void viewsLoad() {
        mathViews = new MathView[5];
        mathViews[0] = findViewById(R.id.the_question);
        mathViews[1] = findViewById(R.id.button_up_start);
        mathViews[2] = findViewById(R.id.button_up_end);
        mathViews[3] = findViewById(R.id.button_down_start);
        mathViews[4] = findViewById(R.id.button_down_end);
    }

    private void setTextSize() {
        for (MathView mathView : mathViews) {
            WebSettings settings = mathView.getSettings();
            settings.setDefaultFontSize(22);
        }
    }

    private void setOnTouch() {
        for (int i = 0; i < 5; i++) {
            mathViews[i].setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        v.performClick();
                    }
                    return true;
                }
            });
        }
    }

    public void setNewQuestion(Question q) {

        mathViews[0].setText(q.getQ());
        mathViews[1].setText(q.getA1());
        mathViews[2].setText(q.getA2());
        mathViews[3].setText(q.getA3());
        mathViews[4].setText(q.getA4());
        ((TextView) findViewById(R.id.score)).setText("ניקוד: " + scores);

        setTextSize();
        answered_wrong = -1;
        freeze = false;
    }

    public void goToNextQuestion() {
        freeze = true;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (questions.isEmpty() || questionAsked == 10) {
                    Intent intent = new Intent(QuestionActivity.this, SummaryActivity.class);
                    intent.putExtra("Scores", scores);
                    intent.putExtra("ASKED", questionAsked);
                    intent.putExtra("RIGHTS", questionsAnsweredRight);
                    startActivity(intent);
                    QuestionActivity.this.finish();
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                } else {
                    question = questions.remove(0);
                    resetButtonsColors();
                    setNewQuestion(question);
                    questionAsked++;
                }
            }
        }, 300);

    }


    private void resetButtonsColors() {
        String[] originalColors = {"#00796B", "#FFA000", "#7B1FA2", "#303F9F"};
        for (int i = 0; i < originalColors.length; i++) {
            ((GradientDrawable) mathViews[i + 1].getBackground()).setColor(Color.parseColor(originalColors[i]));
        }
    }

    public void answerClicked(View view) {
        if (freeze) {
            return;
        }
        boolean isRightClicked = answers[question.getRightAnswer()] == view.getId();
        if (isRightClicked) rightAnswerClicked(view);
        else wrongAnswerClicked(view);
    }

    private void vibrate() {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {0, 80, 80, 80};
        v.vibrate(VibrationEffect.createWaveform(pattern, -1));

    }

    private void wrongAnswerClicked(View view) {
        if (answered_wrong == view.getId()) {
            return;
        }
//        scores -= 5;
        ap.play(this, R.raw.wrong_buzzert);
        ((GradientDrawable) view.getBackground()).setColor(Color.parseColor("#FF0000"));
        vibrate();
        ((TextView) findViewById(R.id.score)).setText("ניקוד: " + scores);

        if (answered_wrong == -1) {
            answered_wrong = view.getId();
            return;
        }
        goToNextQuestion();
    }

    private void rightAnswerClicked(View view) {
        questionsAnsweredRight++;
        if (answered_wrong != -1) {
            scores += 5;
        } else {
            scores += 10;
        }
        ap.play(this, R.raw.correct_sound);
        ((GradientDrawable) view.getBackground()).setColor(Color.parseColor("#2EFF00"));
        goToNextQuestion();
    }

    boolean backPressed = false;

    @Override
    public void onBackPressed() {
        if (backPressed) {
            Intent welcomeIntent = new Intent(QuestionActivity.this, WelcomeScreen.class);
            QuestionActivity.this.startActivity(welcomeIntent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            QuestionActivity.this.finish();
        } else {
            backPressed = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    backPressed = false;
                }
            }, 3500);
            Toast.makeText(QuestionActivity.this, "לחץ שוב על מנת לחזור למסך הראשי", Toast.LENGTH_LONG).show();
        }
    }
}