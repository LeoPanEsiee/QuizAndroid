package com.example.courstest1.Activities;

import static com.example.courstest1.model.CertificateManager.trustEveryone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
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

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
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
    QuestionBank mQuestionBank ;
    private int mRemainingQuestionCount;
    Question mCurrentQuestion;
    private int mScore;
    public static final String BUNDLE_STATE_SCORE = "BUNDLE_STATE_SCORE";
    public static final String BUNDLE_STATE_QUESTION = "BUNDLE_STATE_QUESTION";
    public static final String BUNDLE_STATE_QUESTION_BANK = "BUNDLE_STATE_QUESTION_BANK";
    public static final String BUNDLE_STATE_CURRENT_QUESTION = "BUNDLE_STATE_CURRENT_QUESTION";
    public static final String BUNDLE_STATE_TIMER_RUNNING = "BUNDLE_STATE_TIMER_RUNNING";
    public static final String BUNDLE_STATE_CURRENT_TIMER_MS = "BUNDLE_STATE_CURRENT_TIMER_MS";

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
    private static final long START_TIME_IN_MS = 20 * 1000;
    private long mTimeLeftInMs = START_TIME_IN_MS;

    //Used for playing sounds
    MediaPlayer player;


    String username;



    /**
     * On creation of the activity
     * @param savedInstanceState saved instance
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        System.out.println("ON CREATE");

        setContentView(R.layout.activity_game);


        mTextViewQuestion = findViewById(R.id.game_activity_textview_question);

        gettingViews();

        Intent intent = getIntent();
        mQuestionBank = (QuestionBank) intent.getSerializableExtra("QuestionBank");
        //mQuestionBank = generateQuestions();
        mCurrentQuestion = mQuestionBank.getCurrentQuestion();
        displayQuestion(mCurrentQuestion);


        createNotificationChannel();

        mEnableTouchEvents = true;

        if(savedInstanceState != null){
            mRemainingQuestionCount = savedInstanceState.getInt(BUNDLE_STATE_QUESTION);
            mScore = savedInstanceState.getInt(BUNDLE_STATE_SCORE);
        }else{
            mRemainingQuestionCount = 5;
            mScore = 0;
        }


        mTextViewTimer = findViewById(R.id.game_activity_textview_countdown_timer);


        startTimer();



        liste[0] = mGameButton1;
        liste[1] = mGameButton2;
        liste[2] = mGameButton3;
        liste[3] = mGameButton4;

        username = getIntent().getStringExtra("username");
    }


    private void gettingViews(){

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
    }




    /**
     * For app rotation
     * We'll save the current game information to use them when the rotation is over
     * @param savedInstanceState saved instance
     */
    @Override
    protected void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        System.out.println("ON SAVE");
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt(BUNDLE_STATE_SCORE, mScore);
        savedInstanceState.putInt(BUNDLE_STATE_QUESTION, mRemainingQuestionCount);

        savedInstanceState.putSerializable(BUNDLE_STATE_QUESTION_BANK, mQuestionBank);
        savedInstanceState.putSerializable(BUNDLE_STATE_CURRENT_QUESTION, mCurrentQuestion);

        savedInstanceState.putSerializable(BUNDLE_STATE_TIMER_RUNNING, mTimerRunning);
        savedInstanceState.putSerializable(BUNDLE_STATE_CURRENT_TIMER_MS, mTimeLeftInMs);
    }

    /**
     * For app rotation
     * We'll use the saved information to recreate the view
     * @param savedInstanceState saved instance
     */
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        System.out.println("ON RESTORE");
        super.onRestoreInstanceState(savedInstanceState);

        gettingViews();

        mScore = savedInstanceState.getInt(BUNDLE_STATE_SCORE);
        mRemainingQuestionCount = savedInstanceState.getInt(BUNDLE_STATE_QUESTION);

        mQuestionBank = (QuestionBank) savedInstanceState.getSerializable(BUNDLE_STATE_QUESTION_BANK);
        mCurrentQuestion = (Question) savedInstanceState.getSerializable(BUNDLE_STATE_CURRENT_QUESTION);
        displayQuestion(mCurrentQuestion);
        mTimerRunning = (boolean) savedInstanceState.getSerializable(BUNDLE_STATE_TIMER_RUNNING);
        mTimeLeftInMs = (long) savedInstanceState.getSerializable(BUNDLE_STATE_CURRENT_TIMER_MS);

        if(mTimerRunning){
            mTimerRunning = false;

            mCountDownTimer.cancel();
            mCountDownTimer = null;
            //endingBackgroundTasks();
            startTimer();
        }
    }

    //NOTIFICATIONS

    /**
     * For notifications
     * When leaving activity, we will start a timer that ask the player to come back play again in x seconds
     */
    @Override
    protected void onPause() {
        System.out.println("ON PAUSE");
        super.onPause();
        if(mRemainingQuestionCount>0){
            wasInBackground = true;
            //endingBackgroundTasks();
            startingNotificationTimer();
            //stopSound();
        }
    }

    /**
     * For notifications
     * When the user comes back, we will stop the notification timer and not send it
     */
    @Override
    protected void onResume() {
        System.out.println("ON RESUME");
        super.onResume();
        if (wasInBackground) {
            gettingViews();
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
        Intent intent = new Intent(GameActivity.this, ReminderBroadcast.class);
        pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),1253, intent,0);

        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        long timeAtButtonClick = System.currentTimeMillis();

        long duration = 5 * 1000;

        alarmManager.set(AlarmManager.RTC_WAKEUP,
                timeAtButtonClick + duration,
                pendingIntent);
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

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        System.out.println("Dispatch");
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
            Toast.makeText(GameActivity.this, getString(R.string.correct), Toast.LENGTH_SHORT).show();
            mScore+=10;
            playSound(R.raw.correct_sound);
        }else{
            if(mTimerRunning){
                Toast.makeText(GameActivity.this, getString(R.string.incorrect), Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(GameActivity.this, getString(R.string.timeout), Toast.LENGTH_SHORT).show();
            }
            playSound(R.raw.incorrect_sound);
        }

        //endingBackgroundTasks();
        mTimerRunning = false;
        try{
            mCountDownTimer.cancel();
            mCountDownTimer = null;
        }catch(NullPointerException e){
            System.out.println(e);
        }
        //stopSound();


        new Handler().postDelayed(() -> {
            for(int i=0;i<4;i++){
            liste[i].setVisibility(View.VISIBLE);}
            mJokerPress = 0;
            liste[mQuestionBank.getCurrentQuestion().getAnswerIndex()].setBackgroundColor(Color.parseColor("#0B91E4"));
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
            //mEnableTouchEvents = true;
        }, 2000);
    }


    /**
     * When last Question is answered, display a screen
     * If Ok is pressed, we go back to Main Activity
     */
    private void endGame(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(getString(R.string.well_done))
                .setMessage("Score :" + mScore)
                .setPositiveButton("OK", (dialog, which) -> {
                    Intent intent = new Intent();
                    intent.putExtra(BUNDLE_EXTRA_SCORE, mScore);
                    setResult(RESULT_OK, intent);
                    finish();
                })
                .create()
                .show();

        new UpdatingScore().execute(username, String.valueOf(mScore));
    }


    /**
     * For Question Timer
     * Start a new timer and decreases it each seconds
     * Display the timer and updates it each seconds
     * When timer runs, the answer is incorrect
     */
    private void startTimer(){
        if(!mTimerRunning){
            mCountDownTimer = new CountDownTimer(mTimeLeftInMs, 1000) {
                @Override
                public void onTick(long l) {
                    mTimeLeftInMs = l;
                    updateCountDownText();

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

        System.out.println("ON STOP");
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

        //endingBackgroundTasks();
        mCountDownTimer.cancel();
        mCountDownTimer = null;
        new UpdatingScore().execute(username, String.valueOf(mScore));
    }

    /**
     * For forfeit
     * Ending all background tasks
     */
    /*
    private void endingBackgroundTasks(){
        if(mTimeLeftInMs < 9 * 1000){
            stopSound();
        }
        mCountDownTimer.cancel();
        mEnableTouchEvents = false;
    }
    */

        int DB_score = 0;

    public class UpdatingScore extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            new GetScore().execute("http://109.221.187.188:8005/getScore.php?username=" + username);
        }

        @Override
        protected String doInBackground(String... params) {

            trustEveryone();
            URL url;
            HttpURLConnection urlConnection = null;
            try {
                String username = params[0];
                String score = params[1];

                if(DB_score < Integer.parseInt(score)){
                    url = new URL("http://109.221.187.188:8005/updateScore.php?username="+username+"&score="+score+"");
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setDoOutput(true);
                    urlConnection.setChunkedStreamingMode(0);


                    OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());
                    out.flush();
                }


            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }

            return null;
        }

    }

    public class GetScore extends AsyncTask<String, Void, Void> {

        protected Void doInBackground(String... urls) {
            trustEveryone();
            String urlOfData = urls[0];
            String data = "";

            try{
                URL url = new URL(urlOfData) ;
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = connection.getInputStream();

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                data = bufferedReader.readLine();
                DB_score = Integer.parseInt(data);

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
        }
    }




}