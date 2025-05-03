package com.example.cardealership_cursovaya;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;


public class LoginActivity extends AppCompatActivity {
    private TextView tvLogin, tvRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        tvLogin = findViewById(R.id.tv_login);
        tvRegister = findViewById(R.id.tv_register);

        if (savedInstanceState == null) { // Установка начального фрагмента
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new LoginFragment()).commit();
        }

        setupTabListeners();
    }

    private void setupTabListeners() {
        tvLogin.setOnClickListener(v -> showLoginFragment());
        tvRegister.setOnClickListener(v -> showRegisterFragment());
    }

    public void showLoginFragment() {
        // Обновляем UI переключателя
        tvLogin.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_auth_tab_selected));
        tvRegister.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_auth_tab_unselected));

        // Заменяем фрагмент
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new LoginFragment())
                .commit();
    }

    public void showRegisterFragment() {
        // Обновляем UI переключателя
        tvRegister.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_auth_tab_selected));
        tvLogin.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_auth_tab_unselected));

        // Заменяем фрагмент
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new RegisterFragment()).commit();
    }

}