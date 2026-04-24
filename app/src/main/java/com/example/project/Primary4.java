package com.example.project;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Primary4 extends AppCompatActivity {

    private TextView tvUserName, tvUserEmail;
    private APIService apiService;
    private String userToken;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_primary4_profile);

        tvUserName = findViewById(R.id.textname);
        tvUserEmail = findViewById(R.id.textmail);
        TextView btnExit = findViewById(R.id.btn_exit);

        apiService = APIClient.getApiService();

        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        userToken = prefs.getString("auth_token", "");
        userId = prefs.getString("user_id", "");

        tvUserName.setText(prefs.getString("current_user_name", "Загрузка..."));
        tvUserEmail.setText(prefs.getString("current_user_email", ""));

        if (!userToken.isEmpty()) {
            fetchUserProfile();
        }

        setupBottomNavigation();

        btnExit.setOnClickListener(v -> logoutUser());
    }

    private void fetchUserProfile() {

        apiService.getOrders("Bearer " + userToken, 1, 1).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(Primary4.this, "Ошибка синхронизации", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void logoutUser() {
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        prefs.edit().clear().apply();

        Intent intent = new Intent(this, LogSignIn.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
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
            } else return;

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
