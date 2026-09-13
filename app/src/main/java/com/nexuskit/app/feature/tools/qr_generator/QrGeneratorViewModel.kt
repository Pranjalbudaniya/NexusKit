package com.nexuskit.app.feature.tools.qr_generator

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexuskit.app.domain.usecase.tool.TrackToolOpenedUseCase
import com.nexuskit.app.domain.usecase.tool.TrackToolParams
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

data class QrGeneratorUiState(
    val selectedType: QrType = QrType.TEXT,
    // Fields
    val textContent: String = "https://github.com",
    val urlContent: String = "https://",
    val wifiSsid: String = "",
    val wifiPassword: String = "",
    val wifiSecurity: String = "WPA",
    val emailAddress: String = "",
    val emailSubject: String = "",
    val emailBody: String = "",
    val phoneNumber: String = "",
    val smsMessage: String = "",
    // Generated payload
    val rawPayload: String = "https://github.com",
    val qrBitmap: Bitmap? = null,
    val isGenerating: Boolean = false
)

@HiltViewModel
class QrGeneratorViewModel @Inject constructor(
    private val trackToolOpenedUseCase: TrackToolOpenedUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(QrGeneratorUiState())
    val uiState: StateFlow<QrGeneratorUiState> = _uiState.asStateFlow()

    private var qrJob: Job? = null

    init {
        viewModelScope.launch {
            trackToolOpenedUseCase(TrackToolParams("qr_generator"))
        }
        generateQr()
    }

    fun onTypeSelected(type: QrType) {
        _uiState.update { it.copy(selectedType = type) }
        updatePayloadAndGenerate()
    }

    fun onTextChanged(text: String) {
        _uiState.update { it.copy(textContent = text) }
        updatePayloadAndGenerate()
    }

    fun onUrlChanged(url: String) {
        _uiState.update { it.copy(urlContent = url) }
        updatePayloadAndGenerate()
    }

    fun onWifiChanged(ssid: String, pass: String, security: String) {
        _uiState.update {
            it.copy(
                wifiSsid = ssid,
                wifiPassword = pass,
                wifiSecurity = security
            )
        }
        updatePayloadAndGenerate()
    }

    fun onEmailChanged(address: String, subject: String, body: String) {
        _uiState.update {
            it.copy(
                emailAddress = address,
                emailSubject = subject,
                emailBody = body
            )
        }
        updatePayloadAndGenerate()
    }

    fun onPhoneChanged(phone: String, sms: String) {
        _uiState.update {
            it.copy(
                phoneNumber = phone,
                smsMessage = sms
            )
        }
        updatePayloadAndGenerate()
    }

    private fun updatePayloadAndGenerate() {
        val state = _uiState.value
        val payload = when (state.selectedType) {
            QrType.TEXT -> state.textContent
            QrType.URL -> state.urlContent
            QrType.WIFI -> QrCodeEngine.buildWifiString(state.wifiSsid, state.wifiPassword, state.wifiSecurity)
            QrType.EMAIL -> QrCodeEngine.buildEmailString(state.emailAddress, state.emailSubject, state.emailBody)
            QrType.PHONE -> if (state.smsMessage.isBlank()) QrCodeEngine.buildPhoneString(state.phoneNumber) else QrCodeEngine.buildSmsString(state.phoneNumber, state.smsMessage)
        }
        _uiState.update { it.copy(rawPayload = payload) }
        generateQr()
    }

    private fun generateQr() {
        val payload = _uiState.value.rawPayload
        qrJob?.cancel()
        qrJob = viewModelScope.launch {
            _uiState.update { it.copy(isGenerating = true) }
            val bitmap = withContext(Dispatchers.Default) {
                QrCodeEngine.generateQrBitmap(payload)
            }
            _uiState.update { it.copy(qrBitmap = bitmap, isGenerating = false) }
        }
    }
}
