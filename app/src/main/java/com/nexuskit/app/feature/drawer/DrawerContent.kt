package com.nexuskit.app.feature.drawer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nexuskit.app.domain.model.CategoryInfo
import com.nexuskit.app.domain.model.ToolInfo
import com.nexuskit.app.ui.component.toImageVector
import com.nexuskit.app.ui.theme.ChipShape
import com.nexuskit.app.ui.theme.LocalSpacing

/**
 * The content rendered inside the ModalNavigationDrawer.
 * Injected via DrawerViewModel — lists all tools grouped by category
 * plus a recent tools section and a home button.
 *
 * [currentToolId] drives the active-tool highlight.
 * [onToolClick]   called when user taps a tool row.
 * [onHomeClick]   called when user taps the Home button.
 */
@Composable
fun DrawerContent(
    currentToolId: String?,
    onToolClick: (toolId: String) -> Unit,
    onHomeClick: () -> Unit,
    viewModel: DrawerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val spacing = LocalSpacing.current

    Surface(
        modifier = Modifier
            .fillMaxHeight()
            .width(320.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .statusBarsPadding()
        ) {
            // ── Header: app name ───────────────────────────────────────────
            Text(
                text     = uiState.appName,
                style    = MaterialTheme.typography.titleLarge,
                color    = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(
                    horizontal = spacing.xl,
                    vertical   = spacing.lg
                )
            )

            // ── Search field ───────────────────────────────────────────────
            Surface(
                shape = ChipShape,
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spacing.lg)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = spacing.md, vertical = spacing.sm)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(spacing.sm))
                    BasicTextField(
                        value = uiState.searchQuery,
                        onValueChange = viewModel::onSearchQueryChanged,
                        singleLine = true,
                        textStyle = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        decorationBox = { innerTextField ->
                            if (uiState.searchQuery.isEmpty()) {
                                Text(
                                    text  = "Search tools…",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                        .copy(alpha = 0.6f)
                                )
                            }
                            innerTextField()
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(Modifier.height(spacing.md))

            // ── Scrollable tool list ───────────────────────────────────────
            LazyColumn(modifier = Modifier.weight(1f)) {

                // Recent tools section (hidden during search)
                if (uiState.displayedRecentTools.isNotEmpty() &&
                    uiState.searchQuery.isBlank()) {

                    item(key = "recent_header") {
                        DrawerSectionLabel("Recent")
                    }
                    items(
                        items = uiState.displayedRecentTools,
                        key   = { "recent_${it.id}" }
                    ) { tool ->
                        DrawerToolRow(
                            tool          = tool,
                            isActive      = tool.id == currentToolId,
                            onClick       = { onToolClick(tool.id) }
                        )
                    }
                    item(key = "recent_divider") {
                        HorizontalDivider(
                            modifier = Modifier.padding(
                                horizontal = spacing.lg,
                                vertical   = spacing.sm
                            ),
                            color = MaterialTheme.colorScheme.outlineVariant
                        )
                    }
                }

                // Categories + tools
                uiState.displayedCategories.forEach { category ->
                    item(key = "cat_${category.id}") {
                        DrawerSectionLabel(category.name)
                    }
                    items(
                        items = category.tools,
                        key   = { "${category.id}_${it.id}" }
                    ) { tool ->
                        DrawerToolRow(
                            tool     = tool,
                            isActive = tool.id == currentToolId,
                            onClick  = { onToolClick(tool.id) }
                        )
                    }
                }

                // No results state
                if (uiState.hasNoResults) {
                    item(key = "no_results") {
                        Text(
                            text     = "No tools found",
                            style    = MaterialTheme.typography.bodyMedium,
                            color    = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(
                                horizontal = spacing.xl,
                                vertical   = spacing.lg
                            )
                        )
                    }
                }
            }

            // ── Bottom: Home button ────────────────────────────────────────
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            DrawerToolRow(
                icon    = Icons.Filled.Home,
                label   = "Home",
                isActive= false,
                onClick = onHomeClick,
                modifier = Modifier.padding(vertical = spacing.xs)
            )
        }
    }
}

// ── Sub-composables ─────────────────────────────────────────────────────────

@Composable
private fun DrawerSectionLabel(title: String) {
    val spacing = LocalSpacing.current
    Text(
        text     = title.uppercase(),
        style    = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
        color    = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
        modifier = Modifier.padding(
            start  = spacing.xl,
            end    = spacing.xl,
            top    = spacing.md,
            bottom = spacing.xs
        )
    )
}

@Composable
private fun DrawerToolRow(
    tool: ToolInfo,
    isActive: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    DrawerToolRow(
        icon     = tool.iconName.toImageVector(),
        label    = tool.name,
        isActive = isActive,
        onClick  = onClick,
        modifier = modifier
    )
}

@Composable
private fun DrawerToolRow(
    icon: ImageVector,
    label: String,
    isActive: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = LocalSpacing.current

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = if (isActive)
                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                else
                    MaterialTheme.colorScheme.surface
            )
            .clickable(onClick = onClick)
            .padding(horizontal = spacing.xl, vertical = spacing.md)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isActive)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(22.dp)
        )
        Spacer(Modifier.width(spacing.lg))
        Text(
            text  = label,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal
            ),
            color = if (isActive)
                MaterialTheme.colorScheme.onPrimaryContainer
            else
                MaterialTheme.colorScheme.onSurface
        )
    }
}
