# ZoneAlarm — brief

Location-triggered alarm for Android: rings like an alarm clock when the
phone enters a user-defined zone. Native Kotlin + Jetpack Compose,
offline-first, no backend.

## Key decisions
- Per-alarm one-shot / recurring mode; map picker; adjustable radius
- Full-screen siren + sound + vibration; Stop + 10-min Snooze
- Works closed/killed (system geofence delivery); survives reboot
- Follows system dark/light theme (Material3 dynamic color)
- Sideload APK first; Play later (background-location declaration needed)

## Status
v1.0 MVP complete — all 7 phases approved. See `CHANGELOG.md` for history.
Maps: MapLibre + OpenFreeMap (keyless) + Nominatim search — no API key.
Open items (user-side): release keystore, physical-device pass
(checklist in `phase_6_testing.md`), Play submission.
