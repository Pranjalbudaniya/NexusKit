package com.nexuskit.app.domain.model.enums

/**
 * All 53 card/icon shapes available in the shape picker.
 * Matches the shape picker grid shown in the settings screenshot (9 rows × 6 cols, last row 5).
 *
 * The actual Compose Shape for each entry is resolved in the presentation layer
 * via CardShapeType.toComposeShape() defined in ui/theme/ShapeResolver.kt (Spec 08).
 * No Compose import here — pure enum only.
 *
 * Row layout matches the screenshot grid order (left→right, top→bottom).
 */
enum class CardShapeType(val displayName: String) {

    // ── Row 1 · Rounded Rectangles ─────────────────────────────────────────
    ROUNDED_EXTRA_SMALL("Rounded XS"),
    ROUNDED_SMALL("Rounded S"),
    ROUNDED_MEDIUM("Rounded M"),
    ROUNDED_LARGE("Rounded L"),
    ROUNDED_EXTRA_LARGE("Rounded XL"),
    ROUNDED_FULL("Pill"),

    // ── Row 2 · Polygons ───────────────────────────────────────────────────
    HEXAGON("Hexagon"),
    DIAMOND("Diamond"),
    SCALLOP_4("Scallop 4"),
    SCALLOP_8("Scallop 8"),
    SCALLOP_12("Scallop 12"),
    TICKET("Ticket"),

    // ── Row 3 · Circle Variants ────────────────────────────────────────────
    CIRCLE("Circle"),
    SQUIRCLE("Squircle"),
    CIRCLE_FLAT("Flat Circle"),
    PENTAGON("Pentagon"),
    HEXAGON_FLAT("Hex Flat"),
    OVAL_VERTICAL("Oval V"),

    // ── Row 4 · Irregular ──────────────────────────────────────────────────
    TEARDROP("Teardrop"),
    HEPTAGON("Heptagon"),
    HEPTAGON_THIN("Heptagon Thin"),
    ARCH("Arch"),
    STAR_4("Star 4"),
    BANNER("Banner"),

    // ── Row 5 · Hearts & Soft ──────────────────────────────────────────────
    HEART("Heart"),
    HEART_SHARP("Heart Sharp"),
    RECT_BOTTOM_ROUNDED("Bottom Round"),
    TOMBSTONE("Tombstone"),
    STADIUM("Stadium"),
    OVAL_HORIZONTAL("Oval H"),

    // ── Row 6 · Geometric Outlines ─────────────────────────────────────────
    THIN_DIAMOND("Thin Diamond"),
    THIN_OCTAGON("Thin Octagon"),
    BLOB("Blob"),
    STAR_8("Star 8"),
    PLUS("Plus"),
    CLOVER("Clover"),

    // ── Row 7 · Floral / Cloud ─────────────────────────────────────────────
    SCALLOP_CIRCLE("Scallop Circle"),
    SCALLOP_ROUND("Scallop Round"),
    CLOUD("Cloud"),
    SPEECH_BUBBLE("Speech Bubble"),
    FLOWER_4("Flower 4"),
    FLOWER_ROUND("Flower"),

    // ── Row 8 · Burst / Sun ────────────────────────────────────────────────
    STARBURST_SHARP("Starburst Sharp"),
    STARBURST_ROUND("Starburst"),
    SUNFLOWER("Sunflower"),
    LOTUS("Lotus"),
    SAKURA("Sakura"),
    CLOVER_ROUND("Clover Round"),

    // ── Row 9 · Special ────────────────────────────────────────────────────
    CROSS_PIXEL("Pixel Cross"),
    CIRCLE_DOTTED("Circle Dotted"),
    HOURGLASS("Hourglass"),
    SLASH("Slash"),
    NONE("No Shape");

    companion object {
        fun fromName(name: String): CardShapeType =
            entries.firstOrNull { it.name == name } ?: ROUNDED_MEDIUM
    }
}
