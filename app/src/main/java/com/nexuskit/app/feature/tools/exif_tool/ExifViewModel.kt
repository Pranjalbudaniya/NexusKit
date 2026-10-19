package com.nexuskit.app.feature.tools.exif_tool

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.nexuskit.app.core.util.GallerySaver
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

data class ExifUiState(
    val selectedUri: Uri? = null,
    val isAnalyzing: Boolean = false,
    val isStripping: Boolean = false,
    val analysisResult: ExifAnalysisResult? = null,
    val cleanedFile: File? = null,
    val isSavedToGallery: Boolean = false,
    val selectedFilter: String = "ALL",
    val errorMessage: String? = null,
    val toastMessage: String? = null
)

@HiltViewModel
class ExifViewModel @Inject constructor(
    application: Application,
    private val trackToolOpenedUseCase: TrackToolOpenedUseCase
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(ExifUiState())
    val uiState: StateFlow<ExifUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            trackToolOpenedUseCase(TrackToolParams("exif_tool"))
        }
    }

    fun onImageSelected(uri: Uri?) {
        if (uri == null) return
        _uiState.update { it.copy(selectedUri = uri, isAnalyzing = true, cleanedFile = null, isSavedToGallery = false, errorMessage = null) }
        viewModelScope.launch(Dispatchers.IO) {
            val result = ExifEngine.extractExif(getApplication(), uri).getOrNull()
            _uiState.update { it.copy(isAnalyzing = false, analysisResult = result) }
        }
    }

    fun stripMetadata() {
        val uri = _uiState.value.selectedUri ?: return
        _uiState.update { it.copy(isStripping = true, errorMessage = null) }
        viewModelScope.launch(Dispatchers.IO) {
            val res = ExifEngine.stripExifMetadata(getApplication(), uri)
            res.fold(
                onSuccess = { file ->
                    _uiState.update { it.copy(isStripping = false, cleanedFile = file, isSavedToGallery = false) }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isStripping = false, errorMessage = error.message ?: "Failed to clean photo") }
                }
            )
        }
    }

    fun saveCleanedToGallery(context: Context) {
        val file = _uiState.value.cleanedFile ?: return
        viewModelScope.launch(Dispatchers.IO) {
            val res = GallerySaver.saveFileToGallery(
                context = context,
                file = file,
                displayName = "Cleaned_${System.currentTimeMillis()}",
                mimeType = "image/jpeg"
            )
            res.fold(
                onSuccess = {
                    _uiState.update { it.copy(isSavedToGallery = true, toastMessage = "Cleaned photo saved to Gallery (Pictures/NexusKit)") }
                },
                onFailure = { err ->
                    _uiState.update { it.copy(toastMessage = "Failed to save: ${err.localizedMessage}") }
                }
            )
        }
    }

    fun onFilterSelected(filter: String) {
        _uiState.update { it.copy(selectedFilter = filter) }
    }

    fun clearToast() {
        _uiState.update { it.copy(toastMessage = null) }
    }

    fun clear() {
        _uiState.update { ExifUiState() }
    }
}
