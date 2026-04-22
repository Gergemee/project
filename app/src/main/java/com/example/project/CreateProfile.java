package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

public class CreateProfile extends AppCompatActivity {
    String[] ex = {"Мужской", "Женский"};

    private EditText etFirstName, etMiddleName, etLastName, etBirthDate, etGender, etEmail;
    private Button btnNext;

    private boolean isFormValid() {
        // Проверяем каждое поле на пустоту
        String name = etFirstName.getText().toString().trim();
        String middleName = etMiddleName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();
        String date = etBirthDate.getText().toString().trim();
        String gender = etGender.getText().toString().trim();
        String email = etEmail.getText().toString().trim();

        // Кнопка активна, если все строки не пустые и email корректен
        return !name.isEmpty() && !middleName.isEmpty() && !lastName.isEmpty()
                && !date.isEmpty() && !gender.isEmpty()
                && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    private final TextWatcher registrationWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // Каждый раз при вводе символа проверяем всю форму
            btnNext.setEnabled(isFormValid());

            // Визуально меняем прозрачность кнопки
            btnNext.setAlpha(isFormValid() ? 1.0f : 0.5f);
        }

        @Override
        public void afterTextChanged(Editable s) {}
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);

        Spinner spinner = findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, ex);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        etFirstName.addTextChangedListener(registrationWatcher);
        etMiddleName.addTextChangedListener(registrationWatcher);
        etLastName.addTextChangedListener(registrationWatcher);
        etBirthDate.addTextChangedListener(registrationWatcher);
        etGender.addTextChangedListener(registrationWatcher);
        etEmail.addTextChangedListener(registrationWatcher);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Создаем намерение перейти на экран пароля
                Intent intent = new Intent( CreateProfile.this, PasswordCreateActivity.class);

                // Передаем все строки по порядку
                intent.putExtra("first_name", etFirstName.getText().toString().trim());
                intent.putExtra("middle_name", etMiddleName.getText().toString().trim());
                intent.putExtra("last_name", etLastName.getText().toString().trim());
                intent.putExtra("birth_date", etBirthDate.getText().toString().trim());
                intent.putExtra("gender", etGender.getText().toString().trim());
                intent.putExtra("email", etEmail.getText().toString().trim());

                startActivity(intent);
            }
        });
    }
}