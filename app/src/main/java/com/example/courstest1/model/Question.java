package com.example.courstest1.model;

import java.io.Serializable;
import java.util.List;

public class Question implements Serializable {

    private final String mQuestion;
    private final List<String> mChoiceList;
    private final int mAnswerIndex;
    private final String mHintPhrase;


    public Question(String question, List<String> choiceList, int answerIndex, String hintPhrase) {
        mQuestion = question;
        mChoiceList = choiceList;
        mAnswerIndex = answerIndex;
        mHintPhrase = hintPhrase;
    }

    public String getQuestion() {
        return mQuestion;
    }

    public String getHint() {
        return mHintPhrase;
    }

    public List<String> getChoiceList() {
        return mChoiceList;
    }

    public int getAnswerIndex() {
        return mAnswerIndex;
    }
}
