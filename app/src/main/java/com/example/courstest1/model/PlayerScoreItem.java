package com.example.courstest1.model;


public class PlayerScoreItem {
    private String username;
    private String score;

    public PlayerScoreItem(String text1, String text2) {
        username = text1;
        score = text2;
    }


    public String getText1() {
        return username;
    }

    public String getText2() {
        return score;
    }
}