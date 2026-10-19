package com.nexuskit.app.feature.tools.diceware_generator

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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
import androidx.compose.material3.Slider
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
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
fun DicewareScreen(
    navController: NavHostController,
    onBack: () -> Unit,
    viewModel: DicewareViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val spacing = LocalSpacing.current
    val clipboardManager = LocalClipboardManager.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var isPasswordVisible by remember { mutableStateOf(false) }
    val delimScrollState = rememberScrollState()

    val delimiters = listOf("-" to "Hyphen (-)", " " to "Space ( )", "." to "Period (.)", "_" to "Underscore (_)", "" to "None")

    DrawerNavigation(
        navController = navController,
        currentToolId = "diceware_generator"
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Diceware & Password Audit",
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
                // Generated Passphrase Display Card
                val gen = uiState.generatedPassphrase
                if (gen != null) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                        ) {
                            Column(modifier = Modifier.padding(spacing.md)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Generated Passphrase",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = MaterialTheme.colorScheme.primary
                                    ) {
                                        Text(
                                            text = "${gen.entropyBits} bits entropy",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.onPrimary,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(spacing.sm))

                                Text(
                                    text = gen.passphrase,
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp
                                    ),
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )

                                Spacer(modifier = Modifier.height(spacing.md))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(spacing.sm)
                                ) {
                                    Button(
                                        onClick = {
                                            clipboardManager.setText(AnnotatedString(gen.passphrase))
                                            scope.launch { snackbarHostState.showSnackbar("Passphrase copied!") }
                                        },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(spacing.xs))
                                        Text("Copy")
                                    }
                                    OutlinedButton(
                                        onClick = { viewModel.generate() },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(spacing.xs))
                                        Text("Regenerate")
                                    }
                                }

                                Spacer(modifier = Modifier.height(spacing.xs))

                                OutlinedButton(
                                    onClick = { viewModel.testGeneratedInAuditor() },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(Icons.Default.Security, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(spacing.xs))
                                    Text("Test in Strength Auditor Below")
                                }
                            }
                        }
                    }
                }

                // Generator Settings Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(spacing.md)) {
                            Text(
                                text = "Diceware Settings",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                            )

                            Spacer(modifier = Modifier.height(spacing.sm))

                            // Word count slider
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Word Count", style = MaterialTheme.typography.bodySmall)
                                Text("${uiState.wordCount} words", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                            }
                            Slider(
                                value = uiState.wordCount.toFloat(),
                                onValueChange = { viewModel.onWordCountChanged(it.toInt()) },
                                valueRange = 3f..10f,
                                steps = 6
                            )

                            // Delimiter selector
                            Text("Word Separator", style = MaterialTheme.typography.bodySmall)
                            Spacer(modifier = Modifier.height(spacing.xs))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(delimScrollState),
                                horizontalArrangement = Arrangement.spacedBy(spacing.xs)
                            ) {
                                delimiters.forEach { (delim, label) ->
                                    FilterChip(
                                        selected = uiState.delimiter == delim,
                                        onClick = { viewModel.onDelimiterChanged(delim) },
                                        label = { Text(label, style = MaterialTheme.typography.labelSmall) }
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(spacing.sm))

                            // Switches
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Capitalize words", style = MaterialTheme.typography.bodySmall)
                                Switch(
                                    checked = uiState.capitalize,
                                    onCheckedChange = { viewModel.onCapitalizeToggled(it) }
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Include random number", style = MaterialTheme.typography.bodySmall)
                                Switch(
                                    checked = uiState.includeNumber,
                                    onCheckedChange = { viewModel.onIncludeNumberToggled(it) }
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Include special symbol (!@#)", style = MaterialTheme.typography.bodySmall)
                                Switch(
                                    checked = uiState.includeSymbol,
                                    onCheckedChange = { viewModel.onIncludeSymbolToggled(it) }
                                )
                            }
                        }
                    }
                }

                // Password Strength Auditor Card
                item {
                    val strength = uiState.passwordStrength
                    val scoreColor = when (strength.score) {
                        0 -> MaterialTheme.colorScheme.error
                        1 -> Color(0xFFE65100) // Orange
                        2 -> Color(0xFFFBC02D) // Yellow
                        3 -> Color(0xFF43A047) // Green
                        else -> Color(0xFF2E7D32) // Dark Green
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(spacing.md)) {
                            Text(
                                text = "Password Strength Auditor",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                            )
                            Spacer(modifier = Modifier.height(spacing.xs))
                            Text(
                                text = "Test any password or passphrase against brute-force entropy algorithms.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(spacing.sm))

                            OutlinedTextField(
                                value = uiState.customPasswordInput,
                                onValueChange = { viewModel.onCustomPasswordChanged(it) },
                                placeholder = { Text("Type password to audit...") },
                                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                trailingIcon = {
                                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                        Icon(
                                            if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                            contentDescription = "Toggle visibility"
                                        )
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )

                            if (uiState.customPasswordInput.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(spacing.md))

                                // Score Meter
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Strength: ${strength.scoreLabel}",
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = scoreColor
                                        )
                                    )
                                    Text(
                                        text = "Crack time: ${strength.crackTimeEstimate}",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontWeight = FontWeight.Medium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    )
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                LinearProgressIndicator(
                                    progress = { (strength.score + 1) / 5f },
                                    modifier = Modifier.fillMaxWidth().height(6.dp),
                                    color = scoreColor,
                                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                                )

                                Spacer(modifier = Modifier.height(spacing.md))

                                // Checklist
                                ChecklistRow("At least 12 characters", strength.isLongEnough)
                                ChecklistRow("Uppercase letters (A-Z)", strength.hasUppercase)
                                ChecklistRow("Lowercase letters (a-z)", strength.hasLowercase)
                                ChecklistRow("Numeric digits (0-9)", strength.hasNumbers)
                                ChecklistRow("Special symbols (!@#$%^&*)", strength.hasSymbols)

                                if (strength.suggestions.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(spacing.sm))
                                    Text(
                                        text = "Recommendations:",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    strength.suggestions.forEach { sug ->
                                        Text(
                                            text = "• $sug",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
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
private fun ChecklistRow(label: String, passed: Boolean) {
    val spacing = LocalSpacing.current
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            if (passed) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
            contentDescription = null,
            tint = if (passed) Color(0xFF2E7D32) else MaterialTheme.colorScheme.outline,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(spacing.xs))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = if (passed) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.outline
        )
    }
}
