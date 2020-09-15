package com.example.mathexercises;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

/**
 * an activity choosing subjects of the questions
 */

public class Customizing_Questions extends AppCompatActivity {

    List<CheckBox> checkboxes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customizing__questions);

        checkboxes_init();
    }

    /**
     * build checkboxes view
     */
    private void checkboxes_init() {
        checkboxes = new LinkedList<>();
        LayoutInflater inflater = getLayoutInflater();
        QuestionsGetter.subjectsDictInit(); // getting subjects list
        for (Enumeration<String> e = QuestionsGetter.subjectsDict.keys(); e.hasMoreElements(); ) {
            View view = inflater.inflate(R.layout.subject_choose_checkbox_view, null);
            checkboxes.add((CheckBox) view);
            ((CheckBox) view).setText(e.nextElement());
            ViewGroup main = findViewById(R.id.linear_checkbox);
            main.addView(view, 0);
        }
    }

    /**
     * method sends the subjects to next activity (QuestionsActivity)
     *
     * @param view view clicked
     */
    public void chooseClicked(View view) {
        StringBuilder checked = new StringBuilder();
        for (CheckBox checkBox : checkboxes) {
            if (checkBox.isChecked()) {
                checked.append(checkBox.getText()).append(" ");
            }
        }
        if (checked.length() == 0) { //in case no checkbox checked
            Toast.makeText(this, "יש לבחור לפחות נושא אחד", Toast.LENGTH_LONG).show();
            return;
        }
        Intent intent = new Intent(Customizing_Questions.this, QuestionActivity.class);
        intent.putExtra("SUBJECTS", checked.toString());
        startActivity(intent);
        Customizing_Questions.this.finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    // back to welcome screen if back pressed
    public void onBackPressed() {
        Intent intent = new Intent(Customizing_Questions.this, WelcomeScreen.class);
        startActivity(intent);
        Customizing_Questions.this.finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}