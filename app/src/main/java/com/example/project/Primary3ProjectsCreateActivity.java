package com.example.project;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Primary3ProjectsCreateActivity extends AppCompatActivity {

    private EditText etType, etProjectName, etStartDate, etEndDate, etTarget, etSource, etCategory;
    private Button btnConfirm;


    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_primary3_projects_create);

        // Инициализация всех полей
        etType = findViewById(R.id.et_type); // Item 1
        etProjectName = findViewById(R.id.et_project_name);
        etStartDate = findViewById(R.id.et_start_date);
        etEndDate = findViewById(R.id.et_end_date);
        etTarget = findViewById(R.id.et_target); // Item 1 (Кому)
        etSource = findViewById(R.id.et_source); // example.com
        etCategory = findViewById(R.id.et_category); // Item 1 (Категория)

        btnConfirm = findViewById(R.id.btn_confirm);

        // Слушатель кнопки подтверждения
        btnConfirm.setOnClickListener(v -> {
            saveAndReturn();
        });
        setupBottomNavigation();
    }
    private void saveAndReturn() {
        String name = etProjectName.getText().toString().trim();
        String type = etType.getText().toString().trim();

        if (name.isEmpty()) {
            etProjectName.setError("Введите название проекта");
            return;
        }

        // Сохраняем данные нового проекта в память
        SharedPreferences prefs = getSharedPreferences("ProjectPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        // Для учебного проекта сохраним хотя бы название и дату
        editor.putString("last_project_name", name);
        editor.putString("last_project_date", "Только что");
        editor.putBoolean("has_new_project", true); // Флаг, что проект создан
        editor.apply();

        Toast.makeText(this, "Проект создан!", Toast.LENGTH_SHORT).show();

        // Закрываем страницу и возвращаемся к списку
        finish();
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