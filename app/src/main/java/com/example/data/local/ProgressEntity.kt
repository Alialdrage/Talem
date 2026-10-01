package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_progress")
data class UserProgress(
    @PrimaryKey val id: Int = 1,
    val completedLessonsIds: String = "", // Comma-separated IDs: "1,2"
    val favoriteSurahs: String = "001,112", // Comma-separated: "001,112"
    val lastSurahId: String = "001",
    val lastAyahIndex: Int = 0,
    val practiceRecordingsCount: Int = 0,
    val totalListeningSeconds: Long = 0,
    val preferredReciter: String = "husr", // "husr" or "minsh"
    val playbackSpeed: Float = 1.0f
)

@Entity(tableName = "practice_notes")
data class PracticeNote(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val surahId: String,
    val ayahNumber: Int,
    val note: String,
    val dateEpoch: Long = System.currentTimeMillis()
)
