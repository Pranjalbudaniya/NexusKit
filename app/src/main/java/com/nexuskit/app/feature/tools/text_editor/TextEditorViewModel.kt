package com.nexuskit.app.feature.tools.text_editor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexuskit.app.domain.model.Note
import com.nexuskit.app.domain.repository.INoteRepository
import com.nexuskit.app.domain.usecase.tool.TrackToolOpenedUseCase
import com.nexuskit.app.domain.usecase.tool.TrackToolParams
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TextStats(
    val charCount: Int = 0,
    val wordCount: Int = 0,
    val lineCount: Int = 0,
    val readingTimeMinutes: Int = 0
)

data class TextEditorUiState(
    val searchQuery: String = "",
    val editingNote: Note? = null,
    val isEditing: Boolean = false,
    val currentTitle: String = "",
    val currentContent: String = "",
    val currentIsPinned: Boolean = false,
    val stats: TextStats = TextStats()
)

@HiltViewModel
class TextEditorViewModel @Inject constructor(
    private val noteRepository: INoteRepository,
    private val trackToolOpenedUseCase: TrackToolOpenedUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(TextEditorUiState())
    val uiState: StateFlow<TextEditorUiState> = _uiState.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val notesList: StateFlow<List<Note>> = _uiState
        .map { it.searchQuery }
        .distinctUntilChanged()
        .flatMapLatest { query ->
            if (query.isBlank()) {
                noteRepository.getAllNotes()
            } else {
                noteRepository.searchNotes(query.trim())
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    init {
        viewModelScope.launch {
            trackToolOpenedUseCase(TrackToolParams("text_editor"))
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun onCreateNewNote() {
        val newNote = Note(
            id = 0,
            title = "",
            content = "",
            isPinned = false,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        _uiState.update {
            it.copy(
                editingNote = newNote,
                isEditing = true,
                currentTitle = "",
                currentContent = "",
                currentIsPinned = false,
                stats = calculateStats("")
            )
        }
    }

    fun onSelectNote(note: Note) {
        _uiState.update {
            it.copy(
                editingNote = note,
                isEditing = true,
                currentTitle = note.title,
                currentContent = note.content,
                currentIsPinned = note.isPinned,
                stats = calculateStats(note.content)
            )
        }
    }

    fun onTitleChanged(title: String) {
        _uiState.update { it.copy(currentTitle = title) }
    }

    fun onContentChanged(content: String) {
        _uiState.update {
            it.copy(
                currentContent = content,
                stats = calculateStats(content)
            )
        }
    }

    fun onTogglePin() {
        _uiState.update { it.copy(currentIsPinned = !it.currentIsPinned) }
    }

    fun onTogglePinForNote(note: Note) {
        viewModelScope.launch {
            noteRepository.setPinned(note.id, !note.isPinned)
        }
    }

    fun onSaveAndCloseNote() {
        val state = _uiState.value
        val title = state.currentTitle.trim()
        val content = state.currentContent.trim()

        if (title.isNotEmpty() || content.isNotEmpty()) {
            val noteToSave = (state.editingNote ?: Note()).copy(
                title = if (title.isEmpty()) "Untitled Note" else title,
                content = state.currentContent,
                isPinned = state.currentIsPinned
            )
            viewModelScope.launch {
                noteRepository.saveNote(noteToSave)
            }
        } else if (state.editingNote != null && state.editingNote.id != 0L) {
            viewModelScope.launch {
                noteRepository.deleteNote(state.editingNote)
            }
        }

        _uiState.update {
            it.copy(
                isEditing = false,
                editingNote = null,
                currentTitle = "",
                currentContent = ""
            )
        }
    }

    fun onDeleteCurrentNote() {
        val note = _uiState.value.editingNote
        if (note != null && note.id != 0L) {
            viewModelScope.launch {
                noteRepository.deleteNote(note)
            }
        }
        _uiState.update {
            it.copy(
                isEditing = false,
                editingNote = null,
                currentTitle = "",
                currentContent = ""
            )
        }
    }

    fun onDeleteNote(note: Note) {
        viewModelScope.launch {
            noteRepository.deleteNote(note)
        }
    }

    private fun calculateStats(text: String): TextStats {
        val chars = text.length
        val words = if (text.isBlank()) 0 else text.trim().split(WHITESPACE_REGEX).size
        val lines = if (text.isEmpty()) 0 else text.lines().size
        val readTime = kotlin.math.max(1, (words / 200.0).toInt())
        return TextStats(
            charCount = chars,
            wordCount = words,
            lineCount = lines,
            readingTimeMinutes = if (words > 0) readTime else 0
        )
    }

    companion object {
        private val WHITESPACE_REGEX = Regex("\\s+")
    }
}
