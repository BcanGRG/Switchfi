# Switchfi

📶 Smart Wi‑Fi switcher for Android that monitors signal strength and auto‑connects you to the strongest known network. ⚡ Seamless, battery‑friendly background switching with zero fuss. 🧩 Built with Kotlin, Jetpack Compose, MVI, and Clean Architecture.

## Features
- **Auto switch to strongest known Wi‑Fi**: Follows Android policies via Wi‑Fi Suggestions.
- **Battery‑friendly**: Lightweight observation and OS‑driven handoff.
- **Networks screen**: Current / known / nearby SSIDs with RSSI levels.
- **Network detail**: Security type, signal trend, suggestion management.
- **Single‑Activity + type‑safe Compose Navigation**.

## Tech Stack
- **Language**: Kotlin
- **UI**: Jetpack Compose, Material 3
- **Architecture**: Clean Architecture + MVI
- **DI**: Hilt
- **Background**: WorkManager
- **Navigation**: Navigation Compose (2.8+ type‑safe destinations)

## Requirements
- Android Studio (Giraffe or newer)
- Min SDK 26+ (TBD), Target SDK latest stable
- Permissions: `NEARBY_WIFI_DEVICES` (API 33+), `ACCESS_FINE_LOCATION`, `ACCESS_WIFI_STATE`, `CHANGE_WIFI_STATE`

## Getting Started
```bash
./gradlew clean assembleDebug
```

## Roadmap
1. Project setup (Gradle, modules, MVI base)
2. Permissions & onboarding
3. Type‑safe Navigation graph
4. Wi‑Fi Suggestions repository
5. Background switching strategy
6. Scan & measure (RSSI)
7. Networks list screen
8. Network detail screen
9. Settings / Known networks
10. Tests & CI

## Contributing
- Use feature branches and conventional commits.
- Run tests before opening PRs.
- Keep layers aligned with Clean Architecture (`domain/`, `data/`, `ui/`).

## License
TBD
