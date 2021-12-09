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

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class RegisterActivity extends AppCompatActivity {


    private Button mButtonValidateRegister;

    private EditText mEditTextRegisterUsername;
    private EditText mEditTextRegisterPassword;
    private EditText mEditTextRegisterConfirmPassword;

    private boolean userFlag = false;
    private boolean passFlag = false;
    private boolean confirmFlag = false;

    private String usernameString;
    private String passwordString;
    private String confirmString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        ImageView imageViewTopQuizz = findViewById(R.id.imageView_topquizz);
        imageViewTopQuizz.setImageResource(R.drawable.topquizz);

        mEditTextRegisterUsername = findViewById(R.id.ET_register_username);
        mEditTextRegisterPassword = findViewById(R.id.ET_register_password);
        mEditTextRegisterConfirmPassword = findViewById(R.id.ET_register_confirm_password);

        mButtonValidateRegister = findViewById(R.id.button_validate_register);
        mButtonValidateRegister.setEnabled(false);

        activateButton();


        mButtonValidateRegister.setOnClickListener(view -> {
            usernameString = mEditTextRegisterUsername.getText().toString();
            passwordString = mEditTextRegisterPassword.getText().toString();
            confirmString = mEditTextRegisterConfirmPassword.getText().toString();
            if(!passwordString.equals(confirmString)){
                Toast.makeText(this, "Passwords do not match ", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "New user created ! ", Toast.LENGTH_SHORT).show();
                new AddingNewUser().execute(usernameString,passwordString);
            }
        });
    }

    private void activateButton(){

        mEditTextRegisterUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                userFlag = !(s.toString().isEmpty());
                mButtonValidateRegister.setEnabled(userFlag && passFlag && confirmFlag);
            }
        });
        mEditTextRegisterPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                passFlag = !(s.toString().isEmpty());
                mButtonValidateRegister.setEnabled(userFlag && passFlag && confirmFlag);
            }
        });
        mEditTextRegisterConfirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                confirmFlag = !(s.toString().isEmpty());
                mButtonValidateRegister.setEnabled(userFlag && passFlag && confirmFlag);
            }
        });
    }

    public class AddingNewUser extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            trustEveryone();
            URL url;
            HttpURLConnection urlConnection = null;
            try {
                String username = params[0];
                String password = params[1];
                url = new URL("https://10.0.2.2/newUser.php?username="+username+"&password="+password+"");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setChunkedStreamingMode(0);


                OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Intent intent = new Intent();
            intent.setClass(getApplicationContext(),MenuActivity.class);
            intent.putExtra("username", usernameString);
            startActivity(intent);
        }
    }

}