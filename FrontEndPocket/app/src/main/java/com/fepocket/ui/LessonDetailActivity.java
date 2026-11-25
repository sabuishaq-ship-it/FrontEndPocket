package com.fepocket.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.fepocket.R;
import com.fepocket.db.DatabaseHelper;
import com.fepocket.model.Category;
import com.fepocket.model.Lesson;
import com.google.android.material.card.MaterialCardView;

public class LessonDetailActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView tvDetailTitle, tvDetailCategory, tvDetailExplanation, tvDetailCode, tvDetailQuiz, tvDetailNotes, tvDetailDocLink;
    private Button btnEditLesson, btnDeleteLesson, btnOpenDoc;
    private ImageView ivEditNotes;
    private MaterialCardView cardNotes;
    private DatabaseHelper dbHelper;
    private int lessonId;
    private Lesson lesson;
    private boolean isAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson_detail);

        dbHelper = new DatabaseHelper(this);

        toolbar = findViewById(R.id.toolbar);
        tvDetailTitle = findViewById(R.id.tvDetailTitle);
        tvDetailCategory = findViewById(R.id.tvDetailCategory);
        tvDetailExplanation = findViewById(R.id.tvDetailExplanation);
        tvDetailCode = findViewById(R.id.tvDetailCode);
        tvDetailQuiz = findViewById(R.id.tvDetailQuiz);
        tvDetailNotes = findViewById(R.id.tvDetailNotes);
        tvDetailDocLink = findViewById(R.id.tvDetailDocLink);
        btnEditLesson = findViewById(R.id.btnEditLesson);
        btnDeleteLesson = findViewById(R.id.btnDeleteLesson);
        btnOpenDoc = findViewById(R.id.btnOpenDoc);
        cardNotes = findViewById(R.id.cardNotes);
        ivEditNotes = findViewById(R.id.ivEditNotes);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        isAdmin = getIntent().getBooleanExtra("is_admin", false);
        if (isAdmin) {
            btnEditLesson.setVisibility(View.VISIBLE);
            btnDeleteLesson.setVisibility(View.VISIBLE);
            cardNotes.setVisibility(View.GONE);
        } else {
            btnEditLesson.setVisibility(View.GONE);
            btnDeleteLesson.setVisibility(View.GONE);
            cardNotes.setVisibility(View.VISIBLE);
            ivEditNotes.setVisibility(View.VISIBLE);
        }

        lesson = (Lesson) getIntent().getSerializableExtra("lesson");
        if (lesson != null) {
            lessonId = lesson.getId();
            displayLesson();
        } else {
            lessonId = getIntent().getIntExtra("lesson_id", -1);
            loadLesson();
        }

        btnEditLesson.setOnClickListener(v -> {
            Intent intent = new Intent(LessonDetailActivity.this, AddLessonActivity.class);
            intent.putExtra("lesson_id", lessonId);
            intent.putExtra("category_id", lesson.getCategoryId());
            startActivity(intent);
        });

        btnDeleteLesson.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Delete Lesson")
                    .setMessage("Are you sure you want to delete this lesson?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        dbHelper.deleteLesson(lessonId);
                        Toast.makeText(this, "Lesson deleted", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        btnOpenDoc.setOnClickListener(v -> {
            String url = lesson.getDocumentationUrl();
            if (url == null || url.isEmpty()) {
                Toast.makeText(this, "No documentation URL available", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                url = "https://" + url;
            }
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browserIntent);
        });

        ivEditNotes.setOnClickListener(v -> showEditNotesDialog());
    }

    private void showEditNotesDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Personal Notes");

        EditText input = new EditText(this);
        input.setText(lesson.getPersonalNotes());
        input.setPadding(32, 16, 32, 16);
        builder.setView(input);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String newNotes = input.getText().toString().trim();
            dbHelper.updatePersonalNotes(lesson.getId(), newNotes);
            lesson.setPersonalNotes(newNotes);
            tvDetailNotes.setText(newNotes);
            Toast.makeText(this, "Notes updated", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void displayLesson() {
        if (lesson != null) {
            tvDetailTitle.setText(lesson.getTitle());
            tvDetailExplanation.setText(lesson.getExplanation());
            tvDetailCode.setText(lesson.getCodeExample());
            tvDetailQuiz.setText(lesson.getQuizQuestion());
            tvDetailNotes.setText(lesson.getPersonalNotes());
            tvDetailDocLink.setText(lesson.getDocumentationUrl());

            Category category = dbHelper.getCategoryById(lesson.getCategoryId());
            tvDetailCategory.setText(category != null ? category.getName() : "Unknown");
        }
    }

    private void loadLesson() {
        lesson = dbHelper.getLessonById(lessonId);
        displayLesson();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadLesson();
    }
}
