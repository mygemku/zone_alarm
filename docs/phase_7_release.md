# Phase 7: Release (ZoneAlarm)

## Config
- R8 full mode + resource shrink enabled; keep-rules for alarm/geofence
  entry points + Room entity (`app/proguard-rules.pro`).
- Version strategy: `versionCode`++ per release, semver `versionName`
  (now 1 / 1.0).

## Verification (2026-09-28)
- `assembleRelease` + `bundleRelease` → BUILD SUCCESSFUL (62 tasks, R8 clean)
- `app-release-unsigned.apk` 1.6MB, `app-release.aab` 3.6MB
  (`app/build/outputs/...`). Release keystore not created (held by user) —
  production AAB must be signed with it; debug-signed bundle proves config.

## Docs & procedure
- `README.md`: setup, build/test commands, layout, signing + Play steps.
- Signing: generate keystore outside repo → `signingConfigs.release`
  (env-var passwords) → `bundleRelease` → Play tracks.
- Play: background-location declaration required; listing, rating,
  internal → closed → production.
- CI: not requested; standard `assembleDebug` + `testDebugUnitTest` job
  fits `.github/workflows/` when wanted.
