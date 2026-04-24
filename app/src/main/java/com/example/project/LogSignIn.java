package com.example.project;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LogSignIn extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private APIService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_sign_in);

        apiService = APIClient.getApiService();

        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.but_login);
        TextView Registr = findViewById(R.id.textView2);
        Button btnVK = findViewById(R.id.btnvk);
        Button btnYandex = findViewById(R.id.btnyan);

        btnLogin.setEnabled(false);
        etEmail.addTextChangedListener(loginTextWatcher);
        etPassword.addTextChangedListener(loginTextWatcher);

        btnLogin.setOnClickListener(v -> loginViaApi());

        Registr.setOnClickListener(v -> startActivity(new Intent(this, CreateProfile.class)));
        btnVK.setOnClickListener(v -> startActivity(new Intent(this, PasswordCode.class)));
        btnYandex.setOnClickListener(v -> startActivity(new Intent(this, PasswordCode.class)));
    }

    private void loginViaApi() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        JsonObject body = new JsonObject();
        body.addProperty("identity", email);
        body.addProperty("password", password);

        btnLogin.setEnabled(false);
        btnLogin.setText("Вход...");

        apiService.authWithPassword(body).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    JsonObject resp = response.body();

                    String token = "";
                    if (resp.has("token")) {
                        token = resp.get("token").getAsString();
                    }

                    String name = "Пользователь";
                    String userId = "";

                    if (resp.has("record")) {
                        JsonObject record = resp.getAsJsonObject("record");

                        if (record.has("firstName") && !record.get("firstName").isJsonNull()) {
                            name = record.get("firstName").getAsString();
                        }

                        if (record.has("id")) {
                            userId = record.get("id").getAsString();
                        }
                    }

                    if (token.isEmpty()) {
                        Toast.makeText(LogSignIn.this, "Ошибка: Сервер не прислал токен", Toast.LENGTH_SHORT).show();
                        btnLogin.setEnabled(true);
                        return;
                    }

                    saveAuthData(token, email, name, userId);
                    startActivity(new Intent(LogSignIn.this, PasswordCode.class));
                    finish();

                } else {
                    btnLogin.setEnabled(true);
                    btnLogin.setText("Войти");
                    Toast.makeText(LogSignIn.this, "Неверный логин или пароль", Toast.LENGTH_SHORT).show();
                }
            }


            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                btnLogin.setEnabled(true);
                btnLogin.setText("Войти");
                Toast.makeText(LogSignIn.this, "Сетевая ошибка", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveAuthData(String token, String email, String name, String userId) {
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("auth_token", token);
        editor.putString("current_user_email", email);
        editor.putString("current_user_name", name);
        editor.putString("user_id", userId);
        editor.apply();
    }

    private final TextWatcher loginTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            boolean isReady = !etEmail.getText().toString().trim().isEmpty()
                    && etPassword.getText().toString().trim().length() >= 8;
            btnLogin.setEnabled(isReady);
        }
        @Override
        public void afterTextChanged(Editable s) {}
    };
}
