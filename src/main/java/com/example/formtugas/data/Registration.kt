package com.example.formtugas.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "registrations")
data class Registration(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userEmail: String,
    val seminarId: Int,
    val seminarTitle: String,
    val registrationDate: String,
    val userName: String,
    val userPhone: String,
    val userGender: String
) : Serializable
