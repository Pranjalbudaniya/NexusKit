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
    const val TOOL_JWT_DECODER         = "tool/jwt_decoder"
    const val TOOL_JSON_CONVERTER      = "tool/json_converter"
    const val TOOL_UNIX_TIMESTAMP      = "tool/unix_timestamp"
    const val TOOL_CRON_EXPLAINER      = "tool/cron_explainer"
    const val TOOL_LOREM_GENERATOR     = "tool/lorem_generator"
    const val TOOL_LINE_SORTER         = "tool/line_sorter"
    const val TOOL_FANCY_TEXT          = "tool/fancy_text"
    const val TOOL_TEXT_TO_SPEECH      = "tool/text_to_speech"
    const val TOOL_BARCODE_GENERATOR   = "tool/barcode_generator"
    const val TOOL_DICEWARE_GENERATOR  = "tool/diceware_generator"
    const val TOOL_GRADIENT_GENERATOR  = "tool/gradient_generator"
    const val TOOL_TOTP_AUTHENTICATOR  = "tool/totp_authenticator"
    const val TOOL_TEXT_SUITE          = "tool/text_suite"
    const val TOOL_SCIENCE_EDUCATION   = "tool/science_education"
    const val TOOL_HEALTH_FITNESS      = "tool/health_fitness"
    const val TOOL_PRODUCTIVITY_SUITE  = "tool/productivity_suite"
    const val TOOL_REFERENCE_SHEETS    = "tool/reference_sheets"
    const val TOOL_SIP_CALCULATOR      = "tool/sip_calculator"
    const val TOOL_ENCRYPTION          = "tool/encryption_tool"
    const val TOOL_RANDOMIZER          = "tool/randomizer"
    const val TOOL_EXIF                = "tool/exif_tool"
    const val TOOL_STEGANOGRAPHY       = "tool/steganography"
    const val TOOL_SOUND_METER         = "tool/sound_meter"
    const val TOOL_MARKDOWN_EDITOR     = "tool/markdown_editor"

    // ── Route builders ────────────────────────────────────────────────────────

    /** Builds a tool route from a toolId. Use this in click handlers. */
    fun toolRoute(toolId: String): String = "$TOOL_PREFIX/$toolId"

    /** Returns true if a route string is a tool route. */
    fun isToolRoute(route: String?): Boolean = route?.startsWith("$TOOL_PREFIX/") == true

    /** Extracts toolId from a tool route string. Returns null if not a tool route. */
    fun toolIdFromRoute(route: String): String? =
        if (isToolRoute(route)) route.removePrefix("$TOOL_PREFIX/") else null
}
