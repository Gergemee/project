package com.example.project;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Primary3 extends AppCompatActivity {

    private LinearLayout projectsContainer;
    private APIService apiService;
    private String userToken;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_primary3_projects);

        apiService = APIClient.getApiService();

        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        userToken = prefs.getString("auth_token", "");

        projectsContainer = findViewById(R.id.your_card_id);

        projectsContainer.setVisibility(View.GONE);

        View btnAddProject = findViewById(R.id.btn_add_project);
        btnAddProject.setOnClickListener(v -> {
            Intent intent = new Intent(Primary3.this, Primary3ProjectsCreateActivity.class);
            startActivity(intent);
        });

        setupBottomNavigation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!userToken.isEmpty()) {
            loadProjects();
        } else {
            Toast.makeText(this, "Вы не авторизованы", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadProjects() {
        apiService.getProjects("Bearer " + userToken, 1, 50).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    projectsContainer.removeAllViews();
                    JsonObject body = response.body();

                    if (body.has("items")) {
                        JsonArray items = body.getAsJsonArray("items");

                        if (items.size() > 0) {
                            for (JsonElement element : items) {
                                JsonObject project = element.getAsJsonObject();

                                String title = project.has("title") ? project.get("title").getAsString() : "Без названия";
                                String date = project.has("date_start") ? project.get("date_start").getAsString() : "Дата не указана";

                                addProjectCard(title, date);
                            }
                            projectsContainer.setVisibility(View.VISIBLE);
                        } else {
                            projectsContainer.setVisibility(View.GONE);
                            Toast.makeText(Primary3.this, "Проектов пока нет", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(Primary3.this, "Сервер вернул ошибку: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                projectsContainer.setVisibility(View.GONE);
                Toast.makeText(Primary3.this, "Ошибка сети: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addProjectCard(String title, String date) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View card = inflater.inflate(R.layout.activity_project, projectsContainer, false);

        TextView tvTitle = card.findViewById(R.id.your_text_view_id);
        TextView tvDate = card.findViewById(R.id.textView21);

        if (tvTitle != null) tvTitle.setText(title);
        if (tvDate != null) tvDate.setText(date);

        projectsContainer.addView(card);
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
