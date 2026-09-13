package com.nexuskit.app.feature.tools.password_generator

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

enum class SecurityToolMode(val displayName: String) {
    PASSWORD("Password"),
    UUID("UUID v4")
}

data class PasswordGeneratorUiState(
    val selectedMode: SecurityToolMode = SecurityToolMode.PASSWORD,
    // Password state
    val passwordLength: Int = 16,
    val includeUpper: Boolean = true,
    val includeLower: Boolean = true,
    val includeDigits: Boolean = true,
    val includeSymbols: Boolean = true,
    val excludeAmbiguous: Boolean = false,
    val generatedPassword: String = "",
    val passwordStrength: PasswordStrength = PasswordStrength.STRONG,
    // UUID state
    val uuidCount: Int = 5,
    val uuidUppercase: Boolean = false,
    val uuidIncludeHyphens: Boolean = true,
    val generatedUuids: List<String> = emptyList()
)

@HiltViewModel
class PasswordGeneratorViewModel @Inject constructor(
    private val trackToolOpenedUseCase: TrackToolOpenedUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(PasswordGeneratorUiState())
    val uiState: StateFlow<PasswordGeneratorUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            trackToolOpenedUseCase(TrackToolParams("password_generator"))
        }
        generateNewPassword()
        generateNewUuids()
    }

    fun onModeSelected(mode: SecurityToolMode) {
        _uiState.update { it.copy(selectedMode = mode) }
    }

    fun onLengthChanged(length: Int) {
        _uiState.update { it.copy(passwordLength = length) }
        generateNewPassword()
    }

    fun onToggleUpper(enabled: Boolean) {
        _uiState.update { it.copy(includeUpper = enabled) }
        generateNewPassword()
    }

    fun onToggleLower(enabled: Boolean) {
        _uiState.update { it.copy(includeLower = enabled) }
        generateNewPassword()
    }

    fun onToggleDigits(enabled: Boolean) {
        _uiState.update { it.copy(includeDigits = enabled) }
        generateNewPassword()
    }

    fun onToggleSymbols(enabled: Boolean) {
        _uiState.update { it.copy(includeSymbols = enabled) }
        generateNewPassword()
    }

    fun onToggleAmbiguous(enabled: Boolean) {
        _uiState.update { it.copy(excludeAmbiguous = enabled) }
        generateNewPassword()
    }

    fun generateNewPassword() {
        val state = _uiState.value
        val pass = SecurityEngine.generatePassword(
            length = state.passwordLength,
            includeUpper = state.includeUpper,
            includeLower = state.includeLower,
            includeDigits = state.includeDigits,
            includeSymbols = state.includeSymbols,
            excludeAmbiguous = state.excludeAmbiguous
        )
        val strength = SecurityEngine.calculateStrength(pass)
        _uiState.update {
            it.copy(
                generatedPassword = pass,
                passwordStrength = strength
            )
        }
    }

    fun onUuidCountChanged(count: Int) {
        _uiState.update { it.copy(uuidCount = count) }
        generateNewUuids()
    }

    fun onToggleUuidUppercase(enabled: Boolean) {
        _uiState.update { it.copy(uuidUppercase = enabled) }
        generateNewUuids()
    }

    fun onToggleUuidHyphens(enabled: Boolean) {
        _uiState.update { it.copy(uuidIncludeHyphens = enabled) }
        generateNewUuids()
    }

    fun generateNewUuids() {
        val state = _uiState.value
        val list = SecurityEngine.generateUuids(
            count = state.uuidCount,
            uppercase = state.uuidUppercase,
            includeHyphens = state.uuidIncludeHyphens
        )
        _uiState.update { it.copy(generatedUuids = list) }
    }
}
