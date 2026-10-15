# Phase 1: Discovery (ZoneAlarm, approved)

## App
**ZoneAlarm** — location-triggered alarm: rings like an alarm clock when the
phone enters a user-defined geographic zone.

## Audience & devices
General Android users (commuters, travelers). Phone-only. minSdk 26,
targetSdk/compileSdk 36.

## MVP features (locked)
- Map-picker zone setup (user chose map picker over current-location-only)
- Adjustable radius per alarm (100m–2km slider, per mockup)
- Per-alarm mode: one-shot (fire once, auto-disable) or recurring (fire on
  every entry) — user asked for both, settable per alarm
- Full-screen siren UI with sound + vibration + dismiss/snooze
- Alarm list management (enable/disable/edit/delete)
- Geofences survive reboot (re-registration)

## Permissions (with Play-policy justification)
- `ACCESS_FINE/COARSE_LOCATION` — zone detection
- `ACCESS_BACKGROUND_LOCATION` — geofence transitions while app closed
  (Play declaration required at Play submission)
- `POST_NOTIFICATIONS` — alarm notification channel (API 33+)
- `USE_FULL_SCREEN_INTENT` — full-screen alarm UI (API 34+)
- `FOREGROUND_SERVICE` (+ mediaPlayback type) — reliable alarm sound
- `SCHEDULE_EXACT_ALARM` — snooze timing
- `RECEIVE_BOOT_COMPLETED` — re-register geofences after reboot
- `VIBRATE`

## Offline
Offline-first, local-only MVP. Map tiles need network at setup time only;
runtime geofencing works offline via GPS. No backend.

## Stack (locked)
Native Kotlin + Jetpack Compose (background location + full-screen alarm are
framework-heavy; single-platform need). Room + DataStore, Hilt, Coroutines/Flow,
Play Services Geofencing client + Maps Compose. Sideload APK first, Play later.

## Reference
UI mockup: `../location_alarm_app.html` (home list, map editor with radius
slider, ringing screen; dark + cyan theme, Jakarta sample places).
**Theming requirement (locked):** the app follows the system theme —
dark UI when the system is dark, light UI when light (mockup shows the
dark variant; light variant derived from the same palette).
