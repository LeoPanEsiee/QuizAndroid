package com.example.courstest1.controller;

import android.content.Intent;
import android.os.Bundle;

import com.example.courstest1.R;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.example.courstest1.model.User;
import com.google.gson.Gson;

import java.sql.Connection;
import java.sql.DriverManager;

public class MainActivity extends AppCompatActivity {
    private TextView mTextViewGreeting;
    private EditText mEditTextName;
    private Button mButtonPlay;

    private Button mButtonBestScore;

    private String firstName;
    private int lastScore;

    User user = new User();


    private static final int GAME_ACTIVITY_REQUEST_CODE = 42;
    private static final String SHARED_PREF_USER_INFO = "SHARED_PREF_USER_INFO";
    private static final String SHARED_PREF_USER_INFO_NAME = "SHARED_PREF_USER_INFO_NAME";


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (GAME_ACTIVITY_REQUEST_CODE == requestCode && RESULT_OK == resultCode) {
            // Fetch the score from the Intent


            Gson gson = new Gson();
            firstName = getSharedPreferences(SHARED_PREF_USER_INFO, MODE_PRIVATE).getString(SHARED_PREF_USER_INFO_NAME, null);
            lastScore = data.getIntExtra(GameActivity.BUNDLE_EXTRA_SCORE, 0);
            String json = "{\"playerName\" : \"" + firstName + "\",\"playerScore\" : \"" + lastScore +"\"}";
            user = gson.fromJson(json, User.class);

            displayUserInfo();
        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextViewGreeting = findViewById(R.id.textview_greeting);
        mEditTextName = findViewById(R.id.edittext_name);
        mButtonPlay = findViewById(R.id.button_play);


        mButtonBestScore = findViewById(R.id.button_bestscore);

        mButtonPlay.setEnabled(false);



        Gson gson = new Gson();
        firstName = getSharedPreferences(SHARED_PREF_USER_INFO, MODE_PRIVATE).getString(SHARED_PREF_USER_INFO_NAME, null);
        lastScore = getSharedPreferences(SHARED_PREF_USER_INFO, MODE_PRIVATE).getInt(GameActivity.BUNDLE_EXTRA_SCORE, 0);
        String json = "{\"playerName\" : \"" + firstName + "\",\"playerScore\" : \"" + lastScore +"\"}";
        user = gson.fromJson(json, User.class);
        displayUserInfo();


        mEditTextName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mButtonPlay.setEnabled(!s.toString().isEmpty());
            }
        });

        mButtonPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSharedPreferences(SHARED_PREF_USER_INFO, MODE_PRIVATE)
                    .edit()
                    .putString(SHARED_PREF_USER_INFO_NAME, mEditTextName.getText().toString())
                    .apply();



                // Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();
                String host = "jdbc:mariadb://109.221.187.188:3306/QUIZZ";
                String username = "SuperUser";
                String password ="password1";
                try {
                    Class.forName("org.mariadb.jdbc.Driver");
                    Connection con = DriverManager.getConnection( host, username, password );
                    Toast.makeText(MainActivity.this, " - Success!", Toast.LENGTH_LONG).show();

                    Intent gameActivityIntent = new Intent(MainActivity.this, GameActivity.class);
                    startActivityForResult(gameActivityIntent, GAME_ACTIVITY_REQUEST_CODE);
                } catch (Exception err) {
                    Toast.makeText(MainActivity.this, "error :" + err.getMessage(), Toast.LENGTH_LONG).show();

                }

            }
        });

    }


    private void displayUserInfo(){
        if(user.getFirstName() != null){
            mTextViewGreeting.setText(getString(R.string.WelcomeBack) + user.getFirstName() + "\n" +
                    getString(R.string.LastScoreWas)+ user.getScore() + getString(R.string.WillDoBetter));

        }
    }





}