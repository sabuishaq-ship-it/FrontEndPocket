package com.fepocket.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fepocket.R;
import com.fepocket.adapter.LessonAdapter;
import com.fepocket.db.DatabaseHelper;
import com.fepocket.model.Lesson;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class LessonsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView tvToolbarTitle;
    private EditText etSearch;
    private RecyclerView rvLessons;
    private FloatingActionButton fabAdd;
    private DatabaseHelper dbHelper;
    private LessonAdapter lessonAdapter;
    private int categoryId;
    private boolean isAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lessons);

        dbHelper = new DatabaseHelper(this);

        toolbar = findViewById(R.id.toolbar);
        tvToolbarTitle = findViewById(R.id.tvToolbarTitle);
        etSearch = findViewById(R.id.etSearch);
        rvLessons = findViewById(R.id.rvLessons);
        fabAdd = findViewById(R.id.fabAdd);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        categoryId = getIntent().getIntExtra("category_id", -1);
        String categoryName = getIntent().getStringExtra("category_name");
        tvToolbarTitle.setText(categoryName);

        SharedPreferences prefs = getSharedPreferences("login_prefs", MODE_PRIVATE);
        isAdmin = "admin".equals(prefs.getString("role", "user"));

        if (!isAdmin) {
            fabAdd.setVisibility(View.GONE);
        }

        rvLessons.setLayoutManager(new LinearLayoutManager(this));
        loadLessons();

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim();
                List<Lesson> filtered = dbHelper.searchLessons(categoryId, query);
                lessonAdapter.updateList(filtered);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(LessonsActivity.this, AddLessonActivity.class);
            intent.putExtra("category_id", categoryId);
            intent.putExtra("category_name", getIntent().getStringExtra("category_name"));
            startActivity(intent);
        });
    }

    private void loadLessons() {
        List<Lesson> lessons = dbHelper.getLessonsByCategory(categoryId);
        lessonAdapter = new LessonAdapter(this, lessons, dbHelper, isAdmin);
        rvLessons.setAdapter(lessonAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadLessons();
    }
}
