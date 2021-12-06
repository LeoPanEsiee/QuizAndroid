package com.example.courstest1.controller;

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


import com.example.courstest1.model.InputStreamOperations;
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
import java.net.MalformedURLException;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;

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



        QuestionBank questionBank = new QuestionBank();
        new DownloadData(questionBank).execute("https://10.0.2.2/getQuestions.php");


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


    private class DownloadData extends AsyncTask<String, Void, QuestionBank> {
        QuestionBank imageView;

        DownloadData(QuestionBank imageView) {
            this.imageView = imageView;
        }
        String data = "";

        protected QuestionBank doInBackground(String... urls) {


            trustEveryone();


            String urlOfData = urls[0];

            QuestionBank questionBank = new QuestionBank();
            try{
                URL url = new URL(urlOfData) ;
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                //connection.connect();
                InputStream inputStream = connection.getInputStream();

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line ="";
                while(line!=null){
                    line = bufferedReader.readLine();
                    data = data + line;
                }

                //System.out.println(data);

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

                    questionBank.add(question);

                    System.out.println(question);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return questionBank;
        }

        protected void onPostExecute(QuestionBank result) {
            //imageView.setImageBitmap(result);
        }
    }

    private void trustEveryone() {
        try {
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier(){
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }});
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, new X509TrustManager[]{new X509TrustManager(){
                public void checkClientTrusted(X509Certificate[] chain,
                                               String authType) throws CertificateException {}
                public void checkServerTrusted(X509Certificate[] chain,
                                               String authType) throws CertificateException {}
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }}}, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(
                    context.getSocketFactory());
        } catch (Exception e) { // should never happen
            e.printStackTrace();
        }
    }



}