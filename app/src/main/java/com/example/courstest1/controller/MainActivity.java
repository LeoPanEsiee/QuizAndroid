package com.example.courstest1.controller;

import android.content.Intent;
import android.os.Bundle;

import com.example.courstest1.R;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private TextView mTextViewGreeting;
    private EditText mEditTextName;
    private Button mButtonPlay;
    private static final int GAME_ACTIVITY_REQUEST_CODE = 42;
    private static final String SHARED_PREF_USER_INFO = "SHARED_PREF_USER_INFO";
    private static final String SHARED_PREF_USER_INFO_NAME = "SHARED_PREF_USER_INFO_NAME";


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (GAME_ACTIVITY_REQUEST_CODE == requestCode && RESULT_OK == resultCode) {
            // Fetch the score from the Intent
            int score = data.getIntExtra(GameActivity.BUNDLE_EXTRA_SCORE, 0);
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

        String firstName = getSharedPreferences(SHARED_PREF_USER_INFO, MODE_PRIVATE).
                getString(SHARED_PREF_USER_INFO_NAME, null);
        int lastScore = getSharedPreferences(SHARED_PREF_USER_INFO, MODE_PRIVATE).getInt(GameActivity.BUNDLE_EXTRA_SCORE, 0);

        if(firstName != null){
            mTextViewGreeting.setText(getString(R.string.WelcomeBack) + firstName + "\n" +
                    getString(R.string.LastScoreWas)+ lastScore + getString(R.string.WillDoBetter));

        }


        mEditTextName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                // This is where we'll check the user input
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

                Intent gameActivityIntent = new Intent(MainActivity.this, GameActivity.class);
                startActivityForResult(gameActivityIntent, GAME_ACTIVITY_REQUEST_CODE);
            }
        });


    }




}