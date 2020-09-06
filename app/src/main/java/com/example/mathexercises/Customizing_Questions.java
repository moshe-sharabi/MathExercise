package com.example.mathexercises;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Toast;

import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

public class Customizing_Questions extends AppCompatActivity {

    List<CheckBox> checkboxes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customizing__questions);

        checkboxes_init();
    }

    private void checkboxes_init() {
        checkboxes = new LinkedList<>();
        LayoutInflater inflater = getLayoutInflater();
        QuestionsGetter.subjectsDictInit();
        for (Enumeration<String> e = QuestionsGetter.subjectsDict.keys(); e.hasMoreElements(); ) {
            View view = inflater.inflate(R.layout.subject_choose_checkbox_view, null);
            checkboxes.add((CheckBox) view);
            ((CheckBox) view).setText(e.nextElement());
            ViewGroup main = (ViewGroup) findViewById(R.id.linear_checkbox);
            main.addView(view, 0);
        }
    }

    public void chooseClicked(View view) {
        StringBuilder checked = new StringBuilder();
        for (CheckBox checkBox : checkboxes) {
            if (checkBox.isChecked()) {
                checked.append(checkBox.getText()).append(" ");
            }
        }
        if (checked.length() == 0) {
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
    public void onBackPressed() {
        Intent intent = new Intent(Customizing_Questions.this, WelcomeScreen.class);
        startActivity(intent);
        Customizing_Questions.this.finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}