package com.nexuskit.app.feature.tools.unix_timestamp

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
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
fun UnixTimestampScreen(
    navController: NavHostController,
    onBack: () -> Unit,
    viewModel: UnixTimestampViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val spacing = LocalSpacing.current
    val clipboardManager = LocalClipboardManager.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val presetScrollState = rememberScrollState()
    val offsetScrollState = rememberScrollState()

    DrawerNavigation(
        navController = navController,
        currentToolId = "unix_timestamp"
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Unix Timestamp Converter",
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
                // Live Clock Hero Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Column(modifier = Modifier.padding(spacing.md)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.Schedule,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(spacing.xs))
                                    Text(
                                        text = "Current Unix Time",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                                IconButton(
                                    onClick = { viewModel.togglePauseLive() },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        if (uiState.isLivePaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                                        contentDescription = if (uiState.isLivePaused) "Resume" else "Pause",
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(spacing.sm))

                            // Large Seconds Display
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "${uiState.liveSeconds}",
                                        style = MaterialTheme.typography.headlineMedium.copy(
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                    Text(
                                        text = "Seconds (Epoch)",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        clipboardManager.setText(AnnotatedString(uiState.liveSeconds.toString()))
                                        scope.launch { snackbarHostState.showSnackbar("Seconds copied") }
                                    }
                                ) {
                                    Icon(
                                        Icons.Default.ContentCopy,
                                        contentDescription = "Copy seconds",
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(spacing.xs))

                            // Milliseconds row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "${uiState.liveMillis}",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Medium
                                        ),
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                    Text(
                                        text = "Milliseconds",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        clipboardManager.setText(AnnotatedString(uiState.liveMillis.toString()))
                                        scope.launch { snackbarHostState.showSnackbar("Milliseconds copied") }
                                    },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        Icons.Default.ContentCopy,
                                        contentDescription = "Copy millis",
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Timestamp to Date Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(spacing.md)) {
                            Text(
                                text = "Timestamp → Human Date",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                            )
                            Spacer(modifier = Modifier.height(spacing.xs))

                            // Presets row
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(presetScrollState),
                                horizontalArrangement = Arrangement.spacedBy(spacing.xs)
                            ) {
                                TimestampPreset.entries.forEach { preset ->
                                    FilterChip(
                                        selected = false,
                                        onClick = { viewModel.applyPreset(preset) },
                                        label = { Text(preset.label, style = MaterialTheme.typography.labelSmall) }
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(spacing.xs))

                            // Offset chips
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(offsetScrollState),
                                horizontalArrangement = Arrangement.spacedBy(spacing.xs)
                            ) {
                                listOf(
                                    -3600L to "-1 hr",
                                    3600L to "+1 hr",
                                    -86400L to "-1 day",
                                    86400L to "+1 day",
                                    604800L to "+7 days",
                                    2592000L to "+30 days"
                                ).forEach { (offsetSec, label) ->
                                    FilterChip(
                                        selected = false,
                                        onClick = { viewModel.addOffset(offsetSec) },
                                        label = { Text(label, style = MaterialTheme.typography.labelSmall) }
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(spacing.sm))

                            OutlinedTextField(
                                value = uiState.inputTimestamp,
                                onValueChange = { viewModel.onInputTimestampChanged(it) },
                                label = { Text("Epoch Timestamp") },
                                placeholder = { Text("e.g. 1774000000") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                trailingIcon = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        FilterChip(
                                            selected = uiState.isInputSeconds,
                                            onClick = { viewModel.setUnit(true) },
                                            label = { Text("Sec", style = MaterialTheme.typography.labelSmall) },
                                            modifier = Modifier.height(28.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        FilterChip(
                                            selected = !uiState.isInputSeconds,
                                            onClick = { viewModel.setUnit(false) },
                                            label = { Text("Ms", style = MaterialTheme.typography.labelSmall) },
                                            modifier = Modifier.height(28.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                    }
                                }
                            )

                            if (uiState.timestampError != null) {
                                Spacer(modifier = Modifier.height(spacing.xs))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Error, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(spacing.xs))
                                    Text(uiState.timestampError ?: "", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                                }
                            }

                            val details = uiState.timestampDetails
                            if (details != null) {
                                Spacer(modifier = Modifier.height(spacing.md))

                                // Relative pill
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.secondaryContainer,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = spacing.md, vertical = spacing.sm),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Relative Time:",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.onSecondaryContainer
                                        )
                                        Text(
                                            text = details.relativeTime,
                                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.onSecondaryContainer
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(spacing.sm))

                                ResultFieldRow("Local Time", details.localFormatted) {
                                    clipboardManager.setText(AnnotatedString(details.localFormatted))
                                    scope.launch { snackbarHostState.showSnackbar("Copied local time") }
                                }
                                ResultFieldRow("UTC (ISO 8601)", details.isoUtc) {
                                    clipboardManager.setText(AnnotatedString(details.isoUtc))
                                    scope.launch { snackbarHostState.showSnackbar("Copied ISO 8601") }
                                }
                                ResultFieldRow("RFC 2822", details.rfc2822Utc) {
                                    clipboardManager.setText(AnnotatedString(details.rfc2822Utc))
                                    scope.launch { snackbarHostState.showSnackbar("Copied RFC 2822") }
                                }
                                ResultFieldRow("Day of Year", "${details.dayOfYear} (Leap Year: ${if (details.isLeapYear) "Yes" else "No"})", null)
                            }
                        }
                    }
                }

                // Date to Timestamp Card
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
                                    text = "Human Date → Timestamp",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                                )
                                OutlinedButton(
                                    onClick = { viewModel.loadSampleDate() },
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    modifier = Modifier.height(30.dp)
                                ) {
                                    Text("Sample", style = MaterialTheme.typography.labelSmall)
                                }
                            }

                            Spacer(modifier = Modifier.height(spacing.sm))

                            OutlinedTextField(
                                value = uiState.inputDateString,
                                onValueChange = { viewModel.onInputDateChanged(it) },
                                label = { Text("Date & Time String") },
                                placeholder = { Text("YYYY-MM-DD HH:mm:ss") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )

                            if (uiState.dateError != null) {
                                Spacer(modifier = Modifier.height(spacing.xs))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Error, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(spacing.xs))
                                    Text(uiState.dateError ?: "", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                                }
                            }

                            if (uiState.convertedEpochSeconds != null) {
                                Spacer(modifier = Modifier.height(spacing.sm))
                                ResultFieldRow("Epoch Seconds", "${uiState.convertedEpochSeconds}") {
                                    clipboardManager.setText(AnnotatedString("${uiState.convertedEpochSeconds}"))
                                    scope.launch { snackbarHostState.showSnackbar("Seconds copied") }
                                }
                                ResultFieldRow("Epoch Milliseconds", "${uiState.convertedEpochMillis}") {
                                    clipboardManager.setText(AnnotatedString("${uiState.convertedEpochMillis}"))
                                    scope.launch { snackbarHostState.showSnackbar("Milliseconds copied") }
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
private fun ResultFieldRow(
    label: String,
    value: String,
    onCopy: (() -> Unit)?
) {
    val spacing = LocalSpacing.current
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = spacing.xs)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing.md, vertical = spacing.sm),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Medium
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            if (onCopy != null) {
                IconButton(onClick = onCopy, modifier = Modifier.size(28.dp)) {
                    Icon(
                        Icons.Default.ContentCopy,
                        contentDescription = "Copy $label",
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
