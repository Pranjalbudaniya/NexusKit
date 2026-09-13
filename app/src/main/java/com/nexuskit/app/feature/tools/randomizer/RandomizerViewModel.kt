package com.nexuskit.app.feature.tools.randomizer

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

enum class RandomizerMode { NUMBERS, DICE, COIN, LIST }

data class RandomizerUiState(
    val selectedMode: RandomizerMode = RandomizerMode.NUMBERS,
    // Numbers
    val minNumber: Int = 1,
    val maxNumber: Int = 100,
    val numberCount: Int = 1,
    val allowDuplicates: Boolean = false,
    val numberResult: NumberRangeResult = RandomizerEngine.generateNumbers(1, 100, 1),
    // Dice
    val selectedDiceType: DiceType = DiceType.D6,
    val diceCount: Int = 2,
    val diceResult: DiceRollResult = RandomizerEngine.rollDice(DiceType.D6, 2),
    // Coin
    val coinResult: String = "Heads",
    val headsCount: Int = 0,
    val tailsCount: Int = 0,
    // List Decision
    val listInputText: String = "Pizza\nBurger\nSushi\nTacos\nPasta",
    val pickedItem: String? = null,
    val shuffledList: List<String> = emptyList()
)

@HiltViewModel
class RandomizerViewModel @Inject constructor(
    private val trackToolOpenedUseCase: TrackToolOpenedUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RandomizerUiState())
    val uiState: StateFlow<RandomizerUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            trackToolOpenedUseCase(TrackToolParams("randomizer"))
        }
    }

    fun onModeSelected(mode: RandomizerMode) {
        _uiState.update { it.copy(selectedMode = mode) }
    }

    // Numbers
    fun onRangeChanged(min: Int, max: Int, count: Int, duplicates: Boolean) {
        _uiState.update {
            it.copy(
                minNumber = min,
                maxNumber = max,
                numberCount = count,
                allowDuplicates = duplicates
            )
        }
    }

    fun generateNumbers() {
        val s = _uiState.value
        val res = RandomizerEngine.generateNumbers(s.minNumber, s.maxNumber, s.numberCount, !s.allowDuplicates)
        _uiState.update { it.copy(numberResult = res) }
    }

    // Dice
    fun onDiceTypeSelected(type: DiceType) {
        _uiState.update { it.copy(selectedDiceType = type) }
        rollDice()
    }

    fun onDiceCountChanged(count: Int) {
        _uiState.update { it.copy(diceCount = count.coerceIn(1, 10)) }
        rollDice()
    }

    fun rollDice() {
        val s = _uiState.value
        val res = RandomizerEngine.rollDice(s.selectedDiceType, s.diceCount)
        _uiState.update { it.copy(diceResult = res) }
    }

    // Coin
    fun flipCoin() {
        val side = RandomizerEngine.flipCoin()
        _uiState.update {
            it.copy(
                coinResult = side,
                headsCount = if (side == "Heads") it.headsCount + 1 else it.headsCount,
                tailsCount = if (side == "Tails") it.tailsCount + 1 else it.tailsCount
            )
        }
    }

    fun resetCoinScores() {
        _uiState.update { it.copy(headsCount = 0, tailsCount = 0) }
    }

    // List
    fun onListInputChanged(text: String) {
        _uiState.update { it.copy(listInputText = text) }
    }

    fun pickFromList() {
        val items = _uiState.value.listInputText.lines()
        val picked = RandomizerEngine.pickFromList(items)
        _uiState.update { it.copy(pickedItem = picked) }
    }

    fun shuffleList() {
        val items = _uiState.value.listInputText.lines()
        val shuffled = RandomizerEngine.shuffleList(items)
        _uiState.update { it.copy(shuffledList = shuffled) }
    }
}
