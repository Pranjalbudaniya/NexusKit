package com.nexuskit.app.feature.tools.text_suite

import android.widget.Toast
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.nexuskit.app.navigation.DrawerNavigation
import com.nexuskit.app.ui.theme.LocalSpacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextSuiteScreen(
    navController: NavHostController,
    onBack: () -> Unit,
    initialTab: TextTab = TextTab.WORD_COUNTER,
    viewModel: TextSuiteViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val spacing = LocalSpacing.current
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    LaunchedEffect(initialTab) {
        viewModel.onTabSelected(initialTab)
    }

    DrawerNavigation(
        navController = navController,
        currentToolId = "text_suite"
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Text & Writing Suite",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold)
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background
                    )
                )
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // ── Tab selector ─────────────────────────────────────────────
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = spacing.md, vertical = spacing.xs)
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(spacing.xs)
                ) {
                    val tabs = listOf(
                        TextTab.WORD_COUNTER to "Word Counter",
                        TextTab.CLEANER_SORTER to "Cleaner & Sorter",
                        TextTab.REVERSER_PALINDROME to "Reverser & Palindrome",
                        TextTab.REPEATER_TRUNCATOR to "Repeater & Truncator",
                        TextTab.LOREM_IPSUM to "Lorem Ipsum",
                        TextTab.EXTRACTOR to "Info Extractor"
                    )
                    tabs.forEach { (tab, label) ->
                        FilterChip(
                            selected = uiState.selectedTab == tab,
                            onClick = { viewModel.onTabSelected(tab) },
                            label = { Text(label) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }
                }

                when (uiState.selectedTab) {
                    TextTab.WORD_COUNTER -> {
                        WordCounterView(uiState = uiState, viewModel = viewModel, spacing = spacing)
                    }
                    TextTab.CLEANER_SORTER -> {
                        CleanerSorterView(uiState = uiState, viewModel = viewModel, spacing = spacing, onCopy = {
                            clipboardManager.setText(AnnotatedString(uiState.cleanerOutput))
                            Toast.makeText(context, "Copied cleaned text", Toast.LENGTH_SHORT).show()
                        })
                    }
                    TextTab.REVERSER_PALINDROME -> {
                        ReverserPalindromeView(uiState = uiState, viewModel = viewModel, spacing = spacing)
                    }
                    TextTab.REPEATER_TRUNCATOR -> {
                        RepeaterTruncatorView(uiState = uiState, viewModel = viewModel, spacing = spacing)
                    }
                    TextTab.LOREM_IPSUM -> {
                        LoremIpsumView(uiState = uiState, viewModel = viewModel, spacing = spacing, onCopy = {
                            clipboardManager.setText(AnnotatedString(uiState.loremOutput))
                            Toast.makeText(context, "Copied Lorem Ipsum", Toast.LENGTH_SHORT).show()
                        })
                    }
                    TextTab.EXTRACTOR -> {
                        ExtractorView(uiState = uiState, viewModel = viewModel, spacing = spacing)
                    }
                }
            }
        }
    }
}

@Composable
private fun WordCounterView(uiState: TextSuiteUiState, viewModel: TextSuiteViewModel, spacing: com.nexuskit.app.ui.theme.NexusKitSpacing) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = spacing.md, vertical = spacing.sm),
        verticalArrangement = Arrangement.spacedBy(spacing.md)
    ) {
        item {
            OutlinedTextField(
                value = uiState.wordCounterInput,
                onValueChange = viewModel::onWordCounterInputChanged,
                label = { Text("Type or paste text to analyze") },
                modifier = Modifier.fillMaxWidth().height(160.dp),
                shape = RoundedCornerShape(12.dp)
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(modifier = Modifier.padding(spacing.md), verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
                    Text("Real-Time Text Statistics", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        StatBadge("Words", "${uiState.stats.words}")
                        StatBadge("Characters", "${uiState.stats.charactersWithSpaces}")
                        StatBadge("No Spaces", "${uiState.stats.charactersNoSpaces}")
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        StatBadge("Sentences", "${uiState.stats.sentences}")
                        StatBadge("Paragraphs", "${uiState.stats.paragraphs}")
                        StatBadge("Read Time", "~${uiState.stats.readingTimeSeconds}s")
                    }
                }
            }
        }
    }
}

@Composable
private fun StatBadge(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f))
    }
}

@Composable
private fun CleanerSorterView(uiState: TextSuiteUiState, viewModel: TextSuiteViewModel, spacing: com.nexuskit.app.ui.theme.NexusKitSpacing, onCopy: () -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = spacing.md, vertical = spacing.sm),
        verticalArrangement = Arrangement.spacedBy(spacing.sm)
    ) {
        item {
            OutlinedTextField(
                value = uiState.cleanerInput,
                onValueChange = viewModel::onCleanerInputChanged,
                label = { Text("Messy Lines / Text") },
                modifier = Modifier.fillMaxWidth().height(140.dp),
                shape = RoundedCornerShape(12.dp)
            )
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Button(onClick = viewModel::cleanWhitespace, modifier = Modifier.weight(1f)) { Text("Trim Spaces") }
                Button(onClick = viewModel::removeDuplicates, modifier = Modifier.weight(1f)) { Text("Dedup Lines") }
            }
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                OutlinedButton(onClick = viewModel::sortLinesAscending, modifier = Modifier.weight(1f)) { Text("Sort A → Z") }
                OutlinedButton(onClick = viewModel::sortLinesDescending, modifier = Modifier.weight(1f)) { Text("Sort Z → A") }
            }
        }

        if (uiState.cleanerOutput.isNotEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
                ) {
                    Column(modifier = Modifier.padding(spacing.md), verticalArrangement = Arrangement.spacedBy(spacing.xs)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("Cleaned / Sorted Result", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                            IconButton(onClick = onCopy) {
                                Icon(Icons.Filled.ContentCopy, contentDescription = "Copy", modifier = Modifier.size(18.dp))
                            }
                        }
                        Text(uiState.cleanerOutput, style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace))
                    }
                }
            }
        }
    }
}

@Composable
private fun ReverserPalindromeView(uiState: TextSuiteUiState, viewModel: TextSuiteViewModel, spacing: com.nexuskit.app.ui.theme.NexusKitSpacing) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = spacing.md, vertical = spacing.sm),
        verticalArrangement = Arrangement.spacedBy(spacing.md)
    ) {
        item {
            OutlinedTextField(
                value = uiState.reverserInput,
                onValueChange = viewModel::onReverserInputChanged,
                label = { Text("Input Text String") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (uiState.isPalindrome) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainer
                )
            ) {
                Column(modifier = Modifier.padding(spacing.md), verticalArrangement = Arrangement.spacedBy(spacing.xs)) {
                    Text("Palindrome Detection", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                    Text(uiState.palindromeMessage, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
            ) {
                Column(modifier = Modifier.padding(spacing.md), verticalArrangement = Arrangement.spacedBy(spacing.xs)) {
                    Text("Reversed Text", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                    Text(uiState.reversedOutput, style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace, color = MaterialTheme.colorScheme.primary))
                }
            }
        }
    }
}

@Composable
private fun RepeaterTruncatorView(uiState: TextSuiteUiState, viewModel: TextSuiteViewModel, spacing: com.nexuskit.app.ui.theme.NexusKitSpacing) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = spacing.md, vertical = spacing.sm),
        verticalArrangement = Arrangement.spacedBy(spacing.md)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
            ) {
                Column(modifier = Modifier.padding(spacing.md), verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
                    Text("Text Repeater", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                    OutlinedTextField(
                        value = uiState.repeaterText,
                        onValueChange = viewModel::onRepeaterTextChanged,
                        label = { Text("Text to Repeat") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                    Text("Repeat Count: ${uiState.repeaterCount}", style = MaterialTheme.typography.bodySmall)
                    Slider(
                        value = uiState.repeaterCount.toFloat(),
                        onValueChange = { viewModel.onRepeaterCountChanged(it.toInt()) },
                        valueRange = 1f..50f
                    )
                    Text(uiState.repeaterOutput, style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace))
                }
            }
        }
    }
}

@Composable
private fun LoremIpsumView(uiState: TextSuiteUiState, viewModel: TextSuiteViewModel, spacing: com.nexuskit.app.ui.theme.NexusKitSpacing, onCopy: () -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = spacing.md, vertical = spacing.sm),
        verticalArrangement = Arrangement.spacedBy(spacing.md)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
            ) {
                Column(modifier = Modifier.padding(spacing.md), verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Lorem Ipsum Generator", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                        IconButton(onClick = onCopy) {
                            Icon(Icons.Filled.ContentCopy, contentDescription = "Copy", modifier = Modifier.size(18.dp))
                        }
                    }
                    Text("Paragraphs: ${uiState.loremParagraphs}", style = MaterialTheme.typography.bodySmall)
                    Slider(
                        value = uiState.loremParagraphs.toFloat(),
                        onValueChange = { viewModel.onLoremParagraphsChanged(it.toInt()) },
                        valueRange = 1f..8f,
                        steps = 6
                    )
                    Text(uiState.loremOutput, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
private fun ExtractorView(uiState: TextSuiteUiState, viewModel: TextSuiteViewModel, spacing: com.nexuskit.app.ui.theme.NexusKitSpacing) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = spacing.md, vertical = spacing.sm),
        verticalArrangement = Arrangement.spacedBy(spacing.md)
    ) {
        item {
            OutlinedTextField(
                value = uiState.extractorInput,
                onValueChange = viewModel::onExtractorInputChanged,
                label = { Text("Paste block of text to scan") },
                modifier = Modifier.fillMaxWidth().height(120.dp),
                shape = RoundedCornerShape(12.dp)
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
            ) {
                Column(modifier = Modifier.padding(spacing.md), verticalArrangement = Arrangement.spacedBy(spacing.xs)) {
                    Text("Extracted Emails (${uiState.extractedEmails.size})", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                    if (uiState.extractedEmails.isEmpty()) Text("None found", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    else uiState.extractedEmails.forEach { Text("• $it", style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.primary)) }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                    Text("Extracted URLs (${uiState.extractedUrls.size})", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                    if (uiState.extractedUrls.isEmpty()) Text("None found", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    else uiState.extractedUrls.forEach { Text("• $it", style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.primary)) }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                    Text("Extracted Phone Numbers (${uiState.extractedPhones.size})", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                    if (uiState.extractedPhones.isEmpty()) Text("None found", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    else uiState.extractedPhones.forEach { Text("• $it", style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.primary)) }
                }
            }
        }
    }
}
