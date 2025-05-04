package com.example.cardealership_cursovaya.main;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.cardealership_cursovaya.R;
import com.example.cardealership_cursovaya.auth.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class ProfileFragment extends Fragment {
    private FirebaseAuth mAuth;
    private TextView ShowLogin;
    private Button btnLogout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        mAuth = FirebaseAuth.getInstance();
        ShowLogin = view.findViewById(R.id.login_user);
        btnLogout = view.findViewById(R.id.btn_logout);
        showUserData();
        setupLogoutButton();

        return view;
    }
    private void showUserData() {
        FirebaseUser user = mAuth.getCurrentUser();
        ShowLogin.setText(user.getEmail());
    }

    private void setupLogoutButton() {
        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            redirectToLogin();
        });
    }

    private void redirectToLogin() {
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        if (getActivity() != null) {
            getActivity().finish();
        }
    }

}






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