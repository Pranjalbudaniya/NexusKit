<div align="center">

# ⚡ NexusKit
### *The Ultimate Offline Utility & Productivity Suite for Android*

[![Android](https://img.shields.io/badge/Platform-Android%2010%2B%20(API%2029%2B)-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://android.com)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0.21-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Compose](https://img.shields.io/badge/Jetpack%20Compose-2024.10.00-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![Clean Architecture](https://img.shields.io/badge/Architecture-Clean%20%2B%20MVI%2FMVVM-F43F5E?style=for-the-badge)]()
[![Offline First](https://img.shields.io/badge/Offline-100%25%20No%20Internet%20Required-10B981?style=for-the-badge)]()
[![License](https://img.shields.io/badge/License-MIT-blue?style=for-the-badge)](LICENSE)

<br />

**NexusKit** is an open-source, private, and lightning-fast Swiss Army knife for Android. Packed with 20+ offline calculation, conversion, text, developer, and productivity tools in a sleek Material You (M3) design — zero accounts, zero analytics, zero ads, zero battery drain.

[Features](#-feature-suite) • [Widgets](#-home-screen-widgets) • [Tech Stack](#-architecture--tech-stack) • [Building](#-getting-started) • [Privacy](#-privacy--security)

</div>

---

## 🌟 Why NexusKit?

- 🔒 **100% Offline & Private** — Never communicates with external servers. No telemetry, no ads, no trackers, and no user accounts.
- ⚡ **Instant Response Times** — Native Jetpack Compose UI running pure local Kotlin calculation engines.
- 🔋 **Zero Battery Drain** — No background wake-locks, polling workers, or heavy daemon services.
- 🎨 **Deep Customization** — Material You Dynamic Color, AMOLED Dark Theme, Custom Font Providers, and 50+ geometric card shape customizers.
- 📱 **Battery-Efficient Widgets** — Lightweight 4x2 Quick Grid and 4x1 Quick Bar for 1-tap access to your daily tools.

---

## 🛠️ Feature Suite

### 🔢 1. Number & Mathematics
| Tool | Description |
| :--- | :--- |
| **Scientific Calculator** | Fast arithmetic with live expression parsing, memory registers, and history log. |
| **Percentage & Bill Split** | Instant percentage math, tip calculation, margins, and multi-person bill splitting. |
| **Radix Base Converter** | Real-time simultaneous radix conversion between **Binary, Octal, Decimal, and Hexadecimal**. |
| **Date & Time Calculator** | Calculate exact difference between dates (years, months, days) and add/subtract duration. |
| **Discount & Sales Tax** | Compute final sale price, discount savings, and VAT/GST with percentage breakdown. |
| **Loan & EMI Calculator** | Monthly amortization, total payable interest, and loan payoff schedules. |
| **BMI & Ideal Weight** | Body Mass Index calculation, classification categories, and healthy target weight ranges. |

---

### 🔄 2. Converters & Generators
| Tool | Description |
| :--- | :--- |
| **Universal Unit Converter** | Convert across **Length, Mass, Temperature, Speed, Digital Storage, Energy, and Volume**. |
| **QR Code Generator** | Generate high-contrast offline QR codes for raw text, website URLs, and Wi-Fi credentials. |
| **Password & UUID Suite** | Cryptographically secure random password generator with entropy meter and UUID v4 generator. |
| **Cryptographic Hash Engine** | Generate instant checksums using **MD5, SHA-1, SHA-256, and SHA-512** digests. |
| **Harmonious Color Palette** | Generate complementary, monochromatic, and analogous swatches with 1-tap HEX/RGB/HSL copy. |

---

### 📝 3. Text & Developer Tools
| Tool | Description |
| :--- | :--- |
| **Markdown Notes & Editor** | Offline scratchpad and note editor with live word count, character statistics, and reading time. |
| **Case Converter** | Transform text into `UPPERCASE`, `lowercase`, `camelCase`, `snake_case`, `kebab-case`, `Title Case`, and URL `slug`. |
| **Text Inspector & Diff** | Real-time word frequency analysis, whitespace cleanup, and visual line-by-line diff comparison. |
| **Developer Toolbox** | **JSON Formatter & Validator**, Base64 & URL Encoder/Decoder, RegEx tester, and device hardware specs. |
| **Quick Reference Sheets** | Offline cheat sheets for **Markdown syntax, ASCII & Unicode tables, HTTP status codes, and NATO phonetic alphabet**. |

---

### 🚀 4. Productivity & Utilities
| Tool | Description |
| :--- | :--- |
| **Pomodoro Focus Timer** | Circular progress countdown with work/break intervals and session history. |
| **Task Checklist & Todos** | Priority-tagged daily task checklist with instant toggle and deletion. |
| **Habit Streak Tracker** | Track daily habits with automatic streak calculations. |
| **Precision Stopwatch** | Multi-lap precision stopwatch with millisecond accuracy. |
| **World Clock** | View time differences across global timezones and major international cities. |
| **Health & Fitness Suite** | Calculate **BMR, TDEE daily calorie expenditure, and hydration water intake**. |

---

### 🔦 5. Flashlight & Text-to-Morse Transmitter
- **Live Text-to-Morse Code**: Type any custom phrase (e.g., `SOS`, emergency coordinates, or custom messages) and transmit it in real time.
- **Synchronized Multi-Channel Output**:
  - 📸 **Camera LED Flashlight** (Hardware torch blinking)
  - 💡 **Full-Screen Strobe & Beacon Light**
  - 🔊 **800Hz Synthesized Audio Beeps** (Pure sine-wave tone synthesizer with anti-click envelope)
  - 📳 **Haptic Vibration Pulses**
- **Speed & Presets**: Adjustable transmission speed (5 to 35 WPM), loop mode toggle, and quick emergency presets (`SOS`, `HELP`, `MAYDAY`, `HELLO`, `SAFE`).

---

## 📱 Home Screen Widgets

NexusKit includes 2 lightweight, battery-optimized Android App Widgets built with native `RemoteViews`:

1. **Quick Tools Grid (4x2)**: Direct 1-tap launch into 8 core tools (*Calculator, Flashlight/Morse, Unit Converter, Notes, QR Generator, Stopwatch, Passwords, BMI*) plus Global Search.
2. **Quick Bar (4x1 / 2x1)**: Sleek compact utility toolbar for rapid access.

> **Optimization Note**: 0% background battery drain. Uses static `PendingIntent`s and requires no polling services or background battery usage.

---

## 🏗️ Architecture & Tech Stack

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
│   └── tools/              # Individual tool engines, ViewModels & screens
└── ui/
    ├── component/          # Reusable Material 3 design components
    └── theme/              # Color schemes, Typography, 50+ Custom Shapes
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

## 🚀 Getting Started

### Prerequisites
- Android Studio Ladybug (2024.2.1+) or newer
- JDK 17 or JDK 21 (bundled with Android Studio)
- Android SDK with API 35 installed

### Clone and Build

```bash
# 1. Clone repository
git clone https://github.com/Pranjalbudaniya/NexusKit.git
cd NexusKit

# 2. Build Debug APK
./gradlew assembleDebug

# 3. Install on connected device via ADB
./gradlew installDebug
```

---

## 🔒 Privacy & Security

NexusKit is engineered from the ground up for strict offline privacy:
- 🚫 **No Internet Connection Required**: Operates completely air-gapped.
- 🚫 **No Tracking or Analytics**: No Firebase, no telemetry, no third-party SDKs.
- 🚫 **No User Accounts**: Your data stays exclusively in your device's sandboxed storage.
- 🔒 **Open Source**: Full transparency with 100% inspectable source code.

---

## 🤝 Contributing

Contributions, feature ideas, and pull requests are welcome!

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/NewAwesomeTool`)
3. Commit your Changes (`git commit -m 'Add NewAwesomeTool'`)
4. Push to the Branch (`git push origin feature/NewAwesomeTool`)
5. Open a Pull Request

---

## 📄 License

This project is licensed under the **MIT License** — see the [LICENSE](LICENSE) file for details.

<div align="center">
  <sub>Built with ❤️ using Kotlin & Jetpack Compose</sub>
</div>
