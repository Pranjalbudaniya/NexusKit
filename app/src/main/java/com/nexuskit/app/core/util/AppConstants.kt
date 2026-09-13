package com.nexuskit.app.core.util

object AppConstants {

    /* ── App ────────────────────────────────────────────── */
    const val APP_NAME_DEFAULT   = "NexusKit"
    const val APP_DATABASE_NAME  = "nexuskit_db"
    const val APP_DATASTORE_NAME = "nexuskit_prefs"

    /* ── Home Screen ────────────────────────────────────── */
    const val FAVORITES_MAX_COUNT  = 8
    const val GRID_COLUMNS_DEFAULT = 2
    const val GRID_COLUMNS_MIN     = 2
    const val GRID_COLUMNS_MAX     = 5

    /* ── Timing ─────────────────────────────────────────── */
    const val SPLASH_DURATION_MS    = 1000L
    const val SEARCH_DEBOUNCE_MS    = 300L
    const val ANIMATION_DURATION_MS = 300
    const val STAGGER_DELAY_MS      = 50L

    /* ── DataStore Keys ─────────────────────────────────── */
    // All values serialised to DataStore as String or primitive.
    // Complex types (maps, lists) use JSON via kotlinx.serialization.
    const val PREF_APP_NAME          = "pref_app_name"
    const val PREF_THEME_MODE        = "pref_theme_mode"         // enum name string
    const val PREF_ACCENT_COLOR      = "pref_accent_color"       // Long ARGB int; 0L = no override, use dynamic/Material You color
    const val PREF_FONT_FAMILY       = "pref_font_family"        // enum name string
    const val PREF_CARD_SHAPE        = "pref_card_shape"         // enum name string
    const val PREF_FAV_CARD_SHAPE    = "pref_fav_card_shape"     // enum name string
    const val PREF_ONBOARDING_DONE   = "pref_onboarding_done"    // Boolean
    const val PREF_FAVORITES         = "pref_favorites"          // JSON List<String>
    const val PREF_HIDDEN_TOOLS      = "pref_hidden_tools"       // JSON List<String>
    const val PREF_CATEGORY_ORDER    = "pref_category_order"     // JSON List<String>
    const val PREF_CATEGORY_MODES    = "pref_category_modes"     // JSON Map<String,String>
    const val PREF_CATEGORY_EXPANDED = "pref_category_expanded"  // JSON Map<String,Boolean>
    const val PREF_TOOL_ORDER        = "pref_tool_order"         // JSON Map<String,List<String>>
    const val PREF_STATUS_BAR        = "pref_status_bar"         // enum name string
    const val PREF_DYNAMIC_COLOR     = "pref_dynamic_color"      // Boolean
    const val PREF_SHADOW_ELEVATION  = "pref_shadow_elevation"   // enum name string
    const val PREF_SPACING_MODE      = "pref_spacing_mode"       // enum name string
    const val PREF_GRID_COLUMNS      = "pref_grid_columns"       // JSON Map<String,Int>
}
