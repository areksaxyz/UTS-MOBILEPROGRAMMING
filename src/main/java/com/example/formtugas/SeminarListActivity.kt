package com.example.formtugas

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.formtugas.data.AppDatabase
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

class SeminarListActivity : AppCompatActivity() {

    private lateinit var rvAllSeminars: RecyclerView
    private lateinit var bottomNav: BottomNavigationView
    private val db by lazy { AppDatabase.getDatabase(this) }
    private val sessionManager by lazy { SessionManager(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seminar_list)

        rvAllSeminars = findViewById(R.id.rvAllSeminars)
        bottomNav = findViewById(R.id.bottomNavigation)

        setupSeminarList()
        setupBottomNavigation()
    }

    private fun setupSeminarList() {
        lifecycleScope.launch {
            db.seminarDao().getAllSeminars().collect { list ->
                rvAllSeminars.layoutManager = LinearLayoutManager(this@SeminarListActivity)
                rvAllSeminars.adapter = SeminarAdapter(list) { seminar ->
                    val intent = Intent(this@SeminarListActivity, SeminarDetailActivity::class.java).apply {
                        putExtra("SEMINAR", seminar)
                    }
                    startActivity(intent)
                }
            }
        }
    }

    private fun setupBottomNavigation() {
        bottomNav.selectedItemId = R.id.nav_seminar
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_seminar -> true
                R.id.nav_history -> {
                    startActivity(Intent(this, HistoryActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    finish()
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