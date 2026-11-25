package com.fepocket.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.fepocket.R;
import com.fepocket.db.DatabaseHelper;
import com.fepocket.model.Lesson;
import com.google.android.material.textfield.TextInputLayout;

public class AddLessonActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView tvToolbarTitle;
    private EditText etLessonTitle, etLessonDescription, etLessonExplanation, etLessonCode, etLessonQuiz, etLessonNotes, etLessonDocUrl;
    private Spinner spDifficulty;
    private Button btnSaveLesson, btnCancelLesson;
    private DatabaseHelper dbHelper;
    private TextInputLayout tilPersonalNotes;
    private int categoryId;
    private int lessonId = -1;
    private boolean isEditMode = false;
    private boolean isAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_lesson);

        dbHelper = new DatabaseHelper(this);

        toolbar = findViewById(R.id.toolbar);
        tvToolbarTitle = findViewById(R.id.tvToolbarTitle);
        etLessonTitle = findViewById(R.id.etLessonTitle);
        etLessonDescription = findViewById(R.id.etLessonDescription);
        etLessonExplanation = findViewById(R.id.etLessonExplanation);
        etLessonCode = findViewById(R.id.etLessonCode);
        etLessonQuiz = findViewById(R.id.etLessonQuiz);
        etLessonNotes = findViewById(R.id.etLessonNotes);
        tilPersonalNotes = findViewById(R.id.tilPersonalNotes);
        etLessonDocUrl = findViewById(R.id.etLessonDocUrl);
        spDifficulty = findViewById(R.id.spDifficulty);
        btnSaveLesson = findViewById(R.id.btnSaveLesson);
        btnCancelLesson = findViewById(R.id.btnCancelLesson);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        SharedPreferences prefs = getSharedPreferences("login_prefs", MODE_PRIVATE);
        isAdmin = "admin".equals(prefs.getString("role", "user"));
        if (!isAdmin) {
            Toast.makeText(this, "Access denied. Admins only.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(AddLessonActivity.this, HomeActivity.class));
            finish();
            return;
        }

        tilPersonalNotes.setVisibility(View.GONE);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.difficulty_levels, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDifficulty.setAdapter(adapter);

        categoryId = getIntent().getIntExtra("category_id", -1);
        lessonId = getIntent().getIntExtra("lesson_id", -1);

        if (lessonId != -1) {
            isEditMode = true;
            tvToolbarTitle.setText("Edit Lesson");
            loadLessonData();
        } else {
            tvToolbarTitle.setText("Add Lesson");
        }

        btnSaveLesson.setOnClickListener(v -> {
            if (validateInputs()) {
                saveLesson();
            }
        });

        btnCancelLesson.setOnClickListener(v -> finish());

        if (savedInstanceState != null) {
            etLessonTitle.setText(savedInstanceState.getString("title", ""));
            etLessonDescription.setText(savedInstanceState.getString("description", ""));
            etLessonExplanation.setText(savedInstanceState.getString("explanation", ""));
            etLessonCode.setText(savedInstanceState.getString("code", ""));
            etLessonQuiz.setText(savedInstanceState.getString("quiz", ""));
            etLessonNotes.setText(savedInstanceState.getString("notes", ""));
            etLessonDocUrl.setText(savedInstanceState.getString("docUrl", ""));
            spDifficulty.setSelection(savedInstanceState.getInt("difficulty_pos", 0));
            isEditMode = savedInstanceState.getBoolean("isEditMode", false);
            lessonId = savedInstanceState.getInt("lessonId", -1);
            categoryId = savedInstanceState.getInt("categoryId", -1);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("title", etLessonTitle.getText().toString());
        outState.putString("description", etLessonDescription.getText().toString());
        outState.putString("explanation", etLessonExplanation.getText().toString());
        outState.putString("code", etLessonCode.getText().toString());
        outState.putString("quiz", etLessonQuiz.getText().toString());
        outState.putString("notes", etLessonNotes.getText().toString());
        outState.putString("docUrl", etLessonDocUrl.getText().toString());
        outState.putInt("difficulty_pos", spDifficulty.getSelectedItemPosition());
        outState.putBoolean("isEditMode", isEditMode);
        outState.putInt("lessonId", lessonId);
        outState.putInt("categoryId", categoryId);
    }

    private void loadLessonData() {
        Lesson lesson = dbHelper.getLessonById(lessonId);
        if (lesson != null) {
            etLessonTitle.setText(lesson.getTitle());
            etLessonDescription.setText(lesson.getDescription());
            etLessonExplanation.setText(lesson.getExplanation());
            etLessonCode.setText(lesson.getCodeExample());
            etLessonQuiz.setText(lesson.getQuizQuestion());
            etLessonNotes.setText(lesson.getPersonalNotes());
            etLessonDocUrl.setText(lesson.getDocumentationUrl());

            String[] difficulties = getResources().getStringArray(R.array.difficulty_levels);
            for (int i = 0; i < difficulties.length; i++) {
                if (difficulties[i].equals(lesson.getDifficulty())) {
                    spDifficulty.setSelection(i);
                    break;
                }
            }
        }
    }

    private boolean validateInputs() {
        if (etLessonTitle.getText().toString().trim().isEmpty()) {
            etLessonTitle.setError("Title is required");
            etLessonTitle.requestFocus();
            return false;
        }
        if (etLessonDescription.getText().toString().trim().isEmpty()) {
            etLessonDescription.setError("Description is required");
            etLessonDescription.requestFocus();
            return false;
        }
        if (etLessonExplanation.getText().toString().trim().isEmpty()) {
            etLessonExplanation.setError("Explanation is required");
            etLessonExplanation.requestFocus();
            return false;
        }
        if (etLessonCode.getText().toString().trim().isEmpty()) {
            etLessonCode.setError("Code example is required");
            etLessonCode.requestFocus();
            return false;
        }
        if (etLessonQuiz.getText().toString().trim().isEmpty()) {
            etLessonQuiz.setError("Quiz question is required");
            etLessonQuiz.requestFocus();
            return false;
        }
        return true;
    }

    private void saveLesson() {
        String title = etLessonTitle.getText().toString().trim();
        String description = etLessonDescription.getText().toString().trim();
        String explanation = etLessonExplanation.getText().toString().trim();
        String code = etLessonCode.getText().toString().trim();
        String quiz = etLessonQuiz.getText().toString().trim();
        String notes = etLessonNotes.getText().toString().trim();
        String docUrl = etLessonDocUrl.getText().toString().trim();
        String difficulty = spDifficulty.getSelectedItem().toString();

        Lesson lesson = new Lesson(categoryId, title, description, explanation, code, quiz, notes, docUrl, difficulty, false);

        if (isEditMode) {
            lesson.setId(lessonId);
            dbHelper.updateLesson(lesson);
            Toast.makeText(this, "Lesson updated", Toast.LENGTH_SHORT).show();
        } else {
            dbHelper.insertLesson(lesson);
            Toast.makeText(this, "Lesson added", Toast.LENGTH_SHORT).show();
        }
        finish();
    }
}
