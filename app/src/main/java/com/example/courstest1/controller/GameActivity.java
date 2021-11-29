package com.example.courstest1.controller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.courstest1.R;
import com.example.courstest1.model.Question;
import com.example.courstest1.model.QuestionBank;

import java.util.Arrays;
import java.util.Locale;
import java.util.Random;

public class GameActivity extends AppCompatActivity implements View.OnClickListener {

    private static final long START_TIME_IN_MS = 30000;

    private TextView mTextViewTimer;
    private CountDownTimer mCountDownTimer;
    private boolean mTimerRunning;
    private long mTimeLeftInMs = START_TIME_IN_MS;

    TextView mTextViewQuestion;
    Button mGameButton1;
    Button mGameButton2;
    Button mGameButton3;
    Button mGameButton4;
    ImageButton mJockerButton;
    Button[] liste =  new Button[4];

    QuestionBank mQuestionBank = generateQuestions();

    private int mRemainingQuestionCount;
    Question mCurrentQuestion;

    private int mScore;
    private int mJokerPress;
    public static final String BUNDLE_EXTRA_SCORE = "BUNDLE_EXTRA_SCORE";
    private boolean mEnableTouchEvents;


    public static final String BUNDLE_STATE_SCORE = "BUNDLE_STATE_SCORE";
    public static final String BUNDLE_STATE_QUESTION = "BUNDLE_STATE_QUESTION";
    public static final String BUNDLE_STATE_QUESTION_BANK = "BUNDLE_STATE_QUESTION_BANK";
    public static final String BUNDLE_STATE_CURRENT_QUESTION = "BUNDLE_STATE_CURRENT_QUESTION";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        mTextViewQuestion = findViewById(R.id.game_activity_textview_question);
        mGameButton1 = findViewById(R.id.game_activity_button_1);
        mGameButton2 = findViewById(R.id.game_activity_button_2);
        mGameButton3 = findViewById(R.id.game_activity_button_3);
        mGameButton4 = findViewById(R.id.game_activity_button_4);
        mJockerButton = findViewById(R.id.game_activity_jocker_button);

        mGameButton1.setOnClickListener(this);
        mGameButton2.setOnClickListener(this);
        mGameButton3.setOnClickListener(this);
        mGameButton4.setOnClickListener(this);
        mJockerButton.setOnClickListener(this);

        mCurrentQuestion = mQuestionBank.getCurrentQuestion();
        displayQuestion(mCurrentQuestion);

        mEnableTouchEvents = true;

        if(savedInstanceState != null){
            mRemainingQuestionCount = savedInstanceState.getInt(BUNDLE_STATE_QUESTION);
            mScore = savedInstanceState.getInt(BUNDLE_STATE_SCORE);
        }else{
            mRemainingQuestionCount = 2;
            mScore = 0;
        }


        mTextViewTimer = findViewById(R.id.game_activity_textview_countdown_timer);
        startTimer();



        liste[0] = mGameButton1;
        liste[1] = mGameButton2;
        liste[2] = mGameButton3;
        liste[3] = mGameButton4;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt(BUNDLE_STATE_SCORE, mScore);
        savedInstanceState.putInt(BUNDLE_STATE_QUESTION, mRemainingQuestionCount);

        savedInstanceState.putSerializable(BUNDLE_STATE_QUESTION_BANK, mQuestionBank);
        savedInstanceState.putSerializable(BUNDLE_STATE_CURRENT_QUESTION, mCurrentQuestion);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        mScore = savedInstanceState.getInt(BUNDLE_STATE_SCORE);
        mRemainingQuestionCount = savedInstanceState.getInt(BUNDLE_STATE_QUESTION);

        mQuestionBank = (QuestionBank) savedInstanceState.getSerializable(BUNDLE_STATE_QUESTION_BANK);
        mCurrentQuestion = (Question) savedInstanceState.getSerializable(BUNDLE_STATE_CURRENT_QUESTION);
        displayQuestion(mCurrentQuestion);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return mEnableTouchEvents && super.dispatchTouchEvent(ev);
    }

    private void displayQuestion(final Question question){
        mTextViewQuestion.setText(question.getQuestion());
        mGameButton1.setText(question.getChoiceList().get(0));
        mGameButton2.setText(question.getChoiceList().get(1));
        mGameButton3.setText(question.getChoiceList().get(2));
        mGameButton4.setText(question.getChoiceList().get(3));
    }


    private QuestionBank generateQuestions(){
        Question question1 = new Question(
                "Who is the creator of Android?",
                Arrays.asList(
                        "Andy Rubin",
                        "Steve Wozniak",
                        "Jake Wharton",
                        "Paul Smith"
                ),
                0,
                "Toys Story child"
        );

        Question question2 = new Question(
                "When did the first man land on the moon?",
                Arrays.asList(
                        "1958",
                        "1962",
                        "1967",
                        "1969"
                ),
                3,
                "Toys Story child"

        );

        Question question3 = new Question(
                "What is the house number of The Simpsons?",
                Arrays.asList(
                        "42",
                        "101",
                        "666",
                        "742"
                ),
                3,
                "Toys Story child"

        );

        return new QuestionBank(Arrays.asList(question1, question2, question3));
    }

    @Override
    public void onClick(View view) {
        int index;
        int rand;
        int id ;

        if(view == mJockerButton){
            Log.println(Log.INFO,"TAG","Jocker presssed");
            switch (mJokerPress) {
                case 0:
                    mScore--;
                    Toast.makeText(this,mQuestionBank.getCurrentQuestionHint(),Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    mScore--;
                    do{
                        rand = new Random().nextInt(4);
                    }while(rand == mQuestionBank.getCurrentQuestion().getAnswerIndex());
                    liste[rand].setVisibility(View.GONE);
                    break;
                case 2:
                    mScore--;
                    liste[mQuestionBank.getCurrentQuestion().getAnswerIndex()].setBackgroundColor(Color.parseColor("#008000"));
                    break;

                default:
            }
            mJokerPress++;
            return;
        }
        if (view == mGameButton1) {
            index = 0;
        } else if (view == mGameButton2) {
            index = 1;
        } else if (view == mGameButton3) {
            index = 2;
        } else if (view == mGameButton4) {
            index = 3;
        } else {
            throw new IllegalStateException("Unknown clicked view : " + view);
        }

        correction(index == mQuestionBank.getCurrentQuestion().getAnswerIndex());
    }






    private void correction(boolean response){
        if(response){
            Toast.makeText(GameActivity.this, "Correct !", Toast.LENGTH_SHORT).show();
            mScore+=10;
        }else{
            if(mTimerRunning){
                Toast.makeText(GameActivity.this, "Incorrect !", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(GameActivity.this, "Timeout !", Toast.LENGTH_SHORT).show();
            }
        }

        mTimerRunning = false;
        mCountDownTimer.cancel();

        mEnableTouchEvents = false;

        new Handler().postDelayed(() -> {
            for(int i=0;i<4;i++){
            liste[i].setVisibility(View.VISIBLE);}
            mJokerPress = 0;
            liste[mQuestionBank.getCurrentQuestion().getAnswerIndex()].setBackgroundColor(Color.parseColor("#6200ee"));
            mRemainingQuestionCount--;

            if (mRemainingQuestionCount > 0) {
                mCurrentQuestion = mQuestionBank.getNextQuestion();
                displayQuestion(mCurrentQuestion);

                //reset timer
                mTimeLeftInMs = START_TIME_IN_MS;
                startTimer();
                updateCountDownText();

            } else {
                endGame();

            }
            mEnableTouchEvents = true;
        }, 2000);
    }


    private void endGame(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Well done!")
                .setMessage("Your score is " + mScore)
                .setPositiveButton("OK", (dialog, which) -> {
                    Intent intent = new Intent();
                    intent.putExtra(BUNDLE_EXTRA_SCORE, mScore);
                    setResult(RESULT_OK, intent);
                    finish();
                })
                .create()
                .show();
    }


    private void startTimer(){
        mCountDownTimer = new CountDownTimer(mTimeLeftInMs, 1000) {
            @Override
            public void onTick(long l) {
                mTimeLeftInMs = l;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                mTimerRunning = false;
                correction(false);
            }
        }.start();
        mTimerRunning = true;
    }


    private void updateCountDownText(){
        int minutes = (int) (mTimeLeftInMs/1000) / 60;
        int seconds = (int) (mTimeLeftInMs/1000) % 60;
        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        mTextViewTimer.setText(timeLeftFormatted);
    }
}