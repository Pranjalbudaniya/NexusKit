package com.nexuskit.app.feature.home

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nexuskit.app.R
import com.nexuskit.app.domain.model.CategoryInfo
import com.nexuskit.app.domain.model.ToolInfo
import com.nexuskit.app.domain.model.enums.DisplayMode
import com.nexuskit.app.ui.component.CategoryHeader
import com.nexuskit.app.ui.component.FavouritesRow
import com.nexuskit.app.ui.component.ToolCard
import com.nexuskit.app.ui.component.ToolIconItem
import com.nexuskit.app.ui.component.ToolInfoBottomSheet
import com.nexuskit.app.ui.component.ToolListItem
import com.nexuskit.app.ui.component.ToolPill
import com.nexuskit.app.feature.search.SearchOverlay
import com.nexuskit.app.ui.theme.LocalSpacing
import com.nexuskit.app.ui.theme.SearchBarShape
import com.nexuskit.app.ui.theme.toComposeShape

/**
 * Root home screen composable.
 * Reads live state from HomeViewModel and renders the full home layout.
 *
 * [onToolClick] is called when user taps a tool — NavGraph handles navigation.
 * [onSettingsClick] is called when settings icon is tapped.
 *
 * Search overlay (Spec 10) is layered on top via Box — does not navigate away.
 */
@Composable
fun HomeScreen(
    onToolClick: (toolId: String) -> Unit,
    onSettingsClick: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var isSearchActive by remember { mutableStateOf(false) }

    // Resolve shapes and elevation once here — passed down to components (Memoized)
    val cardShape  = remember(uiState.preferences.cardShape) { uiState.preferences.cardShape.toComposeShape() }
    val favShape   = remember(uiState.preferences.favCardShape) { uiState.preferences.favCardShape.toComposeShape() }
    val elevation  = remember(uiState.preferences.shadowElevation) { Dp(uiState.preferences.shadowElevation.dp) }

    Box(modifier = Modifier.fillMaxSize()) {

        // ── Main home content ──────────────────────────────────────────────
        Scaffold(
            topBar = {
                HomeTopBar(
                    appName = uiState.preferences.appName,
                    onSettingsClick = onSettingsClick
                )
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // ── Search bar — always pinned ─────────────────────────────
                HomeSearchBarCollapsed(
                    onTap = { isSearchActive = true },
                    modifier = Modifier.padding(
                        horizontal = LocalSpacing.current.lg,
                        vertical   = LocalSpacing.current.sm
                    )
                )

                // ── Scrollable body ────────────────────────────────────────
                LazyColumn(
                    contentPadding = PaddingValues(bottom = LocalSpacing.current.xxxl),
                    modifier = Modifier.fillMaxSize()
                ) {

                    // Favourites section
                    item(key = "favourites") {
                        FavouritesRow(
                            tools = uiState.favouriteTools,
                            favShape = favShape,
                            elevation = elevation,
                            onToolClick = { toolId ->
                                viewModel.onToolOpened(toolId)
                                onToolClick(toolId)
                            },
                            onToolLongPress = { tool -> viewModel.onToolLongPress(tool) },
                            showFirstLaunchHint = !uiState.preferences.onboardingCompleted,
                            modifier = Modifier.padding(
                                top = LocalSpacing.current.md,
                                bottom = LocalSpacing.current.sm
                            )
                        )
                    }

                    // Divider after favourites (only if favs are visible)
                    if (uiState.favouriteTools.isNotEmpty()) {
                        item(key = "divider_after_favs") {
                            HomeDivider()
                        }
                    }

                    // Categories
                    itemsIndexed(
                        items = uiState.categories,
                        key   = { _, cat -> cat.id }
                    ) { _, category ->

                        Column {
                            CategoryHeader(
                                categoryInfo = category,
                                onToggle = {
                                    viewModel.onToggleCategoryExpand(
                                        category.id,
                                        category.isExpanded
                                    )
                                },
                                modifier = Modifier.padding(
                                    horizontal = LocalSpacing.current.lg,
                                    vertical   = LocalSpacing.current.xs
                                )
                            ) {
                                // Tools rendered inside expanded category
                                CategoryToolsContent(
                                    category    = category,
                                    cardShape   = cardShape,
                                    elevation   = elevation,
                                    onToolClick = { tool ->
                                        viewModel.onToolOpened(tool.id)
                                        onToolClick(tool.id)
                                    },
                                    onToolLongPress = { tool ->
                                        viewModel.onToolLongPress(tool)
                                    }
                                )
                            }

                            HomeDivider()
                        }
                    }
                }
            }
        }

        // ── Search overlay (Spec 10) — layered on top ─────────────────────
        if (isSearchActive) {
            SearchOverlay(
                onDismiss = { isSearchActive = false },
                onToolClick = { toolId ->
                    isSearchActive = false
                    viewModel.onToolOpened(toolId)
                    onToolClick(toolId)
                }
            )
        }

        // ── Tool info bottom sheet (long press) ────────────────────────────
        uiState.selectedToolForInfo?.let { tool ->
            ToolInfoBottomSheet(
                toolInfo          = tool,
                isFavourited      = uiState.favouriteTools.any { it.id == tool.id },
                cardShape         = cardShape,
                onFavouriteToggle = { viewModel.onToggleFavourite(tool.id) },
                onUse             = {
                    viewModel.onToolOpened(tool.id)
                    onToolClick(tool.id)
                },
                onDismiss = { viewModel.onDismissToolInfo() }
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// SUB-COMPOSABLES
// ═══════════════════════════════════════════════════════════════════════════════

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeTopBar(
    appName: String,
    onSettingsClick: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text  = appName,
                style = MaterialTheme.typography.titleLarge
            )
        },
        actions = {
            IconButton(onClick = onSettingsClick) {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = stringResource(R.string.settings_title),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    )
}

/**
 * Collapsed search bar — always visible below the top bar.
 * Tapping triggers the search overlay (Spec 10).
 * It is NOT a real text field — it is a tappable surface that looks like one.
 */
@Composable
private fun HomeSearchBarCollapsed(
    onTap: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = LocalSpacing.current
    Surface(
        onClick = onTap,
        shape = SearchBarShape,
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = spacing.lg, vertical = spacing.md)
        ) {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.width(spacing.md))
            Text(
                text  = stringResource(R.string.search_hint),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
    }
}

/**
 * Renders tool items inside an expanded category using the category's DisplayMode.
 *
 * Important: this must NOT use LazyColumn / LazyVerticalGrid internally —
 * it lives inside a LazyColumn item already.
 * Uses chunked Row layout for CARD and ICON_GRID modes.
 */
@Composable
private fun CategoryToolsContent(
    category: CategoryInfo,
    cardShape: Shape,
    elevation: Dp,
    onToolClick: (ToolInfo) -> Unit,
    onToolLongPress: (ToolInfo) -> Unit
) {
    val spacing = LocalSpacing.current
    val tools   = category.tools
    if (tools.isEmpty()) return

    when (category.displayMode) {

        DisplayMode.CARD -> {
            val cols = category.gridColumns.coerceIn(2, 5)
            ToolsChunkedGrid(
                tools         = tools,
                columns       = cols,
                itemContent   = { tool, index ->
                    ToolCard(
                        toolInfo     = tool,
                        shape        = cardShape,
                        elevation    = elevation,
                        onClick      = { onToolClick(tool) },
                        onLongPress  = { onToolLongPress(tool) },
                        staggerIndex = index,
                        modifier     = Modifier.weight(1f)
                    )
                }
            )
        }

        DisplayMode.PILL -> {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spacing.lg, vertical = spacing.sm),
                verticalArrangement = Arrangement.spacedBy(spacing.xs)
            ) {
                tools.forEachIndexed { index, tool ->
                    ToolPill(
                        toolInfo     = tool,
                        onClick      = { onToolClick(tool) },
                        onLongPress  = { onToolLongPress(tool) },
                        staggerIndex = index
                    )
                }
            }
        }

        DisplayMode.LIST -> {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(spacing.xxs)
            ) {
                tools.forEachIndexed { index, tool ->
                    ToolListItem(
                        toolInfo     = tool,
                        onClick      = { onToolClick(tool) },
                        onLongPress  = { onToolLongPress(tool) },
                        staggerIndex = index
                    )
                }
            }
        }

        DisplayMode.ICON_GRID -> {
            // Icon grid uses more columns than card grid
            val iconCols = (category.gridColumns + 2).coerceIn(3, 7)
            ToolsChunkedGrid(
                tools         = tools,
                columns       = iconCols,
                itemContent   = { tool, index ->
                    ToolIconItem(
                        toolInfo     = tool,
                        shape        = cardShape,
                        elevation    = elevation,
                        onClick      = { onToolClick(tool) },
                        onLongPress  = { onToolLongPress(tool) },
                        staggerIndex = index,
                        modifier     = Modifier.weight(1f)
                    )
                }
            )
        }
    }
}

/**
 * Non-lazy chunked grid. Splits [tools] into rows of [columns].
 * Fills the last row with empty Spacers so items stay consistent widths.
 *
 * Must be used instead of LazyVerticalGrid inside a LazyColumn item.
 */
@Composable
private fun ToolsChunkedGrid(
    tools: List<ToolInfo>,
    columns: Int,
    itemContent: @Composable androidx.compose.foundation.layout.RowScope.(tool: ToolInfo, index: Int) -> Unit
) {
    val spacing = LocalSpacing.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = spacing.lg, vertical = spacing.sm),
        verticalArrangement = Arrangement.spacedBy(spacing.sm)
    ) {
        tools.chunked(columns).forEachIndexed { rowIndex, rowTools ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.sm)
            ) {
                rowTools.forEachIndexed { colIndex, tool ->
                    itemContent(tool, rowIndex * columns + colIndex)
                }
                // Fill empty slots in last row so items keep consistent widths
                repeat(columns - rowTools.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

/** Thin horizontal rule between sections and categories. */
@Composable
private fun HomeDivider() {
    val spacing = LocalSpacing.current
    HorizontalDivider(
        modifier  = Modifier.padding(horizontal = spacing.lg, vertical = spacing.xs),
        color     = MaterialTheme.colorScheme.outlineVariant
    )
}
