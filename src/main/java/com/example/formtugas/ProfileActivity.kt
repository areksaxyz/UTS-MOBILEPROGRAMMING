package com.example.formtugas

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class ProfileActivity : AppCompatActivity() {

    private lateinit var ivProfilePicture: ImageView
    private lateinit var tvProfileName: TextView
    private lateinit var tvProfileEmail: TextView
    private lateinit var tvProfileGender: TextView
    private lateinit var tvProfileHobbies: TextView
    private lateinit var tvProfileCity: TextView
    private lateinit var etOldPassword: TextInputEditText
    private lateinit var etNewPassword: TextInputEditText
    private lateinit var etConfirmPassword: TextInputEditText
    private lateinit var tilOldPassword: com.google.android.material.textfield.TextInputLayout
    private lateinit var tilNewPassword: com.google.android.material.textfield.TextInputLayout
    private lateinit var tilConfirmPassword: com.google.android.material.textfield.TextInputLayout
    private lateinit var btnUpdatePassword: Button
    private lateinit var btnChangePicture: Button
    private lateinit var bottomNav: BottomNavigationView
    private val sessionManager by lazy { SessionManager(this) }
    private val db by lazy { com.example.formtugas.data.AppDatabase.getDatabase(this) }

    private val pickImageLauncher = registerForActivityResult(androidx.activity.result.contract.ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            val internalUri = saveImageToInternalStorage(it)
            if (internalUri != null) {
                ivProfilePicture.setImageURI(internalUri)
                sessionManager.saveProfileImage(internalUri.toString())
                Toast.makeText(this, "Foto profil diperbarui", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Gagal menyimpan foto", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveImageToInternalStorage(uri: android.net.Uri): android.net.Uri? {
        return try {
            val inputStream = contentResolver.openInputStream(uri)
            val file = java.io.File(filesDir, "profile_picture.jpg")
            val outputStream = java.io.FileOutputStream(file)
            inputStream?.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
            android.net.Uri.fromFile(file)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        ivProfilePicture = findViewById(R.id.ivProfilePicture)
        tvProfileName = findViewById(R.id.tvProfileName)
        tvProfileEmail = findViewById(R.id.tvProfileEmail)
        tvProfileGender = findViewById(R.id.tvProfileGender)
        tvProfileHobbies = findViewById(R.id.tvProfileHobbies)
        tvProfileCity = findViewById(R.id.tvProfileCity)
        
        etOldPassword = findViewById(R.id.etOldPassword)
        etNewPassword = findViewById(R.id.etNewPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        
        tilOldPassword = findViewById(R.id.tilOldPassword)
        tilNewPassword = findViewById(R.id.tilNewPassword)
        tilConfirmPassword = findViewById(R.id.tilConfirmPassword)

        btnUpdatePassword = findViewById(R.id.btnUpdatePassword)
        btnChangePicture = findViewById(R.id.btnChangePicture)
        bottomNav = findViewById(R.id.bottomNavigation)

        // Set default menu selection
        bottomNav.selectedItemId = R.id.nav_profile

        // Load data dari SessionManager
        val email = sessionManager.getUserEmail()
        tvProfileName.text = sessionManager.getUserName()
        tvProfileEmail.text = email

        // Load detail user dari Database Room
        lifecycleScope.launch {
            val user = db.userDao().getUserByEmail(email)
            if (user != null) {
                tvProfileGender.text = user.gender ?: "-"
                tvProfileHobbies.text = user.hobbies ?: "-"
                tvProfileCity.text = user.city ?: "-"
            }
        }
        
        // Load profile image
        sessionManager.getProfileImage()?.let {
            try {
                ivProfilePicture.setImageURI(android.net.Uri.parse(it))
            } catch (e: Exception) {
                ivProfilePicture.setImageResource(android.R.drawable.ic_menu_gallery) // Gunakan drawable sistem sebagai default
            }
        }

        btnUpdatePassword.setOnClickListener {
            validateAndUpdatePassword()
        }

        btnChangePicture.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_seminar -> {
                    startActivity(Intent(this, SeminarListActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_history -> {
                    startActivity(Intent(this, HistoryActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_profile -> true
                R.id.nav_logout -> {
                    showLogoutConfirmation()
                    true
                }
                else -> false
            }
        }
    }

    private fun showLogoutConfirmation() {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Apakah Anda yakin ingin keluar?")
            .setPositiveButton("Ya") { _, _ ->
                sessionManager.logout()
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
            .setNegativeButton("Tidak", null)
            .show()
    }

    private fun validateAndUpdatePassword() {
        val oldPass = etOldPassword.text.toString()
        val newPass = etNewPassword.text.toString()
        val confirmPass = etConfirmPassword.text.toString()

        var isValid = true

        // Reset errors
        tilOldPassword.error = null
        tilNewPassword.error = null
        tilConfirmPassword.error = null

        if (oldPass.isEmpty()) {
            tilOldPassword.error = "Password lama wajib diisi"
            isValid = false
        }

        // Validate New Password (Same rules as RegisterActivity)
        val hasUppercase = newPass.any { it.isUpperCase() }
        val hasSpecialChar = newPass.any { !it.isLetterOrDigit() }

        when {
            newPass.isEmpty() -> {
                tilNewPassword.error = "Password baru tidak boleh kosong"
                isValid = false
            }
            newPass.length < 8 -> {
                tilNewPassword.error = "Password minimal 8 karakter"
                isValid = false
            }
            !hasUppercase -> {
                tilNewPassword.error = "Password wajib ada 1 huruf kapital"
                isValid = false
            }
            !hasSpecialChar -> {
                tilNewPassword.error = "Password wajib ada 1 karakter spesial"
                isValid = false
            }
            newPass == oldPass -> {
                tilNewPassword.error = "Password baru tidak boleh sama dengan password lama"
                isValid = false
            }
        }

        if (confirmPass != newPass) {
            tilConfirmPassword.error = "Konfirmasi password tidak cocok"
            isValid = false
        }

        if (isValid) {
            lifecycleScope.launch {
                val user = db.userDao().getUserByEmail(sessionManager.getUserEmail())
                if (user != null) {
                    if (user.password == oldPass) {
                        db.userDao().updatePassword(user.email, newPass)
                        Toast.makeText(this@ProfileActivity, "Password berhasil diperbarui!", Toast.LENGTH_SHORT).show()
                        etOldPassword.text?.clear()
                        etNewPassword.text?.clear()
                        etConfirmPassword.text?.clear()
                    } else {
                        tilOldPassword.error = "Password lama salah"
                    }
                }
            }
        }
    }
}