package com.nexuskit.app.feature.search

import androidx.lifecycle.viewModelScope
import com.nexuskit.app.core.base.BaseViewModel
import com.nexuskit.app.core.base.Resource
import com.nexuskit.app.core.util.AppConstants
import com.nexuskit.app.domain.model.SearchResult
import com.nexuskit.app.domain.model.enums.SearchQueryType
import com.nexuskit.app.domain.usecase.search.ParseSearchParams
import com.nexuskit.app.domain.usecase.search.ParseSearchQueryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// ── UI State ─────────────────────────────────────────────────────────────────

data class SearchUiState(
    val query: String = "",
    val result: SearchResult = SearchResult(),
    val isLoading: Boolean = false,
    val error: String? = null
) {
    val isEmpty: Boolean get() = query.isBlank()
    val hasResults: Boolean get() = !isEmpty && result.queryType != SearchQueryType.EMPTY
}

// ── ViewModel ─────────────────────────────────────────────────────────────────

@OptIn(FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val parseSearchQuery: ParseSearchQueryUseCase
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private val queryFlow = MutableStateFlow("")

    init {
        // Debounce keystrokes — only parse after user stops typing for 300ms
        viewModelScope.launch {
            queryFlow
                .debounce(AppConstants.SEARCH_DEBOUNCE_MS)
                .distinctUntilChanged()
                .collect { query -> parseQuery(query) }
        }
    }

    fun onQueryChanged(query: String) {
        _uiState.update { it.copy(query = query) }
        queryFlow.value = query
        // If query is blank, clear results immediately (no debounce needed)
        if (query.isBlank()) {
            _uiState.update { it.copy(result = SearchResult(), isLoading = false) }
        } else {
            _uiState.update { it.copy(isLoading = true) }
        }
    }

    fun onClear() {
        _uiState.update { SearchUiState() }
        queryFlow.value = ""
    }

    private fun parseQuery(query: String) {
        if (query.isBlank()) return
        launchSafe {
            when (val resource = parseSearchQuery(ParseSearchParams(query))) {
                is Resource.Success -> _uiState.update {
                    it.copy(result = resource.data, isLoading = false, error = null)
                }
                is Resource.Error   -> _uiState.update {
                    it.copy(error = resource.message, isLoading = false)
                }
                else -> {}
            }
        }
    }

    override fun handleException(throwable: Throwable) {
        _uiState.update { it.copy(error = throwable.message, isLoading = false) }
    }
}
