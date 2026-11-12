# Earthdawn Effect Tracker 🎲

An intuitive Android application designed for Earthdawn tabletop RPG players and Game Masters to track active effect modifiers, stat adjustments, and round durations during combat and encounters.

---

## ✨ Features

- ⚡ **Track Stat Modifiers & Effects**: Easily add temporary or ongoing bonuses and penalties to combat statistics.
- 📜 **Earthdawn Attribute Presets + Free Text**: Choose from predefined Earthdawn stats (*Physical Defense*, *Mystic Defense*, *Social Defense*, *Physical Armor*, *Mystic Armor*, *Initiative*, *Steps*, *Karma*, etc.) or enter custom attributes.
- 📊 **Aggregated Stat Totals**: Displays real-time cumulative modifier totals per attribute with color-coded badges (green for positive, red for negative, neutral for zero).
- ⏳ **Round-Based Duration Tracking**: Advance to the next round with a single tap. Expired effects (reaching 0 rounds) are automatically purged.
- 🔄 **Quick Reset & Manual Deletion**: Clear individual effects or reset the entire session tracker instantly.

---

## 🛠️ Built With

- **Language**: [Kotlin](https://kotlinlang.org/)
- **UI Architecture**: Material 3 Design, ViewBinding, Navigation Component
- **State Management**: Android Jetpack `ViewModel`, `StateFlow`
- **Minimum Android SDK**: API 24 (Android 7.0+)
- **Target Android SDK**: API 37

---

## 🚀 Building & Running

### Prerequisites
- Android Studio Ladybug (or newer)
- JDK 17 / JDK 21

### Build APK
Run the following Gradle command to assemble the release APK:
```bash
./gradlew assembleRelease
```
Or build debug APK:
```bash
./gradlew assembleDebug
```

---

## 📄 License

This project is licensed under the GNU General Public License v3.0 - see the [LICENSE](LICENSE) file for details.
