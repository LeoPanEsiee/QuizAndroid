package com.example.courstest1.controller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
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
import com.example.courstest1.model.ReminderBroadcast;

import java.util.Arrays;
import java.util.Locale;
import java.util.Random;

public class GameActivity extends AppCompatActivity implements View.OnClickListener {

    //View
    TextView mTextViewQuestion;
    Button mGameButton1;
    Button mGameButton2;
    Button mGameButton3;
    Button mGameButton4;
    ImageButton mJockerButton;
    Button[] liste =  new Button[4];

    //Model
    QuestionBank mQuestionBank = generateQuestions();
    private int mRemainingQuestionCount;
    Question mCurrentQuestion;
    private int mScore;
    public static final String BUNDLE_STATE_SCORE = "BUNDLE_STATE_SCORE";
    public static final String BUNDLE_STATE_QUESTION = "BUNDLE_STATE_QUESTION";
    public static final String BUNDLE_STATE_QUESTION_BANK = "BUNDLE_STATE_QUESTION_BANK";
    public static final String BUNDLE_STATE_CURRENT_QUESTION = "BUNDLE_STATE_CURRENT_QUESTION";

    //Jokers
    private int mJokerPress;
    public static final String BUNDLE_EXTRA_SCORE = "BUNDLE_EXTRA_SCORE";
    private boolean mEnableTouchEvents;

    //Used for sending notifications when user isn't in the app
    AlarmManager alarmManager;
    PendingIntent pendingIntent;
    boolean wasInBackground;

    //Timer
    private TextView mTextViewTimer;
    private CountDownTimer mCountDownTimer;
    private boolean mTimerRunning;
    private static final long START_TIME_IN_MS = 30 * 1000;
    private long mTimeLeftInMs = START_TIME_IN_MS;

    //Used for playing sounds
    MediaPlayer player;


    /**
     * On creation of the activity
     * @param savedInstanceState saved instance
     */
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

    /**
     * For app rotation
     * We'll save the current game information to use them when the rotation is over
     * @param savedInstanceState saved instance
     */
    @Override
    protected void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt(BUNDLE_STATE_SCORE, mScore);
        savedInstanceState.putInt(BUNDLE_STATE_QUESTION, mRemainingQuestionCount);

        savedInstanceState.putSerializable(BUNDLE_STATE_QUESTION_BANK, mQuestionBank);
        savedInstanceState.putSerializable(BUNDLE_STATE_CURRENT_QUESTION, mCurrentQuestion);
    }

    /**
     * For app rotation
     * We'll use the saved information to recreate the view
     * @param savedInstanceState saved instance
     */
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        mScore = savedInstanceState.getInt(BUNDLE_STATE_SCORE);
        mRemainingQuestionCount = savedInstanceState.getInt(BUNDLE_STATE_QUESTION);

        mQuestionBank = (QuestionBank) savedInstanceState.getSerializable(BUNDLE_STATE_QUESTION_BANK);
        mCurrentQuestion = (Question) savedInstanceState.getSerializable(BUNDLE_STATE_CURRENT_QUESTION);
        displayQuestion(mCurrentQuestion);
    }

    //NOTIFICATIONS

    /**
     * For notifications
     * When leaving activity, we will start a timer that ask the player to come back play again in x seconds
     */
    @Override
    protected void onPause() {
        super.onPause();
        if(mRemainingQuestionCount>0){
            wasInBackground = true;
            startingNotificationTimer();
        }
    }

    /**
     * For notifications
     * When the user comes back, we will stop the notification timer and not send it
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (wasInBackground) {
            wasInBackground = false;
            //stop notification timer
            alarmManager.cancel(pendingIntent);
        }
    }

    /**
     * For notifications
     * Starting a timer of x seconds and send a notification to the user at the end of the timer
     */
    private void startingNotificationTimer(){
        Toast.makeText(this, "Pressed reminder", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(GameActivity.this, ReminderBroadcast.class);
        pendingIntent = PendingIntent.getBroadcast(GameActivity.this,0, intent,0);

        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        long timeAtButtonClick = System.currentTimeMillis();

        long duration = 5 * 3;

        alarmManager.set(AlarmManager.RTC_WAKEUP,
                timeAtButtonClick + duration,
                pendingIntent);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return mEnableTouchEvents && super.dispatchTouchEvent(ev);
    }

    /**
     * For the view
     * Display the question and its 4 possible anwsers
     * @param question question to be displayed
     */
    private void displayQuestion(final Question question){
        mTextViewQuestion.setText(question.getQuestion());
        mGameButton1.setText(question.getChoiceList().get(0));
        mGameButton2.setText(question.getChoiceList().get(1));
        mGameButton3.setText(question.getChoiceList().get(2));
        mGameButton4.setText(question.getChoiceList().get(3));
    }


    /**
     * For the model
     * Question Bank
     * @return QuestionBank the list of questions available
     */
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

    /**
     * Detecting which answer or joker is pressed
     * also deactivate an answer when using second joker
     * @param view current View containing the buttons
     */
    @Override
    public void onClick(View view) {
        int index;
        int rand;

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


    /**
     * Check if the answer is correct
     * Sends Toasts and play Sounds
     * Start next question and reset timer
     * Stop timer if the last question was asked
     *
     * @param response boolean if answer is correct
     */
    private void correction(boolean response){
        if(response){
            Toast.makeText(GameActivity.this, "Correct !", Toast.LENGTH_SHORT).show();
            mScore+=10;
            playSound(R.raw.correct_sound);
        }else{
            if(mTimerRunning){
                Toast.makeText(GameActivity.this, "Incorrect !", Toast.LENGTH_SHORT).show();
                playSound(R.raw.incorrect_sound);
            }else{
                Toast.makeText(GameActivity.this, "Timeout !", Toast.LENGTH_SHORT).show();
                playSound(R.raw.incorrect_sound);
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


    /**
     * When last Question is answered, display a screen
     * If Ok is pressed, we go back to Main Activity
     */
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


    /**
     * For Question Timer
     * Start a new timer and decreases it each seconds
     * Display the timer and updates it each seconds
     * When timer runs, the answer is incorrect
     */
    private void startTimer(){
        mCountDownTimer = new CountDownTimer(mTimeLeftInMs, 1000) {
            @Override
            public void onTick(long l) {
                mTimeLeftInMs = l;
                updateCountDownText();
                //System.out.println(mTimeLeftInMs);
                if(mTimeLeftInMs > 7.5 * 1000 && mTimeLeftInMs < 9 * 1000){
                    playSound(R.raw.tick_sounds);
                }
            }

            @Override
            public void onFinish() {
                mTimerRunning = false;
                correction(false);
            }
        }.start();
        mTimerRunning = true;
    }


    /**
     * For Question timer
     * Format the timer time to display it
     */
    private void updateCountDownText(){
        int minutes = (int) (mTimeLeftInMs/1000) / 60;
        int seconds = (int) (mTimeLeftInMs/1000) % 60;
        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        mTextViewTimer.setText(timeLeftFormatted);
    }


    /**
     * For Sound
     * Play the sound
     * @param soundId R.raw.name_of_sound.wav
     */
    public void playSound(int soundId){
        if(player == null){
            player = MediaPlayer.create(this, soundId);
            player.setOnCompletionListener(mediaPlayer -> stopSound());
        }

        player.start();
    }
    public void stopSound(){
        if(player != null){
            player.release();
            player = null;
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        stopSound();
    }

    /**
     * When Pressing Back,
     * A screen shows that the player has forfeited
     */
    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Forfeit")
                .setMessage("Your score is " + mScore)
                .setPositiveButton("OK", (dialog, which) -> {
                    Intent intent = new Intent();
                    intent.putExtra(BUNDLE_EXTRA_SCORE, mScore);
                    setResult(RESULT_OK, intent);
                    finish();
                })
                .create()
                .show();

        endingBackgroundTasks();
    }

    /**
     * For forfeit
     * Ending all background tasks
     */
    private void endingBackgroundTasks(){
        stopSound();

        mTimerRunning = false;
        mCountDownTimer.cancel();
        mEnableTouchEvents = false;
    }

}