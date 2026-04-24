package com.example.project;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Primary extends AppCompatActivity {

    private APIService apiService;
    private String userToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_primary_main);

        apiService = APIClient.getApiService();

        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        userToken = prefs.getString("auth_token", "");

        setupCategoryButtons();
        setupBottomNavigation();
    }

    private void setupBottomNavigation() {
        View btnHome = findViewById(R.id.nav_home);
        View btnCatalog = findViewById(R.id.nav_catalog);
        View btnProjects = findViewById(R.id.nav_projects);
        View btnProfile = findViewById(R.id.nav_profile);

        View.OnClickListener navListener = v -> {
            Intent intent;
            int id = v.getId();

            if (id == R.id.nav_home) return;

            if (id == R.id.nav_catalog) {
                intent = new Intent(this, PrimaryMain2Activity.class);
            } else if (id == R.id.nav_projects) {
                intent = new Intent(this, Primary3.class);
            } else if (id == R.id.nav_profile) {
                intent = new Intent(this, Primary4.class);
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

    private void setupCategoryButtons() {
        View btnAll = findViewById(R.id.btnFilterAll);
        View btnWomen = findViewById(R.id.btnFilterWomen);
        View btnMen = findViewById(R.id.btnFilterMen);

        View.OnClickListener filterListener = v -> {
            btnAll.setAlpha(0.5f);
            btnWomen.setAlpha(0.5f);
            btnMen.setAlpha(0.5f);
            v.setAlpha(1.0f);

            Intent intent = new Intent(this, PrimaryMain2Activity.class);
            if (v.getId() == R.id.btnFilterMen) {
                intent.putExtra("CATEGORY", "men");
            } else if (v.getId() == R.id.btnFilterWomen) {
                intent.putExtra("CATEGORY", "women");
            }
            startActivity(intent);
        };

        btnAll.setOnClickListener(filterListener);
        btnWomen.setOnClickListener(filterListener);
        btnMen.setOnClickListener(filterListener);
    }
}
