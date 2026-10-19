package com.nexuskit.app.feature.tools.steganography

import android.app.Application
import android.content.Context
import android.graphics.BitmapFactory
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
import javax.inject.Inject

enum class StegoTab {
    ENCODE, DECODE
}

data class StegoUiState(
    val selectedTab: StegoTab = StegoTab.ENCODE,
    // Encode state
    val coverImageUri: Uri? = null,
    val coverCapacityBytes: Int = 0,
    val encodeMessage: String = "",
    val encodePassword: String = "",
    val isEncoding: Boolean = false,
    val encodeResult: StegoEncodeResult? = null,
    val encodeError: String? = null,
    val isSavedToGallery: Boolean = false,
    // Decode state
    val decodeImageUri: Uri? = null,
    val decodePassword: String = "",
    val isDecoding: Boolean = false,
    val decodeResult: StegoDecodeResult? = null,
    val decodeError: String? = null,
    val toastMessage: String? = null
)

@HiltViewModel
class SteganographyViewModel @Inject constructor(
    application: Application,
    private val trackToolOpenedUseCase: TrackToolOpenedUseCase
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(StegoUiState())
    val uiState: StateFlow<StegoUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            trackToolOpenedUseCase(TrackToolParams("steganography"))
        }
    }

    fun onTabSelected(tab: StegoTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun onCoverImageSelected(uri: Uri?) {
        if (uri == null) return
        viewModelScope.launch(Dispatchers.IO) {
            val capacity = runCatching {
                val input = getApplication<Application>().contentResolver.openInputStream(uri)
                val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                BitmapFactory.decodeStream(input, null, options)
                SteganographyEngine.calculateCapacityBytes(options.outWidth, options.outHeight)
            }.getOrDefault(0)

            _uiState.update {
                it.copy(
                    coverImageUri = uri,
                    coverCapacityBytes = capacity,
                    encodeResult = null,
                    encodeError = null,
                    isSavedToGallery = false
                )
            }
        }
    }

    fun onEncodeMessageChanged(msg: String) {
        _uiState.update { it.copy(encodeMessage = msg, encodeError = null) }
    }

    fun onEncodePasswordChanged(pass: String) {
        _uiState.update { it.copy(encodePassword = pass, encodeError = null) }
    }

    fun encode() {
        val current = _uiState.value
        val uri = current.coverImageUri
        if (uri == null) {
            _uiState.update { it.copy(encodeError = "Please select a cover image first") }
            return
        }
        if (current.encodeMessage.isBlank()) {
            _uiState.update { it.copy(encodeError = "Please enter a secret message") }
            return
        }

        _uiState.update { it.copy(isEncoding = true, encodeError = null) }
        viewModelScope.launch(Dispatchers.IO) {
            val res = SteganographyEngine.encodeMessage(
                context = getApplication(),
                imageUri = uri,
                message = current.encodeMessage,
                password = current.encodePassword.ifBlank { null }
            )
            res.fold(
                onSuccess = { result ->
                    _uiState.update { it.copy(isEncoding = false, encodeResult = result, encodeError = null, isSavedToGallery = false) }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isEncoding = false, encodeError = error.message ?: "Encoding failed") }
                }
            )
        }
    }

    fun saveToGallery(context: Context) {
        val file = _uiState.value.encodeResult?.outputFile ?: return
        viewModelScope.launch(Dispatchers.IO) {
            val res = GallerySaver.saveFileToGallery(
                context = context,
                file = file,
                displayName = "Stego_${System.currentTimeMillis()}",
                mimeType = "image/png"
            )
            res.fold(
                onSuccess = {
                    _uiState.update { it.copy(isSavedToGallery = true, toastMessage = "Saved to Gallery (Pictures/NexusKit)") }
                },
                onFailure = { err ->
                    _uiState.update { it.copy(toastMessage = "Failed to save: ${err.localizedMessage}") }
                }
            )
        }
    }

    fun onDecodeImageSelected(uri: Uri?) {
        if (uri == null) return
        _uiState.update {
            it.copy(
                decodeImageUri = uri,
                decodeResult = null,
                decodeError = null
            )
        }
    }

    fun onDecodePasswordChanged(pass: String) {
        _uiState.update { it.copy(decodePassword = pass, decodeError = null) }
    }

    fun decode() {
        val current = _uiState.value
        val uri = current.decodeImageUri
        if (uri == null) {
            _uiState.update { it.copy(decodeError = "Please select an image to extract from") }
            return
        }

        _uiState.update { it.copy(isDecoding = true, decodeError = null) }
        viewModelScope.launch(Dispatchers.IO) {
            val res = SteganographyEngine.decodeMessage(
                context = getApplication(),
                imageUri = uri,
                password = current.decodePassword.ifBlank { null }
            )
            res.fold(
                onSuccess = { result ->
                    _uiState.update { it.copy(isDecoding = false, decodeResult = result, decodeError = null) }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isDecoding = false, decodeError = error.message ?: "Decoding failed") }
                }
            )
        }
    }

    fun clearToast() {
        _uiState.update { it.copy(toastMessage = null) }
    }
}
