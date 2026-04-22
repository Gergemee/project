package com.example.project;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class PasswordCreateActivity extends AppCompatActivity {

    // Переменные для хранения данных с предыдущего экрана
    private String firstName, middleName, lastName, birthDate, gender, email;

    private EditText etNewPassword;
    private EditText etConfirmPassword;
    private Button btnSave;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_create);

        // Извлекаем все данные из Intent
        firstName = getIntent().getStringExtra("first_name");
        middleName = getIntent().getStringExtra("middle_name");
        lastName = getIntent().getStringExtra("last_name");
        birthDate = getIntent().getStringExtra("birth_date");
        gender = getIntent().getStringExtra("gender");
        email = getIntent().getStringExtra("email");

        // Теперь эти переменные доступны в этом классе.
        // Вы можете, например, вывести приветствие: "Иван, придумайте пароль"
        etNewPassword = findViewById(R.id.new_password);
        etConfirmPassword = findViewById(R.id.confirm_password);
        btnSave = findViewById(R.id.btnsave);

        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString("registered_email", email);       // email мы получили из Intent ранее
        editor.putString("registered_password", String.valueOf(etNewPassword)); // текущий введенный пароль
        editor.apply();

        Toast.makeText(this, "Регистрация завершена!", Toast.LENGTH_SHORT).show();

        TextWatcher passwordWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkPasswords(); // Вызываем нашу проверку
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

        // Проверяем: не пусты ли поля и равны ли они друг другу
        boolean isMatching = !pass.isEmpty() && pass.equals(confirmPass);

        // Дополнительно: проверка на минимальную длину (например, 8 символов)
        boolean isLongEnough = pass.length() >= 8;

        if (isMatching && isLongEnough) {
            btnSave.setEnabled(true);
            btnSave.setAlpha(1.0f);
        } else {
            btnSave.setEnabled(false);
            btnSave.setAlpha(0.5f);
        }
    }
}