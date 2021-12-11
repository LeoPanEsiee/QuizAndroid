package com.example.courstest1.Activities;

import static com.example.courstest1.model.CertificateManager.trustEveryone;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.AsyncTask;
import android.os.Bundle;

import com.example.courstest1.R;
import com.example.courstest1.model.LeaderboardAdapter;
import com.example.courstest1.model.PlayerScoreItem;
import com.example.courstest1.model.Question;
import com.example.courstest1.model.QuestionBank;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

public class LeaderboardActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    ArrayList<PlayerScoreItem> exampleList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        exampleList = new ArrayList<>();


        mRecyclerView = findViewById(R.id.RecyclerView_Leaderboard);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);



        new GetLeaderboard().execute("https://109.221.187.188:8005/top10.php");

    }

    public class GetLeaderboard extends AsyncTask<String, Void, Void> {

        protected Void doInBackground(String... urls) {
            trustEveryone();
            String urlOfData = urls[0];
            String data = "";

            try {
                URL url = new URL(urlOfData);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = connection.getInputStream();

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line = "";
                while (line != null) {
                    line = bufferedReader.readLine();
                    data = data + line;
                }

                JSONArray JA = new JSONArray(data);
                for (int i = 0; i < JA.length(); i++) {
                    JSONObject obj = (JSONObject) JA.get(i);
                    System.out.println(obj.getString("login") + " "+ obj.getString("score"));
                    exampleList.add(new PlayerScoreItem(obj.getString("login"), obj.getString("score")));
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            mAdapter = new LeaderboardAdapter(exampleList);
            mRecyclerView.setLayoutManager(mLayoutManager);
            mRecyclerView.setAdapter(mAdapter);
        }
    }
}