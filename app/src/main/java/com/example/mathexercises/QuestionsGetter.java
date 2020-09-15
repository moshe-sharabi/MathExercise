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

/**
 * class representing getter for questions.
 */
public class QuestionsGetter {
    private ArrayList<Question> questions;
    public static Dictionary<String, Integer> subjectsDict;

    /**
     * constructor without filtering subjects
     *
     * @param inputStream input stream to read questions from.
     */
    QuestionsGetter(InputStream inputStream) {
        subjectsDictInit();
        List<String> lines;
        lines = parser(inputStream);
        questions = builder(lines);
    }

    /**
     * constructor with filtering subjects
     *
     * @param inputStream input stream to read questions from.
     * @param subjects    subjects to save
     */
    QuestionsGetter(InputStream inputStream, final String subjects) {
        this(inputStream);

        //filtering questions not suit to the subjects list
        questions.removeIf(new Predicate<Question>() {
            @Override
            public boolean test(Question question) {
                return !question.isIntersectsSubjects(subjects);
            }
        });
    }

    /**
     * initializing subjects list to a dictionary.
     */
    public static void subjectsDictInit() {
        subjectsDict = new Hashtable<>();
        subjectsDict.put("חדוא", 1);
        subjectsDict.put("טריגו", 2);
        subjectsDict.put("סדרות", 3);
        subjectsDict.put("קומבינטוריקה", 4);
        subjectsDict.put("אלגברה", 5);
        subjectsDict.put("גבולות", 6);
    }

    /**
     * get lines from input
     *
     * @param inputStream input text
     * @return formatted to- with text or Tex brackets
     */
    private List<String> parser(InputStream inputStream) {
        Scanner input = new Scanner(inputStream);
        List<String> lines = new ArrayList<>();

        while (input.hasNextLine()) {
            lines.add(stringFormatter(input.nextLine()));
        }
        return lines;
    }

    /**
     * formatting a string line to - with text or Tex brackets
     *
     * @param s line
     * @return formatted line
     */
    private String stringFormatter(String s) {
        Matcher m = Pattern.compile("([א-ת]+[\\s:,.]*)").matcher(s); //pattern of hebrew text
        int prev = 0;
        StringBuilder builder = new StringBuilder();
        while (m.find()) {
            String prevText = s.substring(prev, m.start()); //math Tex before the hebrew part
            prev = m.end();
            String delimiter = m.group();
            if (!prevText.trim().equals("")) {
                builder.append("$" + prevText.trim() + "$ ");
            }
            builder.append(delimiter);
        }
        String tailText = s.substring(prev); //math Tex after the hebrew part
        if (!tailText.trim().equals("")) {
            builder.append("$" + tailText.trim() + "$");
        }
        return builder.toString().replace("#", "$ , $").trim().replace("\\", "\\\\");
    }

    /**
     * Question objects generator
     *
     * @param lines all lines (not only one question lines)
     * @return list of Question s
     */
    private ArrayList<Question> builder(List<String> lines) {
        ArrayList<Question> questions = new ArrayList<>();
        for (int i = 0; i < lines.size(); i += 7) {
            String q = lines.get(i);
            String[] ans = new String[]{lines.get(i + 1), lines.get(i + 2), lines.get(i + 3), lines.get(i + 4)};
            String[] subjects = lines.get(i + 5).split(" ");
            questions.add(new Question(q, ans, subjects));
        }
        return questions;
    }

    /**
     * questions Getter
     *
     * @return Questions list
     */
    public ArrayList<Question> getQuestions() {
        return questions;
    }
}
