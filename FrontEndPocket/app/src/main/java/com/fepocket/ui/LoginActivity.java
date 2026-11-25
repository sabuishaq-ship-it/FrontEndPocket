package com.fepocket.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fepocket.R;
import com.fepocket.db.DatabaseHelper;
import com.fepocket.model.User;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private CheckBox cbRemember;
    private Button btnLogin;
    private TextView tvCreateAccount, tvForgotPassword;
    private DatabaseHelper dbHelper;
    private SharedPreferences loginPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dbHelper = new DatabaseHelper(this);
        loginPrefs = getSharedPreferences("login_prefs", MODE_PRIVATE);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        cbRemember = findViewById(R.id.cbRememberMe);
        btnLogin = findViewById(R.id.btnLogin);
        tvCreateAccount = findViewById(R.id.tvCreateAccount);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);

        tvForgotPassword.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
        });

        restorePrefs();

        btnLogin.setOnClickListener(v -> attemptLogin());

        tvCreateAccount.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });

        if (savedInstanceState != null) {
            etEmail.setText(savedInstanceState.getString("email", ""));
            etPassword.setText(savedInstanceState.getString("password", ""));
            cbRemember.setChecked(savedInstanceState.getBoolean("remember", false));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("email", etEmail.getText().toString());
        outState.putString("password", etPassword.getText().toString());
        outState.putBoolean("remember", cbRemember.isChecked());
    }

    private void restorePrefs() {
        boolean rememberMe = loginPrefs.getBoolean("remember_me", false);
        if (rememberMe) {
            etEmail.setText(loginPrefs.getString("email", ""));
            etPassword.requestFocus();
            cbRemember.setChecked(true);
        }
    }

    private void attemptLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty()) {
            etEmail.setError("Email is required");
            etEmail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Invalid email format");
            etEmail.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            etPassword.setError("Password is required");
            etPassword.requestFocus();
            return;
        }

        User user = dbHelper.loginUser(email, password);
        if (user != null) {
            SharedPreferences.Editor editor = loginPrefs.edit();
            if (cbRemember.isChecked()) {
                editor.putString("email", email);
                editor.putBoolean("remember_me", true);
            } else {
                editor.clear();
            }
            editor.putInt("user_id", user.getId());
            editor.putString("role", dbHelper.getUserRole(user.getId()));
            editor.apply();

            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            intent.putExtra("user_id", user.getId());
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show();
        }
    }
}
