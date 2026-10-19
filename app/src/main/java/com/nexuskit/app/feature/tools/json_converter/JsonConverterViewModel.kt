package com.nexuskit.app.feature.tools.json_converter

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

data class JsonConverterUiState(
    val mode: ConvertMode = ConvertMode.JSON_TO_CSV,
    val input: String = "",
    val output: String = "",
    val errorMessage: String? = null,
    val delimiter: Char = ',',
    val expandDots: Boolean = true
)

@HiltViewModel
class JsonConverterViewModel @Inject constructor(
    application: Application,
    private val trackToolOpenedUseCase: TrackToolOpenedUseCase
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(JsonConverterUiState())
    val uiState: StateFlow<JsonConverterUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            trackToolOpenedUseCase(TrackToolParams("json_converter"))
        }
    }

    fun onModeChanged(newMode: ConvertMode) {
        _uiState.update { it.copy(mode = newMode) }
        convert()
    }

    fun onInputChanged(newInput: String) {
        _uiState.update { it.copy(input = newInput) }
        convert()
    }

    fun onDelimiterChanged(newDelimiter: Char) {
        _uiState.update { it.copy(delimiter = newDelimiter) }
        convert()
    }

    fun onExpandDotsToggled(enabled: Boolean) {
        _uiState.update { it.copy(expandDots = enabled) }
        convert()
    }

    private fun convert() {
        val s = _uiState.value
        val raw = s.input.trim()
        if (raw.isEmpty()) {
            _uiState.update { it.copy(output = "", errorMessage = null) }
            return
        }

        val result = when (s.mode) {
            ConvertMode.JSON_TO_CSV -> JsonConverterEngine.jsonToCsv(raw, s.delimiter)
            ConvertMode.CSV_TO_JSON -> JsonConverterEngine.csvToJson(raw, s.delimiter, s.expandDots)
            ConvertMode.JSON_TO_YAML -> JsonConverterEngine.jsonToYaml(raw)
            ConvertMode.YAML_TO_JSON -> JsonConverterEngine.yamlToJson(raw)
        }

        result.fold(
            onSuccess = { converted ->
                _uiState.update { it.copy(output = converted, errorMessage = null) }
            },
            onFailure = { err ->
                _uiState.update {
                    it.copy(
                        output = "",
                        errorMessage = err.localizedMessage ?: "Conversion error"
                    )
                }
            }
        )
    }

    fun swap() {
        val s = _uiState.value
        if (s.output.isBlank()) return
        val newMode = when (s.mode) {
            ConvertMode.JSON_TO_CSV -> ConvertMode.CSV_TO_JSON
            ConvertMode.CSV_TO_JSON -> ConvertMode.JSON_TO_CSV
            ConvertMode.JSON_TO_YAML -> ConvertMode.YAML_TO_JSON
            ConvertMode.YAML_TO_JSON -> ConvertMode.JSON_TO_YAML
        }
        _uiState.update {
            it.copy(
                mode = newMode,
                input = s.output,
                output = "",
                errorMessage = null
            )
        }
        convert()
    }

    fun loadSample() {
        val s = _uiState.value
        val sample = when (s.mode) {
            ConvertMode.JSON_TO_CSV, ConvertMode.JSON_TO_YAML -> """[
  {
    "id": 101,
    "name": "Sarah Connor",
    "role": "Lead Architect",
    "active": true,
    "contact": {
      "email": "sarah@cyberdyne.org",
      "city": "Los Angeles"
    }
  },
  {
    "id": 102,
    "name": "John Doe",
    "role": "Security Analyst",
    "active": false,
    "contact": {
      "email": "johndoe@proton.me",
      "city": "Berlin"
    }
  }
]""".trimIndent()
            ConvertMode.CSV_TO_JSON -> """id,name,role,active,contact.email,contact.city
101,Sarah Connor,Lead Architect,true,sarah@cyberdyne.org,Los Angeles
102,John Doe,Security Analyst,false,johndoe@proton.me,Berlin""".trimIndent()
            ConvertMode.YAML_TO_JSON -> """server:
  host: 127.0.0.1
  port: 8080
  debug: true
database:
  name: nexus_prod
  pool_size: 20
tags:
  - developer
  - offline
  - utilities""".trimIndent()
        }
        onInputChanged(sample)
    }

    fun clear() {
        _uiState.update { it.copy(input = "", output = "", errorMessage = null) }
    }
}
