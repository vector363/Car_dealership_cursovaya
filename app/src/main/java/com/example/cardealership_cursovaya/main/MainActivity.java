package com.example.cardealership_cursovaya.main;


import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.cardealership_cursovaya.R;
import com.example.cardealership_cursovaya.auth.LoginActivity;
import com.example.cardealership_cursovaya.auth.SessionManager;
import com.example.cardealership_cursovaya.catalog.CatalogFragment;
import com.example.cardealership_cursovaya.catalog.favorite.FavoriteFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNav;
    private SessionManager sessionManager;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManager = new SessionManager(this);
        mAuth = FirebaseAuth.getInstance();

        // проверка аутентификации
        if (!sessionManager.isLoggedIn() || mAuth.getCurrentUser() == null) {
            redirectToLogin();
            return;
        }

        setContentView(R.layout.activity_main);
        setupUI();
    }

    private void setupUI() {
        bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setItemActiveIndicatorEnabled(false);
        setupNavigation();
    }

    private void redirectToLogin() { //Запуск активности для авторизации
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void setupNavigation() {
        loadFragment(new CatalogFragment());

        Menu menu = bottomNav.getMenu();
        MenuItem adminMenuItem = menu.findItem(R.id.nav_favorite);
        adminMenuItem.setVisible(true);

        bottomNav.setOnNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            if (item.getItemId() == R.id.nav_catalog) {
                selectedFragment = new CatalogFragment();
            } else if (item.getItemId() == R.id.nav_favorite) {
                selectedFragment = new FavoriteFragment();
            } else if (item.getItemId() == R.id.nav_profile) {
                selectedFragment = new ProfileFragment();
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment);
                return true;
            }
            return false;
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

}