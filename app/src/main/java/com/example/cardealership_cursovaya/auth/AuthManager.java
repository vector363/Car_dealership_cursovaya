package com.example.cardealership_cursovaya.auth;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.firebase.auth.FirebaseAuth;

public class AuthManager {
    private static final String PREFS_NAME = "auth_prefs";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";

    private final SharedPreferences prefs;
    private final FirebaseAuth mAuth;

    public AuthManager(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        mAuth = FirebaseAuth.getInstance();
    }

    public void saveLoginState(String email) {
        prefs.edit()
                .putString(KEY_EMAIL, email)
                .putBoolean(KEY_IS_LOGGED_IN, true)
                .apply();
    }

    public void clearLoginState() {
        prefs.edit().clear().apply();
    }

    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public String getSavedEmail() {
        return prefs.getString(KEY_EMAIL, null);
    }

    public FirebaseAuth getAuth() {
        return mAuth;
    }
}