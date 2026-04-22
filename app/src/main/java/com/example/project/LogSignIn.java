package com.example.project;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LogSignIn extends AppCompatActivity {

    private String userEmail;
    private String userToken;

    private boolean isAuthorized = false;

    private String generateFakeToken() {
        // Генерируем случайную строку, похожую на реальный Access Token
        return java.util.UUID.randomUUID().toString().replace("-", "");
    }

    ActivityResultLauncher<Intent> loginLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    // 1. Получаем почту из результата
                    userEmail = result.getData().getStringExtra("result_email");

                    // 2. Генерируем токен (имитируем ответ от VK/Яндекса)
                    userToken = generateFakeToken();

                    // 3. Меняем статус
                    isAuthorized = true;

                    // Теперь можно обновить интерфейс или перейти на главный экран
                    Toast.makeText(this, "Вход выполнен! Токен: " + userToken.substring(0, 8) + "...", Toast.LENGTH_LONG).show();

                    goToDashboard(); // Переход на внутреннюю страницу приложения
                }
            }
    );

    private void goToDashboard() {
        Intent intent = new Intent(this, Primary4.class);
        intent.putExtra("EXTRA_TOKEN", userToken);
        intent.putExtra("EXTRA_EMAIL", userEmail);
        startActivity(intent);
        finish();
    }
    private EditText etEmail;
    private EditText etPassword;
    private Button btnLogin;
    private final TextWatcher loginTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // Получаем текст из обоих полей
            String emailInput = etEmail.getText().toString().trim();
            String passwordInput = etPassword.getText().toString().trim();

            // Условие: кнопка активна, если оба поля не пусты
            // Можно добавить доп. условие, например: passwordInput.length() >= 6
            boolean isReady = !emailInput.isEmpty() && !passwordInput.isEmpty();

            btnLogin.setEnabled(isReady);
        }

        @Override
        public void afterTextChanged(Editable s) {}
    };
    @SuppressLint("MissingInflatedId")


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_sign_in);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.but_login);

        // По умолчанию выключаем кнопку при запуске
        btnLogin.setEnabled(false);

        // Добавляем слушатель к обоим полям
        etEmail.addTextChangedListener(loginTextWatcher);
        etPassword.addTextChangedListener(loginTextWatcher);

        Button btnVK = findViewById(R.id.btnvk);
        Button btnYandex = findViewById(R.id.btnyan);

        btnVK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://vk.com?..."; // Здесь должна быть ваша ссылка для OAuth
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });

// Переход на Яндекс
        btnYandex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://yandex.ru?..."; // Ссылка для авторизации Яндекс
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });
        btnLogin.setOnClickListener(v -> {
            String inputEmail = etEmail.getText().toString().trim();
            String inputPassword = etPassword.getText().toString().trim();

            // Достаем сохраненные данные
            SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            String savedEmail = prefs.getString("registered_email", "");
            String savedPassword = prefs.getString("registered_password", "");

            // Сравниваем
            if (inputEmail.equals(savedEmail) && inputPassword.equals(savedPassword)) {
                // Успех! Переходим на экран ПИН-кода
                Intent intent = new Intent(LogSignIn.this, PasswordCode.class);
                startActivity(intent);
            } else {
                // Ошибка
                Toast.makeText(this, "Неверный Email или Пароль", Toast.LENGTH_LONG).show();

                // Можно подсветить поле красным
                etPassword.setError("Проверьте правильность пароля");
            }
        });
        // Получаем доступ к хранилищу
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

// Сохраняем имя и почту
        editor.putString("current_user_name", "Эдуард"); // Здесь может быть переменная с именем
        editor.putString("current_user_email", String.valueOf(etEmail)); // Почта, которую ввел пользователь
        editor.apply(); // Применяем изменения
    }
}