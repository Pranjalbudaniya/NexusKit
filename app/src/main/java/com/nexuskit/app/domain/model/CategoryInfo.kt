package com.nexuskit.app.domain.model

import com.nexuskit.app.core.util.AppConstants
import com.nexuskit.app.domain.model.enums.DisplayMode

/**
 * Represents a tool category on the home screen.
 *
 * [id]          Unique identifier (e.g. "utilities"). Must match ToolInfo.categoryId.
 * [iconName]    Material Icons Extended name, resolved at UI layer only.
 * [order]       Default sort order; overridden by user reordering saved in DataStore.
 * [isExpanded]  Default expand state; overridden by user preference in DataStore.
 * [displayMode] Default display mode; overridden per-category in DataStore.
 * [gridColumns] Default column count for CARD / ICON_GRID modes.
 * [tools]       Populated at runtime by GetCategoriesUseCase — NOT stored here.
 */
data class CategoryInfo(
    val id: String,
    val name: String,
    val iconName: String,
    val order: Int = 0,
    val isExpanded: Boolean = false,
    val displayMode: DisplayMode = DisplayMode.CARD,
    val gridColumns: Int = AppConstants.GRID_COLUMNS_DEFAULT,
    val tools: List<ToolInfo> = emptyList()
)
