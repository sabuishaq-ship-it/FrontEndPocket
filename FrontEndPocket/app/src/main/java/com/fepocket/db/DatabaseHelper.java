package com.fepocket.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Base64;

import com.fepocket.model.Category;
import com.fepocket.model.Lesson;
import com.fepocket.model.User;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "frontend_pocket.db";
    private static final int DATABASE_VERSION = 7;

    private static final String TABLE_USERS = "users";
    private static final String TABLE_CATEGORIES = "categories";
    private static final String TABLE_LESSONS = "lessons";

    private static final String COLUMN_ID = "id";

    private static final String COLUMN_FULL_NAME = "full_name";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_PROFILE_IMAGE = "profile_image";
    private static final String COLUMN_ROLE = "role";

    private static final String COLUMN_CATEGORY_NAME = "category_name";
    private static final String COLUMN_CATEGORY_ICON = "category_icon";

    private static final String COLUMN_CATEGORY_ID = "category_id";
    private static final String COLUMN_LESSON_TITLE = "lesson_title";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_EXPLANATION = "explanation";
    private static final String COLUMN_CODE_EXAMPLE = "code_example";
    private static final String COLUMN_QUIZ_QUESTION = "quiz_question";
    private static final String COLUMN_PERSONAL_NOTES = "personal_notes";
    private static final String COLUMN_DOCUMENTATION_URL = "documentation_url";
    private static final String COLUMN_DIFFICULTY = "difficulty";
    private static final String COLUMN_IS_FAVORITE = "is_favorite";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_FULL_NAME + " TEXT, "
                + COLUMN_USERNAME + " TEXT UNIQUE, "
                + COLUMN_EMAIL + " TEXT UNIQUE, "
                + COLUMN_PASSWORD + " TEXT, "
                + COLUMN_PROFILE_IMAGE + " BLOB, "
                + COLUMN_ROLE + " TEXT DEFAULT 'user')";

        String CREATE_CATEGORIES_TABLE = "CREATE TABLE " + TABLE_CATEGORIES + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_CATEGORY_NAME + " TEXT, "
                + COLUMN_CATEGORY_ICON + " TEXT)";

        String CREATE_LESSONS_TABLE = "CREATE TABLE " + TABLE_LESSONS + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_CATEGORY_ID + " INTEGER, "
                + COLUMN_LESSON_TITLE + " TEXT, "
                + COLUMN_DESCRIPTION + " TEXT, "
                + COLUMN_EXPLANATION + " TEXT, "
                + COLUMN_CODE_EXAMPLE + " TEXT, "
                + COLUMN_QUIZ_QUESTION + " TEXT, "
                + COLUMN_PERSONAL_NOTES + " TEXT, "
                + COLUMN_DOCUMENTATION_URL + " TEXT, "
                + COLUMN_DIFFICULTY + " TEXT, "
                + COLUMN_IS_FAVORITE + " INTEGER DEFAULT 0, "
                + "FOREIGN KEY (" + COLUMN_CATEGORY_ID + ") REFERENCES "
                + TABLE_CATEGORIES + "(" + COLUMN_ID + "))";

        db.execSQL(CREATE_USERS_TABLE);
        db.execSQL(CREATE_CATEGORIES_TABLE);
        db.execSQL(CREATE_LESSONS_TABLE);

        seedAdmin(db);
        seedCategories(db);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LESSONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    private void seedAdmin(SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_FULL_NAME, "Admin");
        values.put(COLUMN_USERNAME, "admin");
        values.put(COLUMN_EMAIL, "admin@fepocket.com");
        values.put(COLUMN_PASSWORD, hashPassword("admin123"));
        values.put(COLUMN_ROLE, "admin");
        db.insertWithOnConflict(TABLE_USERS, null, values, SQLiteDatabase.CONFLICT_IGNORE);
    }

    private void seedCategories(SQLiteDatabase db) {
        String[] categories = {"HTML", "CSS", "JavaScript", "Bootstrap", "React"};
        String[] icons = {"ic_html", "ic_css", "ic_javascript", "ic_bootstrap", "ic_react"};
        for (int i = 0; i < categories.length; i++) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_CATEGORY_NAME, categories[i]);
            values.put(COLUMN_CATEGORY_ICON, icons[i]);
            db.insertWithOnConflict(TABLE_CATEGORIES, null, values, SQLiteDatabase.CONFLICT_IGNORE);
        }
    }

    public boolean isAdmin(int userId) {
        return "admin".equals(getUserRole(userId));
    }

    public String getUserRole(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{COLUMN_ROLE},
                COLUMN_ID + "=?", new String[]{String.valueOf(userId)},
                null, null, null);
        String role = "user";
        if (cursor != null && cursor.moveToFirst()) {
            String r = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ROLE));
            role = r != null ? r : "user";
            cursor.close();
        }
        db.close();
        return role;
    }

    private static final int SALT_LENGTH = 16;
    private static final int HASH_LENGTH = 32;
    private static final int PBKDF2_ITERATIONS = 10000;

    private String hashPassword(String password) {
        try {
            byte[] salt = new byte[SALT_LENGTH];
            new SecureRandom().nextBytes(salt);
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, PBKDF2_ITERATIONS, HASH_LENGTH * 8);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] hash = factory.generateSecret(spec).getEncoded();
            return Base64.encodeToString(salt, Base64.DEFAULT) + ":" + Base64.encodeToString(hash, Base64.DEFAULT);
        } catch (Exception e) {
            throw new RuntimeException("Password hashing failed", e);
        }
    }

    private boolean checkPassword(String password, String storedHash) {
        try {
            String[] parts = storedHash.split(":");
            byte[] salt = Base64.decode(parts[0], Base64.DEFAULT);
            byte[] storedHashBytes = Base64.decode(parts[1], Base64.DEFAULT);
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, PBKDF2_ITERATIONS, HASH_LENGTH * 8);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] computedHash = factory.generateSecret(spec).getEncoded();
            return MessageDigest.isEqual(computedHash, storedHashBytes);
        } catch (Exception e) {
            return false;
        }
    }

    // ==================== USER METHODS ====================

    public long insertUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_FULL_NAME, user.getFullName());
        values.put(COLUMN_USERNAME, user.getUsername());
        values.put(COLUMN_EMAIL, user.getEmail());
        values.put(COLUMN_PASSWORD, hashPassword(user.getPassword()));
        values.put(COLUMN_PROFILE_IMAGE, user.getProfileImage());
        values.put(COLUMN_ROLE, "user");
        long result = db.insert(TABLE_USERS, null, values);
        db.close();
        return result;
    }

    public User loginUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, null,
                COLUMN_EMAIL + "=?", new String[]{email},
                null, null, null);
        User user = null;
        if (cursor != null && cursor.moveToFirst()) {
            user = cursorToUser(cursor);
            if (checkPassword(password, user.getPassword())) {
                cursor.close();
                db.close();
                return user;
            }
            cursor.close();
        }
        db.close();
        return null;
    }

    public boolean resetPasswordByEmail(String email, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PASSWORD, hashPassword(newPassword));
        int rows = db.update(TABLE_USERS, values,
                COLUMN_EMAIL + "=?", new String[]{email});
        db.close();
        return rows > 0;
    }

    public boolean isEmailExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{COLUMN_ID},
                COLUMN_EMAIL + "=?", new String[]{email},
                null, null, null);
        boolean exists = cursor != null && cursor.getCount() > 0;
        if (cursor != null) cursor.close();
        db.close();
        return exists;
    }

    public boolean isUsernameExists(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{COLUMN_ID},
                COLUMN_USERNAME + "=?", new String[]{username},
                null, null, null);
        boolean exists = cursor != null && cursor.getCount() > 0;
        if (cursor != null) cursor.close();
        db.close();
        return exists;
    }

    public User getUserById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, null,
                COLUMN_ID + "=?", new String[]{String.valueOf(id)},
                null, null, null);
        User user = null;
        if (cursor != null && cursor.moveToFirst()) {
            user = cursorToUser(cursor);
            cursor.close();
        }
        db.close();
        return user;
    }

    public int updateUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_FULL_NAME, user.getFullName());
        values.put(COLUMN_USERNAME, user.getUsername());
        values.put(COLUMN_EMAIL, user.getEmail());
        values.put(COLUMN_PROFILE_IMAGE, user.getProfileImage());
        int rows = db.update(TABLE_USERS, values,
                COLUMN_ID + "=?", new String[]{String.valueOf(user.getId())});
        db.close();
        return rows;
    }

    public boolean changePassword(int userId, String oldPassword, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{COLUMN_PASSWORD},
                COLUMN_ID + "=?", new String[]{String.valueOf(userId)},
                null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            String currentHash = cursor.getString(0);
            cursor.close();
            if (checkPassword(oldPassword, currentHash)) {
                ContentValues values = new ContentValues();
                values.put(COLUMN_PASSWORD, hashPassword(newPassword));
                int rows = db.update(TABLE_USERS, values,
                        COLUMN_ID + "=?", new String[]{String.valueOf(userId)});
                db.close();
                return rows > 0;
            }
        }
        if (cursor != null) cursor.close();
        db.close();
        return false;
    }

    // ==================== CATEGORY METHODS ====================

    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT c." + COLUMN_ID + ", c." + COLUMN_CATEGORY_NAME
                + ", c." + COLUMN_CATEGORY_ICON
                + ", (SELECT COUNT(*) FROM " + TABLE_LESSONS + " l WHERE l."
                + COLUMN_CATEGORY_ID + " = c." + COLUMN_ID + ") AS lesson_count"
                + " FROM " + TABLE_CATEGORIES + " c";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Category category = new Category(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY_ICON)),
                        cursor.getInt(cursor.getColumnIndex("lesson_count"))
                );
                categories.add(category);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return categories;
    }

    public Category getCategoryById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CATEGORIES, null,
                COLUMN_ID + "=?", new String[]{String.valueOf(id)},
                null, null, null);
        Category category = null;
        if (cursor != null && cursor.moveToFirst()) {
            category = new Category(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY_ICON)),
                    0
            );
            cursor.close();
        }
        db.close();
        return category;
    }

    // ==================== LESSON METHODS ====================

    public long insertLesson(Lesson lesson) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = lessonToContentValues(lesson);
        long result = db.insert(TABLE_LESSONS, null, values);
        db.close();
        return result;
    }

    public List<Lesson> getLessonsByCategory(int categoryId) {
        List<Lesson> lessons = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_LESSONS, null,
                COLUMN_CATEGORY_ID + "=?", new String[]{String.valueOf(categoryId)},
                null, null, COLUMN_ID + " ASC");
        if (cursor != null && cursor.moveToFirst()) {
            do {
                lessons.add(cursorToLesson(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return lessons;
    }

    public List<Lesson> searchLessons(int categoryId, String query) {
        List<Lesson> lessons = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COLUMN_CATEGORY_ID + "=? AND " + COLUMN_LESSON_TITLE + " LIKE ?";
        String[] selectionArgs = new String[]{String.valueOf(categoryId), "%" + query + "%"};
        Cursor cursor = db.query(TABLE_LESSONS, null, selection, selectionArgs,
                null, null, COLUMN_ID + " ASC");
        if (cursor != null && cursor.moveToFirst()) {
            do {
                lessons.add(cursorToLesson(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return lessons;
    }

    public Lesson getLessonById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_LESSONS, null,
                COLUMN_ID + "=?", new String[]{String.valueOf(id)},
                null, null, null);
        Lesson lesson = null;
        if (cursor != null && cursor.moveToFirst()) {
            lesson = cursorToLesson(cursor);
            cursor.close();
        }
        db.close();
        return lesson;
    }

    public int updateLesson(Lesson lesson) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = lessonToContentValues(lesson);
        int rows = db.update(TABLE_LESSONS, values,
                COLUMN_ID + "=?", new String[]{String.valueOf(lesson.getId())});
        db.close();
        return rows;
    }

    public void deleteLesson(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_LESSONS, COLUMN_ID + "=?",
                new String[]{String.valueOf(id)});
        db.close();
    }

    public void updatePersonalNotes(int lessonId, String notes) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PERSONAL_NOTES, notes);
        db.update(TABLE_LESSONS, values,
                COLUMN_ID + "=?", new String[]{String.valueOf(lessonId)});
        db.close();
    }

    public void toggleFavorite(int lessonId, boolean isFavorite) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_IS_FAVORITE, isFavorite ? 1 : 0);
        db.update(TABLE_LESSONS, values,
                COLUMN_ID + "=?", new String[]{String.valueOf(lessonId)});
        db.close();
    }

    public List<Lesson> getFavoriteLessons() {
        List<Lesson> lessons = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_LESSONS, null,
                COLUMN_IS_FAVORITE + "=?", new String[]{"1"},
                null, null, COLUMN_ID + " ASC");
        if (cursor != null && cursor.moveToFirst()) {
            do {
                lessons.add(cursorToLesson(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return lessons;
    }

    public int getLessonCount(int categoryId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM " + TABLE_LESSONS + " WHERE " + COLUMN_CATEGORY_ID + "=?",
                new String[]{String.valueOf(categoryId)});
        int count = 0;
        if (cursor != null && cursor.moveToFirst()) {
            count = cursor.getInt(0);
            cursor.close();
        }
        db.close();
        return count;
    }

    // ==================== HELPER METHODS ====================

    private User cursorToUser(Cursor cursor) {
        User user = new User();
        user.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
        user.setFullName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FULL_NAME)));
        user.setUsername(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME)));
        user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)));
        user.setPassword(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD)));
        user.setProfileImage(cursor.getBlob(cursor.getColumnIndexOrThrow(COLUMN_PROFILE_IMAGE)));
        return user;
    }

    private Lesson cursorToLesson(Cursor cursor) {
        Lesson lesson = new Lesson();
        lesson.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
        lesson.setCategoryId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY_ID)));
        lesson.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LESSON_TITLE)));
        lesson.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)));
        lesson.setExplanation(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EXPLANATION)));
        lesson.setCodeExample(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CODE_EXAMPLE)));
        lesson.setQuizQuestion(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_QUIZ_QUESTION)));
        lesson.setPersonalNotes(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PERSONAL_NOTES)));
        lesson.setDocumentationUrl(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DOCUMENTATION_URL)));
        lesson.setDifficulty(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DIFFICULTY)));
        lesson.setFavorite(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_FAVORITE)) == 1);
        return lesson;
    }

    private ContentValues lessonToContentValues(Lesson lesson) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_CATEGORY_ID, lesson.getCategoryId());
        values.put(COLUMN_LESSON_TITLE, lesson.getTitle());
        values.put(COLUMN_DESCRIPTION, lesson.getDescription());
        values.put(COLUMN_EXPLANATION, lesson.getExplanation());
        values.put(COLUMN_CODE_EXAMPLE, lesson.getCodeExample());
        values.put(COLUMN_QUIZ_QUESTION, lesson.getQuizQuestion());
        values.put(COLUMN_PERSONAL_NOTES, lesson.getPersonalNotes());
        values.put(COLUMN_DOCUMENTATION_URL, lesson.getDocumentationUrl());
        values.put(COLUMN_DIFFICULTY, lesson.getDifficulty());
        values.put(COLUMN_IS_FAVORITE, lesson.isFavorite() ? 1 : 0);
        return values;
    }
}
