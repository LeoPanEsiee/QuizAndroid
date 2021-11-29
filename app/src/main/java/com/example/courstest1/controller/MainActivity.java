package com.example.courstest1.controller;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.example.courstest1.R;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.example.courstest1.model.ReminderBroadcast;
import com.example.courstest1.model.User;
import com.google.gson.Gson;

public class MainActivity extends AppCompatActivity {
    private TextView mTextViewGreeting;
    private EditText mEditTextName;
    private Button mButtonPlay;

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
            lastScore = data != null ? data.getIntExtra(GameActivity.BUNDLE_EXTRA_SCORE, 0) : 0;
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

        mButtonPlay.setOnClickListener((view)->{
            getSharedPreferences(SHARED_PREF_USER_INFO, MODE_PRIVATE)
                    .edit()
                    .putString(SHARED_PREF_USER_INFO_NAME, mEditTextName.getText().toString())
                    .apply();

            Intent gameActivityIntent = new Intent(MainActivity.this, GameActivity.class);
            startActivityForResult(gameActivityIntent, GAME_ACTIVITY_REQUEST_CODE);
        });

        //Used for Notification
        createNotificationChannel();


    }



    private void createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "QuestQuizz";
            String description = "quizz description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("myChannelId", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

    }


    private void displayUserInfo(){
        if(user.getFirstName() != null){
            mTextViewGreeting.setText(getString(R.string.WelcomeBack) + user.getFirstName() + "\n" +
                    getString(R.string.LastScoreWas)+ user.getScore() + getString(R.string.WillDoBetter));

        }
    }





}