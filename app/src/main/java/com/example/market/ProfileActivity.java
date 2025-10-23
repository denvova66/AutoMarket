package com.example.market;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity {
    private TextView userNameText, userEmailText;
    private Button addCarButton, logoutButton;
    private FirebaseAuth mAuth;
    private boolean isLanguageSpinnerInitialized = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();

        initViews();
        loadUserData();
    }

    private void initViews() {
        userNameText = findViewById(R.id.userNameText);
        userEmailText = findViewById(R.id.userEmailText);
        addCarButton = findViewById(R.id.addCarButton);
        logoutButton = findViewById(R.id.logoutButton);
        Spinner languageSpinner = findViewById(R.id.languageSpinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.languages, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        languageSpinner.setAdapter(adapter);

        String currentLanguage = getSharedPreferences("locale_prefs", MODE_PRIVATE).getString("locale", "ru");
        if (currentLanguage.equals("en")) {
            languageSpinner.setSelection(1);
        } else {
            languageSpinner.setSelection(0);
        }

        languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!isLanguageSpinnerInitialized) {
                    isLanguageSpinnerInitialized = true;
                    return;
                }
                String language = position == 0 ? "ru" : "en";
                LocaleUtils.setLocale(ProfileActivity.this, language);
                recreate();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        addCarButton.setOnClickListener(v -> startActivity(new Intent(this, AddCarActivity.class)));
        logoutButton.setOnClickListener(v -> logout());
    }

    private void loadUserData() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            userEmailText.setText(user.getEmail());
            userNameText.setText(user.getDisplayName() != null && !user.getDisplayName().isEmpty() ? user.getDisplayName() : "Пользователь");
        }
    }

    private void logout() {
        mAuth.signOut();
        startActivity(new Intent(this, LoginActivity.class));
        finishAffinity();
    }
}