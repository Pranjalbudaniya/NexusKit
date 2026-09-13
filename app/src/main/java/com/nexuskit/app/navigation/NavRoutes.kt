package com.nexuskit.app.navigation

/**
 * All navigation routes in NexusKit as string constants.
 *
 * ─── HOW TO ADD A NEW TOOL ROUTE ─────────────────────────────────────────────
 * 1. Add a constant below following the "tool/{id}" pattern.
 * 2. Add a composable destination in NavGraph.kt for it.
 * 3. The route string MUST match ToolInfo.route in ToolRegistry (Spec 02).
 * ──────────────────────────────────────────────────────────────────────────────
 */
object NavRoutes {

    // ── Core screens ──────────────────────────────────────────────────────────
    const val SPLASH      = "splash"
    const val ONBOARDING  = "onboarding"
    const val HOME        = "home"
    const val SETTINGS    = "settings"

    // ── Tool screens ──────────────────────────────────────────────────────────
    // Format: "tool/{toolId}" — must match ToolInfo.route in ToolRegistry
    const val TOOL_PREFIX              = "tool"
    const val TOOL_CALCULATOR          = "tool/calculator"
    const val TOOL_PERCENTAGE_CALC     = "tool/percentage_calc"
    const val TOOL_BASE_CONVERTER      = "tool/base_converter"
    const val TOOL_UNIT_CONVERTER      = "tool/unit_converter"
    const val TOOL_QR_GENERATOR        = "tool/qr_generator"
    const val TOOL_PASSWORD_GENERATOR  = "tool/password_generator"
    const val TOOL_TEXT_EDITOR         = "tool/text_editor"
    const val TOOL_STOPWATCH           = "tool/stopwatch"
    const val TOOL_BMI_CALCULATOR      = "tool/bmi_calculator"
    const val TOOL_DATE_CALCULATOR     = "tool/date_calculator"
    const val TOOL_DISCOUNT_CALCULATOR = "tool/discount_calculator"
    const val TOOL_LOAN_CALCULATOR     = "tool/loan_calculator"
    const val TOOL_CASE_CONVERTER      = "tool/case_converter"
    const val TOOL_HASH_GENERATOR      = "tool/hash_generator"
    const val TOOL_COLOR_PALETTE       = "tool/color_palette"
    const val TOOL_WORLD_CLOCK         = "tool/world_clock"
    const val TOOL_SCREEN_LIGHT        = "tool/screen_light"
    const val TOOL_DEVELOPER_TOOLS     = "tool/developer_tools"
    const val TOOL_TEXT_SUITE          = "tool/text_suite"
    const val TOOL_SCIENCE_EDUCATION   = "tool/science_education"
    const val TOOL_HEALTH_FITNESS      = "tool/health_fitness"
    const val TOOL_PRODUCTIVITY_SUITE  = "tool/productivity_suite"
    const val TOOL_REFERENCE_SHEETS    = "tool/reference_sheets"

    // ── Route builders ────────────────────────────────────────────────────────

    /** Builds a tool route from a toolId. Use this in click handlers. */
    fun toolRoute(toolId: String): String = "$TOOL_PREFIX/$toolId"

    /** Returns true if a route string is a tool route. */
    fun isToolRoute(route: String?): Boolean = route?.startsWith("$TOOL_PREFIX/") == true

    /** Extracts toolId from a tool route string. Returns null if not a tool route. */
    fun toolIdFromRoute(route: String): String? =
        if (isToolRoute(route)) route.removePrefix("$TOOL_PREFIX/") else null
}
