# FormAuth Android App

FormAuth is an Android application built with **Kotlin** and **XML** in Android Studio.  
This project was created for a Mobile Programming assignment and focuses on form handling, input validation, selection controls, animations, and custom UI backgrounds.

## Features

- Login form
- Register form
- Real-time validation
- Email format validation
- Password and confirm password validation
- Gender selection using RadioGroup
- Hobby selection using CheckBox (minimum 3)
- City selection using Spinner
- AlertDialog confirmation before submit
- Long press gesture on buttons
- Activity transition animation
- Shake animation for validation errors
- Custom background images for login and register screens

## Technologies Used

- Kotlin
- XML Layout
- Material Components
- Android Studio

## Project Structure

```text
app/
├── src/main/
│   ├── java/com/example/formtugas/
│   │   ├── LoginActivity.kt
│   │   └── RegisterActivity.kt
│   ├── res/
│   │   ├── anim/
│   │   ├── drawable/
│   │   ├── layout/
│   │   └── values/
│   └── AndroidManifest.xml
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
└── README.md
```

## APK File

The generated debug APK can be found at:
`build/outputs/apk/debug/FormTugas-debug.apk`

## How to Run

1. Open the project in Android Studio
2. Wait for Gradle sync to finish
3. Connect an Android device or start an emulator
4. Click Run

## Validation Rules

### Login
- Email must not be empty
- Email must be in valid format
- Password must not be empty

### Register
- Name must not be empty
- Email must be in valid format
- Password must be at least 6 characters
- Confirm password must match password
- Gender must be selected
- At least 3 hobbies must be selected
- City must be selected

## Author

**Advan**  
Mobile Programming Assignment
