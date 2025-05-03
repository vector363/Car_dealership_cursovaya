package com.example.cardealership_cursovaya;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class RegisterFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

//        EditText etEmail = view.findViewById(R.id.register_email);
//        EditText etPassword = view.findViewById(R.id.register_password);
//        Button btnRegister = view.findViewById(R.id.register_button);
//
//        btnRegister.setOnClickListener(v -> {
//            // Обработка регистрации
//            String email = etEmail.getText().toString();
//            String password = etPassword.getText().toString();
//            // Ваша логика регистрации
//        });
//
//        tvLoginLink.setOnClickListener(v -> {
//            // Переход на экран входа
//            if (getActivity() != null) {
//                ((AuthActivity)getActivity()).showLoginFragment();
//            }
//        });

        return view;
    }
}