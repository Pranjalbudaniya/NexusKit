package com.nexuskit.app.feature.tools.line_sorter

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

data class LineSorterUiState(
    val input: String = "",
    val output: String = "",
    val wordFrequencies: List<WordFrequency> = emptyList(),
    val totalLines: Int = 0,
    val totalWords: Int = 0
)

@HiltViewModel
class LineSorterViewModel @Inject constructor(
    application: Application,
    private val trackToolOpenedUseCase: TrackToolOpenedUseCase
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(LineSorterUiState())
    val uiState: StateFlow<LineSorterUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            trackToolOpenedUseCase(TrackToolParams("line_sorter"))
        }
    }

    fun onInputChanged(text: String) {
        val lines = if (text.isBlank()) 0 else text.lines().size
        val freqs = LineSorterEngine.analyzeWordFrequency(text)
        val wordCount = freqs.sumOf { it.count }

        _uiState.update {
            it.copy(
                input = text,
                output = if (it.output.isEmpty()) text else it.output,
                wordFrequencies = freqs,
                totalLines = lines,
                totalWords = wordCount
            )
        }
    }

    fun sort(sortType: LineSorterEngine.SortType) {
        val target = if (_uiState.value.output.isNotEmpty()) _uiState.value.output else _uiState.value.input
        val res = LineSorterEngine.sortLines(target, sortType)
        updateOutput(res)
    }

    fun deduplicate(dedupeType: LineSorterEngine.DedupeType) {
        val target = if (_uiState.value.output.isNotEmpty()) _uiState.value.output else _uiState.value.input
        val res = LineSorterEngine.deduplicate(target, dedupeType)
        updateOutput(res)
    }

    fun trimLines() {
        val target = if (_uiState.value.output.isNotEmpty()) _uiState.value.output else _uiState.value.input
        val res = LineSorterEngine.cleanLines(target, trimWhitespace = true, removeEmpty = false, addLineNumbers = false)
        updateOutput(res)
    }

    fun removeEmptyLines() {
        val target = if (_uiState.value.output.isNotEmpty()) _uiState.value.output else _uiState.value.input
        val res = LineSorterEngine.cleanLines(target, trimWhitespace = false, removeEmpty = true, addLineNumbers = false)
        updateOutput(res)
    }

    fun addLineNumbers() {
        val target = if (_uiState.value.output.isNotEmpty()) _uiState.value.output else _uiState.value.input
        val res = LineSorterEngine.cleanLines(target, trimWhitespace = false, removeEmpty = false, addLineNumbers = true)
        updateOutput(res)
    }

    fun applyPrefixSuffix(prefix: String, suffix: String) {
        val target = if (_uiState.value.output.isNotEmpty()) _uiState.value.output else _uiState.value.input
        val res = LineSorterEngine.cleanLines(target, trimWhitespace = false, removeEmpty = false, addLineNumbers = false, prefix = prefix, suffix = suffix)
        updateOutput(res)
    }

    private fun updateOutput(newOutput: String) {
        val freqs = LineSorterEngine.analyzeWordFrequency(newOutput)
        val wordCount = freqs.sumOf { it.count }
        _uiState.update {
            it.copy(
                output = newOutput,
                wordFrequencies = freqs,
                totalLines = if (newOutput.isBlank()) 0 else newOutput.lines().size,
                totalWords = wordCount
            )
        }
    }

    fun loadSample() {
        val sample = """banana
apple
Orange
Apple
banana
grape 10
grape 2
banana
watermelon
peach
apple""".trimIndent()
        onInputChanged(sample)
        _uiState.update { it.copy(output = sample) }
    }

    fun clear() {
        _uiState.update { LineSorterUiState() }
    }
}
