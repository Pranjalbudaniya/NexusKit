package com.nexuskit.app.data.repository

import com.nexuskit.app.data.datastore.NexusKitDataStore
import com.nexuskit.app.domain.model.UserPreferences
import com.nexuskit.app.domain.model.enums.CardShapeType
import com.nexuskit.app.domain.model.enums.DisplayMode
import com.nexuskit.app.domain.model.enums.FontFamily
import com.nexuskit.app.domain.model.enums.ShadowElevation
import com.nexuskit.app.domain.model.enums.SpacingMode
import com.nexuskit.app.domain.model.enums.StatusBarStyle
import com.nexuskit.app.domain.model.enums.ThemeMode
import com.nexuskit.app.domain.repository.IPreferencesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Thin delegation layer between the domain interface and NexusKitDataStore.
 * Contains no business logic — all logic lives in use cases (Spec 06).
 */
class PreferencesRepositoryImpl @Inject constructor(
    private val dataStore: NexusKitDataStore
) : IPreferencesRepository {

    override fun getUserPreferences(): Flow<UserPreferences> =
        dataStore.userPreferences

    override suspend fun updateAppName(name: String) {
        dataStore.updateAppName(name)
    }

    override suspend fun updateThemeMode(mode: ThemeMode) {
        dataStore.updateThemeMode(mode)
    }

    override suspend fun updateDynamicColorEnabled(enabled: Boolean) {
        dataStore.updateDynamicColorEnabled(enabled)
    }

    override suspend fun updateAccentColor(argb: Long?) {
        dataStore.updateAccentColor(argb)
    }

    override suspend fun updateFontFamily(font: FontFamily) {
        dataStore.updateFontFamily(font)
    }

    override suspend fun updateCardShape(shape: CardShapeType) {
        dataStore.updateCardShape(shape)
    }

    override suspend fun updateFavCardShape(shape: CardShapeType) {
        dataStore.updateFavCardShape(shape)
    }

    override suspend fun updateShadowElevation(elevation: ShadowElevation) {
        dataStore.updateShadowElevation(elevation)
    }

    override suspend fun updateSpacingMode(mode: SpacingMode) {
        dataStore.updateSpacingMode(mode)
    }

    override suspend fun updateStatusBarStyle(style: StatusBarStyle) {
        dataStore.updateStatusBarStyle(style)
    }

    override suspend fun setOnboardingCompleted() {
        dataStore.setOnboardingCompleted()
    }

    override suspend fun addFavorite(toolId: String) {
        dataStore.addFavorite(toolId)
    }

    override suspend fun removeFavorite(toolId: String) {
        dataStore.removeFavorite(toolId)
    }

    override suspend fun setFavorites(toolIds: List<String>) {
        dataStore.setFavorites(toolIds)
    }

    override suspend fun hideTool(toolId: String) {
        dataStore.hideTool(toolId)
    }

    override suspend fun unhideTool(toolId: String) {
        dataStore.unhideTool(toolId)
    }

    override suspend fun updateCategoryOrder(categoryIds: List<String>) {
        dataStore.updateCategoryOrder(categoryIds)
    }

    override suspend fun updateCategoryDisplayMode(categoryId: String, mode: DisplayMode) {
        dataStore.updateCategoryDisplayMode(categoryId, mode)
    }

    override suspend fun updateCategoryExpanded(categoryId: String, expanded: Boolean) {
        dataStore.updateCategoryExpanded(categoryId, expanded)
    }

    override suspend fun updateToolOrder(categoryId: String, toolIds: List<String>) {
        dataStore.updateToolOrder(categoryId, toolIds)
    }

    override suspend fun updateGridColumns(categoryId: String, columns: Int) {
        dataStore.updateGridColumns(categoryId, columns)
    }

    override suspend fun resetToDefaults() {
        dataStore.resetToDefaults()
    }
}
