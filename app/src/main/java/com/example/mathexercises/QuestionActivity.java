package com.example.mathexercises;

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
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.webkit.WebSettings;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;

public class QuestionActivity extends AppCompatActivity {
    final String SHARED_PREF = "sp";
    int questionAsked = 1; //take in count the first question
    int questionsAnsweredRight = 0;
    ArrayList<Question> questions;
    Question question;
    int[] answers = {R.id.button_up_start, R.id.button_up_end, R.id.button_down_start, R.id.button_down_end};
    int scores = 0;
    int answered_wrong = -1; //the wrong answer the user clicked. -1 represents no wrong clicked.
    boolean freeze = false; //freeze to disable other buttons while color interaction or flow animation.
    boolean popup;
    MathView[] mathViews;
    AudioPlayer ap = new AudioPlayer();
    boolean backPressed = false; //flag to determine if the user do double click. true for first back click

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        SharedPreferences mPrefs = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
        popup = mPrefs.getBoolean("popup", true); //restoring user preferred don't popup?
        AssetManager assetManager = getApplicationContext().getAssets();
        String subjects = null;
        try {
            subjects = getIntent().getExtras().getString("SUBJECTS");
            //getting subjects the user chosen in the prev activity. null if the user chosen was random.
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
        if (popup) popupMessage();
    }

    /**
     * popup an explanation message to the user
     */
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
                        SharedPreferences mPrefs = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
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

    /**
     * loading the math (q and answers) to a view array
     */
    private void viewsLoad() {
        mathViews = new MathView[5];
        mathViews[0] = findViewById(R.id.the_question);
        mathViews[1] = findViewById(R.id.button_up_start);
        mathViews[2] = findViewById(R.id.button_up_end);
        mathViews[3] = findViewById(R.id.button_down_start);
        mathViews[4] = findViewById(R.id.button_down_end);
    }

    /**
     * dynamically sets the text to 22
     */
    private void setTextSize() {
        for (MathView mathView : mathViews) {
            WebSettings settings = mathView.getSettings();
            settings.setDefaultFontSize(22);
        }
    }

    /**
     * override onTouch for all answers views. (because touching web view act like web)
     */
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

    /**
     * set the text/Tex in the views
     *
     * @param q new Question
     */
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

    /**
     * load next question or moving to next activity (in case end of questions)
     */
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

    /**
     * resetting buttons color after modifying it in right/wrong answers
     */
    private void resetButtonsColors() {
        String[] originalColors = {"#00796B", "#FFA000", "#7B1FA2", "#303F9F"};
        for (int i = 0; i < originalColors.length; i++) {
            ((GradientDrawable) mathViews[i + 1].getBackground()).setColor(Color.parseColor(originalColors[i]));
        }
    }

    /**
     * handling an answer clicked - checks right/wrong and act by this state
     *
     * @param view
     */
    public void answerClicked(View view) {
        if (freeze) {
            return;
        }
        boolean isRightClicked = answers[question.getRightAnswer()] == view.getId();
        if (isRightClicked) rightAnswerClicked(view);
        else wrongAnswerClicked(view);
    }

    /**
     * vibrating phone. called in case of wrong answer (pattern of wrong)
     */
    private void vibrate() {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {0, 80, 80, 80};
        v.vibrate(VibrationEffect.createWaveform(pattern, -1));

    }

    /**
     * actions in case of wrong answer clicked
     *
     * @param view btn clicked
     */
    private void wrongAnswerClicked(View view) {
        //checks if the user clicked the same wrong prev answer button. if so, nothing happening.
        if (answered_wrong == view.getId()) {
            return;
        }
        ap.play(this, R.raw.wrong_buzzert);
        ((GradientDrawable) view.getBackground()).setColor(Color.parseColor("#FF0000")); // drawing btn in red color.
        vibrate();
//        scores -= 5;
//        ((TextView) findViewById(R.id.score)).setText("ניקוד: " + scores); // updating scores in screen.

        if (answered_wrong == -1) { //in case the first wrong answer clicked in this question.
            answered_wrong = view.getId();
            return;
        }
        goToNextQuestion();
    }

    /**
     * actions in case of right answer clicked
     *
     * @param view btn clicked
     */
    private void rightAnswerClicked(View view) {
        questionsAnsweredRight++;
        if (answered_wrong != -1) {
            //in this case the user clicked wrong answer in a prev click of this question.
            scores += 5;
        } else {
            scores += 10;
        }
        ap.play(this, R.raw.correct_sound);
        ((GradientDrawable) view.getBackground()).setColor(Color.parseColor("#2EFF00"));  // drawing btn in green color.
        goToNextQuestion();
    }

    /**
     * overriding back btn action
     */
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
            }, 3500); //if the user don't click the second time in that delay period the backPressed state reset.
            Toast.makeText(QuestionActivity.this, "לחץ שוב על מנת לחזור למסך הראשי", Toast.LENGTH_LONG).show();
        }
    }
}