package com.example.formtugas

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class ResultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        val tvName = findViewById<TextView>(R.id.tvResultName)
        val tvEmail = findViewById<TextView>(R.id.tvResultEmail)
        val tvPhone = findViewById<TextView>(R.id.tvResultPhone)
        val tvGender = findViewById<TextView>(R.id.tvResultGender)
        val tvSeminar = findViewById<TextView>(R.id.tvResultSeminar)
        val btnBackHome = findViewById<Button>(R.id.btnBackHome)
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigation)

        // Gunakan key yang konsisten dengan SeminarActivity
        val name = intent.getStringExtra("NAME") ?: "-"
        val email = intent.getStringExtra("EMAIL") ?: "-"
        val phone = intent.getStringExtra("PHONE") ?: "-"
        val gender = intent.getStringExtra("GENDER") ?: "-"
        val seminar = intent.getStringExtra("SEMINAR") ?: "-"

        tvName.text = "Nama: $name"
        tvEmail.text = "Email: $email"
        tvPhone.text = "Nomor HP: $phone"
        tvGender.text = "Jenis Kelamin: $gender"
        tvSeminar.text = "Seminar: $seminar"

        btnBackHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }

        bottomNav.selectedItemId = R.id.nav_home
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    startActivity(intent)
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
                R.id.nav_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    true
                }
                R.id.nav_logout -> {
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                    true
                }
                else -> false
            }
        }
    }
}