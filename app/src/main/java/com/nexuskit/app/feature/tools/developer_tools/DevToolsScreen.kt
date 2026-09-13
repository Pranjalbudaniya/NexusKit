package com.nexuskit.app.feature.tools.developer_tools

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
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
fun DevToolsScreen(
    navController: NavHostController,
    onBack: () -> Unit,
    initialTab: DevToolTab = DevToolTab.JSON_FORMATTER,
    viewModel: DevToolsViewModel = hiltViewModel()
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
        currentToolId = "developer_tools"
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Developer Tools",
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
                        DevToolTab.JSON_FORMATTER to "JSON Formatter",
                        DevToolTab.REGEX_TESTER to "Regex Tester",
                        DevToolTab.CRON_BUILDER to "CRON Builder",
                        DevToolTab.SQL_FORMATTER to "SQL Formatter",
                        DevToolTab.HTTP_CODES to "HTTP Status Codes"
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
                    DevToolTab.JSON_FORMATTER -> {
                        JsonFormatterView(uiState = uiState, viewModel = viewModel, spacing = spacing, onCopy = {
                            clipboardManager.setText(AnnotatedString(uiState.jsonOutput))
                            Toast.makeText(context, "Copied JSON to clipboard", Toast.LENGTH_SHORT).show()
                        })
                    }
                    DevToolTab.REGEX_TESTER -> {
                        RegexTesterView(uiState = uiState, viewModel = viewModel, spacing = spacing)
                    }
                    DevToolTab.CRON_BUILDER -> {
                        CronBuilderView(uiState = uiState, viewModel = viewModel, spacing = spacing)
                    }
                    DevToolTab.SQL_FORMATTER -> {
                        SqlFormatterView(uiState = uiState, viewModel = viewModel, spacing = spacing, onCopy = {
                            clipboardManager.setText(AnnotatedString(uiState.sqlOutput))
                            Toast.makeText(context, "Copied SQL to clipboard", Toast.LENGTH_SHORT).show()
                        })
                    }
                    DevToolTab.HTTP_CODES -> {
                        HttpCodesView(uiState = uiState, viewModel = viewModel, spacing = spacing)
                    }
                    else -> {
                        JsonFormatterView(uiState = uiState, viewModel = viewModel, spacing = spacing, onCopy = {})
                    }
                }
            }
        }
    }
}

@Composable
private fun JsonFormatterView(uiState: DevToolsUiState, viewModel: DevToolsViewModel, spacing: com.nexuskit.app.ui.theme.NexusKitSpacing, onCopy: () -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = spacing.md, vertical = spacing.sm),
        verticalArrangement = Arrangement.spacedBy(spacing.sm)
    ) {
        item {
            OutlinedTextField(
                value = uiState.jsonInput,
                onValueChange = viewModel::onJsonInputChanged,
                label = { Text("Input Raw JSON") },
                modifier = Modifier.fillMaxWidth().height(140.dp),
                shape = RoundedCornerShape(12.dp)
            )
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(spacing.sm)) {
                Button(
                    onClick = { viewModel.formatJson(2) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Prettify (2 sp)")
                }
                OutlinedButton(
                    onClick = viewModel::minifyJson,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Minify")
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (uiState.isJsonValid) MaterialTheme.colorScheme.surfaceContainer else MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Column(modifier = Modifier.padding(spacing.md), verticalArrangement = Arrangement.spacedBy(spacing.xs)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (uiState.isJsonValid) "Formatted Output" else "Syntax Error",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (uiState.isJsonValid) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.error
                        )
                        if (uiState.isJsonValid && uiState.jsonOutput.isNotEmpty()) {
                            IconButton(onClick = onCopy) {
                                Icon(Icons.Filled.ContentCopy, contentDescription = "Copy", modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                    Text(
                        text = uiState.jsonOutput.ifEmpty { "Enter JSON above to format" },
                        style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                        color = if (uiState.isJsonValid) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }
    }
}

@Composable
private fun RegexTesterView(uiState: DevToolsUiState, viewModel: DevToolsViewModel, spacing: com.nexuskit.app.ui.theme.NexusKitSpacing) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = spacing.md, vertical = spacing.sm),
        verticalArrangement = Arrangement.spacedBy(spacing.sm)
    ) {
        item {
            OutlinedTextField(
                value = uiState.regexPattern,
                onValueChange = viewModel::onRegexPatternChanged,
                label = { Text("Regular Expression Pattern") },
                placeholder = { Text("e.g. [a-z0-9]+") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            )
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(spacing.xs)) {
                FilterChip(
                    selected = uiState.regexCaseInsensitive,
                    onClick = viewModel::toggleRegexCase,
                    label = { Text("Case Insensitive (i)") }
                )
                FilterChip(
                    selected = uiState.regexDotAll,
                    onClick = viewModel::toggleRegexDotAll,
                    label = { Text("Dot Matches All (s)") }
                )
            }
        }

        item {
            OutlinedTextField(
                value = uiState.regexSampleText,
                onValueChange = viewModel::onRegexSampleChanged,
                label = { Text("Test Text String") },
                modifier = Modifier.fillMaxWidth().height(120.dp),
                shape = RoundedCornerShape(10.dp)
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
            ) {
                Column(modifier = Modifier.padding(spacing.md), verticalArrangement = Arrangement.spacedBy(spacing.xs)) {
                    Text(
                        text = "Matches Found: ${uiState.regexMatches.size}",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    )
                    if (uiState.regexMatches.isEmpty()) {
                        Text("No matches found for current pattern", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    } else {
                        uiState.regexMatches.forEachIndexed { i, m ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text("Match #${i + 1}: ${m.matchText}", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                                    if (m.groups.isNotEmpty()) {
                                        Text("Groups: ${m.groups.joinToString(", ")}", style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CronBuilderView(uiState: DevToolsUiState, viewModel: DevToolsViewModel, spacing: com.nexuskit.app.ui.theme.NexusKitSpacing) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = spacing.md, vertical = spacing.sm),
        verticalArrangement = Arrangement.spacedBy(spacing.sm)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(modifier = Modifier.padding(spacing.md), verticalArrangement = Arrangement.spacedBy(spacing.xs)) {
                    Text("CRON Expression Schedule", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    Text(
                        text = "${uiState.cronMinute} ${uiState.cronHour} ${uiState.cronDayOfMonth} ${uiState.cronMonth} ${uiState.cronDayOfWeek}",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, color = MaterialTheme.colorScheme.primary)
                    )
                    Text(uiState.cronExplanation, style = MaterialTheme.typography.bodyMedium)
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
                    Text("Expression Fields", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        OutlinedTextField(
                            value = uiState.cronMinute,
                            onValueChange = { viewModel.onCronFieldsChanged(it, uiState.cronHour, uiState.cronDayOfMonth, uiState.cronMonth, uiState.cronDayOfWeek) },
                            label = { Text("Min") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = uiState.cronHour,
                            onValueChange = { viewModel.onCronFieldsChanged(uiState.cronMinute, it, uiState.cronDayOfMonth, uiState.cronMonth, uiState.cronDayOfWeek) },
                            label = { Text("Hour") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = uiState.cronDayOfMonth,
                            onValueChange = { viewModel.onCronFieldsChanged(uiState.cronMinute, uiState.cronHour, it, uiState.cronMonth, uiState.cronDayOfWeek) },
                            label = { Text("Dom") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = uiState.cronMonth,
                            onValueChange = { viewModel.onCronFieldsChanged(uiState.cronMinute, uiState.cronHour, uiState.cronDayOfMonth, it, uiState.cronDayOfWeek) },
                            label = { Text("Mon") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = uiState.cronDayOfWeek,
                            onValueChange = { viewModel.onCronFieldsChanged(uiState.cronMinute, uiState.cronHour, uiState.cronDayOfMonth, uiState.cronMonth, it) },
                            label = { Text("Dow") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SqlFormatterView(uiState: DevToolsUiState, viewModel: DevToolsViewModel, spacing: com.nexuskit.app.ui.theme.NexusKitSpacing, onCopy: () -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = spacing.md, vertical = spacing.sm),
        verticalArrangement = Arrangement.spacedBy(spacing.sm)
    ) {
        item {
            OutlinedTextField(
                value = uiState.sqlInput,
                onValueChange = viewModel::onSqlInputChanged,
                label = { Text("Raw SQL Query") },
                modifier = Modifier.fillMaxWidth().height(140.dp),
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
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Formatted SQL", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                        IconButton(onClick = onCopy) {
                            Icon(Icons.Filled.ContentCopy, contentDescription = "Copy", modifier = Modifier.size(18.dp))
                        }
                    }
                    Text(
                        text = uiState.sqlOutput.ifEmpty { "Enter SQL above" },
                        style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace, color = MaterialTheme.colorScheme.primary)
                    )
                }
            }
        }
    }
}

@Composable
private fun HttpCodesView(uiState: DevToolsUiState, viewModel: DevToolsViewModel, spacing: com.nexuskit.app.ui.theme.NexusKitSpacing) {
    val filtered = DevToolsEngine.httpCodes.filter {
        val q = uiState.httpSearchQuery.trim().lowercase()
        q.isEmpty() || it.code.toString().contains(q) || it.phrase.lowercase().contains(q) || it.category.lowercase().contains(q)
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = spacing.md, vertical = spacing.sm),
        verticalArrangement = Arrangement.spacedBy(spacing.sm)
    ) {
        item {
            OutlinedTextField(
                value = uiState.httpSearchQuery,
                onValueChange = viewModel::onHttpSearchChanged,
                placeholder = { Text("Search HTTP status code or name...") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
        }

        items(filtered) { http ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
            ) {
                Column(modifier = Modifier.padding(spacing.md), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("${http.code} ${http.phrase}", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                        Text(http.category, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                    }
                    Text(http.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}
