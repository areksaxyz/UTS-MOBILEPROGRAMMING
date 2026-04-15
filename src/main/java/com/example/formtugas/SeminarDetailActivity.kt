package com.example.formtugas

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class SeminarDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seminar_detail)

        val seminar = intent.getSerializableExtra("SEMINAR") as? Seminar ?: return finish()

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""
        toolbar.setNavigationOnClickListener { finish() }

        findViewById<TextView>(R.id.tvDetailTitle).text = seminar.title
        findViewById<TextView>(R.id.tvDetailCategory).text = seminar.category
        findViewById<TextView>(R.id.tvDetailDate).text = seminar.date
        findViewById<TextView>(R.id.tvDetailTime).text = seminar.time
        findViewById<TextView>(R.id.tvDetailQuota).text = "${seminar.quota} Kursi"
        findViewById<TextView>(R.id.tvDetailLocation).text = seminar.location
        findViewById<TextView>(R.id.tvDetailAddress).text = seminar.address
        findViewById<TextView>(R.id.tvDetailSummary).text = seminar.summary
        findViewById<TextView>(R.id.tvDetailDescription).text = seminar.detail
        findViewById<TextView>(R.id.tvDetailSpeaker).text = seminar.speaker

        // Set Banner Image
        val ivBanner = findViewById<ImageView>(R.id.ivSeminarBanner)
        if (seminar.imageResId != null) {
            ivBanner.setImageResource(seminar.imageResId)
        } else {
            ivBanner.setImageResource(R.drawable.background)
        }

        findViewById<Button>(R.id.btnRegisterFromDetail).setOnClickListener {
            val intent = Intent(this, SeminarActivity::class.java).apply {
                putExtra("SELECTED_SEMINAR", seminar.title)
            }
            startActivity(intent)
        }
    }
}