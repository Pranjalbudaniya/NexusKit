<div align="center">

# ⚡ NexusKit
### *The Ultimate Offline Utility & Productivity Suite for Android*

[![Android](https://img.shields.io/badge/Platform-Android%2010%2B%20(API%2029%2B)-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://android.com)
[![Version](https://img.shields.io/badge/Version-v1.2.0-blue?style=for-the-badge)](https://github.com/Pranjalbudaniya/NexusKit/releases)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0.21-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Compose](https://img.shields.io/badge/Jetpack%20Compose-2024.10.00-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![Clean Architecture](https://img.shields.io/badge/Architecture-Clean%20%2B%20MVI%2FMVVM-F43F5E?style=for-the-badge)]()
[![Offline First](https://img.shields.io/badge/Offline-100%25%20No%20Internet%20Required-10B981?style=for-the-badge)]()
[![License](https://img.shields.io/badge/License-MIT-blue?style=for-the-badge)](LICENSE)

<br />

**NexusKit** is an open-source, private, and lightning-fast Swiss Army knife for Android. Packed with **30 offline calculation, conversion, financial, text, security, privacy, and utility tools** in a sleek Material You (M3) design — zero accounts, zero analytics, zero ads, zero battery drain.

[Features](#-feature-suite) • [Widgets](#-home-screen-widgets) • [Tech Stack](#-architecture--tech-stack) • [Privacy](#-privacy--security)

</div>

---

## 🌟 Why NexusKit?

- 🔒 **100% Offline & Private** — Never communicates with external servers. No telemetry, no ads, no trackers, and no user accounts.
- ⚡ **Instant Response Times** — Native Jetpack Compose UI running pure local Kotlin calculation engines.
- 🔋 **Zero Battery Drain** — No background wake-locks, polling workers, or heavy daemon services.
- 🎨 **Deep Customization** — Material You Dynamic Color, AMOLED Dark Theme, Status Bar styling, and custom card and icon shape customizers.
- 📱 **Battery-Efficient Widgets** — Lightweight 4x2 Quick Grid and 4x1 Quick Bar for 1-tap access to your daily tools.
- 📂 **Seamless System Integration** — Direct system file handler for Markdown documents (`.md`, `.markdown`) via Android "Open with".
- 🖼️ **Direct Gallery Saving** — 1-tap Scoped Storage export to device Gallery (`Pictures/NexusKit`) across all image & visual tools.

---

## 🛠️ Feature Suite (42 Built-in Tools)

### 💻 1. Developer Tools (Dedicated Section)
| Tool | Description |
| :--- | :--- |
| **Developer Tools** | **JSON Formatter & Validator**, Base64 & URL Encoder/Decoder, RegEx tester, and device hardware specs. |
| **JWT Decoder** | Decode JSON Web Tokens (header, payload claims, expiration status), inspect standard claims (`iss`, `sub`, `aud`, `exp`), and verify HMAC-SHA256 signatures offline. |
| **JSON / CSV / YAML** | Bidirectional converter between **JSON, CSV, and YAML** with automatic dot-notation flattening, custom delimiters, and syntax formatting. |
| **Unix Timestamp Converter** | Live ticking epoch clock (seconds & millis), bidirectional human date parsing, relative time, and quick offsets (+1h, +1d, +7d). |
| **Cron Expression Explainer** | Translate standard 5-part cron expressions into plain English, view field breakdowns, and calculate the next 5 upcoming run timestamps. |

---

### 🔢 2. Number & Mathematics
| Tool | Description |
| :--- | :--- |
| **Calculator** | Fast arithmetic with live expression parsing and history log. |
| **SIP Calculator** | Monthly SIP and Lump Sum mutual fund return calculations with compound growth schedules. |
| **Percentage Calculator** | Instant percentage math, tip calculation, margins, and multi-person bill splitting. |
| **Base Converter** | Real-time simultaneous radix conversion between **Binary, Octal, Decimal, and Hexadecimal**. |
| **Date Calculator** | Calculate exact difference between dates (years, months, days) and add/subtract duration. |
| **Discount & Tax** | Compute final sale price, discount savings, and VAT/GST with percentage breakdown. |
| **Loan & EMI** | Monthly amortization, total payable interest, and loan payoff schedules. |
| **BMI Calculator** | Body Mass Index calculation, classification categories, and healthy target weight ranges. |

---

### 📝 3. Text Tools & Speech
| Tool | Description |
| :--- | :--- |
| **Markdown & PDF Exporter** | Full-featured offline Markdown editor: Android "Open with" file association (`.md`/`.markdown`), right slide-out hierarchical outline drawer (collapsible H1–H6), undo/redo history, in-editor search/replace, interactive task checklists in Live Preview, table generator modal, and styled A4 PDF & HTML export. |
| **Notes & Editor** | Offline scratchpad and note editor with live word count, character statistics, and reading time. |
| **Case Converter** | Transform text into `UPPERCASE`, `lowercase`, `camelCase`, `snake_case`, `kebab-case`, `Title Case`, and URL `slug`. |
| **Line Sorter & Frequency** | Sort lines alphabetically (A–Z, Z–A), naturally (1, 2, 10), or by length; remove duplicates; add line numbers; and calculate word frequency distributions. |
| **Fancy Text & Unicode** | Convert text into 25+ aesthetic Unicode styles: **Bold, Italic, Monospace, Cursive, Gothic, Circled, Squared, Small Caps, Fullwidth, and Glitch/Zalgo**. |
| **Lorem Ipsum Generator** | Customizable placeholder generator for paragraphs, sentences, words, and list items with themes (Classic Latin, Tech & Developer, Cybersecurity, Café). |
| **Offline Text-to-Speech** | Natural speech engine using Android's built-in offline TTS engine with custom speech rate, pitch control, and voice language selection. |
| **Text Inspector & Diff** | Real-time word frequency analysis, whitespace cleanup, and visual line-by-line diff comparison. |
| **Quick Cheat Sheets** | Offline cheat sheets for **Markdown syntax, ASCII & Unicode tables, HTTP status codes, and NATO phonetic alphabet**. |

---

### 🎨 4. Generators & Barcodes
| Tool | Description |
| :--- | :--- |
| **Barcode Generator** | Generate **Code 128, EAN-13, EAN-8, UPC-A, ITF, Codabar, Code 39, and PDF417** barcodes with auto-checksum calculation and direct Gallery download. |
| **QR Generator** | Generate high-contrast offline QR codes for raw text, website URLs, and Wi-Fi credentials with Gallery download. |
| **Diceware & Password Audit** | Generate memorable Diceware passphrases using the curated EFF wordlist, and audit any password against brute-force entropy and crack time estimates. |
| **Gradient Studio** | Design vibrant linear, radial, and sweep gradients; export CSS, Jetpack Compose, and Android XML shape code; and save high-resolution HD wallpapers directly to Gallery. |
| **Unit Converter** | Convert across **Length, Mass, Temperature, Speed, Digital Storage, Energy, and Volume**. |
| **Password & UUID** | Cryptographically secure random password generator with entropy meter and UUID v4 generator. |
| **Hash Generator** | Generate instant checksums using **MD5, SHA-1, SHA-256, and SHA-512** digests. |
| **Color Palette** | Generate complementary, monochromatic, and analogous swatches with 1-tap HEX/RGB/HSL copy. |

---

### 🛡️ 5. Security, Privacy & Utilities
| Tool | Description |
| :--- | :--- |
| **2FA Authenticator** | Offline RFC 6238 time-based two-factor authentication (TOTP) code generator with animated 30s countdown ring and private on-device storage. |
| **EXIF & Privacy Stripper** | Inspect embedded photo EXIF tags, detect GPS location hazards with map links, and strip 100% of metadata with direct Gallery download. |
| **Image Steganography** | Conceal and reveal hidden secret text messages inside photo RGB pixels using LSB embedding with optional PBKDF2 + AES-256 password encryption and Gallery save. |
| **Sound & Decibel Meter (SPL)** | Live acoustic microphone sound pressure level (dB SPL) measurement with visual gauge meter, live sparkline history waveform, and environmental noise scale. |
| **Encryption & Decoder** | Multi-cipher offline cryptography: **AES-256, DES, Caesar, Vigenère, Base64, Hex, Binary, and Morse code**. |
| **Randomizer & Decisions** | Tabletop RPG dice rolling (D4 to D100), 3D-styled coin flipper, random number generator, and custom decision list picker. |
| **Productivity Suite** | Pomodoro focus timer, task checklist, habit streak tracker, and offline scratchpad. |
| **Science & Constants** | Interactive periodic table of elements with detailed properties, physics/chemistry constants, and scientific formulas. |
| **Health & Fitness** | Calculate **BMR, TDEE daily calorie expenditure, and daily hydration requirements**. |
| **Stopwatch & Timer** | Multi-lap precision stopwatch with millisecond accuracy and countdown timer. |
| **World Clock** | View time differences across global timezones and major international cities. |
| **Flashlight & Morse** | Camera LED torch, screen beacon, SOS strobe, and custom text-to-Morse optical/audio/haptic transmitter. |

---

### 📱 Home Screen Widgets

NexusKit includes 2 lightweight, battery-optimized Android App Widgets built with native `RemoteViews`:

1. **Quick Tools Grid (4x2)**: Direct 1-tap launch into 8 core tools plus Global Search.
2. **Quick Bar (4x1 / 2x1)**: Sleek compact utility toolbar for rapid access.

> **Optimization Note**: 0% background battery drain. Uses static `PendingIntent`s and requires no polling services or background battery usage.

---

### 🏗️ Architecture & Tech Stack

```
nexuskit/
├── core/
│   ├── util/               # Constants, formatters, math utilities
│   └── database/           # Room Database & TypeConverters
├── data/
│   ├── local/              # Room DAOs, DataStore Preferences
│   └── repository/         # Repository implementations
├── domain/
│   ├── model/              # Domain models, ToolRegistry, CategoryInfo
│   ├── repository/         # Repository contracts
│   └── usecase/            # Pure business logic use cases
├── feature/
│   ├── home/               # Dynamic home screen & category layouts
│   ├── drawer/             # Navigation drawer
│   ├── search/             # Instant overlay search
│   ├── settings/           # Appearance, themes, and configuration
│   └── tools/              # 42 individual tool engines, ViewModels & screens
└── ui/
    ├── component/          # Reusable Material 3 design components
    └── theme/              # Color schemes, Typography, Custom Shapes
```

### Key Libraries & Components
- **Language**: Kotlin 2.0.21
- **UI Framework**: Jetpack Compose BOM 2024.10.00
- **Design System**: Material Design 3 (Material You)
- **Dependency Injection**: Dagger Hilt
- **Architecture**: Clean Architecture (Data → Domain → Presentation) + MVI/MVVM
- **State Management**: Kotlin Coroutines & `StateFlow`
- **Data Persistence**: Jetpack DataStore Preferences & Room
- **Target OS**: Android 10 (API 29) to Android 15 (API 35)

---

### 🔒 Privacy & Security

NexusKit is engineered from the ground up for strict offline privacy:
- 🚫 **No Internet Connection Required**: Operates completely air-gapped.
- 🚫 **No Tracking or Analytics**: No Firebase, no telemetry, no third-party SDKs.
- 🚫 **No User Accounts**: Your data stays exclusively in your device's sandboxed storage.
- 🔒 **Open Source**: Full transparency with 100% inspectable source code.

---

### 🤝 Contributing

Contributions, feature ideas, and pull requests are welcome!

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/NewAwesomeTool`)
3. Commit your Changes (`git commit -m 'Add NewAwesomeTool'`)
4. Push to the Branch (`git push origin feature/NewAwesomeTool`)
5. Open a Pull Request

---

### 📄 License

This project is licensed under the **MIT License** — see the [LICENSE](LICENSE) file for details.

<div align="center">
  <sub>Built with ❤️ using Kotlin & Jetpack Compose</sub>
</div>
