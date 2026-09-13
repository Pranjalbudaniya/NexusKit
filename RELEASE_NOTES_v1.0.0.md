# 🚀 NexusKit v1.0.0 — Initial Release

> **The Ultimate All-in-One Offline Utility & Productivity Suite for Android.**  
> 20+ specialized tool suites, 100% offline functionality, zero telemetry, zero background battery drain, and deep Material 3 customization.

---

## 🌟 Highlights & What's New

### 🛠️ Complete Offline Utility Suite
- **Text & Developer Suite**: Case converter, slugifier, regex tester, JSON prettifier & validator, Base64/Hex/URL encoders, and live character/word analyzer.
- **Security & Privacy**: Cryptographically secure offline password generator with entropy scoring, customizable character subsets, and security recommendations.
- **Smart Math & Financial Calculators**: Percentage calculators, discount calculators, unit converters (Length, Weight, Temp, Storage, Speed, Pressure, Power, Energy), and loan EMI calculators with amortization breakdown.
- **Live Flashlight & Morse Transmitter**: Camera LED torch, screen beacon, SOS mode, and an interactive **Text-to-Morse engine** with synchronized LED flashing, screen strobe, 800Hz synthesized audio, and haptic feedback.
- **QR Code & Barcode Studio**: Fully offline vector QR code generator with instant PNG export and customizable error correction.
- **Science & Education**: Interactive Periodic Table of Elements, Physics Constants reference, and Unit & Conversion formula cheat sheets.
- **Health & Fitness**: BMI / BMR calculator, daily water intake planner, and target heart rate zone estimator.
- **Productivity & Time**: World Clock with multiple timezones, precision stopwatch with lap logging, and lightweight offline markdown text editor.

### 📱 Ultra-Lightweight Android Widgets
- **4x2 Quick Tools Grid**: Instant 1-tap launcher for Flashlight, QR Generator, Password Creator, Unit Converter, Notes, Calculator, Stopwatch, and BMI.
- **4x1 Quick Search & Utility Bar**: System search trigger and quick access bar.
- *Designed with zero background workers or polling services for 0% standby battery consumption.*

### 🎨 Deep Theming & Personalization
- **Full Material 3 Dynamic Engine**: 7 curated aesthetic themes (Nexus Cyan, Emerald Forest, Sunset Crimson, Midnight Slate, Cyberpunk Violet, Solar Amber, Rosewood) + Dynamic Monet wallpaper theming.
- **Live Typography Engine**: Real-time font switching across Inter, Roboto, Outfit, JetBrains Mono, Space Grotesk, Poppins, and Fira Code.
- **Adaptive Card Shapes**: Switch between Round, Squircle, Rounded Square, and Sharp corner styles.
- **Curated Favorites & Drawer Navigation**: Pin favorite tools for 1-tap launch from top shelf or sliding navigation drawer.

---

## 📦 Downloads & Split ABI APKs

NexusKit is packaged with ABI splitting and R8 bytecode optimization for minimal file sizes (~4.6 MB per build). Choose the APK corresponding to your device's architecture:

| Architecture | File Name | Size | Recommended For | SHA-256 Checksum |
| :--- | :--- | :--- | :--- | :--- |
| **ARM64 (64-bit)** | `NexusKit-v1.0.0-arm64-v8a.apk` | 4.64 MB | 99% of modern Android smartphones | `524043D9798435619867AB6BD0A50BB16CBAD7A59A77C96D8572F96FE6E86930` |
| **ARMv7 (32-bit)** | `NexusKit-v1.0.0-armeabi-v7a.apk` | 4.64 MB | Older Android devices | `57BCEB8A19EEF01D8DD14864FCF0754E4F5D37C2305A6605EF6DCC96013FBE2F` |
| **x86_64 (64-bit)** | `NexusKit-v1.0.0-x86_64.apk` | 4.64 MB | Intel/AMD Android emulators & Chromebooks | `562CC8A0F5F9937BE3089FF9B66BD7CBBA53D40A611A8827B89DB3DA5C9CA887` |
| **x86 (32-bit)** | `NexusKit-v1.0.0-x86.apk` | 4.64 MB | 32-bit emulators | `BD7934C67C2DA1990CD0F837583956FA920CB39B412D5E3339E5B4FDB5F8BAA6` |
| **Universal** | `NexusKit-v1.0.0-universal.apk` | 4.74 MB | All architectures combined | `C2F7586D0BBB031C0BFFE821DD24D3F5A54127D495F753D19CBAEDD7E1001DA9` |

---

## 🔒 Privacy & Permissions
- **Zero Internet Permission**: App manifest contains no `android.permission.INTERNET`.
- **Local Data Storage**: All preferences and history remain securely encrypted on-device via Room SQLite & Preferences DataStore.
- **Hardware Access**: Camera Flashlight (for torch/Morse transmitter) and Vibration (for haptics) only when active.

---

**Full Changelog**: https://github.com/Pranjalbudaniya/NexusKit/commits/v1.0.0
