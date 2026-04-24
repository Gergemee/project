package com.example.project;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonObject;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Primary3ProjectsCreateActivity extends AppCompatActivity {

    private EditText etProjectName, etStartDate, etEndDate, etSource;
    private Button btnConfirm;
    private Spinner etType, etTarget, etCategory;
    private APIService apiService;
    private String userToken;

    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_primary3_projects_create);

        apiService = APIClient.getApiService();
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        userToken = "Bearer " + prefs.getString("auth_token", "");

        etType = findViewById(R.id.et_type);
        etProjectName = findViewById(R.id.et_project_name);
        etStartDate = findViewById(R.id.et_start_date);
        etEndDate = findViewById(R.id.et_end_date);
        etTarget = findViewById(R.id.et_target);
        etSource = findViewById(R.id.et_source);
        etCategory = findViewById(R.id.et_category);
        btnConfirm = findViewById(R.id.btn_confirm);

        setupSpinners();

        btnConfirm.setOnClickListener(v -> saveAndReturn());
        setupBottomNavigation();
    }

    private void setupSpinners() {
        String[] types = {"Верхняя одежда", "Футболки и топы", "Брюки и джинсы", "Аксессуары", "Обувь"};
        setupSingleSpinner(etType, types);

        String[] targets = {"Мужчинам", "Женщинам", "Детям (Унисекс)", "Новорожденным"};
        setupSingleSpinner(etTarget, targets);

        String[] categories = {"Повседневная (Casual)", "Спортивная", "Деловая", "Вечерняя мода", "Летняя коллекция"};
        setupSingleSpinner(etCategory, categories);
    }

    private void setupSingleSpinner(Spinner spinner, String[] data) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void saveAndReturn() {
        String name = etProjectName.getText().toString().trim();
        String type = etType.getSelectedItem().toString();
        String start = etStartDate.getText().toString().trim();
        String end = etEndDate.getText().toString().trim();
        String source = etSource.getText().toString().trim();
        String target = etTarget.getSelectedItem().toString();

        if (name.isEmpty()) {
            etProjectName.setError("Введите название проекта");
            return;
        }

        RequestBody rbTitle = RequestBody.create(MediaType.parse("text/plain"), name);
        RequestBody rbType = RequestBody.create(MediaType.parse("text/plain"), type);
        RequestBody rbStart = RequestBody.create(MediaType.parse("text/plain"), start);
        RequestBody rbEnd = RequestBody.create(MediaType.parse("text/plain"), end);
        RequestBody rbSize = RequestBody.create(MediaType.parse("text/plain"), target);
        RequestBody rbSource = RequestBody.create(MediaType.parse("text/plain"), source);

        String userId = getSharedPreferences("UserPrefs", MODE_PRIVATE).getString("user_id", "");
        RequestBody rbUser = RequestBody.create(MediaType.parse("text/plain"), userId);

        MultipartBody.Part filePart = null;

        apiService.createProject(
                userToken,
                rbTitle,
                rbType,
                rbStart,
                rbEnd,
                rbSize,
                rbSource,
                filePart,
                rbUser
        ).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(Primary3ProjectsCreateActivity.this, "Проект успешно создан!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(Primary3ProjectsCreateActivity.this, "Ошибка: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(Primary3ProjectsCreateActivity.this, "Ошибка сети", Toast.LENGTH_SHORT).show();
            }
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
            if (id == R.id.nav_projects) return;

            if (id == R.id.nav_catalog) intent = new Intent(this, PrimaryMain2Activity.class);
            else if (id == R.id.nav_home) intent = new Intent(this, Primary.class);
            else if (id == R.id.nav_profile) intent = new Intent(this, Primary4.class);
            else return;

            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            overridePendingTransition(0, 0);
        };

        btnHome.setOnClickListener(navListener);
        btnCatalog.setOnClickListener(navListener);
        btnProjects.setOnClickListener(navListener);
        btnProfile.setOnClickListener(navListener);
    }
}