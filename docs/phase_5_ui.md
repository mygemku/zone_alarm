# Phase 5: UI (ZoneAlarm)

## Files (14 new/rewritten under `app/src/main/`)
- `ui/theme/Theme.kt` — Material3, follows system theme (dynamic color
  API 31+, static slate/cyan fallback below). Locked P1 requirement.
- `ui/navigation/NavGraph.kt` — list → editor?zoneId → settings
- `ui/list/` — ViewModel (observe, toggle→register/unregister, delete) +
  list screen (cards, FAB, settings action, error snackbars)
- `ui/editor/` — ViewModel (new/edit, validation, save→register) + screen
  (map picker with radius circle, name field, radius slider 100m–2km,
  one-shot/recurring chips, Locate action, Save)
- `ui/settings/` + `data/datastore/SettingsRepository.kt` — default radius
- `ui/components/Common.kt` — zone card, radius slider, mode selector
- `ui/permissions/PermissionScreen.kt` — first-run gate: notifications,
  foreground location, background location with rationale + settings
  fallback; continue always available, gaps shown as warnings
- `MainActivity` — theme + permission gate (re-checks on resume) + nav host
- `alarm/AlarmActivity.kt` — mockup ringing screen: radar waves, zone name,
  Stop + 10-min Snooze (`SnoozeWorker`, WorkManager, no Hilt wiring needed)
- `app/build.gradle.kts` — work-runtime, hilt-navigation-compose

## Out of scope notes
- Map tiles need a real `MAPS_API_KEY` in `local.properties` (still placeholder).
- Snooze is a fixed 10 min for MVP.

## Verification (2026-09-28)
- `assembleDebug` → BUILD SUCCESSFUL (all screens compile; 3 compile errors
  found and fixed during the phase: missing import, extended-icons icon,
  animation API param).
- **Emulator screenshots: BLOCKED, not skipped.** WHPX is operational and the
  AVD passes all compatibility checks, but the guest dies during early boot
  on this i7-2600 (Sandy Bridge) — reproduced across GPU modes and cold
  boots, no adb device ever comes online. Screenshots deferred to a physical
  device run. Screen states therefore verified by build only; runtime
  verification (renders / navigation / permission paths) is open.
- Verification level reached: `assembleDebug` (phase minimum).
