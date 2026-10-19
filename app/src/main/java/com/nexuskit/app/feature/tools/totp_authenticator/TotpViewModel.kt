package com.nexuskit.app.feature.tools.totp_authenticator

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.nexuskit.app.domain.usecase.tool.TrackToolOpenedUseCase
import com.nexuskit.app.domain.usecase.tool.TrackToolParams
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TotpAccountUiItem(
    val account: TotpAccount,
    val currentCode: String,
    val formattedCode: String,
    val error: String? = null
)

data class TotpUiState(
    val items: List<TotpAccountUiItem> = emptyList(),
    val secondsRemaining: Int = 30,
    val progress: Float = 1.0f,
    val isAddDialogOpen: Boolean = false,
    val addError: String? = null
)

@HiltViewModel
class TotpViewModel @Inject constructor(
    application: Application,
    private val repository: TotpRepository,
    private val trackToolOpenedUseCase: TrackToolOpenedUseCase
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(TotpUiState())
    val uiState: StateFlow<TotpUiState> = _uiState.asStateFlow()

    private var tickingJob: Job? = null

    init {
        viewModelScope.launch {
            trackToolOpenedUseCase(TrackToolParams("totp_authenticator"))
        }

        viewModelScope.launch {
            repository.accounts.collect { accounts ->
                refreshCodes(accounts)
            }
        }

        startTicker()
    }

    private fun startTicker() {
        tickingJob?.cancel()
        tickingJob = viewModelScope.launch {
            while (isActive) {
                val nowSec = System.currentTimeMillis() / 1000L
                val remaining = (30 - (nowSec % 30)).toInt()
                val progress = remaining / 30f

                _uiState.update {
                    it.copy(
                        secondsRemaining = remaining,
                        progress = progress
                    )
                }

                // If window rolled over or at 30, recalculate codes
                if (remaining == 30 || remaining == 29) {
                    refreshCodes(repository.accounts.value)
                }

                delay(1000L)
            }
        }
    }

    private fun refreshCodes(accounts: List<TotpAccount>) {
        val nowSec = System.currentTimeMillis() / 1000L
        val items = accounts.map { acc ->
            val codeRes = TotpEngine.generateTotp(
                secretBase32 = acc.secretKey,
                epochSeconds = nowSec,
                period = acc.periodSeconds,
                digits = acc.digits,
                algorithm = acc.algorithm
            )
            val code = codeRes.getOrDefault("------")
            val error = codeRes.exceptionOrNull()?.localizedMessage
            TotpAccountUiItem(
                account = acc,
                currentCode = code,
                formattedCode = TotpEngine.formatCodeForDisplay(code),
                error = error
            )
        }
        _uiState.update { it.copy(items = items) }
    }

    fun openAddDialog() {
        _uiState.update { it.copy(isAddDialogOpen = true, addError = null) }
    }

    fun closeAddDialog() {
        _uiState.update { it.copy(isAddDialogOpen = false, addError = null) }
    }

    fun addManualAccount(name: String, issuer: String, secret: String) {
        val cleanSecret = TotpEngine.cleanBase32Secret(secret)
        if (cleanSecret.isEmpty()) {
            _uiState.update { it.copy(addError = "Secret key cannot be empty") }
            return
        }

        // Validate base32
        val testRes = TotpEngine.generateTotp(cleanSecret)
        if (testRes.isFailure) {
            _uiState.update { it.copy(addError = "Invalid Base32 secret key: ${testRes.exceptionOrNull()?.message}") }
            return
        }

        val account = TotpAccount(
            accountName = if (name.isBlank()) "Account" else name.trim(),
            issuer = if (issuer.isBlank()) "Service" else issuer.trim(),
            secretKey = cleanSecret
        )

        viewModelScope.launch {
            repository.addAccount(account)
            closeAddDialog()
        }
    }

    fun addFromUri(uri: String) {
        val parsed = TotpEngine.parseOtpAuthUri(uri)
        parsed.fold(
            onSuccess = { acc ->
                viewModelScope.launch {
                    repository.addAccount(acc)
                    closeAddDialog()
                }
            },
            onFailure = { err ->
                _uiState.update { it.copy(addError = err.localizedMessage ?: "Invalid URI") }
            }
        )
    }

    fun deleteAccount(id: String) {
        viewModelScope.launch {
            repository.deleteAccount(id)
        }
    }

    fun loadSample() {
        addManualAccount("alex@nexuskit.dev", "GitHub", "JBSWY3DPEHPK3PXP")
    }

    override fun onCleared() {
        super.onCleared()
        tickingJob?.cancel()
    }
}
