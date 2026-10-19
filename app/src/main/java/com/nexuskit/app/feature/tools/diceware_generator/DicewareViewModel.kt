package com.nexuskit.app.feature.tools.diceware_generator

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

data class DicewareUiState(
    val wordCount: Int = 5,
    val delimiter: String = "-",
    val capitalize: Boolean = true,
    val includeNumber: Boolean = true,
    val includeSymbol: Boolean = false,
    val generatedPassphrase: GeneratedPassphrase? = null,
    val customPasswordInput: String = "",
    val passwordStrength: PasswordStrength = DicewareEngine.evaluatePasswordStrength("")
)

@HiltViewModel
class DicewareViewModel @Inject constructor(
    application: Application,
    private val trackToolOpenedUseCase: TrackToolOpenedUseCase
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(DicewareUiState())
    val uiState: StateFlow<DicewareUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            trackToolOpenedUseCase(TrackToolParams("diceware_generator"))
        }
        generate()
    }

    fun onWordCountChanged(count: Int) {
        _uiState.update { it.copy(wordCount = count.coerceIn(3, 10)) }
        generate()
    }

    fun onDelimiterChanged(delim: String) {
        _uiState.update { it.copy(delimiter = delim) }
        generate()
    }

    fun onCapitalizeToggled(enabled: Boolean) {
        _uiState.update { it.copy(capitalize = enabled) }
        generate()
    }

    fun onIncludeNumberToggled(enabled: Boolean) {
        _uiState.update { it.copy(includeNumber = enabled) }
        generate()
    }

    fun onIncludeSymbolToggled(enabled: Boolean) {
        _uiState.update { it.copy(includeSymbol = enabled) }
        generate()
    }

    fun generate() {
        val s = _uiState.value
        val res = DicewareEngine.generatePassphrase(
            wordCount = s.wordCount,
            delimiter = s.delimiter,
            capitalize = s.capitalize,
            includeNumber = s.includeNumber,
            includeSymbol = s.includeSymbol
        )
        _uiState.update { it.copy(generatedPassphrase = res) }
    }

    fun onCustomPasswordChanged(text: String) {
        val strength = DicewareEngine.evaluatePasswordStrength(text)
        _uiState.update { it.copy(customPasswordInput = text, passwordStrength = strength) }
    }

    fun testGeneratedInAuditor() {
        val pass = _uiState.value.generatedPassphrase?.passphrase ?: return
        onCustomPasswordChanged(pass)
    }
}
