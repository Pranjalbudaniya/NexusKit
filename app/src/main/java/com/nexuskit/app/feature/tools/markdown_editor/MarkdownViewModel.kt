package com.nexuskit.app.feature.tools.markdown_editor

import android.app.Application
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.nexuskit.app.domain.usecase.tool.TrackToolOpenedUseCase
import com.nexuskit.app.domain.usecase.tool.TrackToolParams
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

enum class MarkdownTab {
    EDIT, PREVIEW
}

data class MarkdownUiState(
    val markdownText: String = "",
    val selectedTab: MarkdownTab = MarkdownTab.EDIT,
    val stats: DocumentStats = DocumentStats(),
    val isExporting: Boolean = false,
    val exportedPdfFile: File? = null,
    val exportedHtmlFile: File? = null,
    val message: String? = null,
    val documentTitle: String = "Untitled.md",
    val documentUri: Uri? = null,
    val isDirty: Boolean = false,
    val canUndo: Boolean = false,
    val canRedo: Boolean = false,
    val outline: MarkdownOutline = MarkdownOutline(),
    val isOutlineOpen: Boolean = false,
    val expandedHeadingIds: Set<String> = emptySet(),
    val outlineFilterQuery: String = "",
    val isSearchOpen: Boolean = false,
    val searchQuery: String = "",
    val replaceQuery: String = "",
    val searchMatches: List<SearchMatch> = emptyList(),
    val currentMatchIndex: Int = 0,
    val targetScrollLine: Int? = null,
    val showTableDialog: Boolean = false,
    val showLinkDialog: Boolean = false,
    val showNewDocumentConfirmDialog: Boolean = false
)

@HiltViewModel
class MarkdownViewModel @Inject constructor(
    application: Application,
    private val trackToolOpenedUseCase: TrackToolOpenedUseCase
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(MarkdownUiState())
    val uiState: StateFlow<MarkdownUiState> = _uiState.asStateFlow()

    private val undoStack = ArrayDeque<String>()
    private val redoStack = ArrayDeque<String>()
    private var lastRecordedTime = 0L

    init {
        viewModelScope.launch {
            trackToolOpenedUseCase(TrackToolParams("markdown_editor"))
        }

        // Listen for any incoming files pushed via Android intent
        viewModelScope.launch {
            MarkdownDocumentHolder.pendingDocument.collect { pending ->
                if (pending != null) {
                    val doc = MarkdownDocumentHolder.consumePendingDocument()
                    if (doc != null) {
                        loadImportedDocument(doc)
                    }
                }
            }
        }
    }

    private fun loadImportedDocument(doc: MarkdownImportedDocument) {
        undoStack.clear()
        redoStack.clear()
        val stats = MarkdownEngine.calculateStats(doc.content)
        val flatHeadings = MarkdownEngine.extractHeadings(doc.content)
        val outline = MarkdownEngine.buildHeadingTree(flatHeadings)
        val allIds = flatHeadings.map { it.id }.toSet()

        _uiState.update {
            it.copy(
                markdownText = doc.content,
                documentTitle = doc.fileName,
                documentUri = doc.uri,
                isDirty = false,
                stats = stats,
                outline = outline,
                expandedHeadingIds = allIds,
                canUndo = false,
                canRedo = false,
                message = "Opened \"${doc.fileName}\""
            )
        }
    }

    fun onTextChanged(newText: String, recordUndo: Boolean = true) {
        val currentText = _uiState.value.markdownText
        if (newText == currentText) return

        if (recordUndo) {
            val now = System.currentTimeMillis()
            // Record snapshot on significant changes or after a brief typing pause
            if (now - lastRecordedTime > 800 || Math.abs(newText.length - currentText.length) > 2) {
                if (undoStack.size >= 50) undoStack.removeFirst()
                undoStack.addLast(currentText)
                redoStack.clear()
                lastRecordedTime = now
            }
        }

        val stats = MarkdownEngine.calculateStats(newText)
        val flatHeadings = MarkdownEngine.extractHeadings(newText)
        val outline = MarkdownEngine.buildHeadingTree(flatHeadings)

        // Keep current expanded IDs, and auto-expand any new heading IDs
        val currentExpanded = _uiState.value.expandedHeadingIds.toMutableSet()
        flatHeadings.forEach { currentExpanded.add(it.id) }

        // Update search matches if search is open
        val searchMatches = if (_uiState.value.isSearchOpen && _uiState.value.searchQuery.isNotEmpty()) {
            MarkdownEngine.findMatches(newText, _uiState.value.searchQuery)
        } else {
            emptyList()
        }

        _uiState.update {
            it.copy(
                markdownText = newText,
                stats = stats,
                outline = outline,
                expandedHeadingIds = currentExpanded,
                isDirty = true,
                canUndo = undoStack.isNotEmpty(),
                canRedo = redoStack.isNotEmpty(),
                searchMatches = searchMatches,
                currentMatchIndex = if (searchMatches.isNotEmpty()) 0 else 0,
                message = null
            )
        }
    }

    fun undo() {
        if (undoStack.isEmpty()) return
        val previousText = undoStack.removeLast()
        val currentText = _uiState.value.markdownText
        redoStack.addLast(currentText)

        onTextChanged(previousText, recordUndo = false)
        _uiState.update {
            it.copy(
                canUndo = undoStack.isNotEmpty(),
                canRedo = redoStack.isNotEmpty()
            )
        }
    }

    fun redo() {
        if (redoStack.isEmpty()) return
        val nextText = redoStack.removeLast()
        val currentText = _uiState.value.markdownText
        undoStack.addLast(currentText)

        onTextChanged(nextText, recordUndo = false)
        _uiState.update {
            it.copy(
                canUndo = undoStack.isNotEmpty(),
                canRedo = redoStack.isNotEmpty()
            )
        }
    }

    fun onTabSelected(tab: MarkdownTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    // ── Outline Drawer Controls ────────────────────────────────────────────────
    fun toggleOutline(open: Boolean? = null) {
        _uiState.update { current ->
            val next = open ?: !current.isOutlineOpen
            current.copy(isOutlineOpen = next)
        }
    }

    fun toggleHeadingExpanded(id: String) {
        _uiState.update { current ->
            val set = current.expandedHeadingIds.toMutableSet()
            if (set.contains(id)) {
                set.remove(id)
            } else {
                set.add(id)
            }
            current.copy(expandedHeadingIds = set)
        }
    }

    fun expandAllHeadings() {
        val flat = MarkdownEngine.extractHeadings(_uiState.value.markdownText)
        _uiState.update { it.copy(expandedHeadingIds = flat.map { h -> h.id }.toSet()) }
    }

    fun collapseAllHeadings() {
        _uiState.update { it.copy(expandedHeadingIds = emptySet()) }
    }

    fun onOutlineFilterChanged(query: String) {
        _uiState.update { it.copy(outlineFilterQuery = query) }
    }

    fun jumpToHeading(heading: HeadingNode) {
        _uiState.update {
            it.copy(
                targetScrollLine = heading.lineNumber,
                isOutlineOpen = false,
                message = "Jumped to line ${heading.lineNumber}"
            )
        }
    }

    fun clearTargetScrollLine() {
        _uiState.update { it.copy(targetScrollLine = null) }
    }

    // ── Search & Replace ──────────────────────────────────────────────────────
    fun toggleSearch(open: Boolean? = null) {
        _uiState.update { current ->
            val next = open ?: !current.isSearchOpen
            val matches = if (next && current.searchQuery.isNotEmpty()) {
                MarkdownEngine.findMatches(current.markdownText, current.searchQuery)
            } else {
                emptyList()
            }
            current.copy(
                isSearchOpen = next,
                searchMatches = matches,
                currentMatchIndex = 0
            )
        }
    }

    fun onSearchQueryChanged(query: String) {
        val matches = MarkdownEngine.findMatches(_uiState.value.markdownText, query)
        _uiState.update {
            it.copy(
                searchQuery = query,
                searchMatches = matches,
                currentMatchIndex = if (matches.isNotEmpty()) 0 else 0
            )
        }
    }

    fun onReplaceQueryChanged(query: String) {
        _uiState.update { it.copy(replaceQuery = query) }
    }

    fun nextMatch() {
        val matches = _uiState.value.searchMatches
        if (matches.isEmpty()) return
        val nextIndex = (_uiState.value.currentMatchIndex + 1) % matches.size
        _uiState.update {
            it.copy(
                currentMatchIndex = nextIndex,
                targetScrollLine = matches[nextIndex].lineNumber
            )
        }
    }

    fun previousMatch() {
        val matches = _uiState.value.searchMatches
        if (matches.isEmpty()) return
        val prevIndex = if (_uiState.value.currentMatchIndex <= 0) matches.size - 1 else _uiState.value.currentMatchIndex - 1
        _uiState.update {
            it.copy(
                currentMatchIndex = prevIndex,
                targetScrollLine = matches[prevIndex].lineNumber
            )
        }
    }

    fun replaceCurrentMatch() {
        val state = _uiState.value
        val matches = state.searchMatches
        if (matches.isEmpty() || state.currentMatchIndex !in matches.indices) return

        val match = matches[state.currentMatchIndex]
        val updatedText = MarkdownEngine.replaceMatch(state.markdownText, match, state.replaceQuery)
        onTextChanged(updatedText)
    }

    fun replaceAllMatches() {
        val state = _uiState.value
        if (state.searchQuery.isEmpty()) return
        val updatedText = MarkdownEngine.replaceAll(state.markdownText, state.searchQuery, state.replaceQuery)
        onTextChanged(updatedText)
        _uiState.update { it.copy(message = "Replaced all occurrences") }
    }

    // ── Formatting Helpers ────────────────────────────────────────────────────
    fun insertFormatting(prefix: String, suffix: String = "") {
        val current = _uiState.value.markdownText
        val addition = if (current.isEmpty() || current.endsWith("\n")) {
            "$prefix$suffix"
        } else {
            "\n$prefix$suffix"
        }
        onTextChanged(current + addition)
    }

    fun insertTable(rows: Int, cols: Int, hasHeader: Boolean) {
        val tableMd = MarkdownEngine.generateTableTemplate(rows, cols, hasHeader)
        val current = _uiState.value.markdownText
        val prefix = if (current.isEmpty() || current.endsWith("\n\n")) "" else "\n\n"
        onTextChanged(current + prefix + tableMd + "\n")
        _uiState.update { it.copy(showTableDialog = false) }
    }

    fun insertLink(title: String, url: String) {
        val linkMd = "[$title]($url)"
        val current = _uiState.value.markdownText
        val separator = if (current.isEmpty() || current.endsWith(" ") || current.endsWith("\n")) "" else " "
        onTextChanged(current + separator + linkMd)
        _uiState.update { it.copy(showLinkDialog = false) }
    }

    fun toggleChecklistAtLine(lineNumber: Int) {
        val updated = MarkdownEngine.toggleChecklistAtLine(_uiState.value.markdownText, lineNumber)
        onTextChanged(updated)
    }

    fun setShowTableDialog(show: Boolean) {
        _uiState.update { it.copy(showTableDialog = show) }
    }

    fun setShowLinkDialog(show: Boolean) {
        _uiState.update { it.copy(showLinkDialog = show) }
    }

    fun setShowNewDocumentConfirmDialog(show: Boolean) {
        _uiState.update { it.copy(showNewDocumentConfirmDialog = show) }
    }

    fun newDocument() {
        undoStack.clear()
        redoStack.clear()
        _uiState.update {
            MarkdownUiState(
                documentTitle = "Untitled.md",
                documentUri = null,
                isDirty = false
            )
        }
    }

    // ── Storage Operations ────────────────────────────────────────────────────
    fun openDocumentFromUri(context: Context, uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                var fileName = "Document.md"
                context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                    val nameIdx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (nameIdx != -1 && cursor.moveToFirst()) {
                        val queried = cursor.getString(nameIdx)
                        if (!queried.isNullOrBlank()) fileName = queried
                    }
                }

                val content = context.contentResolver.openInputStream(uri)?.use { stream ->
                    stream.bufferedReader(Charsets.UTF_8).readText()
                } ?: ""

                loadImportedDocument(MarkdownImportedDocument(fileName, content, uri))
            } catch (e: Exception) {
                _uiState.update { it.copy(message = "Could not open file: ${e.localizedMessage}") }
            }
        }
    }

    fun saveDocumentToUri(context: Context, uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val text = _uiState.value.markdownText
                context.contentResolver.openOutputStream(uri, "wt")?.use { stream ->
                    stream.bufferedWriter(Charsets.UTF_8).use { writer ->
                        writer.write(text)
                    }
                }

                var fileName = "Document.md"
                context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                    val nameIdx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (nameIdx != -1 && cursor.moveToFirst()) {
                        val queried = cursor.getString(nameIdx)
                        if (!queried.isNullOrBlank()) fileName = queried
                    }
                }

                _uiState.update {
                    it.copy(
                        documentTitle = fileName,
                        documentUri = uri,
                        isDirty = false,
                        message = "Saved \"$fileName\""
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(message = "Failed to save: ${e.localizedMessage}") }
            }
        }
    }

    // ── Exporting ─────────────────────────────────────────────────────────────
    fun exportPdf(onComplete: (File) -> Unit) {
        val text = _uiState.value.markdownText
        if (text.isBlank()) {
            _uiState.update { it.copy(message = "Document is empty. Enter some text first.") }
            return
        }

        val baseName = _uiState.value.documentTitle.substringBeforeLast('.')
        _uiState.update { it.copy(isExporting = true, message = null) }
        viewModelScope.launch(Dispatchers.IO) {
            val res = MarkdownEngine.exportToPdf(getApplication(), text, baseName)
            res.fold(
                onSuccess = { file ->
                    _uiState.update { it.copy(isExporting = false, exportedPdfFile = file) }
                    onComplete(file)
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isExporting = false, message = error.message ?: "Failed to generate PDF") }
                }
            )
        }
    }

    fun exportHtml(onComplete: (File) -> Unit) {
        val text = _uiState.value.markdownText
        if (text.isBlank()) {
            _uiState.update { it.copy(message = "Document is empty. Enter some text first.") }
            return
        }

        val baseName = _uiState.value.documentTitle.substringBeforeLast('.')
        _uiState.update { it.copy(isExporting = true, message = null) }
        viewModelScope.launch(Dispatchers.IO) {
            val res = MarkdownEngine.exportToHtmlFile(getApplication(), text, baseName)
            res.fold(
                onSuccess = { file ->
                    _uiState.update { it.copy(isExporting = false, exportedHtmlFile = file) }
                    onComplete(file)
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isExporting = false, message = error.message ?: "Failed to generate HTML") }
                }
            )
        }
    }

    fun clearMessage() {
        _uiState.update { it.copy(message = null) }
    }
}
