package com.nexuskit.app.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import com.nexuskit.app.core.util.AppConstants
import com.nexuskit.app.domain.model.UserPreferences
import com.nexuskit.app.domain.model.enums.CardShapeType
import com.nexuskit.app.domain.model.enums.DisplayMode
import com.nexuskit.app.domain.model.enums.FontFamily
import com.nexuskit.app.domain.model.enums.ShadowElevation
import com.nexuskit.app.domain.model.enums.SpacingMode
import com.nexuskit.app.domain.model.enums.StatusBarStyle
import com.nexuskit.app.domain.model.enums.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NexusKitDataStore @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

    // ─────────────────────────────────────────────────────────────────────────
    // READ — single combined Flow of the entire UserPreferences snapshot
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Emits the current [UserPreferences] and re-emits on any change.
     * Falls back to defaults on IOException (e.g. first launch, corrupt file).
     */
    val userPreferences: Flow<UserPreferences> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences())
            else throw exception
        }
        .map { prefs -> prefs.toUserPreferences() }

    // ─────────────────────────────────────────────────────────────────────────
    // WRITE — individual update functions (one per preference field)
    // ─────────────────────────────────────────────────────────────────────────

    suspend fun updateAppName(name: String) = dataStore.edit { prefs ->
        prefs[PreferenceKeys.APP_NAME] = name.ifBlank { AppConstants.APP_NAME_DEFAULT }
    }

    suspend fun updateThemeMode(mode: ThemeMode) = dataStore.edit { prefs ->
        prefs[PreferenceKeys.THEME_MODE] = mode.name
    }

    suspend fun updateDynamicColorEnabled(enabled: Boolean) = dataStore.edit { prefs ->
        prefs[PreferenceKeys.DYNAMIC_COLOR] = enabled
    }

    /**
     * Pass null to clear the custom accent color and revert to dynamic/Material You.
     * null is stored as 0L — see DATASTORE DESIGN DECISIONS in spec header.
     */
    suspend fun updateAccentColor(argb: Long?) = dataStore.edit { prefs ->
        prefs[PreferenceKeys.ACCENT_COLOR] = argb ?: 0L
    }

    suspend fun updateFontFamily(font: FontFamily) = dataStore.edit { prefs ->
        prefs[PreferenceKeys.FONT_FAMILY] = font.name
    }

    suspend fun updateCardShape(shape: CardShapeType) = dataStore.edit { prefs ->
        prefs[PreferenceKeys.CARD_SHAPE] = shape.name
    }

    suspend fun updateFavCardShape(shape: CardShapeType) = dataStore.edit { prefs ->
        prefs[PreferenceKeys.FAV_CARD_SHAPE] = shape.name
    }

    suspend fun updateShadowElevation(elevation: ShadowElevation) = dataStore.edit { prefs ->
        prefs[PreferenceKeys.SHADOW_ELEVATION] = elevation.name
    }

    suspend fun updateSpacingMode(mode: SpacingMode) = dataStore.edit { prefs ->
        prefs[PreferenceKeys.SPACING_MODE] = mode.name
    }

    suspend fun updateStatusBarStyle(style: StatusBarStyle) = dataStore.edit { prefs ->
        prefs[PreferenceKeys.STATUS_BAR] = style.name
    }

    suspend fun setOnboardingCompleted() = dataStore.edit { prefs ->
        prefs[PreferenceKeys.ONBOARDING_DONE] = true
    }

    // ── Favourites ────────────────────────────────────────────────────────────

    /**
     * Adds [toolId] to favourites.
     * Silently ignores if already present or if the list is at max capacity.
     */
    suspend fun addFavorite(toolId: String) = dataStore.edit { prefs ->
        val current = PreferenceJsonSerializer.decodeStringList(prefs[PreferenceKeys.FAVORITES])
        if (toolId !in current && current.size < AppConstants.FAVORITES_MAX_COUNT) {
            prefs[PreferenceKeys.FAVORITES] =
                PreferenceJsonSerializer.encodeStringList(current + toolId)
        }
    }

    suspend fun removeFavorite(toolId: String) = dataStore.edit { prefs ->
        val current = PreferenceJsonSerializer.decodeStringList(prefs[PreferenceKeys.FAVORITES])
        prefs[PreferenceKeys.FAVORITES] =
            PreferenceJsonSerializer.encodeStringList(current - toolId)
    }

    /** Replaces the entire favourites list — used when user reorders favourites. */
    suspend fun setFavorites(toolIds: List<String>) = dataStore.edit { prefs ->
        prefs[PreferenceKeys.FAVORITES] =
            PreferenceJsonSerializer.encodeStringList(
                toolIds.take(AppConstants.FAVORITES_MAX_COUNT)
            )
    }

    // ── Hidden Tools ──────────────────────────────────────────────────────────

    suspend fun hideTool(toolId: String) = dataStore.edit { prefs ->
        val current = PreferenceJsonSerializer.decodeStringList(prefs[PreferenceKeys.HIDDEN_TOOLS])
        if (toolId !in current) {
            prefs[PreferenceKeys.HIDDEN_TOOLS] =
                PreferenceJsonSerializer.encodeStringList(current + toolId)
        }
    }

    suspend fun unhideTool(toolId: String) = dataStore.edit { prefs ->
        val current = PreferenceJsonSerializer.decodeStringList(prefs[PreferenceKeys.HIDDEN_TOOLS])
        prefs[PreferenceKeys.HIDDEN_TOOLS] =
            PreferenceJsonSerializer.encodeStringList(current - toolId)
    }

    // ── Category Layout ───────────────────────────────────────────────────────

    suspend fun updateCategoryOrder(categoryIds: List<String>) = dataStore.edit { prefs ->
        prefs[PreferenceKeys.CATEGORY_ORDER] =
            PreferenceJsonSerializer.encodeStringList(categoryIds)
    }

    suspend fun updateCategoryDisplayMode(categoryId: String, mode: DisplayMode) =
        dataStore.edit { prefs ->
            val current = PreferenceJsonSerializer.decodeStringMap(prefs[PreferenceKeys.CATEGORY_MODES])
            prefs[PreferenceKeys.CATEGORY_MODES] =
                PreferenceJsonSerializer.encodeStringMap(current + (categoryId to mode.name))
        }

    suspend fun updateCategoryExpanded(categoryId: String, expanded: Boolean) =
        dataStore.edit { prefs ->
            val current = PreferenceJsonSerializer.decodeBooleanMap(prefs[PreferenceKeys.CATEGORY_EXPANDED])
            prefs[PreferenceKeys.CATEGORY_EXPANDED] =
                PreferenceJsonSerializer.encodeBooleanMap(current + (categoryId to expanded))
        }

    suspend fun updateToolOrder(categoryId: String, toolIds: List<String>) =
        dataStore.edit { prefs ->
            val current = PreferenceJsonSerializer.decodeStringListMap(prefs[PreferenceKeys.TOOL_ORDER])
            prefs[PreferenceKeys.TOOL_ORDER] =
                PreferenceJsonSerializer.encodeStringListMap(current + (categoryId to toolIds))
        }

    suspend fun updateGridColumns(categoryId: String, columns: Int) =
        dataStore.edit { prefs ->
            val clamped = columns.coerceIn(AppConstants.GRID_COLUMNS_MIN, AppConstants.GRID_COLUMNS_MAX)
            val current = PreferenceJsonSerializer.decodeIntMap(prefs[PreferenceKeys.GRID_COLUMNS])
            prefs[PreferenceKeys.GRID_COLUMNS] =
                PreferenceJsonSerializer.encodeIntMap(current + (categoryId to clamped))
        }

    // ── Reset ─────────────────────────────────────────────────────────────────

    /** Clears ALL preferences and restores defaults. Does NOT reset onboarding flag. */
    suspend fun resetToDefaults() = dataStore.edit { prefs ->
        val onboardingDone = prefs[PreferenceKeys.ONBOARDING_DONE] ?: false
        prefs.clear()
        prefs[PreferenceKeys.ONBOARDING_DONE] = onboardingDone
    }

    // ─────────────────────────────────────────────────────────────────────────
    // PRIVATE — mapping raw Preferences to UserPreferences domain model
    // ─────────────────────────────────────────────────────────────────────────

    private fun Preferences.toUserPreferences(): UserPreferences = UserPreferences(

        appName = this[PreferenceKeys.APP_NAME]
            ?: AppConstants.APP_NAME_DEFAULT,

        themeMode = ThemeMode.fromName(
            this[PreferenceKeys.THEME_MODE] ?: ThemeMode.SYSTEM.name
        ),

        dynamicColorEnabled = this[PreferenceKeys.DYNAMIC_COLOR] ?: true,

        // 0L means "not set" / use dynamic color → map back to null
        accentColorArgb = this[PreferenceKeys.ACCENT_COLOR]?.takeIf { it != 0L },

        fontFamily = FontFamily.fromName(
            this[PreferenceKeys.FONT_FAMILY] ?: FontFamily.SYSTEM_DEFAULT.name
        ),

        cardShape = CardShapeType.fromName(
            this[PreferenceKeys.CARD_SHAPE] ?: CardShapeType.ROUNDED_MEDIUM.name
        ),

        favCardShape = CardShapeType.fromName(
            this[PreferenceKeys.FAV_CARD_SHAPE] ?: CardShapeType.ROUNDED_MEDIUM.name
        ),

        shadowElevation = ShadowElevation.fromName(
            this[PreferenceKeys.SHADOW_ELEVATION] ?: ShadowElevation.MEDIUM.name
        ),

        spacingMode = SpacingMode.fromName(
            this[PreferenceKeys.SPACING_MODE] ?: SpacingMode.COMFORTABLE.name
        ),

        statusBarStyle = StatusBarStyle.fromName(
            this[PreferenceKeys.STATUS_BAR] ?: StatusBarStyle.TRANSPARENT.name
        ),

        onboardingCompleted = this[PreferenceKeys.ONBOARDING_DONE] ?: false,

        favoriteToolIds = PreferenceJsonSerializer.decodeStringList(
            this[PreferenceKeys.FAVORITES]
        ),

        hiddenToolIds = PreferenceJsonSerializer.decodeStringList(
            this[PreferenceKeys.HIDDEN_TOOLS]
        ).toSet(),

        categoryOrder = PreferenceJsonSerializer.decodeStringList(
            this[PreferenceKeys.CATEGORY_ORDER]
        ),

        categoryDisplayModes = PreferenceJsonSerializer.decodeStringMap(
            this[PreferenceKeys.CATEGORY_MODES]
        ).mapValues { DisplayMode.fromName(it.value) },

        categoryExpandedStates = PreferenceJsonSerializer.decodeBooleanMap(
            this[PreferenceKeys.CATEGORY_EXPANDED]
        ),

        toolOrderMap = PreferenceJsonSerializer.decodeStringListMap(
            this[PreferenceKeys.TOOL_ORDER]
        ),

        gridColumns = PreferenceJsonSerializer.decodeIntMap(
            this[PreferenceKeys.GRID_COLUMNS]
        )
    )
}
