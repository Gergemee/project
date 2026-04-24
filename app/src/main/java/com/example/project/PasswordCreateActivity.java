package com.example.project;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PasswordCreateActivity extends AppCompatActivity {

    private String firstName, middleName, lastName, birthDate, gender, email;
    private EditText etNewPassword, etConfirmPassword;
    private Button btnSave;

    private APIService apiService;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_create);

        apiService = APIClient.getApiService();

        firstName = getIntent().getStringExtra("first_name");
        middleName = getIntent().getStringExtra("middle_name");
        lastName = getIntent().getStringExtra("last_name");
        birthDate = getIntent().getStringExtra("birth_date");
        gender = getIntent().getStringExtra("gender");
        email = getIntent().getStringExtra("email");

        etNewPassword = findViewById(R.id.new_password);
        etConfirmPassword = findViewById(R.id.confirm_password);
        btnSave = findViewById(R.id.btnsave);

        btnSave.setEnabled(false);
        btnSave.setAlpha(0.5f);

        setupListeners();

        btnSave.setOnClickListener(v -> registerUserOnServer());
    }

    private void registerUserOnServer() {
        String finalPassword = etNewPassword.getText().toString();

        JsonObject body = new JsonObject();
        body.addProperty("email", email);
        body.addProperty("password", finalPassword);
        body.addProperty("passwordConfirm", finalPassword);
        body.addProperty("firstName", firstName);
        body.addProperty("middleName", middleName);
        body.addProperty("lastName", lastName);
        body.addProperty("birthDate", birthDate);
        body.addProperty("gender", gender);

        btnSave.setEnabled(false);
        btnSave.setText("Регистрация...");

        apiService.registerUser(body).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    saveUserDataLocally(email, firstName);

                    Toast.makeText(PasswordCreateActivity.this, "Успешная регистрация!", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(PasswordCreateActivity.this, CreatePasswordCodeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    btnSave.setEnabled(true);
                    btnSave.setText("Сохранить");
                    Toast.makeText(PasswordCreateActivity.this, "Ошибка: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                btnSave.setEnabled(true);
                btnSave.setText("Сохранить");
                Toast.makeText(PasswordCreateActivity.this, "Ошибка сети", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveUserDataLocally(String email, String name) {
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("current_user_email", email);
        editor.putString("current_user_name", name);
        editor.apply();
    }

    private void setupListeners() {
        TextWatcher passwordWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkPasswords();
            }
            @Override
            public void afterTextChanged(Editable s) {}
        };
        etNewPassword.addTextChangedListener(passwordWatcher);
        etConfirmPassword.addTextChangedListener(passwordWatcher);
    }

    private void checkPasswords() {
        String pass = etNewPassword.getText().toString();
        String confirmPass = etConfirmPassword.getText().toString();
        boolean valid = !pass.isEmpty() && pass.equals(confirmPass) && pass.length() >= 8;

        btnSave.setEnabled(valid);
        btnSave.setAlpha(valid ? 1.0f : 0.5f);
    }
}
