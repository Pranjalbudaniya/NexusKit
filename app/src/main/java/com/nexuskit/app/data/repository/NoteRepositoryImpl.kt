package com.nexuskit.app.data.repository

import com.nexuskit.app.data.database.dao.NoteDao
import com.nexuskit.app.data.database.entity.toDomain
import com.nexuskit.app.data.database.entity.toEntity
import com.nexuskit.app.domain.model.Note
import com.nexuskit.app.domain.repository.INoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoteRepositoryImpl @Inject constructor(
    private val noteDao: NoteDao
) : INoteRepository {

    override fun getAllNotes(): Flow<List<Note>> =
        noteDao.getAllNotes().map { list -> list.map { it.toDomain() } }

    override fun searchNotes(query: String): Flow<List<Note>> =
        noteDao.searchNotes(query).map { list -> list.map { it.toDomain() } }

    override suspend fun getNoteById(id: Long): Note? =
        noteDao.getNoteById(id)?.toDomain()

    override suspend fun saveNote(note: Note): Long {
        val now = System.currentTimeMillis()
        val entity = note.copy(
            updatedAt = now,
            createdAt = if (note.createdAt == 0L) now else note.createdAt
        ).toEntity()
        return noteDao.insertNote(entity)
    }

    override suspend fun deleteNote(note: Note) {
        noteDao.deleteNote(note.toEntity())
    }

    override suspend fun deleteNoteById(id: Long) {
        noteDao.deleteNoteById(id)
    }

    override suspend fun setPinned(id: Long, isPinned: Boolean) {
        noteDao.setPinned(id, isPinned, System.currentTimeMillis())
    }
}
