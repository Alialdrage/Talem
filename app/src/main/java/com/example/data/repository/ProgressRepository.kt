package com.example.data.repository

import com.example.data.local.PracticeNote
import com.example.data.local.ProgressDao
import com.example.data.local.UserProgress
import kotlinx.coroutines.flow.Flow

class ProgressRepository(private val dao: ProgressDao) {

    val progress: Flow<UserProgress?> = dao.getProgress()
    val practiceNotes: Flow<List<PracticeNote>> = dao.getAllNotes()

    suspend fun saveProgress(progress: UserProgress) {
        dao.saveProgress(progress)
    }

    suspend fun insertNote(note: PracticeNote) {
        dao.insertNote(note)
    }

    suspend fun deleteNote(id: Long) {
        dao.deleteNoteById(id)
    }
}
