package com.example.cardealership_cursovaya;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;


public class LoginFragment extends Fragment {
    private EditText inputEmail, inputPassword;
    private Button btnLogin;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        initViews(view);
        setupLoginButton();
        return view;
    }

    private void initViews(View view) {
        mAuth = FirebaseAuth.getInstance();
        inputEmail = view.findViewById(R.id.register_email);
        inputPassword = view.findViewById(R.id.register_password);
        btnLogin = view.findViewById(R.id.register_button);

        // Настройка ProgressDialog
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Пожалуйста, подождите...");
        progressDialog.setCancelable(false);
    }

    private void setupLoginButton() {
        btnLogin.setOnClickListener(v -> {
            String email = inputEmail.getText().toString().trim();
            String password = inputPassword.getText().toString().trim();

            if (validateInputs(email, password)) {
                signInUser(email, password);
            }
        });
    }

    private boolean validateInputs(String email, String password) {
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

        return isValid;
    }

    private void signInUser(String email, String password) {
        progressDialog.show();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    progressDialog.dismiss();

                    if (task.isSuccessful()) {
                        checkUserRole();
                    } else {
                        showAuthError(task.getException());
                    }
                });
    }

    private void checkUserRole() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            FirebaseFirestore.getInstance().collection("users")
                    .document(user.getUid()).get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Boolean isAdmin = task.getResult().getBoolean("isAdmin");
                            redirectToAppropriateActivity(isAdmin != null && isAdmin);
                        } else {
                            Toast.makeText(getContext(), "Ошибка проверки роли", Toast.LENGTH_SHORT).show();
                            Log.i("Firebase", "Ошибка проверки роли у пользователя");
                        }
                    });
        }
    }

    private void redirectToAppropriateActivity(boolean isAdmin) {
        Intent intent;
        if (isAdmin) {
            intent = new Intent(getActivity(), AdminActivity.class);
        } else {
            intent = new Intent(getActivity(), MainActivity.class);
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        getActivity().finish();
    }

    private void showAuthError(Exception exception) {
        String errorMessage = "Ошибка входа";

        if (exception instanceof FirebaseAuthInvalidUserException) {
            errorMessage = "Пользователь не найден";
        } else if (exception instanceof FirebaseAuthInvalidCredentialsException) {
            errorMessage = "Неверный email или пароль";
        }

        Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}