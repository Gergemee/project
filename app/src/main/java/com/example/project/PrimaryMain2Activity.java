package com.example.project;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PrimaryMain2Activity extends AppCompatActivity {

    private View layoutCart;
    private TextView tvCartPrice;
    private int totalPrice = 0;
    private LinearLayout layoutCatalog;
    private APIService apiService;
    private String userToken;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_primary_main2);

        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        userToken = "Bearer " + prefs.getString("auth_token", "");
        userId = prefs.getString("user_id", "");

        apiService = APIClient.getApiService();

        layoutCatalog = findViewById(R.id.containercards);
        layoutCart = findViewById(R.id.cartlayout);
        tvCartPrice = findViewById(R.id.tv_cart_total_price);

        if (layoutCart != null) {
            layoutCart.setOnClickListener(v -> {
                Intent intent = new Intent(this, PrimaryMain3Activity.class);
                startActivity(intent);
            });
            layoutCart.setVisibility(View.GONE);
        }

        loadProductsFromServer();
        setupBottomNavigation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCartTotal();
    }

    private void loadCartTotal() {
        apiService.getBasket(userToken).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    JsonObject root = response.body();
                    JsonArray records = root.has("items") ? root.getAsJsonArray("items") : new JsonArray();

                    totalPrice = 0;

                    for (JsonElement recordElement : records) {
                        JsonObject record = recordElement.getAsJsonObject();

                        if (record.has("items") && record.get("items").isJsonArray()) {
                            JsonArray productItems = record.getAsJsonArray("items");

                            for (JsonElement productElement : productItems) {
                                JsonObject product = productElement.getAsJsonObject();

                                int price = 0;
                                if (product.has("price") && !product.get("price").isJsonNull()) {
                                    price = product.get("price").getAsInt();
                                }

                                int qty = 0;
                                if (product.has("quantity") && !product.get("quantity").isJsonNull()) {
                                    qty = product.get("quantity").getAsInt();
                                }

                                totalPrice += (price * qty);
                            }
                        }
                    }
                    updateCartUI();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e("API_ERROR", "Ошибка загрузки суммы: " + t.getMessage());
            }
        });
    }

    private void updateCartUI() {
        if (layoutCart == null) return;
        if (totalPrice > 0) {
            if (tvCartPrice != null) tvCartPrice.setText(totalPrice + " ₽");
            layoutCart.setVisibility(View.VISIBLE);
        } else {
            layoutCart.setVisibility(View.GONE);
        }
    }

    private void sendProductToBasket(String title, int price) {
        JsonArray itemsArray = new JsonArray();
        JsonObject productItem = new JsonObject();
        productItem.addProperty("title", title);
        productItem.addProperty("price", price);
        productItem.addProperty("quantity", 1);
        productItem.addProperty("product_id", "some_id");
        itemsArray.add(productItem);

        JsonObject body = new JsonObject();
        body.addProperty("user_id", userId);
        body.add("items", itemsArray);
        body.addProperty("count", 1);

        apiService.createBasket(userToken, body).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(PrimaryMain2Activity.this, "Добавлено!", Toast.LENGTH_SHORT).show();
                    loadCartTotal();
                }
            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e("API_ERROR", "Ошибка добавления: " + t.getMessage());
            }
        });
    }

    private void loadProductsFromServer() {
        apiService.getProducts(userToken, 50, "-created").enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    JsonArray items = response.body().getAsJsonArray("items");
                    if (items != null) {
                        layoutCatalog.removeAllViews();
                        for (JsonElement element : items) {
                            JsonObject obj = element.getAsJsonObject();
                            String title = obj.has("title") ? obj.get("title").getAsString() : "Товар";
                            int price = obj.has("price") && !obj.get("price").isJsonNull() ? obj.get("price").getAsInt() : 0;
                            addProductToCatalog(title, price, obj.has("description") ? obj.get("description").getAsString() : "");
                        }
                    }
                }
            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {}
        });
    }

    private void addProductToCatalog(String name, int price, String description) {
        View itemView = LayoutInflater.from(this).inflate(R.layout.activity_card_product, layoutCatalog, false);
        ((TextView) itemView.findViewById(R.id.tvProductTitle)).setText(name);
        ((TextView) itemView.findViewById(R.id.tvProductPrice)).setText(price + " ₽");

        itemView.setOnClickListener(v -> showProductDetails(name, description, price));
        itemView.findViewById(R.id.btnProductAction).setOnClickListener(v -> sendProductToBasket(name, price));

        layoutCatalog.addView(itemView);
    }

    private void showProductDetails(String title, String description, int price) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.activity_primary_main4, null);
        bottomSheetDialog.setContentView(view);
        ((TextView) view.findViewById(R.id.tv_product_title)).setText(title);
        ((TextView) view.findViewById(R.id.tv_product_description)).setText(description);
        Button btn = view.findViewById(R.id.btn_add_to_cart);
        btn.setText("Добавить за " + price + " ₽");
        btn.setOnClickListener(v -> {
            sendProductToBasket(title, price);
            bottomSheetDialog.dismiss();
        });
        bottomSheetDialog.show();
    }

    private void setupBottomNavigation() {
        findViewById(R.id.nav_home).setOnClickListener(v -> startActivity(new Intent(this, Primary.class)));
        findViewById(R.id.nav_projects).setOnClickListener(v -> startActivity(new Intent(this, Primary3.class)));
        findViewById(R.id.nav_profile).setOnClickListener(v -> startActivity(new Intent(this, Primary4.class)));
    }
}
