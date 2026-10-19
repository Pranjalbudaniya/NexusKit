package com.nexuskit.app.feature.tools.barcode_generator

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.nexuskit.app.domain.usecase.tool.TrackToolOpenedUseCase
import com.nexuskit.app.domain.usecase.tool.TrackToolParams
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BarcodeUiState(
    val content: String = "",
    val selectedFormat: SupportedBarcodeFormat = SupportedBarcodeFormat.CODE_128,
    val showTextLabel: Boolean = true,
    val barcodeBitmap: Bitmap? = null,
    val errorMessage: String? = null
)

@HiltViewModel
class BarcodeViewModel @Inject constructor(
    application: Application,
    private val trackToolOpenedUseCase: TrackToolOpenedUseCase
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(BarcodeUiState())
    val uiState: StateFlow<BarcodeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            trackToolOpenedUseCase(TrackToolParams("barcode_generator"))
        }
    }

    fun onContentChanged(newContent: String) {
        _uiState.update { it.copy(content = newContent) }
        generate()
    }

    fun onFormatChanged(newFormat: SupportedBarcodeFormat) {
        _uiState.update { it.copy(selectedFormat = newFormat) }
        generate()
    }

    fun onShowTextLabelToggled(show: Boolean) {
        _uiState.update { it.copy(showTextLabel = show) }
        generate()
    }

    fun generate() {
        val s = _uiState.value
        val raw = s.content.trim()
        if (raw.isEmpty()) {
            _uiState.update { it.copy(barcodeBitmap = null, errorMessage = null) }
            return
        }

        val res = BarcodeEngine.generateBarcodeBitmap(
            content = raw,
            format = s.selectedFormat,
            showTextLabel = s.showTextLabel
        )

        res.fold(
            onSuccess = { bmp ->
                _uiState.update { it.copy(barcodeBitmap = bmp, errorMessage = null) }
            },
            onFailure = { err ->
                _uiState.update { it.copy(barcodeBitmap = null, errorMessage = err.localizedMessage) }
            }
        )
    }

    fun loadSample() {
        val sample = _uiState.value.selectedFormat.sample
        onContentChanged(sample)
    }

    fun clear() {
        _uiState.update { it.copy(content = "", barcodeBitmap = null, errorMessage = null) }
    }
}
