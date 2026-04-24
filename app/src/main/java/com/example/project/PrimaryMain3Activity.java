package com.example.project;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PrimaryMain3Activity extends AppCompatActivity {

    private LinearLayout cartItemsContainer;
    private TextView tvEmptyMessage, tvTotalSum;
    private Button btnCheckout;
    private int totalSum = 0;
    private APIService apiService;
    private String userToken;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_primary_main3);

        apiService = APIClient.getApiService();
        String token = getSharedPreferences("UserPrefs", MODE_PRIVATE).getString("auth_token", "");
        userToken = "Bearer " + token;

        cartItemsContainer = findViewById(R.id.cartItemsContainer);
        tvEmptyMessage = findViewById(R.id.tv_empty_cart_message);
        tvTotalSum = findViewById(R.id.tv_total_sum);
        btnCheckout = findViewById(R.id.btn_checkout);

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        View trash = findViewById(R.id.ivTrash);
        if (trash != null) trash.setOnClickListener(v -> clearAllCart());

        btnCheckout.setOnClickListener(v -> createOrderOnServer());

        loadBasketFromServer();
    }

    private void loadBasketFromServer() {
        apiService.getBasket(userToken).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    cartItemsContainer.removeAllViews();
                    JsonObject body = response.body();

                    JsonArray records = body.has("items") ? body.getAsJsonArray("items") : new JsonArray();

                    for (JsonElement recordElement : records) {
                        JsonObject record = recordElement.getAsJsonObject();
                        String basketId = record.has("id") ? record.get("id").getAsString() : "";

                        if (record.has("items") && record.get("items").isJsonArray()) {
                            JsonArray productItems = record.getAsJsonArray("items");

                            for (JsonElement productElement : productItems) {
                                JsonObject product = productElement.getAsJsonObject();

                                String name = "Товар";
                                if (product.has("title") && !product.get("title").isJsonNull()) {
                                    name = product.get("title").getAsString();
                                } else if (product.has("product_name") && !product.get("product_name").isJsonNull()) {
                                    name = product.get("product_name").getAsString();
                                }

                                int price = 0;
                                if (product.has("price") && !product.get("price").isJsonNull()) {
                                    price = product.get("price").getAsInt();
                                }

                                int quantity = 1;
                                if (product.has("quantity") && !product.get("quantity").isJsonNull()) {
                                    quantity = product.get("quantity").getAsInt();
                                }

                                addProductToLayout(basketId, name, price, quantity);
                            }
                        }
                    }
                    updateTotal();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(PrimaryMain3Activity.this, "Ошибка загрузки", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addProductToLayout(String basketId, String name, int price, int initialCount) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View itemView = inflater.inflate(R.layout.activity_cart_product, cartItemsContainer, false);

        itemView.setTag(price);
        itemView.setContentDescription(basketId);

        TextView tvName = itemView.findViewById(R.id.CartItemTitle);
        TextView tvPrice = itemView.findViewById(R.id.CartItemPrice);
        TextView tvCount = itemView.findViewById(R.id.CartItemQty);
        View btnPlus = itemView.findViewById(R.id.btnPlus);
        View btnMinus = itemView.findViewById(R.id.btnMinus);
        View btnRemove = itemView.findViewById(R.id.CartItemRemove);

        if (tvName != null) tvName.setText(name);
        if (tvPrice != null) tvPrice.setText(price + " ₽");
        if (tvCount != null) tvCount.setText(initialCount + " штук");

        final int[] currentCount = {initialCount};

        if (btnPlus != null) {
            btnPlus.setOnClickListener(v -> {
                currentCount[0]++;
                updateBasketQuantity(basketId, currentCount[0], tvCount);
            });
        }

        if (btnMinus != null) {
            btnMinus.setOnClickListener(v -> {
                if (currentCount[0] > 1) {
                    currentCount[0]--;
                    updateBasketQuantity(basketId, currentCount[0], tvCount);
                }
            });
        }

        if (btnRemove != null) {
            btnRemove.setOnClickListener(v -> {
                apiService.deleteBasket(userToken, basketId).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            cartItemsContainer.removeView(itemView);
                            updateTotal();
                        }
                    }
                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(PrimaryMain3Activity.this, "Ошибка удаления", Toast.LENGTH_SHORT).show();
                    }
                });
            });
        }

        cartItemsContainer.addView(itemView);
    }

    private void updateBasketQuantity(String basketId, int newQuantity, TextView tvCount) {
        JsonObject body = new JsonObject();
        body.addProperty("quantity", newQuantity);

        apiService.updateBasket(userToken, basketId, body).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    if (tvCount != null) tvCount.setText(newQuantity + " штук");
                    updateTotal();
                }
            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {}
        });
    }

    private void clearAllCart() {
        int count = cartItemsContainer.getChildCount();
        for (int i = 0; i < count; i++) {
            View itemView = cartItemsContainer.getChildAt(i);
            String basketId = itemView.getContentDescription().toString();
            apiService.deleteBasket(userToken, basketId).enqueue(new Callback<Void>() {
                @Override public void onResponse(Call<Void> call, Response<Void> response) {}
                @Override public void onFailure(Call<Void> call, Throwable t) {}
            });
        }
        cartItemsContainer.removeAllViews();
        updateTotal();
    }

    private void createOrderOnServer() {
        JsonObject orderData = new JsonObject();
        orderData.addProperty("total_price", totalSum);
        orderData.addProperty("status", "new");
        String userId = getSharedPreferences("UserPrefs", MODE_PRIVATE).getString("user_id", "");
        orderData.addProperty("user_id", userId);

        apiService.createOrder(userToken, orderData).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(PrimaryMain3Activity.this, "Заказ оформлен!", Toast.LENGTH_LONG).show();
                    cartItemsContainer.removeAllViews();
                    updateTotal();
                }
            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(PrimaryMain3Activity.this, "Ошибка сети", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateTotal() {
        int currentTotal = 0;
        int childCount = cartItemsContainer.getChildCount();

        for (int i = 0; i < childCount; i++) {
            View item = cartItemsContainer.getChildAt(i);
            if (item.getTag() != null) {
                int price = (int) item.getTag();
                TextView tvCount = item.findViewById(R.id.CartItemQty);
                if (tvCount != null) {
                    String cleanString = tvCount.getText().toString().replaceAll("[^0-9]", "");
                    int count = cleanString.isEmpty() ? 0 : Integer.parseInt(cleanString);
                    currentTotal += (price * count);
                }
            }
        }

        this.totalSum = currentTotal;
        tvTotalSum.setText(totalSum + " ₽");

        if (childCount == 0) {
            tvEmptyMessage.setVisibility(View.VISIBLE);
            btnCheckout.setEnabled(false);
            btnCheckout.setAlpha(0.5f);
        } else {
            tvEmptyMessage.setVisibility(View.GONE);
            btnCheckout.setEnabled(true);
            btnCheckout.setAlpha(1.0f);
        }
    }
}
