package com.example.project;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Primary3 extends AppCompatActivity {
    private TextView tvProjectTitle;
    private LinearLayout projectCard;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_primary3_projects); // ваш файл разметки

        // 1. Находим кнопку "+"
        // Убедитесь, что в XML у кнопки "+" ID: android:id="@+id/btn_add_project"
        View btnAddProject = findViewById(R.id.btn_add_project);
        tvProjectTitle = findViewById(R.id.your_text_view_id); // Замените на ваш ID для названия
        projectCard = findViewById(R.id.your_card_id);

        // 2. Устанавливаем слушатель нажатия
        btnAddProject.setOnClickListener(v -> {
            Intent intent = new Intent(Primary3.this, Primary3ProjectsCreateActivity.class);
            startActivity(intent);
        });


        projectCard.setVisibility(View.GONE);

        // Не забудьте вызвать навигацию и другие настройки
        setupBottomNavigation();

    }
    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences prefs = getSharedPreferences("ProjectPrefs", MODE_PRIVATE);
        boolean hasProject = prefs.getBoolean("has_new_project", false);

        if (hasProject) {
            String name = prefs.getString("last_project_name", "");

            // Теперь эти переменные распознаются
            tvProjectTitle.setText(name);
            projectCard.setVisibility(View.VISIBLE);
        }
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

            if (id == R.id.nav_projects) {
                // Если мы уже на главной, ничего не делаем
                return;
            } else if (id == R.id.nav_catalog) {
                intent = new Intent(this, PrimaryMain2Activity.class);
            } else if (id == R.id.nav_home) {
                intent = new Intent(this, Primary.class);
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
}