package com.nexuskit.app.feature.tools.percentage_calc

import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.nexuskit.app.navigation.DrawerNavigation
import com.nexuskit.app.ui.theme.LocalSpacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PercentageScreen(
    navController: NavHostController,
    onBack: () -> Unit,
    viewModel: PercentageViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val spacing = LocalSpacing.current
    val clipboardManager = LocalClipboardManager.current

    DrawerNavigation(
        navController = navController,
        currentToolId = "percentage_calc"
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Percentage Calculator",
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
                        PercentMode.values().forEach { mode ->
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

                // ── Mode Specific Content ────────────────────────────────────────────
                item {
                    when (uiState.selectedMode) {
                        PercentMode.PERCENT_OF -> {
                            Column(verticalArrangement = Arrangement.spacedBy(spacing.md)) {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = MaterialTheme.shapes.large,
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(spacing.md),
                                        verticalArrangement = Arrangement.spacedBy(spacing.sm)
                                    ) {
                                        Text("What is X% of Y?", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                                        OutlinedTextField(
                                            value = uiState.mode1Percent,
                                            onValueChange = { viewModel.onMode1Changed(it, uiState.mode1Total) },
                                            label = { Text("Percentage (%)") },
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                            modifier = Modifier.fillMaxWidth(),
                                            singleLine = true
                                        )
                                        OutlinedTextField(
                                            value = uiState.mode1Total,
                                            onValueChange = { viewModel.onMode1Changed(uiState.mode1Percent, it) },
                                            label = { Text("Total Number (Y)") },
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                            modifier = Modifier.fillMaxWidth(),
                                            singleLine = true
                                        )
                                    }
                                }

                                ResultCard(
                                    label = "${uiState.mode1Percent}% of ${uiState.mode1Total}",
                                    result = uiState.mode1Result,
                                    onCopy = { clipboardManager.setText(AnnotatedString(uiState.mode1Result)) }
                                )
                            }
                        }

                        PercentMode.WHAT_PERCENT -> {
                            Column(verticalArrangement = Arrangement.spacedBy(spacing.md)) {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = MaterialTheme.shapes.large,
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(spacing.md),
                                        verticalArrangement = Arrangement.spacedBy(spacing.sm)
                                    ) {
                                        Text("X is what % of Y?", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                                        OutlinedTextField(
                                            value = uiState.mode2Value,
                                            onValueChange = { viewModel.onMode2Changed(it, uiState.mode2Total) },
                                            label = { Text("Part (X)") },
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                            modifier = Modifier.fillMaxWidth(),
                                            singleLine = true
                                        )
                                        OutlinedTextField(
                                            value = uiState.mode2Total,
                                            onValueChange = { viewModel.onMode2Changed(uiState.mode2Value, it) },
                                            label = { Text("Total (Y)") },
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                            modifier = Modifier.fillMaxWidth(),
                                            singleLine = true
                                        )
                                    }
                                }

                                ResultCard(
                                    label = "${uiState.mode2Value} out of ${uiState.mode2Total}",
                                    result = uiState.mode2Result,
                                    onCopy = { clipboardManager.setText(AnnotatedString(uiState.mode2Result)) }
                                )
                            }
                        }

                        PercentMode.CHANGE -> {
                            Column(verticalArrangement = Arrangement.spacedBy(spacing.md)) {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = MaterialTheme.shapes.large,
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(spacing.md),
                                        verticalArrangement = Arrangement.spacedBy(spacing.sm)
                                    ) {
                                        Text("Percentage Change", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                                        OutlinedTextField(
                                            value = uiState.mode3From,
                                            onValueChange = { viewModel.onMode3Changed(it, uiState.mode3To) },
                                            label = { Text("Initial Value (From)") },
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                            modifier = Modifier.fillMaxWidth(),
                                            singleLine = true
                                        )
                                        OutlinedTextField(
                                            value = uiState.mode3To,
                                            onValueChange = { viewModel.onMode3Changed(uiState.mode3From, it) },
                                            label = { Text("Final Value (To)") },
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                            modifier = Modifier.fillMaxWidth(),
                                            singleLine = true
                                        )
                                    }
                                }

                                ResultCard(
                                    label = "Change from ${uiState.mode3From} to ${uiState.mode3To}",
                                    result = uiState.mode3Result,
                                    onCopy = { clipboardManager.setText(AnnotatedString(uiState.mode3Result)) }
                                )
                            }
                        }

                        PercentMode.TIP_SPLIT -> {
                            Column(verticalArrangement = Arrangement.spacedBy(spacing.md)) {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = MaterialTheme.shapes.large,
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(spacing.md),
                                        verticalArrangement = Arrangement.spacedBy(spacing.sm)
                                    ) {
                                        Text("Bill & Tip Calculator", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                                        OutlinedTextField(
                                            value = uiState.tipBill,
                                            onValueChange = { viewModel.onTipChanged(it, uiState.tipPercent, uiState.tipSplitCount) },
                                            label = { Text("Bill Amount ($)") },
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                            modifier = Modifier.fillMaxWidth(),
                                            singleLine = true
                                        )
                                        OutlinedTextField(
                                            value = uiState.tipPercent,
                                            onValueChange = { viewModel.onTipChanged(uiState.tipBill, it, uiState.tipSplitCount) },
                                            label = { Text("Tip Percentage (%)") },
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                            modifier = Modifier.fillMaxWidth(),
                                            singleLine = true
                                        )
                                        OutlinedTextField(
                                            value = uiState.tipSplitCount,
                                            onValueChange = { viewModel.onTipChanged(uiState.tipBill, uiState.tipPercent, it) },
                                            label = { Text("Split between (people)") },
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                            modifier = Modifier.fillMaxWidth(),
                                            singleLine = true
                                        )
                                    }
                                }

                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = MaterialTheme.shapes.large,
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(spacing.lg),
                                        verticalArrangement = Arrangement.spacedBy(spacing.sm)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text("Tip Amount:", style = MaterialTheme.typography.bodyLarge)
                                            Text(uiState.tipAmount, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                                        }
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text("Total Bill:", style = MaterialTheme.typography.bodyLarge)
                                            Text(uiState.totalAmount, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                                        }
                                        Spacer(modifier = Modifier.height(spacing.xs))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text("Per Person:", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                                            Text(
                                                uiState.perPersonAmount,
                                                style = MaterialTheme.typography.headlineMedium.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.primary
                                                )
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
}

@Composable
private fun ResultCard(
    label: String,
    result: String,
    onCopy: () -> Unit
) {
    val spacing = LocalSpacing.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onCopy),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(spacing.lg),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
                Text(
                    text = result,
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
            Icon(
                imageVector = Icons.Filled.ContentCopy,
                contentDescription = "Copy Result",
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}
