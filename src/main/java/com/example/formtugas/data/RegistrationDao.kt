package com.example.formtugas.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RegistrationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRegistration(registration: Registration)

    @Query("SELECT * FROM registrations WHERE userEmail = :email ORDER BY id DESC")
    fun getRegistrationsByUser(email: String): Flow<List<Registration>>

    @Query("SELECT * FROM registrations WHERE userEmail = :email ORDER BY id DESC LIMIT 1")
    suspend fun getLastRegistrationByUser(email: String): Registration?
}
