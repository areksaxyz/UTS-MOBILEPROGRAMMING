package com.example.formtugas

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.formtugas.data.AppDatabase
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

class HistoryActivity : AppCompatActivity() {

    private lateinit var rvHistory: RecyclerView
    private lateinit var tvEmptyHistory: TextView
    private lateinit var bottomNav: BottomNavigationView
    private val db by lazy { AppDatabase.getDatabase(this) }
    private val sessionManager by lazy { SessionManager(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        rvHistory = findViewById(R.id.rvHistory)
        tvEmptyHistory = findViewById(R.id.tvEmptyHistory)
        bottomNav = findViewById(R.id.bottomNavigation)

        rvHistory.layoutManager = LinearLayoutManager(this)

        loadHistory()
        setupBottomNavigation()
    }

    private fun loadHistory() {
        val email = sessionManager.getUserEmail() ?: ""
        lifecycleScope.launch {
            db.registrationDao().getRegistrationsByUser(email).collect { registrations ->
                if (registrations.isEmpty()) {
                    tvEmptyHistory.visibility = View.VISIBLE
                    rvHistory.visibility = View.GONE
                } else {
                    tvEmptyHistory.visibility = View.GONE
                    rvHistory.visibility = View.VISIBLE
                    rvHistory.adapter = RegistrationAdapter(registrations) { reg ->
                        // Optional: Navigate to detail/result
                        val intent = Intent(this@HistoryActivity, ResultActivity::class.java).apply {
                            putExtra("NAME", reg.userName)
                            putExtra("EMAIL", reg.userEmail)
                            putExtra("PHONE", reg.userPhone)
                            putExtra("GENDER", reg.userGender)
                            putExtra("SEMINAR", reg.seminarTitle)
                        }
                        startActivity(intent)
                    }
                }
            }
        }
    }

    private fun setupBottomNavigation() {
        bottomNav.selectedItemId = R.id.nav_history
        bottomNav.labelVisibilityMode = com.google.android.material.navigation.NavigationBarView.LABEL_VISIBILITY_LABELED
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
                R.id.nav_history -> true
                R.id.nav_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    finish()
                    true
                }
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
}
