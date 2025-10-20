package com.example.market;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class CarDetailActivity extends AppCompatActivity {
    private ImageView carImage;
    private TextView brandModelText, yearText, mileageText, engineText, priceText, descriptionText;
    private Button contactButton, favoriteButton;
    private String carId;
    private Car currentCar;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_detail);

        db = FirebaseFirestore.getInstance();
        carId = getIntent().getStringExtra("car_id");

        initViews();
        loadCarDetails();
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

    private void loadCarDetails() {
        db.collection("cars").document(carId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        currentCar = documentSnapshot.toObject(Car.class);
                        if (currentCar != null) {
                            displayCarDetails();
                        }
                    }
                });
    }

    private void displayCarDetails() {
        brandModelText.setText(currentCar.getBrand() + " " + currentCar.getModel());
        yearText.setText("Год: " + currentCar.getYear());
        mileageText.setText("Пробег: " + currentCar.getMileage() + " км");
        engineText.setText("Объем: " + currentCar.getEngineVolume() + " л");
        priceText.setText(String.format("%.0f руб.", currentCar.getPrice()));
        descriptionText.setText(currentCar.getDescription());

        if (currentCar.getImageUrl() != null) {
            Glide.with(this).load(currentCar.getImageUrl()).into(carImage);
        }
    }

    private void contactSeller() {
        Toast.makeText(this, "Контакты продавца будут здесь", Toast.LENGTH_SHORT).show();
    }

    private void toggleFavorite() {
        Toast.makeText(this, "Добавлено в избранное", Toast.LENGTH_SHORT).show();
    }
}