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
import com.example.courstest1.model.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MenuActivity extends AppCompatActivity {


    User currentUser = new User();
    TextView TextView_name_placeholder;
    TextView TextView_score_placeholder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        ImageView imageViewTopQuizz = findViewById(R.id.imageView_topquizz);
        imageViewTopQuizz.setImageResource(R.drawable.topquizz);


        Intent intent = getIntent();
        String username = intent.getStringExtra("username");

        TextView_name_placeholder = findViewById(R.id.TV_name_placeholder);
        TextView_name_placeholder.setText("Welcome back " + username);


        TextView_score_placeholder = findViewById(R.id.TV_score_placeholder);
        new DownloadData().execute("https://10.0.2.2/getScore.php?username=" + username);




    }

    public class DownloadData extends AsyncTask<String, Void, Void> {

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
            TextView_score_placeholder.setText("Your current score is : "+currentUser.getScore());
        }
    }

    /**
     * When Pressing Back, goes back to Launch
     */
    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, LaunchActivity.class));
    }
}