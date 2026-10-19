package com.nexuskit.app.feature.tools.cron_explainer

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

data class CronUiState(
    val expression: String = "",
    val result: CronExplanationResult? = null,
    val errorMessage: String? = null
)

@HiltViewModel
class CronViewModel @Inject constructor(
    application: Application,
    private val trackToolOpenedUseCase: TrackToolOpenedUseCase
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(CronUiState())
    val uiState: StateFlow<CronUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            trackToolOpenedUseCase(TrackToolParams("cron_explainer"))
        }
        // Initial clean state with empty input
    }

    fun onExpressionChanged(expr: String) {
        val trimmed = expr.trim()
        if (trimmed.isEmpty()) {
            _uiState.update { it.copy(expression = expr, result = null, errorMessage = null) }
            return
        }

        val res = CronEngine.explain(trimmed)
        res.fold(
            onSuccess = { explanation ->
                _uiState.update { it.copy(expression = expr, result = explanation, errorMessage = null) }
            },
            onFailure = { err ->
                _uiState.update {
                    it.copy(
                        expression = expr,
                        result = null,
                        errorMessage = err.localizedMessage ?: "Invalid cron expression"
                    )
                }
            }
        )
    }

    fun applyPreset(cronExpr: String) {
        onExpressionChanged(cronExpr)
    }
}
