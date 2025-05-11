package com.example.cardealership_cursovaya.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.cardealership_cursovaya.admin.AdminActivity;
import com.example.cardealership_cursovaya.main.MainActivity;
import com.example.cardealership_cursovaya.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {
    private TextView switchLogin, switchRegister;
    private SessionManager sessionManager;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManager = new SessionManager(this);
        mAuth = FirebaseAuth.getInstance();

        // Проверка существующей сессии
        if (sessionManager.isLoggedIn() && mAuth.getCurrentUser() != null) {
            redirectBasedOnRole();
            return;
        }

        setContentView(R.layout.activity_login);
        initViews();
        setupTabListeners();

        if (savedInstanceState == null) {
            showLoginFragment();
        }
    }

    private void redirectBasedOnRole() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            FirebaseFirestore.getInstance().collection("users")
                    .document(user.getUid()).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Boolean isAdmin = task.getResult().getBoolean("isAdmin");
                            Intent intent = new Intent(this,
                                    isAdmin != null && isAdmin ? AdminActivity.class : MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        }
                    });
        }
    }
    private void initViews() {
        switchLogin = findViewById(R.id.switch_login);
        switchRegister = findViewById(R.id.switch_register);
    }

    private void checkUserRoleAndRedirect() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            FirebaseFirestore.getInstance().collection("users").document(user.getUid()).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Boolean isAdmin = task.getResult().getBoolean("isAdmin");
                            Intent intent;

                            if (isAdmin != null && isAdmin) { //Выбор активности для пользователей/администраторов
                                intent = new Intent(this, AdminActivity.class);
                            } else {
                                intent = new Intent(this, MainActivity.class);
                            }

                            startActivity(intent);
                            finish();
                        }
                    });
        }
    }

    private void setupTabListeners() {
        switchLogin.setOnClickListener(v -> showLoginFragment());
        switchRegister.setOnClickListener(v -> showRegisterFragment());
    }

    public void showLoginFragment() {
        updateTabAppearance(true);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new LoginFragment()).commit();
    }

    public void showRegisterFragment() {
        updateTabAppearance(false);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new RegisterFragment()).commit();
    }

    private void updateTabAppearance(boolean isLoginSelected) {
        // Установка фона
        switchLogin.setBackground(ContextCompat.getDrawable(this,
                isLoginSelected ? R.drawable.bg_auth_tab_selected : R.drawable.bg_auth_tab_unselected));

        switchRegister.setBackground(ContextCompat.getDrawable(this,
                isLoginSelected ? R.drawable.bg_auth_tab_unselected : R.drawable.bg_auth_tab_selected));

        // Установка цвета текста
        switchLogin.setTextColor(ContextCompat.getColor(this,
                isLoginSelected ? R.color.UnswitchColor : R.color.colorTextWhite));

        switchRegister.setTextColor(ContextCompat.getColor(this,
                isLoginSelected ? R.color.colorTextWhite : R.color.UnswitchColor));
    }
}