package com.nexuskit.app.feature.tools.fancy_text

import android.app.Application
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

data class FancyTextUiState(
    val input: String = "",
    val selectedCategory: String = "All",
    val items: List<FancyTextItem> = emptyList()
)

@HiltViewModel
class FancyTextViewModel @Inject constructor(
    application: Application,
    private val trackToolOpenedUseCase: TrackToolOpenedUseCase
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(FancyTextUiState())
    val uiState: StateFlow<FancyTextUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            trackToolOpenedUseCase(TrackToolParams("fancy_text"))
        }
        updateItems("", "All")
    }

    fun onInputChanged(text: String) {
        _uiState.update { it.copy(input = text) }
        updateItems(text, _uiState.value.selectedCategory)
    }

    fun onCategorySelected(category: String) {
        _uiState.update { it.copy(selectedCategory = category) }
        updateItems(_uiState.value.input, category)
    }

    private fun updateItems(text: String, category: String) {
        val all = FancyTextEngine.convertAll(text)
        val filtered = if (category == "All") all else all.filter { it.style.category == category }
        _uiState.update { it.copy(items = filtered) }
    }

    fun loadSample() {
        onInputChanged("Hello NexusKit ✨")
    }

    fun clear() {
        onInputChanged("")
    }
}
