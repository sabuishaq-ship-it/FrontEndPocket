# Front-End Pocket - Project Structure

```
FrontEndPocket/
│
├── build.gradle                              # Root build configuration
├── settings.gradle                           # Project settings & repositories
├── gradle.properties                         # Gradle JVM properties
│
├── gradle/
│   └── wrapper/
│       └── gradle-wrapper.properties          # Gradle version 8.5
│
└── app/
    ├── build.gradle                          # App dependencies (Material 3, CardView, etc.)
    ├── proguard-rules.pro
    │
    └── src/main/
        ├── AndroidManifest.xml               # 9 activities, permissions
        │
        ├── java/com/fepocket/
        │   │
        │   ├── adapter/
        │   │   ├── CategoryAdapter.java      # Category cards adapter
        │   │   └── LessonAdapter.java        # Lesson cards adapter
        │   │
        │   ├── db/
        │   │   └── DatabaseHelper.java       # SQLite DB (users, categories, lessons)
        │   │
        │   ├── model/
        │   │   ├── User.java                 # User model (Serializable)
        │   │   ├── Category.java             # Category model (Serializable)
        │   │   └── Lesson.java               # Lesson model (Serializable)
        │   │
        │   └── ui/
        │       ├── WelcomeActivity.java      # Animated welcome screen
        │       ├── LoginActivity.java        # Login + Remember Me + Forgot Password link
        │       ├── ForgotPasswordActivity.java # Password reset by email
        │       ├── RegisterActivity.java     # Registration form
        │       ├── HomeActivity.java         # Tech cards grid + profile avatar
        │       ├── LessonsActivity.java      # Lesson list + search + FAB
        │       ├── LessonDetailActivity.java # Full lesson viewer
        │       ├── AddLessonActivity.java    # Add/Edit lesson form
        │       └── ProfileActivity.java      # Profile + stats + settings
        │
        └── res/
            │
            ├── drawable/
            │   ├── bg_welcome.xml            # Welcome gradient background
            │   ├── bg_button_rounded.xml     # Rounded button shape
            │   ├── bg_card_rounded.xml       # Rounded card shape
            │   ├── bg_difficulty.xml         # Difficulty badge background
            │   ├── ic_logo.xml               # App logo vector
            │   ├── ic_html.xml               # HTML icon
            │   ├── ic_css.xml                # CSS icon
            │   ├── ic_javascript.xml         # JavaScript icon
            │   ├── ic_bootstrap.xml          # Bootstrap icon
            │   ├── ic_react.xml              # React icon
            │   ├── ic_favorite_filled.xml    # Filled heart icon
            │   ├── ic_favorite_border.xml    # Border heart icon
            │   ├── ic_launcher_background.xml
            │   └── ic_launcher_foreground.xml
            │
            ├── layout/
            │   ├── activity_welcome.xml      # Welcome screen layout
            │   ├── activity_login.xml        # Login screen layout + Forgot Password link
            │   ├── activity_forgot_password.xml # Forgot Password screen layout
            │   ├── activity_register.xml     # Register screen layout
            │   ├── activity_home.xml         # Home screen layout
            │   ├── activity_lessons.xml      # Lessons screen layout
            │   ├── activity_lesson_detail.xml # Lesson detail layout
            │   ├── activity_add_lesson.xml   # Add/Edit lesson layout
            │   ├── activity_profile.xml      # Profile screen layout
            │   ├── item_category.xml         # Category card item
            │   ├── item_lesson.xml           # Lesson card item
            │   ├── dialog_edit_profile.xml   # Edit profile dialog
            │   └── dialog_change_password.xml # Change password dialog
            │
            ├── values/
            │   ├── colors.xml               # Color palette (primary, tech colors)
            │   ├── strings.xml              # All string resources
            │   ├── arrays.xml               # Difficulty levels array
            │   └── themes.xml               # Material Design 3 theme
            │
            └── mipmap-anydpi-v26/
                ├── ic_launcher.xml           # Adaptive launcher icon
                └── ic_launcher_round.xml     # Round launcher icon
```

## Summary

| Category | Count |
|----------|-------|
| Java Classes | 12 |
| XML Layouts | 13 |
| Drawables | 14 |
| Value Resources | 4 |
| Build Files | 6 |
| Other | 4 |
| **Total** | **53 files** |
