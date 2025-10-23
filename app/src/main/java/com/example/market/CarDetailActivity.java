package com.example.market;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class CarDetailActivity extends AppCompatActivity {
    private ImageView carImage;
    private TextView brandModelText, yearText, mileageText, engineText, priceText, descriptionText;
    private Button contactButton, favoriteButton;
    private Car currentCar;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_detail);

        db = FirebaseFirestore.getInstance();

        // Получаем объект Car из Intent
        currentCar = (Car) getIntent().getSerializableExtra("car");

        initViews();
        displayCarDetails();
    }

    private void initViews() {
        carImage = findViewById(R.id.carImage);
        brandModelText = findViewById(R.id.brandModelText);
        yearText = findViewById(R.id.yearText);
        mileageText = findViewById(R.id.mileageText);
        engineText = findViewById(R.id.engineText);
        priceText = findViewById(R.id.priceText);
        descriptionText = findViewById(R.id.descriptionText);
        contactButton = findViewById(R.id.contactButton);
        favoriteButton = findViewById(R.id.favoriteButton);

        contactButton.setOnClickListener(v -> contactSeller());
        favoriteButton.setOnClickListener(v -> toggleFavorite());
    }

    private void displayCarDetails() {
        if (currentCar != null) {
            brandModelText.setText(currentCar.getBrand() + " " + currentCar.getModel());
            yearText.setText("Год: " + currentCar.getYear());
            mileageText.setText("Пробег: " + currentCar.getMileage() + " км");
            engineText.setText("Объем: " + currentCar.getEngineVolume() + " л");
            priceText.setText(String.format("%.0f руб.", currentCar.getPrice()));
            descriptionText.setText(currentCar.getDescription());

            // Загружаем изображение если есть URL
            if (currentCar.getImageUrl() != null && !currentCar.getImageUrl().isEmpty()) {
                Glide.with(this)
                        .load(currentCar.getImageUrl())
                        .placeholder(R.drawable.ic_car_placeholder)
                        .into(carImage);
            } else {
                // Если нет изображения, показываем placeholder
                carImage.setImageResource(R.drawable.ic_car_placeholder);
            }

            // Обновляем текст кнопки избранного
            updateFavoriteButton();

            // Проверяем владельца автомобиля
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null && currentUser.getUid().equals(currentCar.getOwnerId())) {
                Button editButton = findViewById(R.id.editButton);
                editButton.setVisibility(View.VISIBLE);
                editButton.setOnClickListener(v -> {
                    Intent intent = new Intent(CarDetailActivity.this, EditCarActivity.class);
                    intent.putExtra("car_id", currentCar.getId());
                    startActivity(intent);
                });
            }
        }
    }

    private void updateFavoriteButton() {
        if (currentCar.isFavorite()) {
            favoriteButton.setText("Удалить из избранного");
            favoriteButton.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
        } else {
            favoriteButton.setText("Добавить в избранное");
            favoriteButton.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
        }
    }

    private void contactSeller() {
        Toast.makeText(this, "Контакты продавца будут здесь", Toast.LENGTH_SHORT).show();
    }

    private void toggleFavorite() {
        if (currentCar.isFavorite()) {
            Favorites.removeFavoriteCar(currentCar);
            currentCar.setFavorite(false);
            Toast.makeText(this, "Удалено из избранного", Toast.LENGTH_SHORT).show();
        } else {
            Favorites.addFavoriteCar(currentCar);
            currentCar.setFavorite(true);
            Toast.makeText(this, "Добавлено в избранное", Toast.LENGTH_SHORT).show();
        }
        updateFavoriteButton();
    }
}