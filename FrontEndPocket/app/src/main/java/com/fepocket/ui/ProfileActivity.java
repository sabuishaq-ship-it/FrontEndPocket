package com.fepocket.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.fepocket.R;
import com.fepocket.db.DatabaseHelper;
import com.fepocket.model.User;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private Toolbar toolbar;
    private CircleImageView civProfileImage;
    private TextView tvProfileFullName, tvProfileUsername, tvProfileEmail;
    private TextView tvStatCategories, tvStatLessons, tvStatFavorites;
    private Button btnEditProfile, btnChangePassword, btnLogout;
    private DatabaseHelper dbHelper;
    private int userId;
    private byte[] newImageBytes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        dbHelper = new DatabaseHelper(this);

        toolbar = findViewById(R.id.toolbar);
        civProfileImage = findViewById(R.id.civProfileImage);
        tvProfileFullName = findViewById(R.id.tvProfileFullName);
        tvProfileUsername = findViewById(R.id.tvProfileUsername);
        tvProfileEmail = findViewById(R.id.tvProfileEmail);
        tvStatCategories = findViewById(R.id.tvStatCategories);
        tvStatLessons = findViewById(R.id.tvStatLessons);
        tvStatFavorites = findViewById(R.id.tvStatFavorites);
        btnEditProfile = findViewById(R.id.btnEditProfile);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        btnLogout = findViewById(R.id.btnLogout);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        userId = getIntent().getIntExtra("user_id", -1);
        if (userId == -1) {
            SharedPreferences prefs = getSharedPreferences("login_prefs", MODE_PRIVATE);
            userId = prefs.getInt("user_id", -1);
        }

        loadProfile();

        civProfileImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        btnEditProfile.setOnClickListener(v -> showEditProfileDialog());
        btnChangePassword.setOnClickListener(v -> showChangePasswordDialog());

        btnLogout.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Logout")
                    .setMessage("Are you sure you want to logout?")
                    .setPositiveButton("Logout", (dialog, which) -> {
                        SharedPreferences prefs = getSharedPreferences("login_prefs", MODE_PRIVATE);
                        prefs.edit().clear().apply();
                        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    private void loadProfile() {
        User user = dbHelper.getUserById(userId);
        if (user != null) {
            tvProfileFullName.setText(user.getFullName());
            tvProfileUsername.setText("@" + user.getUsername());
            tvProfileEmail.setText(user.getEmail());

            byte[] imageBlob = user.getProfileImage();
            if (imageBlob != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(imageBlob, 0, imageBlob.length);
                civProfileImage.setImageBitmap(bitmap);
            }

            int categoriesCount = dbHelper.getAllCategories().size();
            int lessonsCount = 0;
            for (com.fepocket.model.Category cat : dbHelper.getAllCategories()) {
                lessonsCount += cat.getLessonCount();
            }
            int favoritesCount = dbHelper.getFavoriteLessons().size();

            tvStatCategories.setText(String.valueOf(categoriesCount));
            tvStatLessons.setText(String.valueOf(lessonsCount));
            tvStatFavorites.setText(String.valueOf(favoritesCount));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap original = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                Bitmap scaled = Bitmap.createScaledBitmap(original, 200, 200, true);
                civProfileImage.setImageBitmap(scaled);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                scaled.compress(Bitmap.CompressFormat.PNG, 100, stream);
                newImageBytes = stream.toByteArray();

                User user = dbHelper.getUserById(userId);
                if (user != null) {
                    user.setProfileImage(newImageBytes);
                    dbHelper.updateUser(user);
                    Toast.makeText(this, "Profile image updated", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showEditProfileDialog() {
        User currentUser = dbHelper.getUserById(userId);
        if (currentUser == null) return;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_edit_profile, null);
        builder.setView(view);

        EditText etEditFullName = view.findViewById(R.id.etEditFullName);
        EditText etEditUsername = view.findViewById(R.id.etEditUsername);
        EditText etEditEmail = view.findViewById(R.id.etEditEmail);
        Button btnSaveProfile = view.findViewById(R.id.btnSaveProfile);
        Button btnCancelProfile = view.findViewById(R.id.btnCancelProfile);

        etEditFullName.setText(currentUser.getFullName());
        etEditUsername.setText(currentUser.getUsername());
        etEditEmail.setText(currentUser.getEmail());

        AlertDialog dialog = builder.create();

        btnSaveProfile.setOnClickListener(v -> {
            String fullName = etEditFullName.getText().toString().trim();
            String username = etEditUsername.getText().toString().trim();
            String email = etEditEmail.getText().toString().trim();

            if (fullName.isEmpty()) {
                etEditFullName.setError("Full name is required");
                return;
            }
            if (username.isEmpty()) {
                etEditUsername.setError("Username is required");
                return;
            }
            if (email.isEmpty()) {
                etEditEmail.setError("Email is required");
                return;
            }
            if (!username.equals(currentUser.getUsername()) && dbHelper.isUsernameExists(username)) {
                etEditUsername.setError("Username already exists");
                return;
            }
            if (!email.equals(currentUser.getEmail()) && dbHelper.isEmailExists(email)) {
                etEditEmail.setError("Email already exists");
                return;
            }

            currentUser.setFullName(fullName);
            currentUser.setUsername(username);
            currentUser.setEmail(email);
            currentUser.setProfileImage(newImageBytes != null ? newImageBytes : currentUser.getProfileImage());

            dbHelper.updateUser(currentUser);
            Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
            loadProfile();
        });

        btnCancelProfile.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void showChangePasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_change_password, null);
        builder.setView(view);

        EditText etCurrentPassword = view.findViewById(R.id.etCurrentPassword);
        EditText etNewPassword = view.findViewById(R.id.etNewPassword);
        EditText etConfirmNewPassword = view.findViewById(R.id.etConfirmNewPassword);
        Button btnSavePassword = view.findViewById(R.id.btnSavePassword);
        Button btnCancelPassword = view.findViewById(R.id.btnCancelPassword);

        AlertDialog dialog = builder.create();

        btnSavePassword.setOnClickListener(v -> {
            String currentPassword = etCurrentPassword.getText().toString().trim();
            String newPassword = etNewPassword.getText().toString().trim();
            String confirmNewPassword = etConfirmNewPassword.getText().toString().trim();

            if (currentPassword.isEmpty()) {
                etCurrentPassword.setError("Current password is required");
                return;
            }
            if (newPassword.isEmpty()) {
                etNewPassword.setError("New password is required");
                return;
            }
            if (confirmNewPassword.isEmpty()) {
                etConfirmNewPassword.setError("Please confirm new password");
                return;
            }
            if (newPassword.length() < 6) {
                etNewPassword.setError("Password must be at least 6 characters");
                return;
            }
            if (!newPassword.equals(confirmNewPassword)) {
                etConfirmNewPassword.setError("Passwords do not match");
                return;
            }

            boolean changed = dbHelper.changePassword(userId, currentPassword, newPassword);
            if (changed) {
                Toast.makeText(this, "Password changed successfully", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            } else {
                etCurrentPassword.setError("Current password is incorrect");
            }
        });

        btnCancelPassword.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
}
