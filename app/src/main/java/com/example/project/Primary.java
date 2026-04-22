package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Primary extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_primary_main);
        setupCategoryButtons();
        setupBottomNavigation();
    }
    private void setupBottomNavigation() {
        View btnHome = findViewById(R.id.nav_home);
        View btnCatalog = findViewById(R.id.nav_catalog);
        View btnProjects = findViewById(R.id.nav_projects);
        View btnProfile = findViewById(R.id.nav_profile);

        // Метод для запуска Activity с защитой от дублирования
        View.OnClickListener navListener = v -> {
            Intent intent;
            int id = v.getId();

            if (id == R.id.nav_home) {
                // Если мы уже на главной, ничего не делаем
                return;
            } else if (id == R.id.nav_catalog) {
                intent = new Intent(this, PrimaryMain2Activity.class);
            } else if (id == R.id.nav_projects) {
                intent = new Intent(this, Primary3.class);
            } else if (id == R.id.nav_profile) {
                intent = new Intent(this, Primary4.class);
            } else {
                return;
            }

            // Этот флаг не создает новую Activity, если она уже открыта
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);

            // Убираем анимацию, чтобы переход казался мгновенным, как в настоящем меню
            overridePendingTransition(0, 0);
        };

        // Присваиваем один лисенер всем кнопкам
        btnHome.setOnClickListener(navListener);
        btnCatalog.setOnClickListener(navListener);
        btnProjects.setOnClickListener(navListener);
        btnProfile.setOnClickListener(navListener);
    }
    private void setupCategoryButtons() {
        View btnAll = findViewById(R.id.btn_filter_all);
        View btnWomen = findViewById(R.id.btn_filter_women);
        View btnMen = findViewById(R.id.btn_filter_men);

        View.OnClickListener filterListener = v -> {
            // Сбрасываем все в полупрозрачный вид
            btnAll.setAlpha(0.5f);
            btnWomen.setAlpha(0.5f);
            btnMen.setAlpha(0.5f);

            // Подсвечиваем нажатую кнопку
            v.setAlpha(1.0f);

            // Здесь в будущем будет логика фильтрации списка
            Toast.makeText(this, "Фильтр активирован", Toast.LENGTH_SHORT).show();
        };

        btnAll.setOnClickListener(filterListener);
        btnWomen.setOnClickListener(filterListener);
        btnMen.setOnClickListener(filterListener);
    }
}