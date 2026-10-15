# Add custom ProGuard rules here if minification is enabled later.
# Keep the full-screen alarm entry points (referenced from manifest/notifications).
-keep class com.zonealarm.app.alarm.AlarmActivity { *; }
-keep class com.zonealarm.app.alarm.AlarmService { *; }
-keep class com.zonealarm.app.geofence.GeofenceReceiver { *; }
-keep class com.zonealarm.app.geofence.BootReceiver { *; }
# Room entities are referenced by generated code; keep fields for safety.
-keep class com.zonealarm.app.data.local.AlarmZoneEntity { *; }
# MapLibre + Retrofit/Gson (keep models)
-keep class org.maplibre.** { *; }
-keep class com.zonealarm.app.data.geocoding.** { *; }
