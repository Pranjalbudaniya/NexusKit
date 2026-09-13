package com.nexuskit.app.domain.repository

import com.nexuskit.app.domain.model.UserPreferences
import com.nexuskit.app.domain.model.enums.CardShapeType
import com.nexuskit.app.domain.model.enums.DisplayMode
import com.nexuskit.app.domain.model.enums.FontFamily
import com.nexuskit.app.domain.model.enums.ShadowElevation
import com.nexuskit.app.domain.model.enums.SpacingMode
import com.nexuskit.app.domain.model.enums.StatusBarStyle
import com.nexuskit.app.domain.model.enums.ThemeMode
import kotlinx.coroutines.flow.Flow

interface IPreferencesRepository {

    /** Combined UserPreferences stream. Re-emits on any preference change. */
    fun getUserPreferences(): Flow<UserPreferences>

    // ── App ──────────────────────────────────────────────────────────────────
    suspend fun updateAppName(name: String)

    // ── Theme ─────────────────────────────────────────────────────────────────
    suspend fun updateThemeMode(mode: ThemeMode)
    suspend fun updateDynamicColorEnabled(enabled: Boolean)
    /** Pass null to clear custom accent and revert to Material You dynamic color. */
    suspend fun updateAccentColor(argb: Long?)
    suspend fun updateFontFamily(font: FontFamily)

    // ── Appearance ────────────────────────────────────────────────────────────
    suspend fun updateCardShape(shape: CardShapeType)
    suspend fun updateFavCardShape(shape: CardShapeType)
    suspend fun updateShadowElevation(elevation: ShadowElevation)
    suspend fun updateSpacingMode(mode: SpacingMode)
    suspend fun updateStatusBarStyle(style: StatusBarStyle)

    // ── Onboarding ────────────────────────────────────────────────────────────
    suspend fun setOnboardingCompleted()

    // ── Favourites ────────────────────────────────────────────────────────────
    suspend fun addFavorite(toolId: String)
    suspend fun removeFavorite(toolId: String)
    /** Replaces the entire favourites list. Used when user reorders favourites. */
    suspend fun setFavorites(toolIds: List<String>)

    // ── Hidden ────────────────────────────────────────────────────────────────
    suspend fun hideTool(toolId: String)
    suspend fun unhideTool(toolId: String)

    // ── Layout ────────────────────────────────────────────────────────────────
    suspend fun updateCategoryOrder(categoryIds: List<String>)
    suspend fun updateCategoryDisplayMode(categoryId: String, mode: DisplayMode)
    suspend fun updateCategoryExpanded(categoryId: String, expanded: Boolean)
    suspend fun updateToolOrder(categoryId: String, toolIds: List<String>)
    suspend fun updateGridColumns(categoryId: String, columns: Int)

    // ── Reset ─────────────────────────────────────────────────────────────────
    /** Resets all preferences to defaults. Preserves onboarding flag. */
    suspend fun resetToDefaults()
}
