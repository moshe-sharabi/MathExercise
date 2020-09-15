package com.example.mathexercises;

import java.util.Arrays;
import java.util.Collections;

/**
 * class represents a question with 4 options to answer
 */
public class Question {
    private String q;
    private String a1, a2, a3, a4;
    private int rightAnswer;
    private String[] subjects;

    /**
     * @param q        the question. string that can contains Tex and regular language
     * @param answers  answers[0] is the right answer. answers[1-3] are wrong
     * @param subjects mathematics subjects. have to be according to the subjectsDictionary.
     */
    public Question(String q, String[] answers, String[] subjects) {
        String right = answers[0];
        Collections.shuffle(Arrays.asList(answers)); //randomize shuffling the answers to hide the right answer.
        this.q = q;
        this.a1 = answers[0];
        this.a2 = answers[1];
        this.a3 = answers[2];
        this.a4 = answers[3];
        this.subjects = subjects;
        this.rightAnswer = Arrays.asList(answers).indexOf(right);
    }

    public String getQ() {
        return q;
    }

    public String getA1() {
        return a1;
    }

    public String getA2() {
        return a2;
    }

    public String getA3() {
        return a3;
    }

    public String getA4() {
        return a4;
    }

    public String[] getSubjects() {
        return subjects;
    }

    /**
     * use to check if the current question match the user customizing subject
     *
     * @param otherSubjects subjects with " " delimiter
     * @return true if the q match
     */
    public boolean isIntersectsSubjects(String otherSubjects) {
        for (String s : subjects) {
            if (otherSubjects.contains(s)) return true;
        }
        return false;
    }

    public int getRightAnswer() {
        return rightAnswer;
    }
}
