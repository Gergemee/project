package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class CreatePasswordCodeActivity extends AppCompatActivity {

    private StringBuilder pinCode = new StringBuilder(); // Хранит введенные цифры
    private View[] dots = new View[4]; // Массив для кружочков-индикаторов
    private final int PIN_LENGTH = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_password_code);

        // Привязываем кружочки
        dots[0] = findViewById(R.id.dot1);
        dots[1] = findViewById(R.id.dot2);
        dots[2] = findViewById(R.id.dot3);
        dots[3] = findViewById(R.id.dot4);

        // Настраиваем кнопки с цифрами (0-9)
        setNumberClickListeners();

        // Настраиваем кнопку удаления (BACK)
        findViewById(R.id.butdelete).setOnClickListener(v -> deleteLastDigit());
    }
    private void setNumberClickListeners() {
        View.OnClickListener listener = v -> {
            if (pinCode.length() < PIN_LENGTH) {
                Button b = (Button) v;
                pinCode.append(b.getText().toString()); // Добавляем цифру в строку
                updateDots(); // Обновляем кружочки

                // Если ввели 4 цифры — проверяем результат
                if (pinCode.length() == PIN_LENGTH) {
                    verifyPin();
                }
            }
        };

        // Привязываем этот лисенер ко всем кнопкам от 0 до 9
        int[] buttonIds = {R.id.button7, R.id.button8, R.id.button9, R.id.button10, R.id.button11,
                R.id.button12, R.id.button13, R.id.button14, R.id.button15, R.id.button16};
        for (int id : buttonIds) {
            findViewById(id).setOnClickListener(listener);
        }
    }
    private void updateDots() {
        for (int i = 0; i < dots.length; i++) {
            if (i < pinCode.length()) {
                // Кружочек закрашен (введена цифра)
                dots[i].setBackgroundResource(R.drawable.zakrashkrug);
                dots[i].setAlpha(1.0f);
            } else {
                // Кружочек пустой
                dots[i].setBackgroundResource(R.drawable.nazakrashkrug);
                dots[i].setAlpha(0.3f);
            }
        }
    }

    private void deleteLastDigit() {
        if (pinCode.length() > 0) {
            pinCode.deleteCharAt(pinCode.length() - 1);
            updateDots();
        }
    }
    private void verifyPin() {
        String finalPin = pinCode.toString();

        // В учебном проекте просто сохраняем в переменную или SharedPreferences
        Toast.makeText(this, "Пин-код установлен: " + finalPin, Toast.LENGTH_SHORT).show();

        // Переход на главный экран (Dashboard)
        Intent intent = new Intent(this, LogSignIn.class);
        startActivity(intent);
        finish();
    }
}