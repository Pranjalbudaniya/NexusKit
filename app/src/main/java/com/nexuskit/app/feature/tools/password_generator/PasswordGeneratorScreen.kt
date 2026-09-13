package com.nexuskit.app.feature.tools.password_generator

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.nexuskit.app.navigation.DrawerNavigation
import com.nexuskit.app.ui.theme.LocalSpacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordGeneratorScreen(
    navController: NavHostController,
    onBack: () -> Unit,
    viewModel: PasswordGeneratorViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val spacing = LocalSpacing.current
    val clipboardManager = LocalClipboardManager.current

    DrawerNavigation(
        navController = navController,
        currentToolId = "password_generator"
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Password & UUID",
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
                        SecurityToolMode.values().forEach { mode ->
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

                if (uiState.selectedMode == SecurityToolMode.PASSWORD) {
                    // ── PASSWORD GENERATOR ────────────────────────────────────────────
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.large,
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
                        ) {
                            Column(
                                modifier = Modifier.padding(spacing.lg),
                                verticalArrangement = Arrangement.spacedBy(spacing.md)
                            ) {
                                Text(
                                    text = uiState.generatedPassword,
                                    style = MaterialTheme.typography.headlineSmall.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                )

                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "Strength: ${uiState.passwordStrength.label}",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    LinearProgressIndicator(
                                        progress = { uiState.passwordStrength.colorFraction },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(6.dp),
                                        color = when (uiState.passwordStrength) {
                                            PasswordStrength.VERY_WEAK, PasswordStrength.WEAK -> MaterialTheme.colorScheme.error
                                            PasswordStrength.FAIR -> MaterialTheme.colorScheme.tertiary
                                            PasswordStrength.STRONG, PasswordStrength.VERY_STRONG -> MaterialTheme.colorScheme.primary
                                        },
                                        trackColor = MaterialTheme.colorScheme.surfaceContainerHighest
                                    )
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(spacing.sm)
                                ) {
                                    Button(
                                        onClick = { viewModel.generateNewPassword() },
                                        modifier = Modifier.weight(1f),
                                        shape = MaterialTheme.shapes.medium
                                    ) {
                                        Icon(imageVector = Icons.Filled.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.size(spacing.xs))
                                        Text("Regenerate")
                                    }
                                    OutlinedButton(
                                        onClick = { clipboardManager.setText(AnnotatedString(uiState.generatedPassword)) },
                                        shape = MaterialTheme.shapes.medium
                                    ) {
                                        Icon(imageVector = Icons.Filled.ContentCopy, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.size(spacing.xs))
                                        Text("Copy")
                                    }
                                }
                            }
                        }
                    }

                    // Options Card
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.large,
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
                        ) {
                            Column(
                                modifier = Modifier.padding(spacing.md),
                                verticalArrangement = Arrangement.spacedBy(spacing.sm)
                            ) {
                                Text(
                                    text = "Options",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                Column {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("Password Length", style = MaterialTheme.typography.bodyMedium)
                                        Text(
                                            "${uiState.passwordLength}",
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                        )
                                    }
                                    Slider(
                                        value = uiState.passwordLength.toFloat(),
                                        onValueChange = { viewModel.onLengthChanged(it.toInt()) },
                                        valueRange = 6f..48f,
                                        steps = 41
                                    )
                                }

                                OptionSwitchRow("Uppercase Letters (A-Z)", uiState.includeUpper) { viewModel.onToggleUpper(it) }
                                OptionSwitchRow("Lowercase Letters (a-z)", uiState.includeLower) { viewModel.onToggleLower(it) }
                                OptionSwitchRow("Numbers (0-9)", uiState.includeDigits) { viewModel.onToggleDigits(it) }
                                OptionSwitchRow("Special Symbols (!@#$)", uiState.includeSymbols) { viewModel.onToggleSymbols(it) }
                                OptionSwitchRow("Exclude Ambiguous (0, O, 1, l)", uiState.excludeAmbiguous) { viewModel.onToggleAmbiguous(it) }
                            }
                        }
                    }
                } else {
                    // ── UUID GENERATOR ────────────────────────────────────────────────
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.large,
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
                        ) {
                            Column(
                                modifier = Modifier.padding(spacing.md),
                                verticalArrangement = Arrangement.spacedBy(spacing.sm)
                            ) {
                                Text(
                                    text = "UUID Settings",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )

                                Column {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("Count", style = MaterialTheme.typography.bodyMedium)
                                        Text("${uiState.uuidCount}", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                                    }
                                    Slider(
                                        value = uiState.uuidCount.toFloat(),
                                        onValueChange = { viewModel.onUuidCountChanged(it.toInt()) },
                                        valueRange = 1f..20f,
                                        steps = 18
                                    )
                                }

                                OptionSwitchRow("Uppercase Letters", uiState.uuidUppercase) { viewModel.onToggleUuidUppercase(it) }
                                OptionSwitchRow("Include Hyphens (-)", uiState.uuidIncludeHyphens) { viewModel.onToggleUuidHyphens(it) }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(spacing.sm)
                                ) {
                                    Button(
                                        onClick = { viewModel.generateNewUuids() },
                                        modifier = Modifier.weight(1f),
                                        shape = MaterialTheme.shapes.medium
                                    ) {
                                        Text("Generate New")
                                    }
                                    OutlinedButton(
                                        onClick = {
                                            val allUuids = uiState.generatedUuids.joinToString("\n")
                                            clipboardManager.setText(AnnotatedString(allUuids))
                                        },
                                        shape = MaterialTheme.shapes.medium
                                    ) {
                                        Text("Copy All")
                                    }
                                }
                            }
                        }
                    }

                    items(uiState.generatedUuids) { uuid ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { clipboardManager.setText(AnnotatedString(uuid)) },
                            shape = MaterialTheme.shapes.medium,
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = spacing.md, vertical = spacing.sm),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = uuid,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Medium
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.weight(1f)
                                )
                                Icon(
                                    imageVector = Icons.Filled.ContentCopy,
                                    contentDescription = "Copy",
                                    modifier = Modifier.size(16.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OptionSwitchRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
