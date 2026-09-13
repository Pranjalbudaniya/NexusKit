package com.nexuskit.app.feature.tools.hash_generator

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

data class HashUiState(
    val inputText: String = "Hello NexusKit",
    val hmacKey: String = "",
    val uppercase: Boolean = false,
    val hashes: List<HashResultItem> = HashEngine.generateHashes("Hello NexusKit", "", false)
)

@HiltViewModel
class HashViewModel @Inject constructor(
    private val trackToolOpenedUseCase: TrackToolOpenedUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HashUiState())
    val uiState: StateFlow<HashUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            trackToolOpenedUseCase(TrackToolParams("hash_generator"))
        }
    }

    fun onInputChanged(input: String) {
        _uiState.update { it.copy(inputText = input) }
        recalculate()
    }

    fun onHmacKeyChanged(key: String) {
        _uiState.update { it.copy(hmacKey = key) }
        recalculate()
    }

    fun onUppercaseToggled(uppercase: Boolean) {
        _uiState.update { it.copy(uppercase = uppercase) }
        recalculate()
    }

    fun onClear() {
        onInputChanged("")
    }

    private fun recalculate() {
        val s = _uiState.value
        val list = HashEngine.generateHashes(s.inputText, s.hmacKey, s.uppercase)
        _uiState.update { it.copy(hashes = list) }
    }
}
