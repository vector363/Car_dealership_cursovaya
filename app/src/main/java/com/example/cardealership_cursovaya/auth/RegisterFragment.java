package com.example.cardealership_cursovaya.auth;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.cardealership_cursovaya.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class RegisterFragment extends Fragment {
    private EditText inputEmail, inputPassword, inputRePassword;
    private Button btnRegister;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        initFirebase();
        initViews(view);
        setupRegisterButton();
        return view;
    }

    private void initFirebase() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    private void initViews(View view) {
        inputEmail = view.findViewById(R.id.register_email);
        inputPassword = view.findViewById(R.id.register_password);
        inputRePassword = view.findViewById(R.id.register_password_replay);
        btnRegister = view.findViewById(R.id.register_button);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Регистрация");
        progressDialog.setMessage("Создаем ваш аккаунт...");
        progressDialog.setCancelable(false);
    }

    private void setupRegisterButton() {
        btnRegister.setOnClickListener(v -> {
            String email = inputEmail.getText().toString().trim();
            String password = inputPassword.getText().toString().trim();
            String rePassword = inputRePassword.getText().toString().trim();

            if (validateInputs(email, password, rePassword)) {
                registerUser(email, password);
            }
        });
    }

    private boolean validateInputs(String email, String password, String rePassword) {
        boolean isValid = true;

        if (email.isEmpty()) {
            inputEmail.setError("Введите email");
            isValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            inputEmail.setError("Введите корректный email");
            isValid = false;
        }

        if (password.isEmpty()) {
            inputPassword.setError("Введите пароль");
            isValid = false;
        } else if (password.length() < 6) {
            inputPassword.setError("Пароль должен содержать минимум 6 символов");
            isValid = false;
        }

        if (!password.equals(rePassword)) {
            inputRePassword.setError("Пароли не совпадают");
            isValid = false;
        }

        return isValid;
    }

    private void registerUser(String email, String password) {
        progressDialog.show();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        saveUserToFirestore(email);
                    } else {
                        handleRegistrationError(task.getException());
                    }
                });
    }

    private void saveUserToFirestore(String email) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            Map<String, Object> userData = new HashMap<>();
            userData.put("email", email);
            userData.put("isAdmin", false); // По умолчанию обычный пользователь
            userData.put("createdAt", FieldValue.serverTimestamp());

            db.collection("users")
                    .document(user.getUid())
                    .set(userData)
                    .addOnCompleteListener(task -> {
                        progressDialog.dismiss();

                        if (task.isSuccessful()) {
                            showSuccessAndSwitchToLogin();
                        } else {
                            Toast.makeText(getContext(),
                                    "Ошибка сохранения данных", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void showSuccessAndSwitchToLogin() {
        Toast.makeText(getContext(), "Регистрация успешно завершена!", Toast.LENGTH_SHORT).show();

        // Возвращаемся к экрану входа
        if (getActivity() instanceof LoginActivity) {
            ((LoginActivity) getActivity()).showLoginFragment();
        }
    }

    private void handleRegistrationError(Exception exception) {
        progressDialog.dismiss();
        String errorMessage = "Ошибка регистрации";

        if (exception instanceof FirebaseAuthUserCollisionException) {
            errorMessage = "Пользователь с таким email уже существует";
        } else if (exception instanceof FirebaseAuthWeakPasswordException) {
            errorMessage = "Пароль слишком простой";
        } else if (exception instanceof FirebaseAuthInvalidCredentialsException) {
            errorMessage = "Неверный формат email";
        }

        Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}