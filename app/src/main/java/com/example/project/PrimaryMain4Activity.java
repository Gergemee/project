package com.example.project;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView; // Добавлено
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PrimaryMain4Activity extends AppCompatActivity {

    private APIService apiService;
    private String userToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_primary_main4);

        apiService = APIClient.getApiService();
        userToken = getSharedPreferences("UserPrefs", MODE_PRIVATE).getString("auth_token", "");

        showDetailsBottomSheet();
    }

    private void showDetailsBottomSheet() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.activity_primary_main4, null);
        bottomSheetDialog.setContentView(view);

        EditText etDescription = view.findViewById(R.id.tv_product_description);
        EditText etConsumption = view.findViewById(R.id.tvSheetConsumption);
        Button btnAction = view.findViewById(R.id.btn_add_to_cart);

        btnAction.setOnClickListener(v -> {
            String desc = etDescription.getText().toString();
            String cons = etConsumption.getText().toString();
            saveDetailsToApi(desc, cons, bottomSheetDialog);
        });

        bottomSheetDialog.setCancelable(true);
        bottomSheetDialog.show();

        bottomSheetDialog.setOnDismissListener(dialog -> finish());
    }

    private void saveDetailsToApi(String description, String consumption, BottomSheetDialog dialog) {
        JsonObject body = new JsonObject();
        body.addProperty("description", description);
        body.addProperty("consumption", consumption);

        apiService.createBasket("Bearer " + userToken, body).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    dialog.dismiss();
                    Toast.makeText(PrimaryMain4Activity.this, "Успешно!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(PrimaryMain4Activity.this, "Ошибка сервера", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(PrimaryMain4Activity.this, "Ошибка сети", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
