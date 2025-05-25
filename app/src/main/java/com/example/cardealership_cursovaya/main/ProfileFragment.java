package com.example.cardealership_cursovaya.main;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cardealership_cursovaya.R;
import com.example.cardealership_cursovaya.auth.LoginActivity;
import com.example.cardealership_cursovaya.auth.SessionManager;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.net.URL;
import java.io.InputStream;
import java.net.HttpURLConnection;


public class ProfileFragment extends Fragment {
    private FirebaseAuth mAuth;
    private TextView ShowLogin;
    private Button btnfindVin;
    private Button btnLogout;
    private SessionManager sessionManager;
    private EditText vinToFind;
    private TextView vinResult; // Добавим для вывода результатов


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        mAuth = FirebaseAuth.getInstance();
        sessionManager = new SessionManager(requireContext());
        ShowLogin = view.findViewById(R.id.login_user);
        btnLogout = view.findViewById(R.id.btn_logout);

        vinToFind = view.findViewById(R.id.vin_to_find);
        btnfindVin = view.findViewById(R.id.bt_find_vin);
        vinResult = view.findViewById(R.id.vin_result);


        FirebaseUser user = mAuth.getCurrentUser();
        ShowLogin.setText(user.getEmail());


        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            sessionManager.logout();
            redirectToLogin();
        });

        btnfindVin.setOnClickListener(v -> {
            try {
                findVin();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void findVin() throws Exception {
        String vin = vinToFind.getText().toString().trim();
        if (vin.isEmpty()) {
            Toast.makeText(getContext(), "Please enter a VIN", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {
            try {
                URL url = new URL("https://api.api-ninjas.com/v1/vinlookup?vin=" + vin);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("accept", "application/json");
                connection.setRequestProperty("X-Api-Key", "js79A25S3T4mLMf7k12+cA==0p5gyti1deNNuwok\n"); // Добавьте ваш API ключ

                InputStream responseStream = connection.getInputStream();
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(responseStream);

                getActivity().runOnUiThread(() -> {
                    vinResult.setText(root.toString());
                });
            } catch (Exception e) {
                e.printStackTrace();
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    private void redirectToLogin() {
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        requireActivity().finish();
    }
}