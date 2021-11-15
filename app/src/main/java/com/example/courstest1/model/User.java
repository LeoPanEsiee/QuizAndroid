package com.example.courstest1.model;

import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("playerName")
    private String mFirstName;

    @SerializedName("playerScore")
    private int mScore;


    public User(String firstName, int score) {
        mFirstName = firstName;
        mScore = score;
    }

    public User() {
    }

    public String getFirstName() {
        return mFirstName;
    }


    public void setFirstName(String firstName) {
        mFirstName = firstName;
    }

    public int getScore() {
        return mScore;
    }

    public void setScore(int score) {
        mScore = score;
    }

}
