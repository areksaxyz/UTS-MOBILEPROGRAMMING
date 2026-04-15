package com.example.formtugas

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.animation.AnimationUtils
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.formtugas.data.AppDatabase
import com.example.formtugas.data.User
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var tilName: TextInputLayout
    private lateinit var tilEmail: TextInputLayout
    private lateinit var tilPassword: TextInputLayout
    private lateinit var tilConfirmPassword: TextInputLayout

    private lateinit var etName: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var etConfirmPassword: TextInputEditText

    private lateinit var rgGender: RadioGroup
    private lateinit var cbMembaca: CheckBox
    private lateinit var cbOlahraga: CheckBox
    private lateinit var cbMusik: CheckBox
    private lateinit var cbGaming: CheckBox
    private lateinit var cbTraveling: CheckBox
    private lateinit var spinnerCity: Spinner
    private lateinit var btnRegister: Button
    private lateinit var tvGoLoginAction: TextView
    private lateinit var registerCard: android.view.View

    private val db by lazy { AppDatabase.getDatabase(this) }

    private val cityList = listOf(
        "Pilih Kota",
        "Jakarta",
        "Bandung",
        "Surabaya",
        "Yogyakarta",
        "Banjar"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        initViews()
        setupSpinner()
        setupRealtimeValidation()
        showEntryAnimation()

        btnRegister.setOnClickListener {
            playButtonAnimation(btnRegister)
            if (validateForm()) {
                checkAndRegisterUser()
            } else {
                shakeForm()
            }
        }

        btnRegister.setOnLongClickListener {
            Toast.makeText(this, "Gesture long press pada tombol Register", Toast.LENGTH_SHORT).show()
            true
        }

        tvGoLoginAction.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            finish()
        }
    }

    private fun checkAndRegisterUser() {
        val email = etEmail.text.toString().trim()
        
        lifecycleScope.launch {
            val existingUser = db.userDao().getUserByEmail(email)
            if (existingUser != null) {
                tilEmail.error = "Email sudah terdaftar!"
                shakeForm()
            } else {
                showConfirmationDialog()
            }
        }
    }

    private fun initViews() {
        tilName = findViewById(R.id.tilName)
        tilEmail = findViewById(R.id.tilEmail)
        tilPassword = findViewById(R.id.tilPassword)
        tilConfirmPassword = findViewById(R.id.tilConfirmPassword)

        etName = findViewById(R.id.etName)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)

        rgGender = findViewById(R.id.rgGender)

        cbMembaca = findViewById(R.id.cbMembaca)
        cbOlahraga = findViewById(R.id.cbOlahraga)
        cbMusik = findViewById(R.id.cbMusik)
        cbGaming = findViewById(R.id.cbGaming)
        cbTraveling = findViewById(R.id.cbTraveling)

        spinnerCity = findViewById(R.id.spinnerCity)
        btnRegister = findViewById(R.id.btnRegister)
        tvGoLoginAction = findViewById(R.id.tvGoLoginAction)
        registerCard = findViewById(R.id.registerCard)
    }

    private fun setupSpinner() {
        val adapter = object : ArrayAdapter<String>(this, R.layout.spinner_item, cityList) {
            override fun getDropDownView(position: Int, convertView: android.view.View?, parent: android.view.ViewGroup): android.view.View {
                val v = layoutInflater.inflate(R.layout.spinner_dropdown_item, parent, false) as TextView
                v.text = getItem(position)
                return v
            }
        }
        spinnerCity.adapter = adapter
    }

    private fun setupRealtimeValidation() {
        etName.addTextChangedListener(simpleWatcher { validateName() })
        etEmail.addTextChangedListener(simpleWatcher { validateEmail() })
        etPassword.addTextChangedListener(simpleWatcher {
            validatePassword()
            validateConfirmPassword()
        })
        etConfirmPassword.addTextChangedListener(simpleWatcher { validateConfirmPassword() })
    }

    private fun showEntryAnimation() {
        val animation = AnimationUtils.loadAnimation(this, R.anim.fade_slide_up)
        registerCard.startAnimation(animation)
    }

    private fun playButtonAnimation(view: android.view.View) {
        val animation = AnimationUtils.loadAnimation(this, R.anim.button_scale)
        view.startAnimation(animation)
    }

    private fun shakeForm() {
        val animation = AnimationUtils.loadAnimation(this, R.anim.shake)
        registerCard.startAnimation(animation)
    }

    private fun validateName(): Boolean {
        val value = etName.text.toString().trim()
        return if (value.isEmpty()) {
            tilName.error = "Nama tidak boleh kosong"
            false
        } else if (value.any { it.isDigit() }) {
            tilName.error = "Nama tidak boleh mengandung angka"
            false
        } else {
            tilName.error = null
            true
        }
    }

    private fun validateEmail(): Boolean {
        val value = etEmail.text.toString().trim()
        return if (value.isEmpty()) {
            tilEmail.error = "Email tidak boleh kosong"
            false
        } else if (!value.contains("@")) {
            tilEmail.error = "Email wajib mengandung '@'"
            false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(value).matches()) {
            tilEmail.error = "Format email tidak valid"
            false
        } else {
            tilEmail.error = null
            true
        }
    }

    private fun validatePassword(): Boolean {
        val value = etPassword.text.toString()
        val hasUppercase = value.any { it.isUpperCase() }
        val hasSpecialChar = value.any { !it.isLetterOrDigit() }

        return when {
            value.isEmpty() -> {
                tilPassword.error = "Password tidak boleh kosong"
                false
            }
            value.length < 8 -> {
                tilPassword.error = "Password minimal 8 karakter"
                false
            }
            !hasUppercase -> {
                tilPassword.error = "Password wajib ada 1 huruf kapital"
                false
            }
            !hasSpecialChar -> {
                tilPassword.error = "Password wajib ada 1 karakter spesial"
                false
            }
            else -> {
                tilPassword.error = null
                true
            }
        }
    }

    private fun validateConfirmPassword(): Boolean {
        val password = etPassword.text.toString()
        val confirm = etConfirmPassword.text.toString()

        return if (confirm.isEmpty()) {
            tilConfirmPassword.error = "Confirm password tidak boleh kosong"
            false
        } else if (password != confirm) {
            tilConfirmPassword.error = "Password tidak sama"
            false
        } else {
            tilConfirmPassword.error = null
            true
        }
    }

    private fun validateGender(): Boolean {
        return if (rgGender.checkedRadioButtonId == -1) {
            Toast.makeText(this, "Pilih jenis kelamin", Toast.LENGTH_SHORT).show()
            false
        } else {
            true
        }
    }

    private fun validateHobbies(): Boolean {
        var total = 0
        if (cbMembaca.isChecked) total++
        if (cbOlahraga.isChecked) total++
        if (cbMusik.isChecked) total++
        if (cbGaming.isChecked) total++
        if (cbTraveling.isChecked) total++

        return if (total < 3) {
            Toast.makeText(this, "Pilih minimal 3 hobi", Toast.LENGTH_SHORT).show()
            false
        } else {
            true
        }
    }

    private fun validateCity(): Boolean {
        return if (spinnerCity.selectedItemPosition == 0) {
            Toast.makeText(this, "Pilih kota", Toast.LENGTH_SHORT).show()
            false
        } else {
            true
        }
    }

    private fun validateForm(): Boolean {
        val a = validateName()
        val b = validateEmail()
        val c = validatePassword()
        val d = validateConfirmPassword()
        val e = validateGender()
        val f = validateHobbies()
        val g = validateCity()

        return a && b && c && d && e && f && g
    }

    private fun showConfirmationDialog() {
        val checkedId = rgGender.checkedRadioButtonId
        val selectedGender = if (checkedId != -1) {
            findViewById<RadioButton>(checkedId).text.toString()
        } else {
            ""
        }
        val selectedCity = spinnerCity.selectedItem.toString()

        val hobbies = mutableListOf<String>()
        if (cbMembaca.isChecked) hobbies.add("Membaca")
        if (cbOlahraga.isChecked) hobbies.add("Olahraga")
        if (cbMusik.isChecked) hobbies.add("Musik")
        if (cbGaming.isChecked) hobbies.add("Gaming")
        if (cbTraveling.isChecked) hobbies.add("Traveling")

        val message = """
            Nama: ${etName.text.toString().trim()}
            Email: ${etEmail.text.toString().trim()}
            Jenis Kelamin: $selectedGender
            Hobi: ${hobbies.joinToString(", ")}
            Kota: $selectedCity
        """.trimIndent()

        AlertDialog.Builder(this)
            .setTitle("Konfirmasi Data")
            .setMessage(message)
            .setPositiveButton("Submit") { _, _ ->
                saveUserToDb(selectedGender, hobbies.joinToString(", "), selectedCity)
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun saveUserToDb(gender: String, hobbies: String, city: String) {
        val user = User(
            email = etEmail.text.toString().trim(),
            name = etName.text.toString().trim(),
            password = etPassword.text.toString(),
            gender = gender,
            hobbies = hobbies,
            city = city
        )

        lifecycleScope.launch {
            try {
                db.userDao().insertUser(user)
                Toast.makeText(this@RegisterActivity, "Register berhasil", Toast.LENGTH_LONG).show()
                finish()
            } catch (e: Exception) {
                Toast.makeText(this@RegisterActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun simpleWatcher(afterChanged: () -> Unit): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                afterChanged()
            }
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}
