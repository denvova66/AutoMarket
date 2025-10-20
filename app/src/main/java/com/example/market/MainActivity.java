package com.example.market;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ПРИНУДИТЕЛЬНАЯ ИНИЦИАЛИЗАЦИЯ FIREBASE
        try {
            if (FirebaseApp.getApps(this).isEmpty()) {
                FirebaseApp.initializeApp(this);
                Toast.makeText(this, "Firebase инициализирован", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Ошибка инициализации Firebase: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

        setContentView(R.layout.activity_main);

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

            // Сначала показываем тестовые данные
            showTestData();

            // Пытаемся загрузить из Firebase
            db.collection("cars")
                    .orderBy("createdAt", Query.Direction.DESCENDING)
                    .addSnapshotListener((value, error) -> {
                        if (error != null) {
                            Toast.makeText(this, "Ошибка загрузки из Firebase: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (value != null && !value.isEmpty()) {
                            carList.clear();
                            for (DocumentSnapshot doc : value.getDocuments()) {
                                Car car = doc.toObject(Car.class);
                                if (car != null) {
                                    car.setId(doc.getId());
                                    carList.add(car);
                                }
                            }
                            carAdapter.notifyDataSetChanged();
                            Toast.makeText(this, "Данные загружены из Firebase!", Toast.LENGTH_SHORT).show();
                        } else {
                            // Если в Firebase нет данных, оставляем тестовые
                            Toast.makeText(this, "В Firebase нет данных, показаны тестовые", Toast.LENGTH_SHORT).show();
                        }
                    });

        } catch (Exception e) {
            Toast.makeText(this, "Ошибка Firebase: " + e.getMessage(), Toast.LENGTH_LONG).show();
            // Показываем тестовые данные при ошибке
            showTestData();
            e.printStackTrace();
        }
    }

    private void showTestData() {
        carList.clear();
        carList.add(new Car("1", "BMW", "X5", 2020, 45000, 3.0, 3500000.0,
                "Отличное состояние, один владелец, полная сервисная история. Машина в идеальном техническом и косметическом состоянии.", "1", ""));
        carList.add(new Car("2", "Audi", "A4", 2018, 80000, 2.0, 2200000.0,
                "Полная сервисная история у официального дилера. Не бита, не крашена. Все ТО по регламенту.", "1", ""));
        carList.add(new Car("3", "Mercedes", "C-Class", 2019, 60000, 2.0, 2800000.0,
                "Премиум комплектация: кожаный салон, панорамная крыша, подогрев сидений. Обслуживалась у дилера.", "1", ""));
        carList.add(new Car("4", "Toyota", "Camry", 2021, 25000, 2.5, 2400000.0,
                "Новый, в идеальном состоянии. Покрытие керамикой, дополнительная шумоизоляция.", "1", ""));
        carList.add(new Car("5", "Honda", "CR-V", 2017, 90000, 2.4, 1800000.0,
                "Экономичный и надежный. Отличное состояние для своего возраста. Все расходники заменены.", "1", ""));
        carList.add(new Car("6", "Volkswagen", "Tiguan", 2020, 55000, 2.0, 2300000.0,
                "Полный привод, климат-контроль, камера заднего вида. Обслуживалась по регламенту.", "1", ""));

        carAdapter.notifyDataSetChanged();
    }
}