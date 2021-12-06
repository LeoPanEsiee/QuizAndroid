package com.example.courstest1.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuestionBank implements Serializable {

    private final List<Question> mQuestionList;
    private int mQuestionIndex;

    public QuestionBank(List<Question> questionList) {
        mQuestionList = questionList;

        Collections.shuffle(mQuestionList);
    }

    public QuestionBank() {
        mQuestionList = new ArrayList<>();

        Collections.shuffle(mQuestionList);
    }

    public Question getCurrentQuestion() {
        return mQuestionList.get(mQuestionIndex);
    }

    public String getCurrentQuestionHint() {
        return mQuestionList.get(mQuestionIndex).getHint();
    }

    public Question getNextQuestion() {
        mQuestionIndex++;
        return getCurrentQuestion();
    }

    public void add(Question question){
        mQuestionList.add(question);
    }
}