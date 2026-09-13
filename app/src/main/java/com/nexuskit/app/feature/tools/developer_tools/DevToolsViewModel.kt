package com.nexuskit.app.feature.tools.developer_tools

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

data class DevToolsUiState(
    val selectedTab: DevToolTab = DevToolTab.JSON_FORMATTER,
    // JSON
    val jsonInput: String = "{\"name\":\"NexusKit\",\"offline\":true,\"version\":2.0,\"tools\":[\"JSON\",\"Regex\",\"CRON\"]}",
    val jsonOutput: String = "",
    val isJsonValid: Boolean = true,
    // Regex
    val regexPattern: String = "([a-zA-Z0-9._%+-]+)@([a-zA-Z0-9.-]+\\.[a-zA-Z]{2,})",
    val regexSampleText: String = "Contact team at support@nexuskit.app or dev@google.com for info.",
    val regexCaseInsensitive: Boolean = true,
    val regexDotAll: Boolean = false,
    val regexMatches: List<RegexMatchResult> = emptyList(),
    val isRegexValid: Boolean = true,
    // CRON
    val cronMinute: String = "*/15",
    val cronHour: String = "*",
    val cronDayOfMonth: String = "*",
    val cronMonth: String = "*",
    val cronDayOfWeek: String = "*",
    val cronExplanation: String = "",
    // SQL
    val sqlInput: String = "SELECT users.id, users.name, orders.total FROM users LEFT JOIN orders ON users.id = orders.user_id WHERE orders.status = 'completed' ORDER BY orders.created_at DESC LIMIT 10",
    val sqlOutput: String = "",
    // HTTP Codes Search
    val httpSearchQuery: String = ""
)

@HiltViewModel
class DevToolsViewModel @Inject constructor(
    application: Application,
    private val trackToolOpenedUseCase: TrackToolOpenedUseCase
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(DevToolsUiState())
    val uiState: StateFlow<DevToolsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            trackToolOpenedUseCase(TrackToolParams("developer_tools"))
        }
        formatJson()
        testRegex()
        explainCron()
        formatSql()
    }

    fun onTabSelected(tab: DevToolTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun onJsonInputChanged(input: String) {
        _uiState.update { it.copy(jsonInput = input) }
        formatJson()
    }

    fun formatJson(indent: Int = 2) {
        val (valid, res) = DevToolsEngine.formatJson(_uiState.value.jsonInput, indent)
        _uiState.update { it.copy(jsonOutput = res, isJsonValid = valid) }
    }

    fun minifyJson() {
        val (valid, res) = DevToolsEngine.minifyJson(_uiState.value.jsonInput)
        _uiState.update { it.copy(jsonOutput = res, isJsonValid = valid) }
    }

    fun onRegexPatternChanged(pattern: String) {
        _uiState.update { it.copy(regexPattern = pattern) }
        testRegex()
    }

    fun onRegexSampleChanged(sample: String) {
        _uiState.update { it.copy(regexSampleText = sample) }
        testRegex()
    }

    fun toggleRegexCase() {
        _uiState.update { it.copy(regexCaseInsensitive = !it.regexCaseInsensitive) }
        testRegex()
    }

    fun toggleRegexDotAll() {
        _uiState.update { it.copy(regexDotAll = !it.regexDotAll) }
        testRegex()
    }

    private fun testRegex() {
        val (valid, matches) = DevToolsEngine.testRegex(
            _uiState.value.regexPattern,
            _uiState.value.regexSampleText,
            _uiState.value.regexCaseInsensitive,
            _uiState.value.regexDotAll
        )
        _uiState.update { it.copy(isRegexValid = valid, regexMatches = matches) }
    }

    fun onCronFieldsChanged(min: String, hr: String, dom: String, mon: String, dow: String) {
        _uiState.update {
            it.copy(cronMinute = min, cronHour = hr, cronDayOfMonth = dom, cronMonth = mon, cronDayOfWeek = dow)
        }
        explainCron()
    }

    private fun explainCron() {
        val exp = DevToolsEngine.explainCron(
            _uiState.value.cronMinute,
            _uiState.value.cronHour,
            _uiState.value.cronDayOfMonth,
            _uiState.value.cronMonth,
            _uiState.value.cronDayOfWeek
        )
        _uiState.update { it.copy(cronExplanation = exp) }
    }

    fun onSqlInputChanged(sql: String) {
        _uiState.update { it.copy(sqlInput = sql) }
        formatSql()
    }

    private fun formatSql() {
        val formatted = DevToolsEngine.formatSql(_uiState.value.sqlInput)
        _uiState.update { it.copy(sqlOutput = formatted) }
    }

    fun onHttpSearchChanged(q: String) {
        _uiState.update { it.copy(httpSearchQuery = q) }
    }
}
