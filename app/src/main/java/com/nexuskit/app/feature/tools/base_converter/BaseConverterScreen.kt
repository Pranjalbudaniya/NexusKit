package com.nexuskit.app.feature.tools.base_converter

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
fun BaseConverterScreen(
    navController: NavHostController,
    onBack: () -> Unit,
    viewModel: BaseConverterViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val spacing = LocalSpacing.current
    val clipboardManager = LocalClipboardManager.current

    DrawerNavigation(
        navController = navController,
        currentToolId = "base_converter"
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Base Converter",
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = spacing.md),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // ── 4 Major Radix Display Cards ──────────────────────────────────────
                Column(
                    verticalArrangement = Arrangement.spacedBy(spacing.xs),
                    modifier = Modifier.padding(top = spacing.xs)
                ) {
                    BaseCard(
                        base = NumberBase.HEX,
                        value = uiState.hexValue,
                        isActive = uiState.activeBase == NumberBase.HEX,
                        onClick = { viewModel.onActiveBaseSelected(NumberBase.HEX) },
                        onCopy = { clipboardManager.setText(AnnotatedString(uiState.hexValue)) }
                    )
                    BaseCard(
                        base = NumberBase.DEC,
                        value = uiState.decValue,
                        isActive = uiState.activeBase == NumberBase.DEC,
                        onClick = { viewModel.onActiveBaseSelected(NumberBase.DEC) },
                        onCopy = { clipboardManager.setText(AnnotatedString(uiState.decValue)) }
                    )
                    BaseCard(
                        base = NumberBase.OCT,
                        value = uiState.octValue,
                        isActive = uiState.activeBase == NumberBase.OCT,
                        onClick = { viewModel.onActiveBaseSelected(NumberBase.OCT) },
                        onCopy = { clipboardManager.setText(AnnotatedString(uiState.octValue)) }
                    )
                    BaseCard(
                        base = NumberBase.BIN,
                        value = uiState.binValue,
                        isActive = uiState.activeBase == NumberBase.BIN,
                        onClick = { viewModel.onActiveBaseSelected(NumberBase.BIN) },
                        onCopy = { clipboardManager.setText(AnnotatedString(uiState.binValue.replace(" ", ""))) }
                    )
                }

                // ── ASCII / Bit metadata info ─────────────────────────────────────────
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = spacing.xs),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Bits: ${uiState.bitLength}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "ASCII: ${uiState.ascii}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // ── Custom Dynamic Programmer Keypad ─────────────────────────────────
                ProgrammerKeypad(
                    activeBase = uiState.activeBase,
                    onKey = { viewModel.onKeyPressed(it) },
                    onBackspace = { viewModel.onBackspace() },
                    onClear = { viewModel.onClear() }
                )

                Spacer(modifier = Modifier.height(spacing.xs))
            }
        }
    }
}

@Composable
private fun BaseCard(
    base: NumberBase,
    value: String,
    isActive: Boolean,
    onClick: () -> Unit,
    onCopy: () -> Unit
) {
    val spacing = LocalSpacing.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .then(
                if (isActive) Modifier.border(2.dp, MaterialTheme.colorScheme.primary, MaterialTheme.shapes.medium)
                else Modifier
            ),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = if (isActive)
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
            else
                MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing.md, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = base.shortName,
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.size(width = 40.dp, height = 20.dp)
                )

                Text(
                    text = value.ifEmpty { "0" },
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
            }

            IconButton(
                onClick = onCopy,
                modifier = Modifier.size(28.dp)
            ) {
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

@Composable
private fun ProgrammerKeypad(
    activeBase: NumberBase,
    onKey: (String) -> Unit,
    onBackspace: () -> Unit,
    onClear: () -> Unit
) {
    val spacing = LocalSpacing.current

    val rows = listOf(
        listOf("D", "E", "F", "AC"),
        listOf("A", "B", "C", "⌫"),
        listOf("7", "8", "9", "4"),
        listOf("5", "6", "1", "2"),
        listOf("3", "0", "", "")
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(spacing.xs),
        modifier = Modifier.fillMaxWidth()
    ) {
        // Hex Row 1: D, E, F, Clear
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(spacing.xs)) {
            KeyButton(label = "D", activeBase = activeBase, isAction = false, modifier = Modifier.weight(1f)) { onKey("D") }
            KeyButton(label = "E", activeBase = activeBase, isAction = false, modifier = Modifier.weight(1f)) { onKey("E") }
            KeyButton(label = "F", activeBase = activeBase, isAction = false, modifier = Modifier.weight(1f)) { onKey("F") }
            KeyButton(label = "AC", activeBase = activeBase, isAction = true, modifier = Modifier.weight(1f)) { onClear() }
        }
        // Hex Row 2: A, B, C, Backspace
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(spacing.xs)) {
            KeyButton(label = "A", activeBase = activeBase, isAction = false, modifier = Modifier.weight(1f)) { onKey("A") }
            KeyButton(label = "B", activeBase = activeBase, isAction = false, modifier = Modifier.weight(1f)) { onKey("B") }
            KeyButton(label = "C", activeBase = activeBase, isAction = false, modifier = Modifier.weight(1f)) { onKey("C") }
            KeyButton(label = "⌫", activeBase = activeBase, isAction = true, modifier = Modifier.weight(1f)) { onBackspace() }
        }
        // Digits: 7, 8, 9
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(spacing.xs)) {
            KeyButton(label = "7", activeBase = activeBase, isAction = false, modifier = Modifier.weight(1f)) { onKey("7") }
            KeyButton(label = "8", activeBase = activeBase, isAction = false, modifier = Modifier.weight(1f)) { onKey("8") }
            KeyButton(label = "9", activeBase = activeBase, isAction = false, modifier = Modifier.weight(1f)) { onKey("9") }
            KeyButton(label = "4", activeBase = activeBase, isAction = false, modifier = Modifier.weight(1f)) { onKey("4") }
        }
        // Digits: 5, 6, 1, 2
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(spacing.xs)) {
            KeyButton(label = "5", activeBase = activeBase, isAction = false, modifier = Modifier.weight(1f)) { onKey("5") }
            KeyButton(label = "6", activeBase = activeBase, isAction = false, modifier = Modifier.weight(1f)) { onKey("6") }
            KeyButton(label = "1", activeBase = activeBase, isAction = false, modifier = Modifier.weight(1f)) { onKey("1") }
            KeyButton(label = "2", activeBase = activeBase, isAction = false, modifier = Modifier.weight(1f)) { onKey("2") }
        }
        // Digits: 3, 0
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(spacing.xs)) {
            KeyButton(label = "3", activeBase = activeBase, isAction = false, modifier = Modifier.weight(1f)) { onKey("3") }
            KeyButton(label = "0", activeBase = activeBase, isAction = false, modifier = Modifier.weight(3f)) { onKey("0") }
        }
    }
}

@Composable
private fun KeyButton(
    label: String,
    activeBase: NumberBase,
    isAction: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val isEnabled = if (isAction) true else BaseConverterEngine.isValidCharForBase(label.first(), activeBase)

    Box(
        modifier = modifier
            .height(52.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(
                when {
                    !isEnabled -> MaterialTheme.colorScheme.surfaceContainerLow.copy(alpha = 0.3f)
                    isAction -> MaterialTheme.colorScheme.secondaryContainer
                    else -> MaterialTheme.colorScheme.surfaceContainerHigh
                }
            )
            .clickable(enabled = isEnabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = when {
                    !isEnabled -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                    isAction -> MaterialTheme.colorScheme.onSecondaryContainer
                    else -> MaterialTheme.colorScheme.onSurface
                }
            )
        )
    }
}
