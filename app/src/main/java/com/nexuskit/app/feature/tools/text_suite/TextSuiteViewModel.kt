package com.nexuskit.app.feature.tools.text_suite

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

data class TextSuiteUiState(
    val selectedTab: TextTab = TextTab.WORD_COUNTER,
    // Word counter
    val wordCounterInput: String = "NexusKit brings 175+ powerful utility tools together in a single, beautifully customizable offline Android app. Clean design, zero ads, zero tracking.",
    val stats: TextStatistics = TextSuiteEngine.analyzeText("NexusKit brings 175+ powerful utility tools together in a single, beautifully customizable offline Android app. Clean design, zero ads, zero tracking."),
    // Cleaner & Sorter
    val cleanerInput: String = "  Apple\nOrange\n  Apple  \n\nBanana\n  Orange\nMango  ",
    val cleanerOutput: String = "",
    // Reverser & Palindrome
    val reverserInput: String = "A man, a plan, a canal: Panama",
    val isPalindrome: Boolean = true,
    val palindromeMessage: String = "",
    val reversedOutput: String = "",
    // Repeater & Truncator
    val repeaterText: String = "NexusKit ",
    val repeaterCount: Int = 5,
    val repeaterOutput: String = "",
    val truncatorLimit: Int = 50,
    val truncatorOutput: String = "",
    // Lorem Ipsum
    val loremParagraphs: Int = 2,
    val loremOutput: String = TextSuiteEngine.generateLoremIpsum(2),
    // Extractor
    val extractorInput: String = "Reach out at contact@nexuskit.app, support@google.com or visit https://nexuskit.app and call (555) 123-4567.",
    val extractedEmails: List<String> = emptyList(),
    val extractedUrls: List<String> = emptyList(),
    val extractedPhones: List<String> = emptyList()
)

@HiltViewModel
class TextSuiteViewModel @Inject constructor(
    application: Application,
    private val trackToolOpenedUseCase: TrackToolOpenedUseCase
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(TextSuiteUiState())
    val uiState: StateFlow<TextSuiteUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            trackToolOpenedUseCase(TrackToolParams("text_suite"))
        }
        updateReverserAndPalindrome(_uiState.value.reverserInput)
        updateRepeaterAndTruncator()
        extractAll()
    }

    fun onTabSelected(tab: TextTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun onWordCounterInputChanged(text: String) {
        val s = TextSuiteEngine.analyzeText(text)
        _uiState.update { it.copy(wordCounterInput = text, stats = s) }
    }

    fun onCleanerInputChanged(text: String) {
        _uiState.update { it.copy(cleanerInput = text) }
    }

    fun cleanWhitespace() {
        val res = TextSuiteEngine.cleanWhitespace(_uiState.value.cleanerInput)
        _uiState.update { it.copy(cleanerOutput = res) }
    }

    fun removeDuplicates() {
        val res = TextSuiteEngine.removeDuplicateLines(_uiState.value.cleanerInput)
        _uiState.update { it.copy(cleanerOutput = res) }
    }

    fun sortLinesAscending() {
        val res = TextSuiteEngine.sortLines(_uiState.value.cleanerInput, ascending = true)
        _uiState.update { it.copy(cleanerOutput = res) }
    }

    fun sortLinesDescending() {
        val res = TextSuiteEngine.sortLines(_uiState.value.cleanerInput, ascending = false)
        _uiState.update { it.copy(cleanerOutput = res) }
    }

    fun onReverserInputChanged(text: String) {
        _uiState.update { it.copy(reverserInput = text) }
        updateReverserAndPalindrome(text)
    }

    private fun updateReverserAndPalindrome(text: String) {
        val rev = TextSuiteEngine.reverseText(text)
        val (isPal, msg) = TextSuiteEngine.checkPalindrome(text)
        _uiState.update { it.copy(reversedOutput = rev, isPalindrome = isPal, palindromeMessage = msg) }
    }

    fun onRepeaterCountChanged(count: Int) {
        _uiState.update { it.copy(repeaterCount = count) }
        updateRepeaterAndTruncator()
    }

    fun onRepeaterTextChanged(text: String) {
        _uiState.update { it.copy(repeaterText = text) }
        updateRepeaterAndTruncator()
    }

    fun onTruncatorLimitChanged(limit: Int) {
        _uiState.update { it.copy(truncatorLimit = limit) }
        updateRepeaterAndTruncator()
    }

    private fun updateRepeaterAndTruncator() {
        val rep = TextSuiteEngine.repeatText(_uiState.value.repeaterText, _uiState.value.repeaterCount, "\n")
        val tr = TextSuiteEngine.truncateText(_uiState.value.wordCounterInput, _uiState.value.truncatorLimit)
        _uiState.update { it.copy(repeaterOutput = rep, truncatorOutput = tr) }
    }

    fun onLoremParagraphsChanged(p: Int) {
        val out = TextSuiteEngine.generateLoremIpsum(p)
        _uiState.update { it.copy(loremParagraphs = p, loremOutput = out) }
    }

    fun onExtractorInputChanged(text: String) {
        _uiState.update { it.copy(extractorInput = text) }
        extractAll()
    }

    private fun extractAll() {
        val text = _uiState.value.extractorInput
        val emails = TextSuiteEngine.extractEmails(text)
        val urls = TextSuiteEngine.extractUrls(text)
        val phones = TextSuiteEngine.extractPhones(text)
        _uiState.update { it.copy(extractedEmails = emails, extractedUrls = urls, extractedPhones = phones) }
    }
}
