package com.example.project;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomsheet.BottomSheetDialog;

public class PrimaryMain2Activity extends AppCompatActivity {

    private View layoutCart; // Сама панель "В корзину"
    private TextView tvCartPrice; // Текст с ценой (500 Р)
    private int totalPrice = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_primary_main2); // ваш XML

        // 1. Инициализируем панель корзины
        layoutCart = findViewById(R.id.layout_cart_panel);

        tvCartPrice = findViewById(R.id.tv_cart_total_price);

        // По умолчанию скрываем корзину
        layoutCart.setVisibility(View.GONE);

        // 2. Настраиваем кнопки-фильтры (Все, Женщинам, Мужчинам)
        setupCategoryButtons();

        // 3. Запускаем навигацию
        setupBottomNavigation();
        layoutCart.setOnClickListener(v -> {
            Intent intent = new Intent(this, PrimaryMain3Activity.class);
            intent.putExtra("TOTAL_SUM", totalPrice);
            intent.putExtra("ADD_ITEM_1", true);
            intent.putExtra("ADD_ITEM_2", false);
            startActivity(intent);
        });
        addProductToCatalog("Рубашка Воскресенье", "Мужская одежда", 300, "Описание рубашки...");
        addProductToCatalog("Шорты Вторник", "Мужская одежда", 500, "Описание шорт...");
    }
    private void setupCategoryButtons() {
        View btnAll = findViewById(R.id.btn_filter_all);
        View btnWomen = findViewById(R.id.btn_filter_women);
        View btnMen = findViewById(R.id.btn_filter_men);

        View.OnClickListener filterListener = v -> {
            // Сбрасываем все в полупрозрачный вид
            btnAll.setAlpha(0.5f);
            btnWomen.setAlpha(0.5f);
            btnMen.setAlpha(0.5f);

            // Подсвечиваем нажатую кнопку
            v.setAlpha(1.0f);

            // Здесь в будущем будет логика фильтрации списка
            Toast.makeText(this, "Фильтр активирован", Toast.LENGTH_SHORT).show();
        };

        btnAll.setOnClickListener(filterListener);
        btnWomen.setOnClickListener(filterListener);
        btnMen.setOnClickListener(filterListener);
    }
    public void onProductClick(int price) {
        totalPrice += price;

        // Обновляем текст на кнопке
        tvCartPrice.setText(totalPrice + " ₽");

        // Если сумма > 0, показываем панель
        if (totalPrice > 0) {
            layoutCart.setVisibility(View.VISIBLE);
            // Можно добавить анимацию появления
            layoutCart.setAlpha(0f);
            layoutCart.animate().alpha(1f).setDuration(300).start();
        }
    }
    private void setupBottomNavigation() {
        View btnHome = findViewById(R.id.nav_home);
        View btnCatalog = findViewById(R.id.nav_catalog);
        View btnProjects = findViewById(R.id.nav_projects);
        View btnProfile = findViewById(R.id.nav_profile);

        // Метод для запуска Activity с защитой от дублирования
        View.OnClickListener navListener = v -> {
            Intent intent;
            int id = v.getId();

            if (id == R.id.nav_catalog) {
                // Если мы уже на главной, ничего не делаем
                return;
            } else if (id == R.id.nav_profile) {
                intent = new Intent(this, PrimaryMain4Activity.class);
            } else if (id == R.id.nav_projects) {
                intent = new Intent(this, Primary3.class);
            } else if (id == R.id.nav_home) {
                intent = new Intent(this, Primary.class);
            } else {
                return;
            }

            // Этот флаг не создает новую Activity, если она уже открыта
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);

            // Убираем анимацию, чтобы переход казался мгновенным, как в настоящем меню
            overridePendingTransition(0, 0);
        };

        // Присваиваем один лисенер всем кнопкам
        btnHome.setOnClickListener(navListener);
        btnCatalog.setOnClickListener(navListener);
        btnProjects.setOnClickListener(navListener);
        btnProfile.setOnClickListener(navListener);
    }
    @SuppressLint("MissingInflatedId")
    private void showProductDetails(String title, String description, int price) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View bottomSheetView = LayoutInflater.from(this).inflate(R.layout.activity_primary_main4, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        // НАХОДИМ ЭЛЕМЕНТЫ
        TextView tvTitle = bottomSheetView.findViewById(R.id.tv_product_title);
        TextView tvDescription = bottomSheetView.findViewById(R.id.tv_product_description);
        Button btnAdd = bottomSheetView.findViewById(R.id.btn_add_to_cart);

        // УСТАНАВЛИВАЕМ ДАННЫЕ
        tvTitle.setText(title);
        tvDescription.setText(description);
        btnAdd.setText("Добавить за " + price + " ₽");

        // ЛОГИКА НАЖАТИЯ
        btnAdd.setOnClickListener(v -> {
            onProductAddedToCart(price); // Вызываем наш метод
            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.show();
    }
    private void onProductAddedToCart(int price) {
        // 1. Прибавляем цену к общей сумме (переменная totalPrice должна быть в начале класса)
        totalPrice += price;

        // 2. Обновляем текст на синей плашке
        tvCartPrice.setText(totalPrice + " ₽");

        // 3. Показываем плашку, если она была скрыта
        layoutCart.setVisibility(View.VISIBLE);
    }
    private void addProductToCatalog(String name, String category, int price, String description) {
        LinearLayout catalogContainer = findViewById(R.id.containercards); // Контейнер в XML каталога
        LayoutInflater inflater = LayoutInflater.from(this);

        // 1. Создаем View карточки из шаблона
        View itemView = inflater.inflate(R.layout.activity_card_product, catalogContainer, false);

        // 2. Находим элементы и заполняем данными
        TextView tvName = itemView.findViewById(R.id.tvProductTitle);
        TextView tvCategory = itemView.findViewById(R.id.tvProductCategory);
        TextView tvPrice = itemView.findViewById(R.id.tvProductPrice);
        Button btnAdd = itemView.findViewById(R.id.btnProductAction);

        tvName.setText(name);
        tvCategory.setText(category);
        tvPrice.setText(price + " ₽");

        // 3. ДЕЙСТВИЕ №1: Нажатие на саму карточку (открываем описание)
        itemView.setOnClickListener(v -> {
            // Вызываем метод BottomSheet, который мы создали ранее
            showProductDetails(name, description, price);
        });

        // 4. ДЕЙСТВИЕ №2: Нажатие на кнопку "Добавить"
        btnAdd.setOnClickListener(v -> {
            // Вызываем метод обновления синей плашки внизу экрана
            onProductAddedToCart(price);

            // Опционально: можно передать флаг для CartActivity, что товар выбран
            getIntent().putExtra("ADD_ITEM_1", true);

            Toast.makeText(this, "Добавлено: " + name, Toast.LENGTH_SHORT).show();
        });

        // 5. Добавляем карточку в список на экране
        catalogContainer.addView(itemView);
    }
}