package com.example.market;

import android.os.Bundle;
import android.view.View; // <--- ДОБАВЛЕН ЭТОТ ИМПОРТ

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FavoritesActivity extends AppCompatActivity {
    private RecyclerView favoritesRecyclerView;
    private FavoritesAdapter favoritesAdapter;
    private List<Car> favoriteCars;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        initViews();
        loadFavorites();
    }

    private void initViews() {
        favoritesRecyclerView = findViewById(R.id.favoritesRecyclerView);
        favoritesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void loadFavorites() {
        favoriteCars = Favorites.getFavoriteCars();
        favoritesAdapter = new FavoritesAdapter(favoriteCars, this);
        favoritesRecyclerView.setAdapter(favoritesAdapter);

        if (favoriteCars.isEmpty()) {
            findViewById(R.id.emptyText).setVisibility(View.VISIBLE);
            favoritesRecyclerView.setVisibility(View.GONE);
        } else {
            findViewById(R.id.emptyText).setVisibility(View.GONE);
            favoritesRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (favoritesAdapter != null) {
            favoritesAdapter.notifyDataSetChanged();
        }
    }
}