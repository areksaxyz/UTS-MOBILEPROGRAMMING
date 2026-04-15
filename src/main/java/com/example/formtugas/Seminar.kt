package com.example.formtugas

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "seminars")
data class Seminar(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val category: String,
    val date: String,
    val time: String,
    val location: String,
    val address: String,
    val summary: String,
    val detail: String,
    val speaker: String,
    val quota: Int,
    val status: String,
    val imageResId: Int? = null
) : Serializable