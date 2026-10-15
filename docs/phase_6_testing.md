# Phase 6: Testing (ZoneAlarm)

## Unit tests — 13/13 pass (`:app:testDebugUnitTest`, BUILD SUCCESSFUL)
Pure JVM: fake DAO, fake geofence/location/settings, Turbine flow asserts.
No Robolectric, no mocking libs. New test-only deps: `coroutines-test`,
Turbine.
- `ZoneRepositoryImplTest` (4): add/get roundtrip, missing→null,
  observe emits updates, update/remove/disable.
- `AlarmListViewModelTest` (4): toggle-on registers+enables, toggle-off
  unregisters+disables, denial surfaces error, delete unregisters+removes.
- `ZoneEditorViewModelTest` (5): blank name blocks save, save-new registers
  + finishes, edit loads + updates, location-null error, location updates
  position.
- Testability refactors: `GeofenceController`, `CurrentLocationProvider`,
  `SettingsStore` interfaces (+ Hilt bindings). `SettingsRepository`
  (DataStore) has no unit test — needs a device/emulator.

## Instrumented tests — NOT EXECUTED (no emulator possible)
Emulator cannot boot on this i7-2600 (guest dies despite working WHPX;
documented in phase_5_ui.md). No Espresso/Compose tests written — they
would be unrunnable here. Manual checklist instead (below).

## Audit report
- **Fixed:** uncaught `SecurityException` in `GeofenceReceiver` /
  `BootReceiver` would crash the process if permission was revoked between
  registration and delivery. Both now catch it (UI gate re-prompts).
- Main-thread I/O: none blocking (Room/DataStore suspend; `Task.await`
  suspends; `MediaPlayer.prepare()` on local URI is fast — advisory only).
- Coroutine leaks: receiver scopes end with `goAsync.finish()`; service
  scope cancelled in `onDestroy`; ViewModel scopes owned.
- Secrets: none hardcoded; Maps key still a placeholder (Phase 5 note).
- Reliability notes: `START_NOT_STICKY` (killed alarm stays silent — MVP
  accepted); snooze is WorkManager nominal-10-min (inexact by design).

## Manual device checklist (physical device or working emulator)
1. Install debug APK; walk the permission gate (deny paths included).
2. Deny background location → warning shows; Continue works with limits.
3. Create one-shot + recurring alarms via map picker; adjust radius.
4. Toggle off/on; delete; edit name/radius/mode.
5. Trigger entry (small radius + movement, or mock-location app):
   full-screen siren + sound + vibration; Stop; Snooze → re-fires ~10 min.
6. Force-stop app → entry still fires (system-delivered).
7. Reboot → enabled geofences re-registered (logcat `BootReceiver`).
8. Revoke location in Settings → no crash; list prompts again.
