package com.nexuskit.app.feature.tools.discount_calculator

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

data class DiscountUiState(
    val originalPriceStr: String = "100.00",
    val discountPercent: Float = 20f,
    val additionalDiscountPercent: Float = 0f,
    val taxPercent: Float = 0f,
    val currencySymbol: String = "$",
    val result: DiscountResult = DiscountEngine.calculate(100.0, 20.0, 0.0, 0.0)
)

@HiltViewModel
class DiscountViewModel @Inject constructor(
    private val trackToolOpenedUseCase: TrackToolOpenedUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DiscountUiState())
    val uiState: StateFlow<DiscountUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            trackToolOpenedUseCase(TrackToolParams("discount_calculator"))
        }
    }

    fun onPriceChanged(priceStr: String) {
        _uiState.update { it.copy(originalPriceStr = priceStr) }
        recalculate()
    }

    fun onDiscountPercentChanged(percent: Float) {
        _uiState.update { it.copy(discountPercent = percent) }
        recalculate()
    }

    fun onAdditionalDiscountChanged(percent: Float) {
        _uiState.update { it.copy(additionalDiscountPercent = percent) }
        recalculate()
    }

    fun onTaxChanged(percent: Float) {
        _uiState.update { it.copy(taxPercent = percent) }
        recalculate()
    }

    fun onCurrencySelected(symbol: String) {
        _uiState.update { it.copy(currencySymbol = symbol) }
    }

    private fun recalculate() {
        val s = _uiState.value
        val price = s.originalPriceStr.toDoubleOrNull() ?: 0.0
        val res = DiscountEngine.calculate(
            originalPrice = price,
            discountPercent = s.discountPercent.toDouble(),
            additionalDiscountPercent = s.additionalDiscountPercent.toDouble(),
            taxPercent = s.taxPercent.toDouble()
        )
        _uiState.update { it.copy(result = res) }
    }
}
