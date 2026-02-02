package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainMenu2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu2);
    }

    public void onclick(View v) {
        Intent i = new Intent(MainMenu2.this, MainMenu3.class);
        startActivity(i);
    }
    public void onclick1(View v) {
        Intent i = new Intent(MainMenu2.this, MainMenu3.class);
        startActivity(i);
    }
}