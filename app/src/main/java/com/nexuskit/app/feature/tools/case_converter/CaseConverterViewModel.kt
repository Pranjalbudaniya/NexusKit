package com.nexuskit.app.feature.tools.case_converter

import androidx.lifecycle.ViewModel
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

data class CaseConverterUiState(
    val inputText: String = "",
    val conversions: List<ConvertedCase> = CaseConverterEngine.convertAll(""),
    val characterCount: Int = 0,
    val wordCount: Int = 0
)

@HiltViewModel
class CaseConverterViewModel @Inject constructor(
    private val trackToolOpenedUseCase: TrackToolOpenedUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CaseConverterUiState())
    val uiState: StateFlow<CaseConverterUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            trackToolOpenedUseCase(TrackToolParams("case_converter"))
        }
    }

    fun onTextChanged(text: String) {
        val conversions = CaseConverterEngine.convertAll(text)
        val words = if (text.isBlank()) 0 else text.trim().split(Regex("\\s+")).size
        _uiState.update {
            it.copy(
                inputText = text,
                conversions = conversions,
                characterCount = text.length,
                wordCount = words
            )
        }
    }

    fun onClear() {
        onTextChanged("")
    }
}
