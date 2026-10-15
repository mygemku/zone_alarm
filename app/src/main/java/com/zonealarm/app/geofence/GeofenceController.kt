package com.zonealarm.app.geofence

import com.zonealarm.app.domain.model.AlarmZone

/** Testable seam over [GeofenceManager] (Play Services client underneath). */
interface GeofenceController {
    suspend fun register(zone: AlarmZone)
    suspend fun unregister(zoneId: String)
    suspend fun unregisterAll(zoneIds: List<String>)
    suspend fun refresh(zones: List<AlarmZone>)
}
