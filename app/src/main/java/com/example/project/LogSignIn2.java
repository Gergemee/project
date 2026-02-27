package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LogSignIn2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_sign_in2);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(LogSignIn2.this, PasswordCode.class);
                startActivity(i);
            }
        }, 2000);

    }

    public void onwrite() {
    new Handler().postDelayed(new Runnable() {
        @Override
        public void run() {
            Intent i = new Intent(LogSignIn2.this, PasswordCode.class);
            startActivity(i);
        }
    }, 2000);
    }
    public void onclick(View v) {
        Intent i = new Intent(LogSignIn2.this, PasswordCode.class);
        startActivity(i);
    }
}