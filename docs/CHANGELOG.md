# ZoneAlarm — changelog

Companion: `BRIEF.md` (what/why/status). One line per event, newest last.

## Changelog
- **2026-09-26** — Session start. Diagnosed two broken VSCode extensions
  (Buddy 0.0.7: no serve-auth → `Failed to create session: {}`; opencode-chat
  2.11.0: unmaintained, v1 routes → `Unrecognised route!`). Removed both;
  adopted `opencode --continue` terminal flow.
- **2026-09-26** — Created web-app workflow (7 phases + gates); created
  Android workflow (+ Phase 0 toolchain check); wired both via `AGENTS.md`.
- **2026-09-27** — Phase 0 toolchain: Android Studio 2026.1.4.7, JDK 25
  (JBR), SDK 35/36, adb 37, emulator + Pixel_API35 AVD, Flutter 3.47.5,
  Temurin JDK 17, Gradle 8.9, Git 2.55. Repaired npm 12.1.0 → 11.19.0
  (Node 24.21.0 kept). → `TOOLCHAIN.md`.
- **2026-09-27** — ZoneAlarm idea locked. Phase 1 approved (per-alarm
  one-shot/recurring, map picker, full alarm, adjustable radius).
  Phase 2 approved (Room, Geofencing client, alarm service, permissions plan).
- **2026-09-27** — Phase 3 scaffold built (`assembleDebug` green, 10.9MB APK).
  Restructured to `zone_alarm/` (code + `docs/` + assets per project);
  adopted `phase_X_*.md` doc scheme; backfilled phase 1–2 docs.
- **2026-09-27** — Phase 4 data layer approved (Room, repo, geofence engine,
  playback service; build green). Phase 5 UI written (theme, list, map
  editor, settings, permissions, ringing screen + snooze).
- **2026-09-27** — Emulator blocked: VT-x off in BIOS, no hypervisor.
  User enabled VT-x; installed Windows Hypervisor Platform (WHPX usable).
- **2026-09-28** — Emulator screenshot run in progress
  (`docs/screenshots/`).
- **2026-09-28** — Phase 7 done: R8 + shrink on, `bundleRelease` green
  (AAB 3.6MB, unsigned APK 1.6MB), README + release doc written.
- **2026-10-14** — Device testing & bug fixes on physical Xiaomi (HyperOS):
  Fixed MapLibre init crash (`MapLibre.getInstance(this)` in Application class).
  Fixed `CannotAddSourceException` by reusing existing GeoJsonSource via `setGeoJson()`.
  Fixed pin/camera updates: migrated from fragile `AndroidView.update` to reactive
  `LaunchedEffect` keyed on `(map, style, lat, lng, radius)` + one-time click listener.
  Added keyboard search action (`ImeAction.Search`). Verified end-to-end on device:
  map tap moves pin, Nominatim search returns results & animates camera, Locate button
  reads real GPS (-6.2500, 106.8287) and moves pin. All 15 unit tests green. Debug APK deployed.
  Release keystore + Play submission remain user-side.
- **2026-10-15** — UI/UX Redesign via UI UX Pro Max skill:
  Applied "Dark Mode (OLED)" design system with Amber-600 primary (#D97706),
  Indigo-500 accent (#6366F1), on a Slate-950/Slate-900 surface base.
  Created `Color.kt` (semantic palette) and `Type.kt` (custom typography scale).
  Rewrote `Theme.kt` with branded Material3 color schemes and custom shapes.
  Redesigned `AlarmListScreen` (header subtitle, empty state illustration, Extended FAB).
  Redesigned `AlarmZoneCard` (icon badge, custom switch colors, subtle delete button).
  Redesigned `ZoneEditorScreen` (rounded search bar, text+icon Locate button, full-width Save).
  Redesigned `SettingsScreen` (section headers, defaults card, about app card).
  Persisted design system to `zone_alarm/design-system/zonealarm/MASTER.md`.
  15/15 unit tests pass. Redesigned APK verified on device via screenshots.
- **2026-10-15** — Dynamic Light & Dark Theme Support (Auto-adjusting):
  Implemented system theme detection (`isSystemInDarkTheme()`) with full two-theme color scheme:
  Light theme (Emerald / Forest green palette with crisp clean surfaces) and Dark theme (OLED deep green palette).
  Created dark and light map styles (Carto Voyager for Light, Carto Dark Matter for Dark).
  MapLibre in `ZoneEditorScreen` automatically switches style based on active theme mode.
  Verified dynamic switching on physical device via `cmd uimode night yes/no`.
  Captured screenshots for both themes in `docs/screenshots/test_dark_mode.png` and `test_light_mode.png`.
  15/15 unit tests pass. Debug build green.
- **2026-09-28** — User chose truly-free maps. Phase 2 revision approved:
  drop Maps Compose/key; MapLibre Native + OpenFreeMap tiles + Nominatim
  search. Verified: OpenFreeMap keyless/unlimited; maplibre-compose is
  KMP/Beta (rejected for this pure-Android app). Resolved: android-sdk
  latest 11.11.0, retrofit 2.9.0 confirmed (3.0.0 too new to trust blind).
  Implementation resumes next session: swap editor map, add geocoding,
  remove key plumbing, rebuild.
- **2026-10-01** — MapLibre migration done: removed `maps-compose` +
  `MAPS_API_KEY` (manifest/build/local.properties), added
  `maplibre:android-sdk:11.11.0` + OpenFreeMap liberty style (keyless) +
  Retrofit/Nominatim search; rewrote `ZoneEditorScreen` to `MapView` +
  GeoJSON radius circle + marker + tap-to-place + search bar; added
  `GeocodingRepository` + Hilt wiring; updated tests (15/15 pass);
  `assembleDebug` + `bundleRelease` green (AAB 18.7MB, debug APK 56.4MB;
  per-device download ~5-7MB via Bundle). No API key required.
