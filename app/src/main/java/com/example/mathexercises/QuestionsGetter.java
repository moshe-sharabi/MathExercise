package com.example.mathexercises;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.Scanner;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QuestionsGetter {
    private ArrayList<Question> questions;


    public static Dictionary<String, Integer> subjectsDict;

    public static void subjectsDictInit() {
        subjectsDict = new Hashtable<>();
        subjectsDict.put("חדוא", 1);
        subjectsDict.put("טריגו", 2);
        subjectsDict.put("סדרות", 3);
        subjectsDict.put("קומבינטוריקה", 4);
        subjectsDict.put("אלגברה", 5);
        subjectsDict.put("גבולות", 6);
    }

    QuestionsGetter(InputStream inputStream) {
        subjectsDictInit();
        List<String> lines;
        lines = parser(inputStream);
        questions = builder(lines);
    }

    QuestionsGetter(InputStream inputStream, final String subjects) {
        this(inputStream);
        questions.removeIf(new Predicate<Question>() {
            @Override
            public boolean test(Question question) {
                return !question.isIntersectsSubjects(subjects);
            }
        });
    }

    private List<String> parser(InputStream inputStream) {
        Scanner input = new Scanner(inputStream);
        List<String> lines = new ArrayList<>();

        while (input.hasNextLine()) {
            lines.add(stringFormatter(input.nextLine()));
        }
        return lines;
    }

    private String stringFormatter(String s) {
        Matcher m = Pattern.compile("([א-ת]+[\\s:,.]*)").matcher(s);
        int prev = 0;
        StringBuilder builder = new StringBuilder();
        while (m.find()) {
            String prevText = s.substring(prev, m.start());
            prev = m.end();
            String delimiter = m.group();
            if (!prevText.trim().equals("")) {
                builder.append("$" + prevText.trim() + "$ ");
            }
            builder.append(delimiter);
        }
        String tailText = s.substring(prev); // text after last delimiter
        if (!tailText.trim().equals("")) {
            builder.append("$" + tailText.trim() + "$");
        }
        return builder.toString().replace("#", "$ , $").trim().replace("\\", "\\\\");
    }

    private ArrayList<Question> builder(List<String> lines) {
        ArrayList<Question> questions = new ArrayList<>();
        for (int i = 0; i < lines.size(); i += 7) {
            String q = lines.get(i);
            String[] ans = new String[]{lines.get(i + 1), lines.get(i + 2), lines.get(i + 3), lines.get(i + 4)};
//            int[] subjects = toSubjects(lines.get(i + 5));
            String[] subjects = lines.get(i + 5).split(" ");
            questions.add(new Question(q, ans, subjects));
        }
        return questions;
    }

//    private int[] toSubjects(String s) {
//        String[] splatted = s.split(" ");
//        int[] asArray = new int[splatted.length];
//        for (int i = 0; i < splatted.length; i++) {
//            asArray[i] = subjectsDict.get(splatted[i].trim());
//        }
//        return asArray;
//    }


    public ArrayList<Question> getQuestions() {
        return questions;
    }
}
