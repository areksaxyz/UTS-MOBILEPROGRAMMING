package com.example.formtugas

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.formtugas.data.AppDatabase
import com.example.formtugas.data.Registration
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class SeminarActivity : AppCompatActivity() {

    private lateinit var etFullName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPhone: EditText
    private lateinit var rgGender: RadioGroup
    private lateinit var spinnerSeminar: Spinner
    private lateinit var cbAgreement: CheckBox
    private lateinit var btnSubmit: Button
    private lateinit var bottomNav: BottomNavigationView
    private val sessionManager by lazy { SessionManager(this) }
    private val db by lazy { AppDatabase.getDatabase(this) }

    private var seminarList = mutableListOf("Pilih Seminar")
    private var seminarsMap = mutableMapOf<String, Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seminar)

        // Initialize Views
        etFullName = findViewById(R.id.etFullName)
        etEmail = findViewById(R.id.etEmail)
        etPhone = findViewById(R.id.etPhone)
        rgGender = findViewById(R.id.rgGender)
        spinnerSeminar = findViewById(R.id.spinnerSeminar)
        cbAgreement = findViewById(R.id.cbAgreement)
        btnSubmit = findViewById(R.id.btnSubmit)
        bottomNav = findViewById(R.id.bottomNavigation)

        // Auto-fill data dari session
        etFullName.setText(sessionManager.getUserName())
        etEmail.setText(sessionManager.getUserEmail())

        // Get seminar dari intent jika ada
        val preSelectedSeminar = intent.getStringExtra("SELECTED_SEMINAR")

        loadSeminarsFromDb(preSelectedSeminar)

        btnSubmit.setOnClickListener {
            if (validateForm()) {
                val name = etFullName.text.toString()
                val email = etEmail.text.toString()
                val phone = etPhone.text.toString()
                val selectedGenderId = rgGender.checkedRadioButtonId
                val gender = if (selectedGenderId != -1) {
                    findViewById<RadioButton>(selectedGenderId).text.toString()
                } else {
                    "Tidak dipilih"
                }
                val seminarTitle = spinnerSeminar.selectedItem.toString()
                val seminarId = seminarsMap[seminarTitle] ?: 0

                val currentDate = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(Date())

                lifecycleScope.launch {
                    val registration = Registration(
                        userEmail = email,
                        seminarId = seminarId,
                        seminarTitle = seminarTitle,
                        registrationDate = currentDate,
                        userName = name,
                        userPhone = phone,
                        userGender = gender
                    )
                    db.registrationDao().insertRegistration(registration)

                    // Simpan pendaftaran terakhir ke SessionManager (untuk backward compatibility)
                    sessionManager.saveLastRegistration(name, email, phone, gender, seminarTitle)

                    val intent = Intent(this@SeminarActivity, ResultActivity::class.java).apply {
                        putExtra("NAME", name)
                        putExtra("EMAIL", email)
                        putExtra("PHONE", phone)
                        putExtra("GENDER", gender)
                        putExtra("SEMINAR", seminarTitle)
                    }
                    startActivity(intent)
                    finish()
                }
            }
        }

        bottomNav.selectedItemId = R.id.nav_home
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

    private fun setupSpinner() {
        val adapter = object : ArrayAdapter<String>(this, R.layout.spinner_item, seminarList) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val v = super.getView(position, convertView, parent) as TextView
                v.includeFontPadding = false
                return v
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val v = layoutInflater.inflate(R.layout.spinner_dropdown_item, parent, false) as TextView
                v.text = getItem(position)
                v.includeFontPadding = false
                return v
            }
        }
        spinnerSeminar.adapter = adapter
    }

    private fun loadSeminarsFromDb(preSelected: String?) {
        lifecycleScope.launch {
            db.seminarDao().getAllSeminars().collect { list ->
                seminarList.clear()
                seminarList.add("Pilih Seminar")
                list.forEach { 
                    seminarList.add(it.title)
                    seminarsMap[it.title] = it.id
                }
                setupSpinner()
                
                preSelected?.let { title ->
                    val index = seminarList.indexOf(title)
                    if (index != -1) spinnerSeminar.setSelection(index)
                }
            }
        }
    }

    private fun validateForm(): Boolean {
        var isValid = true

        if (etFullName.text.isEmpty()) {
            etFullName.error = "Nama harus diisi"
            isValid = false
        }
        
        val email = etEmail.text.toString()
        if (email.isEmpty()) {
            etEmail.error = "Email harus diisi"
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.error = "Format email salah"
            isValid = false
        }

        if (etPhone.text.isEmpty()) {
            etPhone.error = "Nomor HP harus diisi"
            isValid = false
        }

        if (rgGender.checkedRadioButtonId == -1) {
            Toast.makeText(this, "Pilih jenis kelamin", Toast.LENGTH_SHORT).show()
            isValid = false
        }

        if (spinnerSeminar.selectedItemPosition == 0) {
            Toast.makeText(this, "Pilih seminar yang ingin diikuti", Toast.LENGTH_SHORT).show()
            isValid = false
        }

        if (!cbAgreement.isChecked) {
            Toast.makeText(this, "Anda harus menyetujui persyaratan", Toast.LENGTH_SHORT).show()
            isValid = false
        }

        return isValid
    }
}