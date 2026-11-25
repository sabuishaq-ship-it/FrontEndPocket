package com.fepocket.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fepocket.R;
import com.fepocket.adapter.CategoryAdapter;
import com.fepocket.db.DatabaseHelper;
import com.fepocket.model.User;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private CircleImageView civProfile;
    private TextView tvWelcome;
    private RecyclerView rvCategories;
    private DatabaseHelper dbHelper;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        dbHelper = new DatabaseHelper(this);

        toolbar = findViewById(R.id.toolbar);
        civProfile = findViewById(R.id.civProfile);
        tvWelcome = findViewById(R.id.tvWelcome);
        rvCategories = findViewById(R.id.rvCategories);

        setSupportActionBar(toolbar);

        userId = getIntent().getIntExtra("user_id", -1);
        if (userId == -1) {
            SharedPreferences prefs = getSharedPreferences("login_prefs", MODE_PRIVATE);
            userId = prefs.getInt("user_id", -1);
        }

        loadUserProfile();

        rvCategories.setLayoutManager(new GridLayoutManager(this, 2));
        rvCategories.setAdapter(new CategoryAdapter(this, dbHelper.getAllCategories()));

        civProfile.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
            intent.putExtra("user_id", userId);
            startActivity(intent);
        });
    }

    private void loadUserProfile() {
        User user = dbHelper.getUserById(userId);
        if (user != null) {
            String firstName = user.getFullName();
            if (firstName.contains(" ")) {
                firstName = firstName.substring(0, firstName.indexOf(" "));
            }
            tvWelcome.setText("Welcome back, " + firstName + " \uD83D\uDC4B");

            byte[] imageBlob = user.getProfileImage();
            if (imageBlob != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(imageBlob, 0, imageBlob.length);
                civProfile.setImageBitmap(bitmap);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserProfile();
    }
}
