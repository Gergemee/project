package com.example.project;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CreatePasswordCodeActivity extends AppCompatActivity {

    private StringBuilder pinCode = new StringBuilder();
    private View[] dots = new View[4];
    private final int PIN_LENGTH = 4;

    private APIService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_password_code);

        apiService = APIClient.getApiService();

        dots[0] = findViewById(R.id.dot1);
        dots[1] = findViewById(R.id.dot2);
        dots[2] = findViewById(R.id.dot3);
        dots[3] = findViewById(R.id.dot4);

        setNumberClickListeners();

        findViewById(R.id.butdelete).setOnClickListener(v -> deleteLastDigit());
    }

    private void setNumberClickListeners() {
        View.OnClickListener listener = v -> {
            if (pinCode.length() < PIN_LENGTH) {
                Button b = (Button) v;
                pinCode.append(b.getText().toString());
                updateDots();

                if (pinCode.length() == PIN_LENGTH) {
                    savePinAndFinish();
                }
            }
        };

        int[] buttonIds = {R.id.button7, R.id.button8, R.id.button9, R.id.button10, R.id.button11,
                R.id.button12, R.id.button13, R.id.button14, R.id.button15, R.id.button16};
        for (int id : buttonIds) {
            findViewById(id).setOnClickListener(listener);
        }
    }

    private void updateDots() {
        for (int i = 0; i < dots.length; i++) {
            if (i < pinCode.length()) {
                dots[i].setBackgroundResource(R.drawable.zakrashkrug);
                dots[i].setAlpha(1.0f);
            } else {
                dots[i].setBackgroundResource(R.drawable.nazakrashkrug);
                dots[i].setAlpha(1.0f);
            }
        }
    }

    private void deleteLastDigit() {
        if (pinCode.length() > 0) {
            pinCode.deleteCharAt(pinCode.length() - 1);
            updateDots();
        }
    }

    private void savePinAndFinish() {
        String finalPin = pinCode.toString();

        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        prefs.edit().putString("user_pin", finalPin).apply();

        Toast.makeText(this, "ПИН-код успешно установлен", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, LogSignIn.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
