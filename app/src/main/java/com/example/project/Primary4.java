package com.example.project;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView; // Не забудьте импорт
import androidx.appcompat.app.AppCompatActivity;

public class Primary4 extends AppCompatActivity {

    // Объявляем переменные для текста
    private TextView tvUserName, tvUserEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_primary4_profile);

        tvUserName = findViewById(R.id.textname);
        tvUserEmail = findViewById(R.id.textmail);
        TextView btnExit = findViewById(R.id.btn_exit);

        // Достаем данные из постоянной памяти
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String savedName = prefs.getString("current_user_name", "Гость");
        String savedEmail = prefs.getString("current_user_email", "не указана");

        // Устанавливаем в текстовые поля
        tvUserName.setText(savedName);
        tvUserEmail.setText(savedEmail);

        setupBottomNavigation();

        // Логика кнопки "Выход" — ТЕПЕРЬ ОНА ЕЩЕ И СТИРАЕТ ДАННЫЕ
        btnExit.setOnClickListener(v -> {
            logoutUser();
        });
    }

    private void setupBottomNavigation() {
        View btnHome = findViewById(R.id.nav_home);
        View btnCatalog = findViewById(R.id.nav_catalog);
        View btnProjects = findViewById(R.id.nav_projects);
        View btnProfile = findViewById(R.id.nav_profile);

        View.OnClickListener navListener = v -> {
            Intent intent;
            int id = v.getId();

            if (id == R.id.nav_profile) return;

            if (id == R.id.nav_catalog) {
                intent = new Intent(this, PrimaryMain2Activity.class);
            } else if (id == R.id.nav_projects) {
                intent = new Intent(this, Primary3.class);
            } else if (id == R.id.nav_home) {
                intent = new Intent(this, Primary.class);
            } else {
                return;
            }

            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            overridePendingTransition(0, 0);
        };

        btnHome.setOnClickListener(navListener);
        btnCatalog.setOnClickListener(navListener);
        btnProjects.setOnClickListener(navListener);
        btnProfile.setOnClickListener(navListener);
    }
    private void logoutUser() {
        // 1. Очищаем SharedPreferences
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        prefs.edit().clear().apply(); // Удаляет всё сохраненное

        // 2. Переходим на экран входа
        Intent intent = new Intent(this, LogSignIn.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}