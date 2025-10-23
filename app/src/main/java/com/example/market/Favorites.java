package com.example.market;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Favorites {
    private static final String PREFS_NAME = "favorites_prefs";
    private static final String FAVORITES_KEY = "favorite_cars";
    private static List<Car> favoriteCars = new ArrayList<>();
    private static SharedPreferences prefs;
    private static Gson gson = new Gson();

    public static void init(Context context) {
        if (prefs == null) {
            prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            loadFavorites();
        }
    }

    public static List<Car> getFavoriteCars() {
        return favoriteCars;
    }

    public static void addFavoriteCar(Car car) {
        if (!favoriteCars.contains(car)) {
            favoriteCars.add(car);
            saveFavorites();
        }
    }

    public static void removeFavoriteCar(Car car) {
        favoriteCars.remove(car);
        saveFavorites();
    }

    public static boolean isFavorite(Car car) {
        return favoriteCars.contains(car);
    }

    private static void saveFavorites() {
        String json = gson.toJson(favoriteCars);
        prefs.edit().putString(FAVORITES_KEY, json).apply();
    }

    private static void loadFavorites() {
        String json = prefs.getString(FAVORITES_KEY, null);
        if (json != null) {
            Type type = new TypeToken<ArrayList<Car>>() {}.getType();
            favoriteCars = gson.fromJson(json, type);
        }
    }
}