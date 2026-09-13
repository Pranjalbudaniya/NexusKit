package com.nexuskit.app.domain.repository

import com.nexuskit.app.domain.model.Note
import kotlinx.coroutines.flow.Flow

interface INoteRepository {
    fun getAllNotes(): Flow<List<Note>>
    fun searchNotes(query: String): Flow<List<Note>>
    suspend fun getNoteById(id: Long): Note?
    suspend fun saveNote(note: Note): Long
    suspend fun deleteNote(note: Note)
    suspend fun deleteNoteById(id: Long)
    suspend fun setPinned(id: Long, isPinned: Boolean)
}
