package com.nexuskit.app.feature.tools.line_sorter

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
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
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LineSorterScreen(
    navController: NavHostController,
    onBack: () -> Unit,
    viewModel: LineSorterViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val spacing = LocalSpacing.current
    val clipboardManager = LocalClipboardManager.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var selectedTab by remember { mutableIntStateOf(0) }
    val sortScrollState = rememberScrollState()
    val dedupeScrollState = rememberScrollState()

    DrawerNavigation(
        navController = navController,
        currentToolId = "line_sorter"
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Line Sorter & Frequency",
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
            snackbarHost = { SnackbarHost(snackbarHostState) },
            containerColor = MaterialTheme.colorScheme.background
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(horizontal = spacing.md, vertical = spacing.sm),
                verticalArrangement = Arrangement.spacedBy(spacing.md)
            ) {
                // Quick Action Bar
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(spacing.md)) {
                            Text(
                                text = "Sort Lines",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(spacing.xs))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(sortScrollState),
                                horizontalArrangement = Arrangement.spacedBy(spacing.xs)
                            ) {
                                FilterChip(
                                    selected = false,
                                    onClick = { viewModel.sort(LineSorterEngine.SortType.ALPHABETICAL_ASC) },
                                    label = { Text("A → Z", style = MaterialTheme.typography.labelSmall) }
                                )
                                FilterChip(
                                    selected = false,
                                    onClick = { viewModel.sort(LineSorterEngine.SortType.ALPHABETICAL_DESC) },
                                    label = { Text("Z → A", style = MaterialTheme.typography.labelSmall) }
                                )
                                FilterChip(
                                    selected = false,
                                    onClick = { viewModel.sort(LineSorterEngine.SortType.NATURAL_ASC) },
                                    label = { Text("Natural (1,2,10)", style = MaterialTheme.typography.labelSmall) }
                                )
                                FilterChip(
                                    selected = false,
                                    onClick = { viewModel.sort(LineSorterEngine.SortType.LENGTH_ASC) },
                                    label = { Text("Shortest First", style = MaterialTheme.typography.labelSmall) }
                                )
                                FilterChip(
                                    selected = false,
                                    onClick = { viewModel.sort(LineSorterEngine.SortType.LENGTH_DESC) },
                                    label = { Text("Longest First", style = MaterialTheme.typography.labelSmall) }
                                )
                                FilterChip(
                                    selected = false,
                                    onClick = { viewModel.sort(LineSorterEngine.SortType.REVERSE) },
                                    label = { Text("Reverse", style = MaterialTheme.typography.labelSmall) }
                                )
                                FilterChip(
                                    selected = false,
                                    onClick = { viewModel.sort(LineSorterEngine.SortType.SHUFFLE) },
                                    label = { Text("Shuffle", style = MaterialTheme.typography.labelSmall) }
                                )
                            }

                            Spacer(modifier = Modifier.height(spacing.sm))

                            Text(
                                text = "Deduplicate & Clean",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(spacing.xs))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(dedupeScrollState),
                                horizontalArrangement = Arrangement.spacedBy(spacing.xs)
                            ) {
                                FilterChip(
                                    selected = false,
                                    onClick = { viewModel.deduplicate(LineSorterEngine.DedupeType.REMOVE_DUPLICATES_CASE_SENSITIVE) },
                                    label = { Text("Deduplicate", style = MaterialTheme.typography.labelSmall) }
                                )
                                FilterChip(
                                    selected = false,
                                    onClick = { viewModel.deduplicate(LineSorterEngine.DedupeType.REMOVE_DUPLICATES_CASE_INSENSITIVE) },
                                    label = { Text("Dedupe (Ignore Case)", style = MaterialTheme.typography.labelSmall) }
                                )
                                FilterChip(
                                    selected = false,
                                    onClick = { viewModel.deduplicate(LineSorterEngine.DedupeType.KEEP_UNIQUE_ONLY) },
                                    label = { Text("Keep Unique Only", style = MaterialTheme.typography.labelSmall) }
                                )
                                FilterChip(
                                    selected = false,
                                    onClick = { viewModel.trimLines() },
                                    label = { Text("Trim Spaces", style = MaterialTheme.typography.labelSmall) }
                                )
                                FilterChip(
                                    selected = false,
                                    onClick = { viewModel.removeEmptyLines() },
                                    label = { Text("Remove Blank Lines", style = MaterialTheme.typography.labelSmall) }
                                )
                                FilterChip(
                                    selected = false,
                                    onClick = { viewModel.addLineNumbers() },
                                    label = { Text("Add Numbers", style = MaterialTheme.typography.labelSmall) }
                                )
                            }
                        }
                    }
                }

                // Mode Tabs: Text Editor vs Word Frequency
                item {
                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = MaterialTheme.colorScheme.surface
                    ) {
                        Tab(
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0 },
                            text = { Text("Text (${uiState.totalLines} lines)") }
                        )
                        Tab(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            text = { Text("Word Frequency (${uiState.wordFrequencies.size})") }
                        )
                    }
                }

                if (selectedTab == 0) {
                    // Text Editor Card
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(spacing.md)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Text Content",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                                    )
                                    Row {
                                        OutlinedButton(
                                            onClick = { viewModel.loadSample() },
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                            modifier = Modifier.height(30.dp)
                                        ) {
                                            Text("Sample", style = MaterialTheme.typography.labelSmall)
                                        }
                                        Spacer(modifier = Modifier.width(spacing.xs))
                                        IconButton(
                                            onClick = {
                                                clipboardManager.setText(AnnotatedString(uiState.output))
                                                scope.launch { snackbarHostState.showSnackbar("Copied lines to clipboard") }
                                            },
                                            modifier = Modifier.size(30.dp)
                                        ) {
                                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy", modifier = Modifier.size(16.dp))
                                        }
                                        if (uiState.input.isNotEmpty() || uiState.output.isNotEmpty()) {
                                            Spacer(modifier = Modifier.width(spacing.xs))
                                            IconButton(
                                                onClick = { viewModel.clear() },
                                                modifier = Modifier.size(30.dp)
                                            ) {
                                                Icon(Icons.Default.Clear, contentDescription = "Clear", modifier = Modifier.size(16.dp))
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(spacing.sm))

                                OutlinedTextField(
                                    value = uiState.output,
                                    onValueChange = {
                                        viewModel.onInputChanged(it)
                                    },
                                    placeholder = { Text("Enter or paste lines here...") },
                                    modifier = Modifier.fillMaxWidth(),
                                    textStyle = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                                    minLines = 8,
                                    maxLines = 18
                                )
                            }
                        }
                    }
                } else {
                    // Word Frequency List
                    if (uiState.wordFrequencies.isEmpty()) {
                        item {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "No words to analyze. Enter text on the Text tab.",
                                    modifier = Modifier.padding(spacing.md),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    } else {
                        val maxCount = uiState.wordFrequencies.firstOrNull()?.count ?: 1
                        items(uiState.wordFrequencies.size) { idx ->
                            val item = uiState.wordFrequencies[idx]
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surface,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(spacing.sm)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = item.word,
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                                        )
                                        Text(
                                            text = "${item.count}× (${String.format(java.util.Locale.US, "%.1f", item.percentage)}%)",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    LinearProgressIndicator(
                                        progress = { item.count.toFloat() / maxCount.toFloat() },
                                        modifier = Modifier.fillMaxWidth().height(4.dp),
                                        color = MaterialTheme.colorScheme.primary,
                                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
