# Phase 2: Architecture (ZoneAlarm, approved)

## Module
Single `app` module, `applicationId com.zonealarm.app`.

## Structure
- `data/local/` — Room `AlarmZoneEntity`, DAO, `ZoneAlarmDatabase`
- `data/datastore/` — settings (default radius, sound)
- `data/repo/` — `ZoneRepository` interface + impl
- `geofence/` — `GeofenceManager` (register/unregister via GeofencingClient),
  `GeofenceReceiver` (transition events), `BootReceiver` (re-register on boot)
- `alarm/` — `AlarmService` (foreground service: sound + vibration),
  `AlarmActivity` (full-screen dismiss/snooze)
- `ui/` — `AlarmList`, `ZoneEditor` (map + radius slider + mode), `Settings`;
  ViewModels with StateFlow; Hilt DI; Play Geofencing + Maps Compose

## Data
`AlarmZone(id UUID, name, lat, lng, radiusMeters, mode ONE_SHOT|RECURRING,
soundUri?, isEnabled, createdAt)`. DAO: observe-all flow, get-by-id, upsert,
delete, set-enabled. Geofence `requestId` = zone id.

## Behavior rules
- Monitoring needs no foreground service (system-delivered transitions).
- One-shot: remove geofence + disable zone after firing. Recurring: keep.
- Permission order: notifications → foreground location → background location
  (settings fallback) → full-screen intent → exact-alarm for snooze. Denied
  states are shown in the list, not silent.

## Theming
Material3 with dynamic color (`dynamicDarkColorScheme` /
`dynamicLightColorScheme` on API 31+, static fallback schemes below),
driven by `isSystemInDarkTheme()` — no in-app toggle for MVP. Dark palette
matches the mockup (slate + cyan accent); light palette derived from it.
