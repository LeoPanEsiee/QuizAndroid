package com.example.courstest1.Activities;

import static com.example.courstest1.model.CertificateManager.trustEveryone;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.courstest1.R;
import com.example.courstest1.model.Question;
import com.example.courstest1.model.QuestionBank;
import com.example.courstest1.model.User;

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
import java.util.Locale;

public class MenuActivity extends AppCompatActivity {


    User currentUser = new User();
    TextView TextView_name_placeholder;
    TextView TextView_score_placeholder;

    String username;


    private static final int GAME_ACTIVITY_REQUEST_CODE = 42;


    QuestionBank mQuestionBank = new QuestionBank();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        ImageView imageViewTopQuizz = findViewById(R.id.imageView_topquizz);
        imageViewTopQuizz.setImageResource(R.drawable.topquizz);


        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        currentUser.setFirstName(username);


        TextView_name_placeholder = findViewById(R.id.TV_name_placeholder);
        String welcomeMessage = TextView_name_placeholder.getText().toString();
        TextView_name_placeholder.setText(welcomeMessage + username);

        TextView_score_placeholder = findViewById(R.id.TV_score_placeholder);


        new DownloadUser().execute("http://109.221.187.188:8005/getScore.php?username=" + username);




        findViewById(R.id.button_menu_leaderboard).setOnClickListener(view -> {
            startActivity(new Intent(this, LeaderboardActivity.class));
        });

        findViewById(R.id.button_menu_start).setOnClickListener(view -> {

            new DownloadQuestions().execute("http://109.221.187.188:8005/getQuestions"+Locale.getDefault().getLanguage()+".php");
        });



    }

    @Override
    protected void onResume() {
        super.onResume();
        new DownloadUser().execute("http://109.221.187.188:8005/getScore.php?username=" + username);
    }

    public class DownloadUser extends AsyncTask<String, Void, Void> {

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
                currentUser.setScore(Integer.parseInt(data));

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            String lang = Locale.getDefault().getLanguage();
            String str ="";
            switch(lang){
                case "fr":
                    str = "Votre score actuel est : ";
                    break;
                case "es":
                    str = "Su puntuación actual es : ";
                    break;
                default:
                    str = "Your current score is : ";
            }
            TextView_score_placeholder.setText(str+currentUser.getScore());
        }
    }

    /**
     * When Pressing Back, goes back to Launch
     */
    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, LaunchActivity.class));
    }


    /**
     * Download async task
     * Done in background
     * Get the List of Questions
     */
    public class DownloadQuestions extends AsyncTask<String, Void, QuestionBank> {

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

            mQuestionBank.shuffleQuestions();

            Intent gameActivityIntent = new Intent(MenuActivity.this, GameActivity.class);
            gameActivityIntent.putExtra("QuestionBank", mQuestionBank);
            gameActivityIntent.putExtra("username", currentUser.getFirstName());
            startActivityForResult(gameActivityIntent, GAME_ACTIVITY_REQUEST_CODE);
        }
    }
}