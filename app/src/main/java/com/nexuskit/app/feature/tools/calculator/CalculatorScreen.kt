package com.nexuskit.app.feature.tools.calculator

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.nexuskit.app.navigation.DrawerNavigation
import com.nexuskit.app.ui.theme.LocalSpacing

/**
 * Calculator tool screen.
 *
 * [navController] is required by DrawerNavigation (Spec 11) so the side
 * drawer can navigate to other tools. Every tool screen receives this
 * the same way — see Developer Guide §4 Step 3.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen(
    navController: NavHostController,
    onBack: () -> Unit,
    viewModel: CalculatorViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    DrawerNavigation(navController = navController, currentToolId = "calculator") {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Calculator") },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                CalculatorDisplay(
                    uiState  = uiState,
                    modifier = Modifier.weight(1f)
                )
                CalculatorButtonGrid(
                    onDigit       = viewModel::onDigit,
                    onDecimal     = viewModel::onDecimal,
                    onOperator    = viewModel::onOperator,
                    onEquals      = viewModel::onEquals,
                    onClear       = viewModel::onClear,
                    onBackspace   = viewModel::onBackspace,
                    onToggleSign  = viewModel::onToggleSign,
                    onPercent     = viewModel::onPercent
                )
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// DISPLAY
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun CalculatorDisplay(
    uiState: CalculatorUiState,
    modifier: Modifier = Modifier
) {
    val spacing = LocalSpacing.current
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(spacing.xl),
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.Bottom
    ) {
        if (uiState.displayExpression.isNotEmpty()) {
            Text(
                text     = uiState.displayExpression,
                style    = MaterialTheme.typography.titleMedium,
                color    = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
            Spacer(Modifier.height(spacing.sm))
        }
        Text(
            text     = uiState.displayText,
            style    = MaterialTheme.typography.displayMedium,
            color    = if (uiState.isError)
                           MaterialTheme.colorScheme.error
                       else
                           MaterialTheme.colorScheme.onBackground,
            maxLines = 1
        )
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// BUTTON GRID
// ═══════════════════════════════════════════════════════════════════════════════

private enum class CalcButtonType { DIGIT, OPERATOR, FUNCTION, EQUALS }

@Composable
private fun CalculatorButtonGrid(
    onDigit: (String) -> Unit,
    onDecimal: () -> Unit,
    onOperator: (CalcOperator) -> Unit,
    onEquals: () -> Unit,
    onClear: () -> Unit,
    onBackspace: () -> Unit,
    onToggleSign: () -> Unit,
    onPercent: () -> Unit
) {
    val spacing = LocalSpacing.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(spacing.md),
        verticalArrangement = Arrangement.spacedBy(spacing.sm)
    ) {
        CalcRow {
            CalcButton("AC", CalcButtonType.FUNCTION, onClick = onClear)
            CalcButton("⌫",  CalcButtonType.FUNCTION, onClick = onBackspace)
            CalcButton("%",  CalcButtonType.FUNCTION, onClick = onPercent)
            CalcButton("÷",  CalcButtonType.OPERATOR, onClick = { onOperator(CalcOperator.DIVIDE) })
        }
        CalcRow {
            CalcButton("7", CalcButtonType.DIGIT,    onClick = { onDigit("7") })
            CalcButton("8", CalcButtonType.DIGIT,    onClick = { onDigit("8") })
            CalcButton("9", CalcButtonType.DIGIT,    onClick = { onDigit("9") })
            CalcButton("×", CalcButtonType.OPERATOR, onClick = { onOperator(CalcOperator.MULTIPLY) })
        }
        CalcRow {
            CalcButton("4", CalcButtonType.DIGIT,    onClick = { onDigit("4") })
            CalcButton("5", CalcButtonType.DIGIT,    onClick = { onDigit("5") })
            CalcButton("6", CalcButtonType.DIGIT,    onClick = { onDigit("6") })
            CalcButton("−", CalcButtonType.OPERATOR, onClick = { onOperator(CalcOperator.SUBTRACT) })
        }
        CalcRow {
            CalcButton("1", CalcButtonType.DIGIT,    onClick = { onDigit("1") })
            CalcButton("2", CalcButtonType.DIGIT,    onClick = { onDigit("2") })
            CalcButton("3", CalcButtonType.DIGIT,    onClick = { onDigit("3") })
            CalcButton("+", CalcButtonType.OPERATOR, onClick = { onOperator(CalcOperator.ADD) })
        }
        CalcRow {
            CalcButton("±", CalcButtonType.FUNCTION, onClick = onToggleSign)
            CalcButton("0", CalcButtonType.DIGIT,    onClick = { onDigit("0") })
            CalcButton(".", CalcButtonType.DIGIT,    onClick = onDecimal)
            CalcButton("=", CalcButtonType.EQUALS,   onClick = onEquals)
        }
    }
}

@Composable
private fun CalcRow(content: @Composable RowScope.() -> Unit) {
    val spacing = LocalSpacing.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(spacing.sm),
        content = content
    )
}

@Composable
private fun RowScope.CalcButton(
    label: String,
    type: CalcButtonType,
    onClick: () -> Unit
) {
    val containerColor: Color
    val contentColor: Color
    when (type) {
        CalcButtonType.DIGIT -> {
            containerColor = MaterialTheme.colorScheme.surfaceVariant
            contentColor   = MaterialTheme.colorScheme.onSurfaceVariant
        }
        CalcButtonType.OPERATOR -> {
            containerColor = MaterialTheme.colorScheme.primaryContainer
            contentColor   = MaterialTheme.colorScheme.onPrimaryContainer
        }
        CalcButtonType.FUNCTION -> {
            containerColor = MaterialTheme.colorScheme.secondaryContainer
            contentColor   = MaterialTheme.colorScheme.onSecondaryContainer
        }
        CalcButtonType.EQUALS -> {
            containerColor = MaterialTheme.colorScheme.primary
            contentColor   = MaterialTheme.colorScheme.onPrimary
        }
    }

    Surface(
        onClick  = onClick,
        shape    = MaterialTheme.shapes.large,
        color    = containerColor,
        modifier = Modifier
            .weight(1f)
            .aspectRatio(1f)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text  = label,
                style = MaterialTheme.typography.headlineSmall,
                color = contentColor
            )
        }
    }
}
