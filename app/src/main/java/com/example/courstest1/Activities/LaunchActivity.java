package com.example.courstest1.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import com.example.courstest1.R;

public class LaunchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        findViewById(R.id.button_register).setOnClickListener(view -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });

        findViewById(R.id.button_login).setOnClickListener(view -> {
            startActivity(new Intent(this, LoginActivity.class));
        });

        ImageView imageViewTopQuizz = findViewById(R.id.imageView_topquizz);
        imageViewTopQuizz.setImageResource(R.drawable.topquizz);

    }
}