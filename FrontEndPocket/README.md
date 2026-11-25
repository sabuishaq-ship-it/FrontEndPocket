# Front End Academy

An offline Android application for learning and organizing front-end web development concepts (HTML, CSS, JavaScript, Bootstrap, React).

## Features

- **Authentication**: Register, login with remember me, forgot password
- **Dashboard**: Category grid with lesson counts per category
- **Lessons**: Full CRUD (add, edit, delete), search, favorite toggle
- **Lesson Details**: Explanation, code examples, quiz, documentation URL (opens in browser)
- **User Profile**: Edit profile info, change profile image (gallery), change password
- **Offline Storage**: All data stored locally in SQLite
- **Password Security**: PBKDF2 hashing with salt
- **Lifecycle Preservation**: Form data retained on screen rotation

## Screens

1. Welcome Screen
2. Login Screen
3. Register Screen
4. Forgot Password Screen
5. Home Screen (Dashboard)
6. Lessons List Screen (RecyclerView)
7. Lesson Detail Screen
8. Add/Edit Lesson Screen
9. Profile Screen

## Tech Stack

- **Language**: Java
- **Database**: SQLite (SQLiteOpenHelper)
- **UI**: Material Design 3, ConstraintLayout, RecyclerView, CardView
- **Image**: CircleImageView, gallery picker

## Setup

1. Clone the repository
2. Open in Android Studio
3. Sync Gradle and run on emulator or physical device

### Requirements

- Android SDK 24+ (Android 7.0)
- Android Studio Hedgehog or later
- Gradle 8.2+

## Build

```bash
gradlew assembleDebug
```

## Install

```bash
gradlew installDebug
```

## Project Structure

```
app/src/main/java/com/fepocket/
├── adapter/
│   ├── CategoryAdapter.java
│   └── LessonAdapter.java
├── db/
│   └── DatabaseHelper.java
├── model/
│   ├── User.java
│   ├── Category.java
│   └── Lesson.java
└── ui/
    ├── WelcomeActivity.java
    ├── LoginActivity.java
    ├── RegisterActivity.java
    ├── ForgotPasswordActivity.java
    ├── HomeActivity.java
    ├── LessonsActivity.java
    ├── LessonDetailActivity.java
    ├── AddLessonActivity.java
    └── ProfileActivity.java
```

## Database Schema

- **users**: id, full_name, username, email, password (hashed), profile_image
- **categories**: id, category_name, category_icon
- **lessons**: id, category_id (FK), lesson_title, description, explanation, code_example, quiz_question, personal_notes, documentation_url, difficulty, is_favorite

## Seed Data

5 default categories (HTML, CSS, JavaScript, Bootstrap, React) are pre-loaded on first run. No sample lessons — admin adds lessons manually.

## Admin Access

- Username: `admin`
- Password: `admin123`

## Roles

- **Admin**: Full CRUD on lessons, manages categories
- **User**: View lessons, add personal notes, search, favorite

## Upload to GitHub

1. Create a `.gitignore` file:
```
.gradle/
.idea/
build/
app/build/
local.properties
*.iml
.DS_Store
/captures
.externalNativeBuild/
.cxx/
*.apk
*.aab
```

2. Commit and push:
```bash
git init
git add .
git commit -m "Initial commit"
git remote add origin <repo-url>
git push -u origin main
```
