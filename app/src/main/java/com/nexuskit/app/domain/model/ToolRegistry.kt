package com.nexuskit.app.domain.model

import com.nexuskit.app.domain.model.enums.DisplayMode

/**
 * The single source of truth for every tool and category in NexusKit.
 *
 * ─── HOW TO ADD A NEW TOOL ───────────────────────────────────────────────────
 * 1. Add a ToolInfo entry to [allTools] below.
 * 2. Add a matching route constant to NavRoutes.kt (Spec 07).
 * 3. Create the tool screen Composable in feature/tools/{id}/{Name}Screen.kt
 * 4. Register the route in NavGraph.kt (Spec 07).
 * That's it. The home screen, search, and drawer pick it up automatically.
 *
 * ─── HOW TO ADD A NEW CATEGORY ───────────────────────────────────────────────
 * 1. Add a CategoryInfo entry to [allCategories] below.
 * 2. Set the categoryId on any ToolInfo entries that belong to it.
 * The home screen renders it automatically.
 *
 * ─── iconName RULE ───────────────────────────────────────────────────────────
 * Use the exact name from Material Icons Extended.
 * Reference: https://fonts.google.com/icons
 * The UI layer maps this to ImageVector in ShapeResolver.kt (Spec 08).
 * ──────────────────────────────────────────────────────────────────────────────
 */
object ToolRegistry {

    val allTools: List<ToolInfo> = listOf(

        // ── NUMBER & MATHS ───────────────────────────────────────────────────
        ToolInfo(
            id             = "calculator",
            name           = "Calculator",
            description    = "Basic arithmetic and calculations",
            iconName       = "Calculate",
            categoryId     = "number_maths",
            route          = "tool/calculator",
            searchKeywords = listOf("math", "calc", "arithmetic", "add", "subtract", "multiply")
        ),
        ToolInfo(
            id             = "percentage_calc",
            name           = "Percentage Calculator",
            description    = "Calculate percentages, difference and split bills",
            iconName       = "Percent",
            categoryId     = "number_maths",
            route          = "tool/percentage_calc",
            searchKeywords = listOf("percent", "percentage", "tip", "split", "ratio", "discount", "margin")
        ),
        ToolInfo(
            id             = "base_converter",
            name           = "Base Converter",
            description    = "Convert between Binary, Octal, Decimal and Hex",
            iconName       = "Numbers",
            categoryId     = "number_maths",
            route          = "tool/base_converter",
            searchKeywords = listOf("binary", "hex", "octal", "decimal", "radix", "base", "bits", "bytes")
        ),

        ToolInfo(
            id             = "bmi_calculator",
            name           = "BMI Calculator",
            description    = "Calculate body mass index, category and healthy weight range",
            iconName       = "FitnessCenter",
            categoryId     = "number_maths",
            route          = "tool/bmi_calculator",
            searchKeywords = listOf("bmi", "body mass", "weight", "height", "health", "fitness", "obesity", "underweight")
        ),
        ToolInfo(
            id             = "date_calculator",
            name           = "Date Calculator",
            description    = "Calculate difference between dates or add/subtract days",
            iconName       = "CalendarMonth",
            categoryId     = "number_maths",
            route          = "tool/date_calculator",
            searchKeywords = listOf("date", "calendar", "days", "duration", "time", "difference", "add days", "countdown")
        ),
        ToolInfo(
            id             = "discount_calculator",
            name           = "Discount & Tax",
            description    = "Calculate final price, savings and sales tax",
            iconName       = "LocalOffer",
            categoryId     = "number_maths",
            route          = "tool/discount_calculator",
            searchKeywords = listOf("discount", "tax", "vat", "gst", "sale", "price", "savings", "offer", "percentage")
        ),
        ToolInfo(
            id             = "loan_calculator",
            name           = "Loan & EMI",
            description    = "Calculate monthly EMI, total interest and repayment schedule",
            iconName       = "AccountBalance",
            categoryId     = "number_maths",
            route          = "tool/loan_calculator",
            searchKeywords = listOf("loan", "emi", "interest", "mortgage", "finance", "bank", "principal", "monthly")
        ),
        ToolInfo(
            id             = "sip_calculator",
            name           = "SIP Calculator",
            description    = "Calculate mutual fund SIP & lump sum returns, maturity value and growth schedule",
            iconName       = "TrendingUp",
            categoryId     = "number_maths",
            route          = "tool/sip_calculator",
            searchKeywords = listOf("sip", "investment", "mutual fund", "lumpsum", "returns", "wealth", "interest", "finance", "savings")
        ),

        // ── CONVERTERS ────────────────────────────────────────────────────────
        ToolInfo(
            id             = "unit_converter",
            name           = "Unit Converter",
            description    = "Convert between any units instantly",
            iconName       = "SwapHoriz",
            categoryId     = "converters",
            route          = "tool/unit_converter",
            searchKeywords = listOf("convert", "units", "kg", "km", "celsius", "fahrenheit", "miles", "storage", "speed")
        ),

        // ── GENERATORS ────────────────────────────────────────────────────────
        ToolInfo(
            id             = "qr_generator",
            name           = "QR Generator",
            description    = "Generate QR codes for text, URLs and Wi-Fi",
            iconName       = "QrCode",
            categoryId     = "generators",
            route          = "tool/qr_generator",
            searchKeywords = listOf("qr", "barcode", "scan", "code", "url", "link", "wifi")
        ),
        ToolInfo(
            id             = "password_generator",
            name           = "Password & UUID",
            description    = "Generate strong passwords and UUIDs",
            iconName       = "Password",
            categoryId     = "generators",
            route          = "tool/password_generator",
            searchKeywords = listOf("password", "uuid", "key", "random", "security", "token", "hash")
        ),
        ToolInfo(
            id             = "hash_generator",
            name           = "Hash Generator",
            description    = "Generate MD5, SHA-1, SHA-256 and SHA-512 cryptographic hashes",
            iconName       = "EnhancedEncryption",
            categoryId     = "generators",
            route          = "tool/hash_generator",
            searchKeywords = listOf("hash", "sha256", "md5", "sha1", "sha512", "crypto", "checksum", "digest")
        ),
        ToolInfo(
            id             = "color_palette",
            name           = "Color Palette",
            description    = "Generate harmonious color palettes and copy HEX/RGB/HSL",
            iconName       = "Palette",
            categoryId     = "generators",
            route          = "tool/color_palette",
            searchKeywords = listOf("color", "palette", "hex", "rgb", "hsl", "design", "theme", "swatch")
        ),

        // ── TEXT ──────────────────────────────────────────────────────────────
        ToolInfo(
            id             = "text_editor",
            name           = "Notes & Editor",
            description    = "Write notes with formatting and statistics",
            iconName       = "EditNote",
            categoryId     = "text",
            route          = "tool/text_editor",
            searchKeywords = listOf("note", "write", "text", "editor", "notepad", "memo", "markdown")
        ),
        ToolInfo(
            id             = "case_converter",
            name           = "Case Converter",
            description    = "Convert text to UPPERCASE, lowercase, camelCase, snake_case, slug and more",
            iconName       = "FormatSize",
            categoryId     = "text",
            route          = "tool/case_converter",
            searchKeywords = listOf("case", "uppercase", "lowercase", "camelcase", "snakecase", "kebab", "titlecase", "slug")
        ),
        ToolInfo(
            id             = "text_suite",
            name           = "Text Inspector & Diff",
            description    = "Word & character counter, text diff comparison, slug generator and find-replace",
            iconName       = "CompareArrows",
            categoryId     = "text",
            route          = "tool/text_suite",
            searchKeywords = listOf("text", "diff", "compare", "word count", "character count", "slug", "find", "replace", "inspect")
        ),
        ToolInfo(
            id             = "markdown_editor",
            name           = "Markdown & PDF Exporter",
            description    = "Rich offline Markdown editor with live preview, toolbar shortcuts, and A4 PDF/HTML export",
            iconName       = "Note",
            categoryId     = "text",
            route          = "tool/markdown_editor",
            searchKeywords = listOf("markdown", "md", "pdf", "html", "export", "editor", "notes", "write", "preview")
        ),

        // ── UTILITIES ─────────────────────────────────────────────────────────
        ToolInfo(
            id             = "stopwatch",
            name           = "Stopwatch & Timer",
            description    = "Precision stopwatch with laps and countdown timer",
            iconName       = "Timer",
            categoryId     = "utilities",
            route          = "tool/stopwatch",
            searchKeywords = listOf("stopwatch", "timer", "lap", "countdown", "clock", "time", "pomodoro")
        ),
        ToolInfo(
            id             = "world_clock",
            name           = "World Clock",
            description    = "View time and timezone differences across major world cities",
            iconName       = "Public",
            categoryId     = "utilities",
            route          = "tool/world_clock",
            searchKeywords = listOf("world clock", "timezone", "time", "cities", "utc", "gmt", "clock")
        ),
        ToolInfo(
            id             = "screen_light",
            name           = "Flashlight & Morse",
            description    = "Screen light, camera torch, strobe and custom text-to-Morse code transmitter",
            iconName       = "FlashlightOn",
            categoryId     = "utilities",
            route          = "tool/screen_light",
            searchKeywords = listOf("light", "flashlight", "morse", "morse code", "transmitter", "screen light", "torch", "sos", "strobe", "bright", "signal", "beacon")
        ),
        ToolInfo(
            id             = "developer_tools",
            name           = "Developer Tools",
            description    = "JSON formatter & validator, Base64/URL encoder-decoder, regex tester and device specs",
            iconName       = "Code",
            categoryId     = "developer",
            route          = "tool/developer_tools",
            searchKeywords = listOf("developer", "json", "base64", "url", "encode", "decode", "regex", "specs", "device")
        ),
        ToolInfo(
            id             = "productivity_suite",
            name           = "Productivity Suite",
            description    = "Pomodoro focus timer, task checklist, habit streak tracker and offline scratchpad",
            iconName       = "TaskAlt",
            categoryId     = "utilities",
            route          = "tool/productivity_suite",
            searchKeywords = listOf("productivity", "pomodoro", "todo", "task", "habit", "scratchpad", "focus", "checklist")
        ),
        ToolInfo(
            id             = "science_education",
            name           = "Science & Constants",
            description    = "Interactive periodic table of elements, physics & chemistry constants and scientific formulas",
            iconName       = "Science",
            categoryId     = "utilities",
            route          = "tool/science_education",
            searchKeywords = listOf("science", "periodic table", "elements", "physics", "chemistry", "constants", "formulas", "atoms")
        ),
        ToolInfo(
            id             = "health_fitness",
            name           = "Health & Fitness",
            description    = "BMR & TDEE calorie calculator, daily water requirement, and body fat estimator",
            iconName       = "DirectionsRun",
            categoryId     = "utilities",
            route          = "tool/health_fitness",
            searchKeywords = listOf("health", "fitness", "bmr", "tdee", "water", "calories", "body fat", "nutrition")
        ),
        ToolInfo(
            id             = "reference_sheets",
            name           = "Quick Cheat Sheets",
            description    = "Offline quick reference: Markdown cheat sheet, ASCII/Unicode table, HTTP codes, and NATO alphabet",
            iconName       = "MenuBook",
            categoryId     = "utilities",
            route          = "tool/reference_sheets",
            searchKeywords = listOf("reference", "markdown", "ascii", "unicode", "http", "nato", "cheat sheet", "paper sizes")
        ),
        ToolInfo(
            id             = "encryption_tool",
            name           = "Encryption & Decoder",
            description    = "Encrypt and decrypt with AES, DES, Caesar, Vigenère, Base64, Hex, Binary and Morse code",
            iconName       = "Security",
            categoryId     = "utilities",
            route          = "tool/encryption_tool",
            searchKeywords = listOf("encrypt", "decrypt", "cipher", "aes", "des", "caesar", "vigenere", "base64", "hex", "binary", "morse", "security", "decode", "encode")
        ),
        ToolInfo(
            id             = "randomizer",
            name           = "Randomizer & Decisions",
            description    = "Roll RPG dice, flip coins, generate random numbers, and pick decisions from custom lists",
            iconName       = "Casino",
            categoryId     = "utilities",
            route          = "tool/randomizer",
            searchKeywords = listOf("random", "dice", "coin", "flip", "decision", "picker", "d20", "roll", "shuffle")
        ),
        ToolInfo(
            id             = "exif_tool",
            name           = "EXIF & Privacy Stripper",
            description    = "Inspect embedded photo GPS coordinates, camera specs, and strip all metadata before sharing",
            iconName       = "CameraAlt",
            categoryId     = "utilities",
            route          = "tool/exif_tool",
            searchKeywords = listOf("exif", "metadata", "photo", "privacy", "gps", "location", "strip", "clean", "camera")
        ),
        ToolInfo(
            id             = "steganography",
            name           = "Image Steganography",
            description    = "Conceal and reveal encrypted secret text messages inside photo pixels with zero visual distortion",
            iconName       = "EnhancedEncryption",
            categoryId     = "utilities",
            route          = "tool/steganography",
            searchKeywords = listOf("steganography", "stego", "hide", "secret", "image", "encrypt", "pixels", "lsb", "conceal")
        ),
        ToolInfo(
            id             = "sound_meter",
            name           = "Sound & Decibel Meter (SPL)",
            description    = "Real-time sound pressure level dB measurement with environmental loudness scale and peak meter",
            iconName       = "GraphicEq",
            categoryId     = "utilities",
            route          = "tool/sound_meter",
            searchKeywords = listOf("sound", "decibel", "db", "noise", "spl", "meter", "microphone", "loudness", "audio")
        ),

        // ── DEVELOPER TOOLS ──────────────────────────────────────────────────
        ToolInfo(
            id             = "jwt_decoder",
            name           = "JWT Decoder",
            description    = "Decode JSON Web Tokens, inspect header, payload claims, expiration and verify HS256 signatures offline",
            iconName       = "Key",
            categoryId     = "developer",
            route          = "tool/jwt_decoder",
            searchKeywords = listOf("jwt", "token", "decode", "json web token", "claims", "bearer", "auth", "signature", "verify")
        ),
        ToolInfo(
            id             = "json_converter",
            name           = "JSON / CSV / YAML",
            description    = "Bidirectional conversion between JSON, CSV and YAML with nested object flattening and formatting",
            iconName       = "Transform",
            categoryId     = "developer",
            route          = "tool/json_converter",
            searchKeywords = listOf("json", "csv", "yaml", "converter", "flatten", "table", "data", "transform")
        ),
        ToolInfo(
            id             = "unix_timestamp",
            name           = "Unix Timestamp Converter",
            description    = "Live epoch clock, convert seconds & milliseconds to UTC / Local date and time with relative offsets",
            iconName       = "Schedule",
            categoryId     = "developer",
            route          = "tool/unix_timestamp",
            searchKeywords = listOf("unix", "timestamp", "epoch", "time", "date", "clock", "seconds", "millis", "utc")
        ),
        ToolInfo(
            id             = "cron_explainer",
            name           = "Cron Expression Explainer",
            description    = "Translate cron expressions into plain English, view schedule breakdowns and next 5 run times",
            iconName       = "EventRepeat",
            categoryId     = "developer",
            route          = "tool/cron_explainer",
            searchKeywords = listOf("cron", "schedule", "explainer", "crontab", "time", "interval", "timer", "job")
        ),

        // ── TEXT & UTILITIES ────────────────────────────────────────────────
        ToolInfo(
            id             = "lorem_generator",
            name           = "Lorem Ipsum Generator",
            description    = "Generate customizable placeholder text, paragraphs, sentences and words with themes (Classic, Tech, Cyber)",
            iconName       = "FormatQuote",
            categoryId     = "text",
            route          = "tool/lorem_generator",
            searchKeywords = listOf("lorem", "ipsum", "placeholder", "dummy", "text", "generator", "words", "paragraphs")
        ),
        ToolInfo(
            id             = "line_sorter",
            name           = "Line Sorter & Frequency",
            description    = "Sort lines alphabetically, naturally, remove duplicates, trim spaces, add numbers, and count word frequencies",
            iconName       = "Sort",
            categoryId     = "text",
            route          = "tool/line_sorter",
            searchKeywords = listOf("sort", "lines", "duplicate", "dedupe", "unique", "frequency", "word count", "alphabetical")
        ),
        ToolInfo(
            id             = "fancy_text",
            name           = "Fancy Text & Unicode",
            description    = "Convert plain text into 25+ aesthetic Unicode styles (Bold, Script, Gothic, Fullwidth, Circles, Glitch)",
            iconName       = "AutoAwesome",
            categoryId     = "text",
            route          = "tool/fancy_text",
            searchKeywords = listOf("fancy", "unicode", "font", "aesthetic", "gothic", "cursive", "bold", "italic", "bubble")
        ),
        ToolInfo(
            id             = "text_to_speech",
            name           = "Offline Text-to-Speech",
            description    = "Natural voice speech synthesizer using Android's built-in offline TTS engine with rate and pitch controls",
            iconName       = "RecordVoiceOver",
            categoryId     = "text",
            route          = "tool/text_to_speech",
            searchKeywords = listOf("tts", "speech", "voice", "speak", "text to speech", "read aloud", "audio", "offline")
        ),

        // ── GENERATORS & SECURITY ───────────────────────────────────────────
        ToolInfo(
            id             = "barcode_generator",
            name           = "Barcode Generator",
            description    = "Generate Code128, EAN-13, EAN-8, UPC-A, ITF and Codabar barcodes with direct Gallery download",
            iconName       = "QrCode",
            categoryId     = "generators",
            route          = "tool/barcode_generator",
            searchKeywords = listOf("barcode", "code128", "ean", "ean13", "upc", "retail", "scan", "gallery", "generator")
        ),
        ToolInfo(
            id             = "diceware_generator",
            name           = "Diceware & Password Audit",
            description    = "Generate cryptographically secure memorable Diceware passphrases and audit password crack times",
            iconName       = "Security",
            categoryId     = "generators",
            route          = "tool/diceware_generator",
            searchKeywords = listOf("diceware", "passphrase", "password", "strength", "audit", "entropy", "security", "crack time")
        ),
        ToolInfo(
            id             = "gradient_generator",
            name           = "Gradient Studio",
            description    = "Design vibrant linear, radial and sweep gradients, export CSS/Compose code and save HD wallpapers to Gallery",
            iconName       = "Palette",
            categoryId     = "generators",
            route          = "tool/gradient_generator",
            searchKeywords = listOf("gradient", "wallpaper", "css", "compose", "colors", "radial", "linear", "gallery", "palette")
        ),
        ToolInfo(
            id             = "totp_authenticator",
            name           = "2FA Authenticator",
            description    = "Offline RFC 6238 two-factor authentication (2FA) code generator with animated countdown ring and local storage",
            iconName       = "Security",
            categoryId     = "utilities",
            route          = "tool/totp_authenticator",
            searchKeywords = listOf("totp", "2fa", "authenticator", "otp", "security", "token", "code", "offline", "two factor")
        )
    )

    val allCategories: List<CategoryInfo> = listOf(
        CategoryInfo(id = "number_maths", name = "Number & Maths", iconName = "Calculate",    order = 0),
        CategoryInfo(id = "developer",    name = "Developer Tools",iconName = "Code",         order = 1),
        CategoryInfo(id = "utilities",    name = "Utilities",      iconName = "Build",        order = 2),
        CategoryInfo(id = "converters",   name = "Converters",     iconName = "SwapHoriz",    order = 3),
        CategoryInfo(id = "text",         name = "Text Tools",     iconName = "TextFields",   order = 4),
        CategoryInfo(id = "generators",   name = "Generators",     iconName = "AutoAwesome",  order = 5)
    )

    // ── Lookup helpers ────────────────────────────────────────────────────────

    fun getToolById(id: String): ToolInfo? =
        allTools.find { it.id == id }

    fun getCategoryById(id: String): CategoryInfo? =
        allCategories.find { it.id == id }

    fun getToolsByCategory(categoryId: String): List<ToolInfo> =
        allTools.filter { it.categoryId == categoryId && it.isAvailable }

    fun searchTools(query: String): List<ToolInfo> {
        val q = query.lowercase().trim()
        if (q.isEmpty()) return emptyList()
        return allTools.filter { tool ->
            tool.name.lowercase().contains(q)        ||
            tool.description.lowercase().contains(q) ||
            tool.searchKeywords.any { it.contains(q) }
        }
    }

    val availableToolIds: Set<String>
        get() = allTools.filter { it.isAvailable }.map { it.id }.toSet()

    val defaultCategoryOrder: List<String>
        get() = allCategories.sortedBy { it.order }.map { it.id }
}
