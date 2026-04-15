package com.example.formtugas

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)

    fun saveUser(name: String, email: String) {
        prefs.edit().apply {
            putString("user_name", name)
            putString("user_email", email)
            apply()
        }
    }

    fun getUserName(): String = prefs.getString("user_name", "User") ?: "User"
    fun getUserEmail(): String = prefs.getString("user_email", "") ?: ""

    fun saveProfileImage(uri: String) {
        prefs.edit().putString("profile_image_uri", uri).apply()
    }

    fun getProfileImage(): String? = prefs.getString("profile_image_uri", null)

    fun saveLastRegistration(name: String, email: String, phone: String, gender: String, seminar: String) {
        prefs.edit().apply {
            putString("last_reg_name", name)
            putString("last_reg_email", email)
            putString("last_reg_phone", phone)
            putString("last_reg_gender", gender)
            putString("last_reg_seminar", seminar)
            putString("last_reg_date", java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault()).format(java.util.Date()))
            putBoolean("has_registration", true)
            apply()
        }
    }

    fun hasLastRegistration(): Boolean = prefs.getBoolean("has_registration", false)

    fun getLastRegistration(): Map<String, String> {
        return mapOf(
            "name" to (prefs.getString("last_reg_name", "") ?: ""),
            "email" to (prefs.getString("last_reg_email", "") ?: ""),
            "phone" to (prefs.getString("last_reg_phone", "") ?: ""),
            "gender" to (prefs.getString("last_reg_gender", "") ?: ""),
            "seminar" to (prefs.getString("last_reg_seminar", "") ?: ""),
            "date" to (prefs.getString("last_reg_date", "") ?: "")
        )
    }

    fun logout() {
        prefs.edit().clear().apply()
    }
}