package com.example.formtugas.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.formtugas.Seminar
import kotlinx.coroutines.flow.Flow

@Dao
interface SeminarDao {
    @Query("SELECT * FROM seminars")
    fun getAllSeminars(): Flow<List<Seminar>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSeminars(seminars: List<Seminar>)

    @Query("SELECT * FROM seminars WHERE id = :id")
    suspend fun getSeminarById(id: Int): Seminar?
}
