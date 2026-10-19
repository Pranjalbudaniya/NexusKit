package com.nexuskit.app.feature.tools.markdown_editor

import android.net.Uri
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class MarkdownImportedDocument(
    val fileName: String,
    val content: String,
    val uri: Uri? = null
)

/**
 * Singleton holder to bridge incoming Markdown files opened from external
 * Android intents (or storage picker) into [MarkdownViewModel].
 */
object MarkdownDocumentHolder {

    private val _pendingDocument = MutableStateFlow<MarkdownImportedDocument?>(null)
    val pendingDocument: StateFlow<MarkdownImportedDocument?> = _pendingDocument.asStateFlow()

    fun setPendingDocument(doc: MarkdownImportedDocument) {
        _pendingDocument.value = doc
    }

    fun consumePendingDocument(): MarkdownImportedDocument? {
        val doc = _pendingDocument.value
        _pendingDocument.value = null
        return doc
    }
}
