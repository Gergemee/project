package com.example.project;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class PrimaryMain3Activity extends AppCompatActivity {

    private LinearLayout cartContainer; // Куда будем добавлять шаблоны
    private TextView tvEmptyMessage, tvTotalSum;
    private Button btnCheckout;
    private int totalSum = 0;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_primary_main3);

        // 1. Инициализация базовых элементов экрана
        cartContainer = findViewById(R.id.cart_container);
        tvEmptyMessage = findViewById(R.id.tv_empty_cart_message);
        tvTotalSum = findViewById(R.id.tv_total_sum);
        btnCheckout = findViewById(R.id.btn_checkout);

        // Кнопка "Назад"
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        // 2. Получаем данные из Каталога и добавляем товары по шаблону
        Intent intent = getIntent();
        if (intent.getBooleanExtra("ADD_ITEM_1", false)) {
            addProductToLayout("Рубашка воскресенье для машинного вязания", 300);
        }
        if (intent.getBooleanExtra("ADD_ITEM_2", false)) {
            addProductToLayout("Шорты вторник для машинного вязания", 300);
        }

        // 3. Проверяем состояние корзины (пуста или нет)
        updateTotal();
    }

    private void addProductToLayout(String name, int price) {
        // Создаем "надуватель" макетов
        LayoutInflater inflater = LayoutInflater.from(this);

        // Создаем View на основе вашего XML-шаблона (item_cart)
        View itemView = inflater.inflate(R.layout.activity_cart_product, cartContainer, false);

        // Находим элементы ВНУТРИ шаблона
        TextView tvName = itemView.findViewById(R.id.CartItemTitle);
        TextView tvPrice = itemView.findViewById(R.id.CartItemPrice);
        TextView tvCount = itemView.findViewById(R.id.CartItemQty);
        View btnPlus = itemView.findViewById(R.id.btnPlus);
        View btnMinus = itemView.findViewById(R.id.btnMinus);
        View btnRemove = itemView.findViewById(R.id.CartItemRemove);

        // Устанавливаем начальные данные
        tvName.setText(name);
        tvPrice.setText(price + " ₽");

        // Локальное состояние для этой конкретной карточки
        final int[] count = {1};
        itemView.setTag(price); // Сохраняем цену товара в тег для расчетов

        // Логика кнопок внутри шаблона
        btnPlus.setOnClickListener(v -> {
            count[0]++;
            tvCount.setText(count[0] + " штук");
            updateTotal();
        });

        btnMinus.setOnClickListener(v -> {
            if (count[0] > 1) {
                count[0]--;
                tvCount.setText(count[0] + " штук");
                updateTotal();
            }
        });

        // Удаление карточки
        btnRemove.setOnClickListener(v -> {
            cartContainer.removeView(itemView); // Удаляем этот шаблон из контейнера
            updateTotal();
        });

        // Добавляем готовую карточку в основной контейнер
        cartContainer.addView(itemView);
    }

    /**
     * Метод для пересчета общей суммы и обновления интерфейса
     */
    private void updateTotal() {
        int currentTotal = 0;

        // Проходим циклом по всем добавленным шаблонам в контейнере
        for (int i = 0; i < cartContainer.getChildCount(); i++) {
            View item = cartContainer.getChildAt(i);

            // Достаем цену из тега и количество из текстового поля
            int price = (int) item.getTag();
            TextView tvCount = item.findViewById(R.id.CartItemQty);
            String countStr = tvCount.getText().toString().replaceAll("[^0-9]", "");
            int count = Integer.parseInt(countStr);

            currentTotal += (price * count);
        }

        this.totalSum = currentTotal;
        tvTotalSum.setText(totalSum + " ₽");

        // Если товаров нет — показываем сообщение о пустой корзине
        if (cartContainer.getChildCount() == 0) {
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