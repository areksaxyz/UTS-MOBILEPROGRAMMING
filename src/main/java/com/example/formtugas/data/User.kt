package com.example.formtugas.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey val email: String,
    val name: String,
    val password: String? = null,
    val gender: String? = null,
    val hobbies: String? = null,
    val city: String? = null
)
