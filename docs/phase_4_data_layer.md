# Phase 4: Data layer & business logic (ZoneAlarm)

## Files (13 new, all under `app/src/main/java/com/zonealarm/app/`)
- `domain/model/AlarmZone.kt` — data class + `AlarmMode` (ONE_SHOT/RECURRING)
- `domain/repo/ZoneRepository.kt` — Flow observe, CRUD, setEnabled
- `data/local/` — `AlarmZoneEntity` (+ mappers), `AlarmZoneDao`
  (observe/upsert/delete/setEnabled/getEnabled), `ZoneAlarmDatabase` (v1,
  no migration needed yet; manual migrations once shipped)
- `data/repo/ZoneRepositoryImpl.kt`, `di/AppModule.kt` (db, dao, repo,
  GeofencingClient singletons)
- `geofence/GeofenceManager.kt` — ENTER transitions, `requestId` = zone id,
  `INITIAL_TRIGGER_ENTER`, mutable PendingIntent (API 31+), refresh-all
- `geofence/GeofenceReceiver.kt` — one-shot unregister+disable, else fire
- `geofence/BootReceiver.kt` — re-register enabled zones after reboot
- `alarm/AlarmNotifier.kt` — HIGH alarm channel (alarm tone + vibration),
  full-screen intent to AlarmActivity, Dismiss action
- `alarm/AlarmService.kt` — foreground service, looping `USAGE_ALARM` sound
  (best-effort) + waveform vibration, START_NOT_STICKY
- `alarm/AlarmActivity.kt` — functional minimal (zone name + Stop);
  Phase 5 restyles to mockup ringing screen + snooze
- `app/build.gradle.kts` — added `kotlinx-coroutines-android` and
  `-play-services` 1.8.1 (for `Task.await()`)

## Design notes
- No network clients / auth: local-only MVP per PRD (N/A, no backend).
- No foreground service for monitoring: system delivers transitions.
- Sound failure never blocks vibration + notification (try/catch).

## Verification (2026-09-27)
- `:app:testDebugUnitTest` → NO-SOURCE (no unit tests yet; Phase 6 writes them)
- `:app:assembleDebug` → BUILD SUCCESSFUL (46 tasks, KSP/Hilt/Room clean)
