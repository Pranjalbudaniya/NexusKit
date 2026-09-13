package com.nexuskit.app.feature.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nexuskit.app.R
import com.nexuskit.app.core.util.AppConstants
import com.nexuskit.app.domain.model.MathResult
import com.nexuskit.app.domain.model.ToolInfo
import com.nexuskit.app.domain.model.UnitConversionResult
import com.nexuskit.app.domain.model.enums.SearchQueryType
import com.nexuskit.app.ui.component.toImageVector
import com.nexuskit.app.ui.theme.LocalSpacing
import com.nexuskit.app.ui.theme.SearchBarShape

/**
 * Full-screen search overlay layered on top of HomeScreen.
 * Rendered as a Box child inside HomeScreen — does NOT navigate.
 *
 * [onDismiss]    called when user taps ✕, taps the dim area, or presses back.
 * [onToolClick]  called when user taps a tool result — HomeScreen handles nav.
 */
@Composable
fun SearchOverlay(
    onDismiss: () -> Unit,
    onToolClick: (toolId: String) -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val spacing = LocalSpacing.current
    val focusRequester = remember { FocusRequester() }
    val keyboard = LocalSoftwareKeyboardController.current
    val clipboard = LocalClipboardManager.current

    // Auto-focus the text field when overlay opens
    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    Box(modifier = Modifier.fillMaxSize()) {

        // ── Dimmed background — tapping dismisses ──────────────────────────
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background.copy(alpha = 0.92f))
                .clickable(onClick = onDismiss)
        )

        // ── Content column (not clickable — stops propagation to dim) ──────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .imePadding()
                .clickable(enabled = false) {}  // absorb clicks
        ) {
            // ── Search bar ──────────────────────────────────────────────────
            Surface(
                shape = SearchBarShape,
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spacing.lg, vertical = spacing.sm)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = spacing.lg, vertical = spacing.md)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(spacing.md))

                    // Actual text input
                    BasicTextField(
                        value = uiState.query,
                        onValueChange = viewModel::onQueryChanged,
                        singleLine = true,
                        textStyle = MaterialTheme.typography.bodyLarge.copy(
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(onSearch = { keyboard?.hide() }),
                        decorationBox = { innerTextField ->
                            Box {
                                if (uiState.query.isEmpty()) {
                                    Text(
                                        text  = stringResource(R.string.search_hint),
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                            .copy(alpha = 0.6f)
                                    )
                                }
                                innerTextField()
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .focusRequester(focusRequester)
                    )

                    IconButton(
                        onClick = {
                            if (uiState.query.isEmpty()) onDismiss()
                            else viewModel.onClear()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Clear",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // ── Results panel ───────────────────────────────────────────────
            AnimatedVisibility(
                visible = uiState.hasResults,
                enter = fadeIn(tween(AppConstants.ANIMATION_DURATION_MS)) +
                        slideInVertically(tween(AppConstants.ANIMATION_DURATION_MS)) { -it / 4 },
                exit  = fadeOut(tween(AppConstants.ANIMATION_DURATION_MS))
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = spacing.lg),
                    verticalArrangement = Arrangement.spacedBy(spacing.sm)
                ) {

                    // Math result card
                    uiState.result.mathResult?.takeIf { it.isValid }?.let { math ->
                        item(key = "math") {
                            MathResultCard(
                                math = math,
                                onCopy = {
                                    clipboard.setText(AnnotatedString(math.result))
                                }
                            )
                        }
                    }

                    // Unit conversion card (grouped by category)
                    if (uiState.result.unitConversions.isNotEmpty()) {
                        item(key = "units") {
                            UnitConversionCard(
                                conversions = uiState.result.unitConversions,
                                onCopy = { value ->
                                    clipboard.setText(AnnotatedString(value))
                                }
                            )
                        }
                    }

                    // Tool matches
                    if (uiState.result.matchedTools.isNotEmpty()) {
                        item(key = "tools_header") {
                            Text(
                                text  = "Tools",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = spacing.xs)
                            )
                        }
                        items(
                            items = uiState.result.matchedTools,
                            key   = { it.id }
                        ) { tool ->
                            SearchToolResult(
                                tool    = tool,
                                onClick = {
                                    onDismiss()
                                    onToolClick(tool.id)
                                }
                            )
                        }
                    }

                    // No results state
                    if (uiState.result.queryType == SearchQueryType.EMPTY &&
                        uiState.query.isNotBlank() && !uiState.isLoading) {
                        item(key = "no_results") {
                            Text(
                                text  = stringResource(R.string.search_no_results, uiState.query),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(vertical = spacing.md)
                            )
                        }
                    }

                    // Bottom padding so last result clears keyboard
                    item { Spacer(Modifier.height(spacing.xl)) }
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// RESULT CARD COMPOSABLES
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * Card showing "= 7" for math queries like "3+4".
 * Copy button copies the result to clipboard.
 */
@Composable
private fun MathResultCard(
    math: MathResult,
    onCopy: () -> Unit
) {
    val spacing = LocalSpacing.current

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = spacing.lg, vertical = spacing.md)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text  = math.expression,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
                Text(
                    text  = "= ${math.result}",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            IconButton(onClick = onCopy) {
                Icon(
                    imageVector = Icons.Filled.ContentCopy,
                    contentDescription = "Copy result",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

/**
 * Card showing unit conversion results.
 * Groups all conversions with source value at top, targets listed below.
 * Each row has a copy button.
 */
@Composable
private fun UnitConversionCard(
    conversions: List<UnitConversionResult>,
    onCopy: (value: String) -> Unit
) {
    val spacing = LocalSpacing.current
    val first   = conversions.firstOrNull() ?: return

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(spacing.md)) {

            // Header: category + input value
            Text(
                text  = first.category.displayName,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text  = "${first.inputValue} ${first.inputUnitDisplayName}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = spacing.sm)
            )

            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

            // Conversion rows
            conversions.forEach { conversion ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = spacing.xs)
                ) {
                    Text(
                        text  = conversion.outputValue,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text  = conversion.outputUnitDisplayName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = { onCopy(conversion.outputValue) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ContentCopy,
                            contentDescription = "Copy",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Single tool result row for search results.
 * Shows icon + name + description — tap opens the tool.
 */
@Composable
private fun SearchToolResult(
    tool: ToolInfo,
    onClick: () -> Unit
) {
    val spacing = LocalSpacing.current

    Surface(
        onClick = onClick,
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing.lg, vertical = spacing.md)
        ) {
            Surface(
                shape = MaterialTheme.shapes.small,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = tool.iconName.toImageVector(),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(spacing.sm)
                )
            }
            Spacer(Modifier.width(spacing.md))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text  = tool.name,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (tool.description.isNotBlank()) {
                    Text(
                        text  = tool.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
