package com.example.market;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class EditCarActivity extends AppCompatActivity {
    private EditText brandEditText, modelEditText, yearEditText, mileageEditText,
            engineEditText, priceEditText, descriptionEditText;
    private Button addImageButton, submitButton;
    private ImageView carImageView;
    private Uri imageUri;
    private String carId;
    private Car currentCar;
    private FirebaseFirestore db;
    private FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_car);

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        carId = getIntent().getStringExtra("car_id");

        initViews();
        loadCarDetails();
    }

    private void initViews() {
        brandEditText = findViewById(R.id.brandEditText);
        modelEditText = findViewById(R.id.modelEditText);
        yearEditText = findViewById(R.id.yearEditText);
        mileageEditText = findViewById(R.id.mileageEditText);
        engineEditText = findViewById(R.id.engineEditText);
        priceEditText = findViewById(R.id.priceEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        addImageButton = findViewById(R.id.addImageButton);
        submitButton = findViewById(R.id.submitButton);
        carImageView = findViewById(R.id.carImageView);

        addImageButton.setOnClickListener(v -> selectImage());
        submitButton.setOnClickListener(v -> saveChanges());
    }

    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            carImageView.setImageURI(imageUri);
        }
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
        brandEditText.setText(currentCar.getBrand());
        modelEditText.setText(currentCar.getModel());
        yearEditText.setText(String.valueOf(currentCar.getYear()));
        mileageEditText.setText(String.valueOf(currentCar.getMileage()));
        engineEditText.setText(String.valueOf(currentCar.getEngineVolume()));
        priceEditText.setText(String.valueOf(currentCar.getPrice()));
        descriptionEditText.setText(currentCar.getDescription());

        if (currentCar.getImageUrl() != null) {
            Glide.with(this).load(currentCar.getImageUrl()).into(carImageView);
        }
    }

    private void saveChanges() {
        String brand = brandEditText.getText().toString().trim();
        String model = modelEditText.getText().toString().trim();
        String yearStr = yearEditText.getText().toString().trim();
        String mileageStr = mileageEditText.getText().toString().trim();
        String engineStr = engineEditText.getText().toString().trim();
        String priceStr = priceEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();

        if (brand.isEmpty() || model.isEmpty() || yearStr.isEmpty() ||
                mileageStr.isEmpty() || engineStr.isEmpty() || priceStr.isEmpty()) {
            Toast.makeText(this, "Заполните все обязательные поля", Toast.LENGTH_SHORT).show();
            return;
        }

        currentCar.setBrand(brand);
        currentCar.setModel(model);
        currentCar.setYear(Integer.parseInt(yearStr));
        currentCar.setMileage(Integer.parseInt(mileageStr));
        currentCar.setEngineVolume(Double.parseDouble(engineStr));
        currentCar.setPrice(Double.parseDouble(priceStr));
        currentCar.setDescription(description);

        if (imageUri != null) {
            uploadImageAndUpdateCar();
        } else {
            updateCarInFirestore();
        }
    }

    private void uploadImageAndUpdateCar() {
        final ProgressDialog progressDialog = new ProgressDialog(this); // Добавлено final
        progressDialog.setMessage("Обновление данных...");
        progressDialog.show();

        StorageReference imageRef = storage.getReference().child("car_images/" +
                System.currentTimeMillis() + ".jpg");

        imageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        currentCar.setImageUrl(uri.toString());
                        updateCarInFirestore(progressDialog);
                    });
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Ошибка загрузки фото", Toast.LENGTH_SHORT).show();
                });
    }

    private void updateCarInFirestore() {
        final ProgressDialog progressDialog = new ProgressDialog(this); // Добавлено final
        progressDialog.setMessage("Обновление данных...");
        progressDialog.show();
        updateCarInFirestore(progressDialog);
    }

    private void updateCarInFirestore(final ProgressDialog progressDialog) { // Добавлен параметр
        db.collection("cars").document(carId).set(currentCar)
                .addOnSuccessListener(aVoid -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Изменения сохранены", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Ошибка сохранения", Toast.LENGTH_SHORT).show();
                });
    }
}