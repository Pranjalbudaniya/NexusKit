package com.nexuskit.app.domain.model

import com.nexuskit.app.core.util.AppConstants
import com.nexuskit.app.domain.model.enums.CardShapeType
import com.nexuskit.app.domain.model.enums.DisplayMode
import com.nexuskit.app.domain.model.enums.FontFamily
import com.nexuskit.app.domain.model.enums.ShadowElevation
import com.nexuskit.app.domain.model.enums.SpacingMode
import com.nexuskit.app.domain.model.enums.StatusBarStyle
import com.nexuskit.app.domain.model.enums.ThemeMode

/**
 * Complete snapshot of every user-controlled preference.
 * Persisted by NexusKitDataStore (Spec 04) and read by NexusKitTheme (Theme Spec).
 *
 * COLOR RULE — NO HEX ANYWHERE:
 *   [accentColorArgb] stores color as a Long ARGB integer.
 *   null  = dynamic color ON  (Material You picks from wallpaper).
 *   non-null Long = user chose a specific color in the color picker.
 *   The UI layer converts Long → Color via Color(accentColorArgb) — never via hex string.
 *
 * THEME PIPELINE:
 *   UserPreferences → NexusKitDataStore (Spec 04)
 *                   → GetUserPreferencesUseCase (Spec 06)
 *                   → NexusKitTheme (Theme Spec)
 *                   → Settings UI (Spec 12)
 */
data class UserPreferences(

    // ── App Identity ────────────────────────────────────────────────────────
    val appName: String = AppConstants.APP_NAME_DEFAULT,

    // ── Theme ───────────────────────────────────────────────────────────────
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val dynamicColorEnabled: Boolean = true,
    /** null = use Material You dynamic color. Non-null = user-chosen color as ARGB Long. */
    val accentColorArgb: Long? = null,
    val fontFamily: FontFamily = FontFamily.SYSTEM_DEFAULT,

    // ── Card / Icon Appearance ───────────────────────────────────────────────
    /** Shape used for tool cards across all categories. */
    val cardShape: CardShapeType = CardShapeType.ROUNDED_MEDIUM,
    /** Shape used specifically for the Favourites section (can differ from cardShape). */
    val favCardShape: CardShapeType = CardShapeType.ROUNDED_MEDIUM,
    val shadowElevation: ShadowElevation = ShadowElevation.MEDIUM,
    val spacingMode: SpacingMode = SpacingMode.COMFORTABLE,

    // ── Status / Nav Bar ────────────────────────────────────────────────────
    val statusBarStyle: StatusBarStyle = StatusBarStyle.TRANSPARENT,

    // ── Home Screen Layout ──────────────────────────────────────────────────
    /** Per-category display mode. Key = categoryId. Falls back to DisplayMode.CARD if absent. */
    val categoryDisplayModes: Map<String, DisplayMode> = emptyMap(),
    /** Per-category grid column count. Key = categoryId. Falls back to AppConstants.GRID_COLUMNS_DEFAULT. */
    val gridColumns: Map<String, Int> = emptyMap(),
    /** User-defined category order. List of categoryIds. Falls back to ToolRegistry default order. */
    val categoryOrder: List<String> = emptyList(),
    /** Per-category expand/collapse state. Key = categoryId. Default = all collapsed (false). */
    val categoryExpandedStates: Map<String, Boolean> = emptyMap(),
    /** Per-category tool order. Key = categoryId, Value = ordered list of toolIds. */
    val toolOrderMap: Map<String, List<String>> = emptyMap(),

    // ── Favourites & Hidden ──────────────────────────────────────────────────
    /** Ordered list of favourite toolIds. Max size = AppConstants.FAVORITES_MAX_COUNT (8). */
    val favoriteToolIds: List<String> = emptyList(),
    /** Set of hidden toolIds. Hidden tools do not appear on home screen or in search. */
    val hiddenToolIds: Set<String> = emptySet(),

    // ── Onboarding ──────────────────────────────────────────────────────────
    val onboardingCompleted: Boolean = false
)
