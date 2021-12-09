package com.example.courstest1.Activities;

import static com.example.courstest1.model.CertificateManager.trustEveryone;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.AsyncTask;
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


import com.example.courstest1.model.Question;
import com.example.courstest1.model.QuestionBank;
import com.example.courstest1.model.User;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    private TextView mTextViewGreeting;
    private EditText mEditTextName;

    private EditText mEditTextPassword;
    private Button mButtonPlay;

    private String firstName;
    private int lastScore;

    User user = new User();


    private static final int GAME_ACTIVITY_REQUEST_CODE = 42;
    private static final String SHARED_PREF_USER_INFO = "SHARED_PREF_USER_INFO";
    private static final String SHARED_PREF_USER_INFO_NAME = "SHARED_PREF_USER_INFO_NAME";


    QuestionBank mQuestionBank = new QuestionBank();



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
        mEditTextPassword = findViewById(R.id.edittext_password);
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
            mQuestionBank.shuffleQuestions();
            getSharedPreferences(SHARED_PREF_USER_INFO, MODE_PRIVATE)
                    .edit()
                    .putString(SHARED_PREF_USER_INFO_NAME, mEditTextName.getText().toString())
                    .apply();

            Intent gameActivityIntent = new Intent(MainActivity.this, GameActivity.class);
            gameActivityIntent.putExtra("QuestionBank", mQuestionBank);
            startActivityForResult(gameActivityIntent, GAME_ACTIVITY_REQUEST_CODE);
        });

        //Used for Notification
        createNotificationChannel();


        new DownloadData().execute("https://10.0.2.2/getQuestions.php");


        Button mBDD = findViewById(R.id.button_BD);
        mBDD.setOnClickListener((view -> {
            //new DatabaseManager.CalPHP().execute(mEditTextName.getText().toString(),mEditTextPassword.getText().toString());;

        }));

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


    /**
     * Top String to display the user's info
     */
    private void displayUserInfo(){
        if(user.getFirstName() != null){
            mTextViewGreeting.setText(getString(R.string.WelcomeBack) + user.getFirstName() + "\n" +
                    getString(R.string.LastScoreWas)+ user.getScore() + getString(R.string.WillDoBetter));

        }
    }

    /**
     * Download async task
     * Done in background
     * Get the List of Questions
     */
    public class DownloadData extends AsyncTask<String, Void, QuestionBank> {

        protected QuestionBank doInBackground(String... urls) {
            trustEveryone();
            String urlOfData = urls[0];
            String data = "";

            try{
                URL url = new URL(urlOfData) ;
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = connection.getInputStream();

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line ="";
                while(line!=null){
                    line = bufferedReader.readLine();
                    data = data + line;
                }

                JSONArray JA = new JSONArray(data);
                for(int i = 0 ; i < JA.length();i++){
                    JSONObject obj = (JSONObject) JA.get(i);
                    Question question = new Question();
                    question.setQuestion(obj.getString("description"));

                    question.setChoiceList( Arrays.asList(
                            obj.getString("ans_1"),
                            obj.getString("ans_2"),
                            obj.getString("ans_3"),
                            obj.getString("ans_4")
                            )
                    );

                    question.setAnswerIndex(obj.getInt("result"));
                    question.setHintPhrase(obj.getString("hint"));

                    //mQuestionBank.add(question);
                    mQuestionBank.add(question);

                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            return mQuestionBank;
        }

        @Override
        protected void onPostExecute(QuestionBank questionBank) {
            super.onPostExecute(questionBank);

            System.out.println("********"+mQuestionBank);
        }
    }

}