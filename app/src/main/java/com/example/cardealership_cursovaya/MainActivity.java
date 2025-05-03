package com.example.cardealership_cursovaya;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        TextView tvWelcome = findViewById(R.id.tv_welcome);
        Button btnLogout = findViewById(R.id.btn_logout);

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            tvWelcome.setText("Добро пожаловать, " + user.getEmail());
        }

        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }
}