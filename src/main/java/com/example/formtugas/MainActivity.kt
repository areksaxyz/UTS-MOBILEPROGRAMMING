package com.example.formtugas

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.formtugas.data.AppDatabase
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch
import com.google.android.material.card.MaterialCardView

class MainActivity : AppCompatActivity() {

    private lateinit var tvWelcomeName: TextView
    private lateinit var btnRegisterSeminar: Button
    private lateinit var bottomNav: BottomNavigationView
    private lateinit var rvSeminars: RecyclerView
    private val sessionManager by lazy { SessionManager(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvWelcomeName = findViewById(R.id.tvWelcomeName)
        btnRegisterSeminar = findViewById(R.id.btnRegisterSeminar)
        bottomNav = findViewById(R.id.bottomNavigation)
        rvSeminars = findViewById(R.id.rvSeminars)

        // Ambil data user dari SessionManager
        val userName = sessionManager.getUserName()
        tvWelcomeName.text = "Halo, $userName"

        setupSeminarList()
        updateLastRegistrationUI()

        btnRegisterSeminar.setOnClickListener {
            startActivity(Intent(this, SeminarActivity::class.java))
        }

        bottomNav.selectedItemId = R.id.nav_home

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true
                R.id.nav_seminar -> {
                    startActivity(Intent(this, SeminarListActivity::class.java))
                    true
                }
                R.id.nav_history -> {
                    startActivity(Intent(this, HistoryActivity::class.java))
                    true
                }
                R.id.nav_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
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

    private fun setupSeminarList() {
        findViewById<TextView>(R.id.tvSeeAllSeminars).setOnClickListener {
            startActivity(Intent(this, SeminarListActivity::class.java))
        }
        val email = sessionManager.getUserEmail()
        val seminarDao = AppDatabase.getDatabase(this).seminarDao()

        lifecycleScope.launch {
            seminarDao.getAllSeminars().collect { list ->
                if (list.isEmpty()) {
                    val dummySeminars = listOf(
                        Seminar(
                            title = "Web Development", category = "Teknologi", date = "12 Mei 2026", time = "09:00 - 12:00",
                            location = "Auditorium Utama Universitas Indonesia", address = "Gedung Perpustakaan Pusat UI, Depok, Jawa Barat",
                            summary = "Belajar dasar dan tren web development modern.",
                            detail = "Materi meliputi HTML5, CSS3, JavaScript ES6, dan pengenalan framework React.",
                            speaker = "Andi Saputra, S.Kom", quota = 100, status = "Tersedia",
                            imageResId = R.drawable.web
                        ),
                        Seminar(
                            title = "Mobile Programming", category = "Teknologi", date = "15 Mei 2026", time = "13:00 - 16:00",
                            location = "Google Developers Space Indonesia", address = "Level 4, Pacific Century Place, SCBD, Jakarta Selatan",
                            summary = "Pengenalan pengembangan aplikasi Android.",
                            detail = "Mempelajari Kotlin, XML Layout, Activity Lifecycle, dan dasar-dasar API.",
                            speaker = "Budi Prasetyo, M.T", quota = 50, status = "Tersedia",
                            imageResId = R.drawable.android
                        ),
                        Seminar(
                            title = "UI/UX Design", category = "Desain", date = "18 Mei 2026", time = "10:00 - 12:00",
                            location = "Digital Lounge (DiLo) Bandung", address = "Gedung Bale Motekar, Jl. Banda No. 40, Bandung",
                            summary = "Prinsip desain antarmuka dan pengalaman pengguna.",
                            detail = "Figma, Wireframing, Prototyping, dan User Testing.",
                            speaker = "Sinta Maharani, M.Ds", quota = 75, status = "Hampir Penuh",
                            imageResId = R.drawable.uiux
                        ),
                        Seminar(
                            title = "Data Science", category = "Teknologi", date = "20 Mei 2026", time = "09:00 - 15:00",
                            location = "BLOCK71 Yogyakarta", address = "Innovation Factory, Jl. Prof. Herman Yohanes No. 121, Yogyakarta",
                            summary = "Analisis data besar dan machine learning.",
                            detail = "Python, Pandas, NumPy, Scikit-Learn, dan Visualisasi Data.",
                            speaker = "Dr. Hendra Wijaya", quota = 40, status = "Terbatas",
                            imageResId = R.drawable.data
                        ),
                        Seminar(
                            title = "Cyber Security", category = "Teknologi", date = "25 Mei 2026", time = "10:00 - 14:00",
                            location = "Telkom Landmark Tower", address = "The Hub Lt. 2, Jl. Jend. Gatot Subroto Kav. 52, Jakarta Selatan",
                            summary = "Keamanan jaringan dan etika hacking.",
                            detail = "Network Security, Cryptography, Pentesting, dan OWASP Top 10.",
                            speaker = "Eko Kurniawan, CEH", quota = 60, status = "Tersedia",
                            imageResId = R.drawable.cyber
                        )
                    )
                    seminarDao.insertSeminars(dummySeminars)
                } else {
                    rvSeminars.layoutManager = LinearLayoutManager(this@MainActivity)
                    rvSeminars.adapter = SeminarAdapter(list.take(3)) { seminar ->
                        val intent = Intent(this@MainActivity, SeminarDetailActivity::class.java).apply {
                            putExtra("SEMINAR", seminar)
                        }
                        startActivity(intent)
                    }
                }
            }
        }
    }

    private fun updateLastRegistrationUI() {
        val cardLastReg = findViewById<MaterialCardView>(R.id.cardLastRegistration)
        val tvNoReg = findViewById<TextView>(R.id.tvNoRegistration)

        if (sessionManager.hasLastRegistration()) {
            val data = sessionManager.getLastRegistration()
            cardLastReg.visibility = View.VISIBLE
            tvNoReg.visibility = View.GONE

            findViewById<TextView>(R.id.tvLastSeminarName).text = data["seminar"]
            findViewById<TextView>(R.id.tvLastDate).text = "Daftar pada: ${data["date"]}"

            findViewById<Button>(R.id.btnViewDetails).setOnClickListener {
                val intent = Intent(this, ResultActivity::class.java).apply {
                    putExtra("NAME", data["name"])
                    putExtra("EMAIL", data["email"])
                    putExtra("PHONE", data["phone"])
                    putExtra("GENDER", data["gender"])
                    putExtra("SEMINAR", data["seminar"])
                }
                startActivity(intent)
            }
        } else {
            cardLastReg.visibility = View.GONE
            tvNoReg.visibility = View.VISIBLE
        }
    }

    override fun onResume() {
        super.onResume()
        updateLastRegistrationUI()
    }
}