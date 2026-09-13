package com.nexuskit.app.domain.model.enums

/**
 * All font options available to the user.
 *
 * [fontResName] is the name of the font file in res/font/ (without extension).
 * null = use platform default font (no file required).
 *
 * Font files are added to res/font/ in the Theme Spec.
 * The actual Compose FontFamily is resolved in ui/theme/TypographyResolver.kt (Spec 08).
 */
enum class FontFamily(val displayName: String, val fontResName: String?) {
    SYSTEM_DEFAULT("System Default", null),
    ROBOTO("Roboto",            "roboto"),
    INTER("Inter",              "inter"),
    POPPINS("Poppins",          "poppins"),
    NUNITO("Nunito",            "nunito"),
    MONTSERRAT("Montserrat",    "montserrat"),
    LATO("Lato",                "lato"),
    RALEWAY("Raleway",          "raleway"),
    SOURCE_SANS("Source Sans",  "source_sans"),
    DM_SANS("DM Sans",          "dm_sans"),
    LEXEND("Lexend",            "lexend");

    val requiresFontFile: Boolean get() = fontResName != null

    companion object {
        fun fromName(name: String): FontFamily =
            entries.firstOrNull { it.name == name } ?: SYSTEM_DEFAULT
    }
}
