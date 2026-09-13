package com.nexuskit.app.feature.tools.stopwatch

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.nexuskit.app.navigation.DrawerNavigation
import com.nexuskit.app.ui.theme.LocalSpacing
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StopwatchScreen(
    navController: NavHostController,
    onBack: () -> Unit,
    viewModel: StopwatchViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val spacing = LocalSpacing.current

    DrawerNavigation(
        navController = navController,
        currentToolId = "stopwatch"
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Stopwatch & Timer",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold)
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background
                    )
                )
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(horizontal = spacing.md, vertical = spacing.sm),
                verticalArrangement = Arrangement.spacedBy(spacing.md)
            ) {
                // ── Mode Chips ───────────────────────────────────────────────────────
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(spacing.xs)
                    ) {
                        TimeToolMode.values().forEach { mode ->
                            val isSelected = mode == uiState.selectedMode
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.onModeSelected(mode) },
                                label = { Text(mode.displayName) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            )
                        }
                    }
                }

                if (uiState.selectedMode == TimeToolMode.STOPWATCH) {
                    // ── STOPWATCH DISPLAY ─────────────────────────────────────────────
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.large,
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = spacing.xl, horizontal = spacing.md),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                StopwatchDigits(totalMs = uiState.stopwatchElapsedMillis)

                                Spacer(modifier = Modifier.height(spacing.lg))

                                // Controls: Lap, Play/Pause, Reset
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Lap Button
                                    IconButton(
                                        onClick = { viewModel.recordLap() },
                                        enabled = uiState.isStopwatchRunning,
                                        modifier = Modifier
                                            .size(56.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (uiState.isStopwatchRunning)
                                                    MaterialTheme.colorScheme.secondaryContainer
                                                else
                                                    MaterialTheme.colorScheme.surfaceContainerHigh
                                            )
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Flag,
                                            contentDescription = "Lap",
                                            tint = if (uiState.isStopwatchRunning)
                                                MaterialTheme.colorScheme.onSecondaryContainer
                                            else
                                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                                        )
                                    }

                                    // Main Play / Pause Button
                                    Button(
                                        onClick = { viewModel.toggleStopwatch() },
                                        modifier = Modifier.size(72.dp),
                                        shape = CircleShape,
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (uiState.isStopwatchRunning)
                                                MaterialTheme.colorScheme.errorContainer
                                            else
                                                MaterialTheme.colorScheme.primaryContainer
                                        )
                                    ) {
                                        Icon(
                                            imageVector = if (uiState.isStopwatchRunning) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                                            contentDescription = if (uiState.isStopwatchRunning) "Pause" else "Start",
                                            modifier = Modifier.size(32.dp),
                                            tint = if (uiState.isStopwatchRunning)
                                                MaterialTheme.colorScheme.onErrorContainer
                                            else
                                                MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    }

                                    // Reset Button
                                    IconButton(
                                        onClick = { viewModel.resetStopwatch() },
                                        enabled = uiState.stopwatchElapsedMillis > 0,
                                        modifier = Modifier
                                            .size(56.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (uiState.stopwatchElapsedMillis > 0)
                                                    MaterialTheme.colorScheme.surfaceContainerHighest
                                                else
                                                    MaterialTheme.colorScheme.surfaceContainerHigh
                                            )
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Refresh,
                                            contentDescription = "Reset",
                                            tint = if (uiState.stopwatchElapsedMillis > 0)
                                                MaterialTheme.colorScheme.onSurface
                                            else
                                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // ── Laps Section ──────────────────────────────────────────────────
                    if (uiState.laps.isNotEmpty()) {
                        item {
                            Text(
                                text = "Laps (${uiState.laps.size})",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }

                        items(uiState.laps, key = { it.lapNumber }) { lap ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = MaterialTheme.shapes.medium,
                                colors = CardDefaults.cardColors(
                                    containerColor = when {
                                        lap.isFastest -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                                        lap.isSlowest -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                                        else -> MaterialTheme.colorScheme.surfaceContainer
                                    }
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = spacing.md, vertical = spacing.sm),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(spacing.sm)
                                    ) {
                                        Text(
                                            text = "Lap ${lap.lapNumber}",
                                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                        )
                                        if (lap.isFastest) {
                                            Text(
                                                text = "Fastest",
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        } else if (lap.isSlowest) {
                                            Text(
                                                text = "Slowest",
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                color = MaterialTheme.colorScheme.error
                                            )
                                        }
                                    }

                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = lap.formattedLapTime,
                                            style = MaterialTheme.typography.bodyLarge.copy(
                                                fontFamily = FontFamily.Monospace,
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                        Text(
                                            text = "Total: ${lap.formattedTotalTime}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // ── COUNTDOWN TIMER VIEW ──────────────────────────────────────────
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.large,
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = spacing.xl, horizontal = spacing.md),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                TimerProgressRing(
                                    remainingMillis = uiState.timerRemainingMillis,
                                    totalMillis = uiState.timerTotalMillis,
                                    isFinished = uiState.isTimerFinished
                                )

                                Spacer(modifier = Modifier.height(spacing.lg))

                                // Quick Preset Chips
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(spacing.xs)
                                ) {
                                    listOf(1, 3, 5, 10, 15, 25).forEach { mins ->
                                        SuggestionChip(
                                            onClick = { viewModel.setTimerPreset(mins) },
                                            label = { Text("${mins}m") }
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(spacing.lg))

                                // Controls: Play/Pause, Reset
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Button(
                                        onClick = { viewModel.toggleTimer() },
                                        modifier = Modifier.size(72.dp),
                                        shape = CircleShape,
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (uiState.isTimerRunning)
                                                MaterialTheme.colorScheme.errorContainer
                                            else
                                                MaterialTheme.colorScheme.primaryContainer
                                        )
                                    ) {
                                        Icon(
                                            imageVector = if (uiState.isTimerRunning) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                                            contentDescription = if (uiState.isTimerRunning) "Pause" else "Start",
                                            modifier = Modifier.size(32.dp),
                                            tint = if (uiState.isTimerRunning)
                                                MaterialTheme.colorScheme.onErrorContainer
                                            else
                                                MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(spacing.lg))

                                    IconButton(
                                        onClick = { viewModel.resetTimer() },
                                        modifier = Modifier
                                            .size(56.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Refresh,
                                            contentDescription = "Reset Timer",
                                            tint = MaterialTheme.colorScheme.onSurface
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
}

@Composable
private fun StopwatchDigits(totalMs: Long) {
    val minutes = (totalMs / 1000) / 60
    val seconds = (totalMs / 1000) % 60
    val hundredths = (totalMs % 1000) / 10

    Row(
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = String.format(Locale.US, "%02d:%02d", minutes, seconds),
            style = MaterialTheme.typography.displayLarge.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 56.sp
            ),
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = String.format(Locale.US, ".%02d", hundredths),
            style = MaterialTheme.typography.headlineLarge.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 6.dp)
        )
    }
}

@Composable
private fun TimerProgressRing(
    remainingMillis: Long,
    totalMillis: Long,
    isFinished: Boolean
) {
    Box(
        modifier = Modifier.size(220.dp),
        contentAlignment = Alignment.Center
    ) {
        val fraction = if (totalMillis > 0)
            (remainingMillis.toFloat() / totalMillis.toFloat()).coerceIn(0f, 1f)
        else 0f

        CircularProgressIndicator(
            progress = { fraction },
            modifier = Modifier.fillMaxSize(),
            strokeWidth = 10.dp,
            color = if (isFinished) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceContainerHighest
        )

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = StopwatchViewModel.formatDuration(remainingMillis, includeMillis = false),
                style = MaterialTheme.typography.displayMedium.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                ),
                color = if (isFinished) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
            )
            if (isFinished) {
                Text(
                    text = "Time's up!",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
