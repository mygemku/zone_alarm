# Phase 3: Setup notes (ZoneAlarm)

## Stack (locked in Phase 2)
Native Kotlin + Compose; `applicationId com.zonealarm.app`; minSdk 26,
targetSdk/compileSdk 36. AGP 8.5.2, Kotlin 2.0.20, KSP 2.0.20-1.0.25,
Hilt 2.51.1, Room 2.6.1, Compose BOM 2024.10.01, Play location 21.3.0,
maps-compose 4.3.3.

## Files
`settings.gradle.kts`, `build.gradle.kts`, `gradle.properties`,
`app/build.gradle.kts`, `app/proguard-rules.pro`,
`app/src/main/AndroidManifest.xml` (full Phase 2 permission set + receivers,
AlarmService, AlarmActivity placeholders), `ZoneAlarmApp` (Hilt),
`MainActivity` (placeholder home), `local.properties`
(`sdk.dir` + `MAPS_API_KEY=YOUR_MAPS_API_KEY` — real key still needed for
map tiles in Phase 5).

## Verification (2026-09-27)
- `gradle wrapper --gradle-version 8.9` → BUILD SUCCESSFUL (3m47s, JDK 17.0.20.1)
- `./gradlew assembleDebug` → BUILD SUCCESSFUL (6m24s, 41 tasks)
- Artifact: `app/build/outputs/apk/debug/app-debug.apk` (10.9 MB)
- Notes: Hilt deprecation note (generated code, harmless); NDK strip warning
  for two `.so` files (harmless, debug only).

## Open items for later phases
- Maps API key required before Phase 5 map picker works.
- Release signing (Phase 7, user-held keystore).
