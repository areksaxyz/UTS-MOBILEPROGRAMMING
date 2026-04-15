package com.example.formtugas

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.formtugas.data.AppDatabase
import com.example.formtugas.data.User
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var tilEmail: TextInputLayout
    private lateinit var tilPassword: TextInputLayout
    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var btnLogin: Button
    private lateinit var tvGoRegister: TextView
    private lateinit var loginCard: android.view.View

    private val db by lazy { AppDatabase.getDatabase(this) }
    private val sessionManager by lazy { SessionManager(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initViews()
        showEntryAnimation()

        btnLogin.setOnClickListener {
            playButtonAnimation(btnLogin)
            if (validateLogin()) {
                performLogin()
            } else {
                shakeForm()
            }
        }

        btnLogin.setOnLongClickListener {
            Toast.makeText(this, "Gesture long press pada tombol Login", Toast.LENGTH_SHORT).show()
            true
        }

        tvGoRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
    }

    private fun initViews() {
        tilEmail = findViewById(R.id.tilLoginEmail)
        tilPassword = findViewById(R.id.tilLoginPassword)
        etEmail = findViewById(R.id.etLoginEmail)
        etPassword = findViewById(R.id.etLoginPassword)
        btnLogin = findViewById(R.id.btnLogin)
        tvGoRegister = findViewById(R.id.tvGoRegister)
        loginCard = findViewById(R.id.loginCard)
    }

    private fun performLogin() {
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString()

        lifecycleScope.launch {
            val user = db.userDao().getUserByEmail(email)
            if (user != null) {
                if (user.password == password) {
                    sessionManager.saveUser(user.name, user.email)
                    Toast.makeText(this@LoginActivity, "Login berhasil!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    tilPassword.error = "Password salah"
                    shakeForm()
                }
            } else {
                tilEmail.error = "Email tidak terdaftar"
                shakeForm()
            }
        }
    }

    private fun showEntryAnimation() {
        val animation = AnimationUtils.loadAnimation(this, R.anim.fade_slide_up)
        loginCard.startAnimation(animation)
    }

    private fun playButtonAnimation(view: android.view.View) {
        val animation = AnimationUtils.loadAnimation(this, R.anim.button_scale)
        view.startAnimation(animation)
    }

    private fun shakeForm() {
        val animation = AnimationUtils.loadAnimation(this, R.anim.shake)
        loginCard.startAnimation(animation)
    }

    private fun validateLogin(): Boolean {
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString()

        tilEmail.error = null
        tilPassword.error = null

        var valid = true

        if (email.isEmpty()) {
            tilEmail.error = "Email tidak boleh kosong"
            valid = false
        } else if (!email.contains("@")) {
            tilEmail.error = "Email wajib mengandung '@'"
            valid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.error = "Format email tidak valid"
            valid = false
        }

        if (password.isEmpty()) {
            tilPassword.error = "Password tidak boleh kosong"
            valid = false
        }

        return valid
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}
