package com.example.market;

import android.app.Application;
import android.content.Context;

public class App extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;

        // Инициализация Firebase
        try {
            // Firebase инициализируется автоматически через google-services.json
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Инициализация избранного
        Favorites.init(this);

        // Применяем локаль
        LocaleUtils.applyLocale(this);
    }

    public static Context getContext() {
        return context;
    }
}