# ZoneAlarm — location-triggered alarm (Android)

Rings like an alarm clock when the phone enters a user-defined zone.
Native Kotlin + Jetpack Compose, offline-first, no backend.

## Features (MVP)
- Map-picker zones with adjustable radius (100m–2km)
- Per-alarm one-shot / recurring mode
- Full-screen siren + sound + vibration, Stop + 10-min Snooze
- Works closed/killed; survives reboot; follows system dark/light theme

## Download & Manual Install (.apk)
Direct APK download for Android phones (API 26+ / Android 8.0+):
- **Download APK:** [`ZoneAlarm-v1.0.apk`](release/ZoneAlarm-v1.0.apk) (Raw download: [Direct Link](https://github.com/mygemku/zone_alarm/raw/main/release/ZoneAlarm-v1.0.apk))
- **Installation steps:**
  1. Download `ZoneAlarm-v1.0.apk` directly on Android device.
  2. Open the downloaded file from browser downloads or file manager.
  3. If prompted, enable **"Install unknown apps"** for your browser / file manager.
  4. Tap **Install** and open ZoneAlarm.
  5. Grant Location & Notification permissions when prompted.

## Requirements
- JDK 17, Android SDK (platform + build-tools 35), `ANDROID_HOME` set
  (machine setup recorded in `../TOOLCHAIN.md`)
- No API key needed — maps use MapLibre + OpenFreeMap (keyless) and Nominatim search

## Local development (PowerShell)
```powershell
$env:JAVA_HOME = 'C:\Program Files\Eclipse Adoptium\jdk-17.0.20.101-hotspot'
.\gradlew.bat assembleDebug          # app/build/outputs/apk/debug/app-debug.apk
.\gradlew.bat :app:testDebugUnitTest # 13 unit tests
```
1. Ensure `local.properties` has `sdk.dir` pointing to your Android SDK.
2. Install on emulator/device: `adb install -r <apk>`, grant permissions
   in-app (background location needed for closed-app firing).

## Project layout
`app/src/main/...`: `domain/` (model, repo interface), `data/` (Room,
DataStore, location), `di/` (Hilt), `geofence/` (manager + receivers),
`alarm/` (notifier, playback service, ringing UI, snooze worker),
`ui/` (theme, navigation, list, editor, settings, permissions).
Phase docs: `docs/phase_X_*.md`. Mockup: `location_alarm_app.html`.

## Release
- Versioning: `versionCode`++ per release, `versionName` semver
  (currently 1 / 1.0). R8 + resource shrink on.
- Signing (keystore held by YOU, never in repo):
  1. `keytool -genkey -v -keystore zonealarm-release.jks -alias zonealarm -keyalg RSA -keysize 2048 -validity 10000`
  2. Store outside the repo; add signingConfigs.release in
     `app/build.gradle.kts` pointing at it (passwords via env vars).
  3. `.\gradlew.bat bundleRelease` → upload AAB to Play.
- Verified: `bundleRelease` green, `app-release.aab` 3.6MB,
  `app-release-unsigned.apk` 1.6MB (release keystore still yours to create).
- Play submission needs a background-location declaration (in-app
  justification + demo video) plus listing, content rating, and
  internal → closed → production tracks.
