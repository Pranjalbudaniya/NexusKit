package com.nexuskit.app.feature.tools.lorem_generator

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

data class LoremUiState(
    val type: LoremType = LoremType.PARAGRAPHS,
    val count: Int = 3,
    val theme: LoremTheme = LoremTheme.CLASSIC,
    val startWithLorem: Boolean = true,
    val htmlWrap: Boolean = false,
    val generated: LoremGeneratedResult? = null
)

@HiltViewModel
class LoremViewModel @Inject constructor(
    application: Application,
    private val trackToolOpenedUseCase: TrackToolOpenedUseCase
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(LoremUiState())
    val uiState: StateFlow<LoremUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            trackToolOpenedUseCase(TrackToolParams("lorem_generator"))
        }
        generate()
    }

    fun onTypeChanged(type: LoremType) {
        _uiState.update { it.copy(type = type) }
        generate()
    }

    fun onCountChanged(count: Int) {
        _uiState.update { it.copy(count = count.coerceIn(1, 50)) }
        generate()
    }

    fun onThemeChanged(theme: LoremTheme) {
        _uiState.update { it.copy(theme = theme) }
        generate()
    }

    fun onStartWithLoremToggled(enabled: Boolean) {
        _uiState.update { it.copy(startWithLorem = enabled) }
        generate()
    }

    fun onHtmlWrapToggled(enabled: Boolean) {
        _uiState.update { it.copy(htmlWrap = enabled) }
        generate()
    }

    fun generate() {
        val s = _uiState.value
        val res = LoremEngine.generate(
            type = s.type,
            count = s.count,
            theme = s.theme,
            startWithLorem = s.startWithLorem,
            htmlWrap = s.htmlWrap
        )
        _uiState.update { it.copy(generated = res) }
    }
}
