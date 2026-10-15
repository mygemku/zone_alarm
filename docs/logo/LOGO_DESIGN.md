# ZoneAlarm Logo & App Icon Design System

**Brand:** ZoneAlarm  
**Tagline:** Geofence Wake Alarm — Wake up when you get there  
**Category:** Travel, Navigation & Clock  
**Date:** 2026-10-15  

---

## 1. Creative Concept & Symbology

ZoneAlarm wakes commuters, travelers, and transit passengers when they enter their destination geofence zone. The logo fuses four core metaphors into a unified, high-recognition mark:

```
                  ( 3. Sonar Wave )
           [Crown Ring]
                /\
          /    /  \    \
        ( (   | 2. |   ) )
        ( (   |BELL|   ) )  <-- 1. Pin-Bell Fusion Body
              \    /
               \  /
                \/  <-- 1. GPS Needle
           ( 4. Ground Radar )
```

1. **The Beacon Pin**: Lower half is a precision tapered GPS needle marking the target coordinate origin on the map.
2. **The Alarm Bell**: Upper crown and flared shoulder form a resonant chime bell, conveying wakefulness and audible alerting.
3. **Acoustic Sonar Waves**: Radiating electric indigo wave arcs `( (  ) )` on both flanks represent active geofence proximity scanning and alarm broadcast.
4. **Ground Radar Ripple**: Concentric ground plane ellipses at the needle impact point represent the geofence perimeter radius.
5. **Aperture & Beacon Clapper**: Negative space dark center with a pure white focal core creates maximum contrast and visual punch at small sizes (16dp–48dp).

---

## 2. Color Tokens

| Role | Token | Hex | Usage |
|------|-------|-----|-------|
| **Primary (Sun Amber)** | `Amber500` | `#F59E0B` | Main emblem body, warm wake-up glow |
| **Primary Deep** | `Amber700` | `#B45309` | Lower pin shadow, light-mode high contrast |
| **Crown Gold** | `Amber400` | `#FBBF24` | Bell crown ring, upper specular highlight |
| **Acoustic Pulse** | `Indigo500` | `#6366F1` | Radiating sonar wave arcs |
| **Vibrant Pulse** | `Indigo400` | `#818CF8` | Inner wave pulse, ground radar ripple |
| **Midnight Surface** | `Slate900` | `#0F172A` | Background tile, clapper aperture |
| **Deep Space** | `Slate950` | `#020617` | Radial gradient perimeter |
| **Beacon Light** | `Pure White` | `#FFFFFF` | Center GPS focal pulse, clapper dot |

---

## 3. Android Adaptive Icon Architecture

Designed strictly to the Android 8.0+ (API 26+) adaptive icon specification:
- **Canvas Size:** 108dp × 108dp
- **Safe Zone:** 72dp diameter circle centered at `(54, 54)`
- **Outer Margin:** 18dp parallax/physics buffer around perimeter
- **Zero Clipping Guarantee:** All critical emblem elements are strictly within radius ≤ 30dp from center, leaving 6dp safety margin inside any launcher mask (Circle, Squircle, Rounded Square, Teardrop).

### File Hierarchy in Codebase:
- `app/src/main/res/drawable/ic_launcher_background.xml` — Deep midnight slate background with subtle radar coordinate lines and warm ambient glow.
- `app/src/main/res/drawable/ic_launcher_foreground.xml` — Vector emblem with gradient pin-bell body, wave arcs, crown ring, and clapper.
- `app/src/main/res/drawable/ic_launcher_monochrome.xml` — Material You themed icon for Android 13+ dynamic wallpaper tinting.
- `app/src/main/res/mipmap-anydpi-v26/ic_launcher.xml` — Adaptive launcher configuration.
- `app/src/main/res/mipmap-anydpi-v26/ic_launcher_round.xml` — Adaptive launcher configuration for round icon targets.

---

## 4. In-App Compose Component

The vector logo is implemented as a resolution-independent Jetpack Compose component in:
`app/src/main/java/com/zonealarm/app/ui/components/ZoneAlarmLogo.kt`

- **`ZoneAlarmLogoMark(size, showBackground)`**: Standalone mark canvas. Rendered in:
  - Top app bar in `AlarmListScreen.kt` (36dp).
  - About card in `SettingsScreen.kt` (44dp).
- **`ZoneAlarmLogoLockup(markSize, showSubtitle)`**: Full lockup with branded typography and tagline.

---

## 5. Master Asset Files

Saved in `docs/logo/`:
- `zonealarm_icon_mark.svg`: 512×512 Google Play Store Master Icon.
- `zonealarm_logo_lockup.svg`: 880×260 Horizontal Brand Lockup.
- `zonealarm_monochrome.svg`: 512×512 Single-color silhouette.
- `preview.html`: Interactive web showcase with light/dark toggle and shape mask switcher.
