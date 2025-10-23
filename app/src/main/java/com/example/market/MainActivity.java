package com.example.market;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView carsRecyclerView;
    private CarAdapter carAdapter;
    private List<Car> carList;
    private BottomNavigationView bottomNavigationView;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        // Проверка аутентификации
        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        carList = new ArrayList<>();
        initViews();
        setupBottomNavigation();
        loadCars();
    }

    private void initViews() {
        carsRecyclerView = findViewById(R.id.carsRecyclerView);
        carsRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        carAdapter = new CarAdapter(carList, this);
        carsRecyclerView.setAdapter(carAdapter);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                // Уже на главной
                return true;
            } else if (itemId == R.id.nav_favorites) {
                startActivity(new Intent(this, FavoritesActivity.class));
                return true;
            } else if (itemId == R.id.nav_add) {
                startActivity(new Intent(this, AddCarActivity.class));
                return true;
            } else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                return true;
            }
            return false;
        });
    }

    private void loadCars() {
        try {
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            // Показываем тестовые данные сразу
            showTestData();

            // Пытаемся загрузить из Firebase
            db.collection("cars")
                    .orderBy("createdAt", Query.Direction.DESCENDING)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                            carList.clear();
                            for (DocumentSnapshot doc : task.getResult()) {
                                Car car = doc.toObject(Car.class);
                                if (car != null) {
                                    car.setId(doc.getId());
                                    car.setFavorite(Favorites.isFavorite(car));
                                    carList.add(car);
                                }
                            }
                            carAdapter.notifyDataSetChanged();
                            Toast.makeText(this, "Данные загружены из Firebase!", Toast.LENGTH_SHORT).show();
                        } else {
                            // Если в Firebase нет данных, оставляем тестовые
                            Toast.makeText(this, "В Firebase нет данных, показаны тестовые", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Ошибка загрузки: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });

        } catch (Exception e) {
            Toast.makeText(this, "Ошибка Firebase: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Обновляем статус избранного
        if (carList != null) {
            for (Car car : carList) {
                car.setFavorite(Favorites.isFavorite(car));
            }
        }
        if (carAdapter != null) {
            carAdapter.notifyDataSetChanged();
        }
    }

    private void showTestData() {
        carList.clear();

        String bmwImage = "https://avatars.mds.yandex.net/get-autoru-vos/5662943/de5d0d751fc8c6a0dcb2e9485552d4e4/584x438";
        String audiImage = "https://avatars.mds.yandex.net/get-autoru-vos/1906600/469f1bbe1cee7175f92e1046750f313c/456x342";
        String mercedesImage = "https://avatars.mds.yandex.net/get-autoru-vos/2198467/b978ab9b6f94cb6c6590f10bfa9f215e/1200x900";
        String toyotaImage = "https://s9.auto.drom.ru/photo/v2/RL2Zc0IGTyUGZK6H3jYUGDpdxkf4MIAX9Nvlo91QFb0GPm0rn4TYghqB2gshXkPXv0nw_hs2ZSqK7TCT/gen600.jpg";
        String hondaImage = "https://s.auto.drom.ru/i24226/r/photos/988184/gen448x2_1392058.jpg";
        String vwImage = "https://avatars.mds.yandex.net/get-autoru-vos/2072137/a8acc31d747a4ed2f856abaa85c553ab/584x438";
        String hyundaiImage = "https://s.auto.drom.ru/photo/v2/VCS6jHnHW68hJm4mddUxphm8QsGbAcbkWqlmk4nkiJfor_p0SwuFcqlpfDUsSeAVYfFXOTZktbWhp0o2/ttn.jpg";
        String kiaImage = "https://s.auto.drom.ru/photo/v2/8vsCDGKOWPO6Q6gceJvqSg2Hf6anVmDdrxz8my9bhoqNsv_Vmd70_wnbUnsZgCYQIfOm23PXZIAfnUIR/ttn.jpg";
        String fordImage = "https://50.img.avito.st/image/1/1.1M0tj7aAeCRbIborCfSF80YueiCfOHwkn18ZIJ_sd-aWLHw-WyG6K5s.xfgi1MEc4be1vwwGOMkh0jm6SJFKPga13LhXW_25LXM";
        String nissanImage = "https://avatars.mds.yandex.net/get-autoru-vos/2163944/0a8513e27d4cfa627b5d7bcd0db3ea3e/1200x900";

        carList.add(new Car("1", "BMW", "X5", 2020, 45000, 3.0, 3500000.0,
                "Отличное состояние, один владелец, полная сервисная история.", "1", bmwImage));
        carList.add(new Car("2", "Audi", "A4", 2018, 80000, 2.0, 2200000.0,
                "Полная сервисная история у официального дилера.", "1", audiImage));
        carList.add(new Car("3", "Mercedes", "C-Class", 2019, 60000, 2.0, 2800000.0,
                "Премиум комплектация: кожаный салон, панорамная крыша.", "1", mercedesImage));
        carList.add(new Car("4", "Toyota", "Camry", 2021, 25000, 2.5, 2400000.0,
                "Новый, в идеальном состоянии. Покрытие керамикой.", "1", toyotaImage));
        carList.add(new Car("5", "Honda", "CR-V", 2017, 90000, 2.4, 1800000.0,
                "Экономичный и надежный. Все расходники заменены.", "1", hondaImage));
        carList.add(new Car("6", "Volkswagen", "Tiguan", 2020, 55000, 2.0, 2300000.0,
                "Полный привод, климат-контроль, камера заднего вида.", "1", vwImage));
        carList.add(new Car("7", "Hyundai", "Tucson", 2019, 70000, 2.0, 1600000.0,
                "Комплектация Premium, полный электропакет.", "1", hyundaiImage));
        carList.add(new Car("8", "Kia", "Sportage", 2020, 40000, 2.0, 1700000.0,
                "Современный дизайн, экономичный расход.", "1", kiaImage));
        carList.add(new Car("9", "Ford", "Focus", 2018, 85000, 1.6, 1200000.0,
                "Динамичный хэтчбек, отличная управляемость.", "1", fordImage));
        carList.add(new Car("10", "Nissan", "Qashqai", 2021, 30000, 1.3, 1900000.0,
                "Современный кроссовер, экономичный двигатель.", "1", nissanImage));

        if (carAdapter != null) {
            carAdapter.notifyDataSetChanged();
        }
    }

}