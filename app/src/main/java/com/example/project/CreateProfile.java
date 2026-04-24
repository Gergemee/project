package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CreateProfile extends AppCompatActivity {
    String[] ex = {"Мужской", "Женский"};

    private EditText etFirstName, etMiddleName, etLastName, etBirthDate, etEmail;
    private Spinner etGender;
    private Button btnNext;

    private APIService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);

        apiService = APIClient.getApiService();

        etFirstName = findViewById(R.id.et_first_name);
        etMiddleName = findViewById(R.id.et_middle_name);
        etLastName = findViewById(R.id.et_last_name);
        etBirthDate = findViewById(R.id.et_birth_date);
        etGender = findViewById(R.id.et_gender);
        etEmail = findViewById(R.id.editTextText4);
        btnNext = findViewById(R.id.btn_next);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, ex);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        etGender.setAdapter(adapter);

        setupTextWatchers();

        btnNext.setOnClickListener(v -> {
            Intent intent = new Intent(CreateProfile.this, PasswordCreateActivity.class);
            intent.putExtra("first_name", etFirstName.getText().toString().trim());
            intent.putExtra("middle_name", etMiddleName.getText().toString().trim());
            intent.putExtra("last_name", etLastName.getText().toString().trim());
            intent.putExtra("birth_date", etBirthDate.getText().toString().trim());
            intent.putExtra("gender", etGender.getSelectedItem().toString());
            intent.putExtra("email", etEmail.getText().toString().trim());
            startActivity(intent);
        });

        updateButtonState();
    }

    private void registerInApi(String password) {
        JsonObject body = new JsonObject();
        body.addProperty("email", etEmail.getText().toString().trim());
        body.addProperty("password", password);
        body.addProperty("passwordConfirm", password);
        body.addProperty("firstName", etFirstName.getText().toString().trim());
        body.addProperty("lastName", etLastName.getText().toString().trim());
        body.addProperty("gender", etGender.getSelectedItem().toString());

        apiService.registerUser(body).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(CreateProfile.this, "Успешная регистрация", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(CreateProfile.this, "Ошибка: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(CreateProfile.this, "Ошибка сети", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupTextWatchers() {
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateButtonState();
            }
            @Override
            public void afterTextChanged(Editable s) {}
        };

        etFirstName.addTextChangedListener(watcher);
        etMiddleName.addTextChangedListener(watcher);
        etLastName.addTextChangedListener(watcher);
        etBirthDate.addTextChangedListener(watcher);
        etEmail.addTextChangedListener(watcher);

        etGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateButtonState();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void updateButtonState() {
        boolean valid = isFormValid();
        btnNext.setEnabled(valid);
        btnNext.setAlpha(valid ? 1.0f : 0.5f);
    }

    private boolean isFormValid() {
        String name = etFirstName.getText().toString().trim();
        String middleName = etMiddleName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();
        String date = etBirthDate.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String gender = etGender.getSelectedItem() != null ? etGender.getSelectedItem().toString() : "";

        return !name.isEmpty() && !middleName.isEmpty() && !lastName.isEmpty()
                && !date.isEmpty() && !gender.isEmpty()
                && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
