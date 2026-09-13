package com.nexuskit.app.data.datastore

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.nexuskit.app.core.util.AppConstants

/**
 * Typed DataStore preference keys.
 * Key names are sourced from AppConstants — never defined twice.
 * All keys declared here must have a corresponding constant in AppConstants.
 */
object PreferenceKeys {

    // ── App ──────────────────────────────────────────────────────────────────
    val APP_NAME          = stringPreferencesKey(AppConstants.PREF_APP_NAME)

    // ── Theme ─────────────────────────────────────────────────────────────────
    val THEME_MODE        = stringPreferencesKey(AppConstants.PREF_THEME_MODE)
    val DYNAMIC_COLOR     = booleanPreferencesKey(AppConstants.PREF_DYNAMIC_COLOR)
    /** Stored as Long ARGB. 0L = no override (use dynamic color). */
    val ACCENT_COLOR      = longPreferencesKey(AppConstants.PREF_ACCENT_COLOR)
    val FONT_FAMILY       = stringPreferencesKey(AppConstants.PREF_FONT_FAMILY)

    // ── Appearance ────────────────────────────────────────────────────────────
    val CARD_SHAPE        = stringPreferencesKey(AppConstants.PREF_CARD_SHAPE)
    val FAV_CARD_SHAPE    = stringPreferencesKey(AppConstants.PREF_FAV_CARD_SHAPE)
    val SHADOW_ELEVATION  = stringPreferencesKey(AppConstants.PREF_SHADOW_ELEVATION)
    val SPACING_MODE      = stringPreferencesKey(AppConstants.PREF_SPACING_MODE)
    val STATUS_BAR        = stringPreferencesKey(AppConstants.PREF_STATUS_BAR)

    // ── Onboarding ────────────────────────────────────────────────────────────
    val ONBOARDING_DONE   = booleanPreferencesKey(AppConstants.PREF_ONBOARDING_DONE)

    // ── Home Layout (JSON-serialized complex types) ───────────────────────────
    val FAVORITES         = stringPreferencesKey(AppConstants.PREF_FAVORITES)
    val HIDDEN_TOOLS      = stringPreferencesKey(AppConstants.PREF_HIDDEN_TOOLS)
    val CATEGORY_ORDER    = stringPreferencesKey(AppConstants.PREF_CATEGORY_ORDER)
    val CATEGORY_MODES    = stringPreferencesKey(AppConstants.PREF_CATEGORY_MODES)
    val CATEGORY_EXPANDED = stringPreferencesKey(AppConstants.PREF_CATEGORY_EXPANDED)
    val TOOL_ORDER        = stringPreferencesKey(AppConstants.PREF_TOOL_ORDER)
    val GRID_COLUMNS      = stringPreferencesKey(AppConstants.PREF_GRID_COLUMNS)
}
