package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ProgressDao {

    @Query("SELECT * FROM user_progress WHERE id = 1")
    fun getProgress(): Flow<UserProgress?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveProgress(progress: UserProgress)

    @Query("SELECT * FROM practice_notes ORDER BY dateEpoch DESC")
    fun getAllNotes(): Flow<List<PracticeNote>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: PracticeNote)

    @Query("DELETE FROM practice_notes WHERE id = :id")
    suspend fun deleteNoteById(id: Long)
}
