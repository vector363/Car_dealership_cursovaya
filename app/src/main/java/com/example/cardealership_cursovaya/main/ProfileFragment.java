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
import com.example.cardealership_cursovaya.auth.SessionManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class ProfileFragment extends Fragment {
    private FirebaseAuth mAuth;
    private TextView ShowLogin;
    private Button btnLogout;
    private SessionManager sessionManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        mAuth = FirebaseAuth.getInstance();
        sessionManager = new SessionManager(requireContext());
        ShowLogin = view.findViewById(R.id.login_user);
        btnLogout = view.findViewById(R.id.btn_logout);

        FirebaseUser user = mAuth.getCurrentUser();
        ShowLogin.setText(user.getEmail());

        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            sessionManager.logout();
            redirectToLogin();
        });

        return view;
    }
    private void redirectToLogin() {
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        requireActivity().finish();
    }
}