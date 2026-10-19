package com.nexuskit.app.feature.tools.jwt_decoder

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

data class JwtUiState(
    val tokenInput: String = "",
    val secretKeyInput: String = "",
    val decodeResult: JwtDecodeResult? = null,
    val errorMessage: String? = null,
    val isSignatureVerified: Boolean? = null
)

@HiltViewModel
class JwtViewModel @Inject constructor(
    application: Application,
    private val trackToolOpenedUseCase: TrackToolOpenedUseCase
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(JwtUiState())
    val uiState: StateFlow<JwtUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            trackToolOpenedUseCase(TrackToolParams("jwt_decoder"))
        }
    }

    fun onTokenChanged(token: String) {
        val clean = token.trim()
        if (clean.isBlank()) {
            _uiState.update { it.copy(tokenInput = token, decodeResult = null, errorMessage = null, isSignatureVerified = null) }
            return
        }

        val decodeRes = JwtEngine.decodeJwt(clean)
        decodeRes.fold(
            onSuccess = { res ->
                val verified = if (_uiState.value.secretKeyInput.isNotEmpty()) {
                    JwtEngine.verifyHmacSignature(clean, _uiState.value.secretKeyInput)
                } else null

                _uiState.update {
                    it.copy(
                        tokenInput = token,
                        decodeResult = res,
                        errorMessage = null,
                        isSignatureVerified = verified
                    )
                }
            },
            onFailure = { err ->
                _uiState.update {
                    it.copy(
                        tokenInput = token,
                        decodeResult = null,
                        errorMessage = err.localizedMessage ?: "Invalid JWT token structure",
                        isSignatureVerified = null
                    )
                }
            }
        )
    }

    fun onSecretKeyChanged(secret: String) {
        val token = _uiState.value.tokenInput
        val verified = if (secret.isNotEmpty() && token.isNotEmpty()) {
            JwtEngine.verifyHmacSignature(token, secret)
        } else null

        _uiState.update { it.copy(secretKeyInput = secret, isSignatureVerified = verified) }
    }

    fun loadSampleToken() {
        val sample = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkFsZXggRG9lIiwiYWRtaW4iOnRydWUsImlhdCI6MTUxNjIzOTAyMiwiZXhwIjoxOTAwMDAwMDAwfQ.4fl_cM2bKuhm5m_wQf9X9z1b9F3p8p5O7G9V-X0sU-g"
        onTokenChanged(sample)
    }

    fun clear() {
        _uiState.update { JwtUiState() }
    }
}
