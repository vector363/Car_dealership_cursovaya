package com.example.cardealership_cursovaya;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;


public class LoginFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

//        EditText etEmail = view.findViewById(R.id.login_email);
//        EditText etPassword = view.findViewById(R.id.login_password);
//        Button btnLogin = view.findViewById(R.id.login_button);

//        btnLogin.setOnClickListener(v -> {
//            String email = etEmail.getText().toString();
//            String password = etPassword.getText().toString();
//        });

        return view;
    }

}