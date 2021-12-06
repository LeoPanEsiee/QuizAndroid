package com.example.courstest1.model;

import java.io.Serializable;
import java.util.List;

public class Question implements Serializable {

    private String mQuestion;
    private List<String> mChoiceList;
    private int mAnswerIndex;
    private String mHintPhrase;


    public Question(String question, List<String> choiceList, int answerIndex, String hintPhrase) {
        mQuestion = question;
        mChoiceList = choiceList;
        mAnswerIndex = answerIndex;
        mHintPhrase = hintPhrase;
    }

    public Question() {
        mQuestion = "";
        mChoiceList = null;
        mAnswerIndex = 0;
        mHintPhrase = "";
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

    public void setQuestion(String question) {
        mQuestion = question;
    }

    public void setChoiceList(List<String> choiceList) {
        mChoiceList = choiceList;
    }

    public void setAnswerIndex(int answerIndex) {
        mAnswerIndex = answerIndex;
    }

    public void setHintPhrase(String hintPhrase) {
        mHintPhrase = hintPhrase;
    }

    @Override
    public String toString() {
        return "Question{" +
                "mQuestion='" + mQuestion + '\'' +
                ", mAnswerIndex=" + mAnswerIndex +
                ", mHintPhrase='" + mHintPhrase + '\'' +
                '}';
    }
}
