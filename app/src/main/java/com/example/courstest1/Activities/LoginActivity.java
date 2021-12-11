package com.example.courstest1.Activities;

import static com.example.courstest1.model.CertificateManager.trustEveryone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.courstest1.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

public class LoginActivity extends AppCompatActivity {

    EditText mEditTextLoginUsername;
    EditText mEditTextLoginPassword;

    Button mButtonValidateLogin;

    boolean userFlag;
    boolean passFlag;

    String usernameString;
    String passwordString;

    boolean response = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ImageView imageViewTopQuizz = findViewById(R.id.imageView_topquizz);
        imageViewTopQuizz.setImageResource(R.drawable.topquizz);

        mEditTextLoginUsername = findViewById(R.id.ET_login_username);
        mEditTextLoginPassword = findViewById(R.id.ET_login_password);

        mButtonValidateLogin = findViewById(R.id.button_validate_login);
        mButtonValidateLogin.setEnabled(false);
        activateButton();


        mButtonValidateLogin.setOnClickListener(view -> {
            usernameString = mEditTextLoginUsername.getText().toString();
            passwordString = mEditTextLoginPassword.getText().toString();

            StringBuilder hashedPassword = new StringBuilder();

            try {
                MessageDigest msg = MessageDigest.getInstance("SHA-256");
                byte[] hash = msg.digest(passwordString.getBytes(StandardCharsets.UTF_8));
                for (byte b : hash) {
                    hashedPassword.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
                }
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }

            new CheckLogin(getApplicationContext()).execute("http://109.221.187.188:8005/login.php?username=" + usernameString + "&password=" + hashedPassword);
        });
    }



    private void activateButton(){

        mEditTextLoginUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                userFlag = !(s.toString().isEmpty());
                mButtonValidateLogin.setEnabled(userFlag && passFlag);
            }
        });
        mEditTextLoginPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                passFlag = !(s.toString().isEmpty());
                mButtonValidateLogin.setEnabled(userFlag && passFlag);
            }
        });
    }

    public class CheckLogin extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        Context mContext;
        public CheckLogin(final Context context)
        {
            mContext = context;
        }
        @Override
        protected String doInBackground(String... params) {
            trustEveryone();
            String urlOfData = params[0];
            String data = "";

            try{
                URL url = new URL(urlOfData) ;
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = connection.getInputStream();

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                data = bufferedReader.readLine();
                System.out.println(data);

                response = Boolean.parseBoolean(data);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(response){
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(),MenuActivity.class);
                intent.putExtra("username", usernameString);
                startActivity(intent);
            }else{
                Toast.makeText(mContext,
                        "Incorrect Login or Password",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

}