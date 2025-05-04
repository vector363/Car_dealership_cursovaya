package com.example.cardealership_cursovaya;


import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNav;
    private boolean isAdmin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNav = findViewById(R.id.bottom_nav);
        setupNavigation();
    }
    private void setupNavigation() {
        loadFragment(new CatalogFragment());

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
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }


//    // Метод для скрытия/показа BottomNavigation
//    public void setBottomNavVisibility(boolean visible) {
//        bottomNav.setVisibility(visible ? View.VISIBLE : View.GONE);
//    }













//    private FirebaseAuth mAuth;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        mAuth = FirebaseAuth.getInstance();
//        TextView tvWelcome = findViewById(R.id.tv_welcome);
//        Button btnLogout = findViewById(R.id.btn_logout);
//
//        FirebaseUser user = mAuth.getCurrentUser();
//        if (user != null) {
//            tvWelcome.setText("Добро пожаловать, " + user.getEmail());
//        }
//
//        btnLogout.setOnClickListener(v -> {
//            mAuth.signOut();
//            startActivity(new Intent(this, LoginActivity.class));
//            finish();
//        });
//    }




//    @Override
//    protected void onStart() {
//        super.onStart();
//        if (mAuth.getCurrentUser() == null) {
//            startActivity(new Intent(this, LoginActivity.class));
//            finish();
//        }
//    }
}