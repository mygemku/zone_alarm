package com.zonealarm.app.geofence

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.zonealarm.app.domain.model.AlarmZone
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.tasks.await

/**
 * Registers zone alarms with Play Services Geofencing. System delivers
 * ENTER transitions via [GeofenceReceiver] even when the app is closed —
 * no foreground service is needed for monitoring.
 *
 * Callers must hold location permission (enforced by the Phase 5 UI flow).
 */
@Singleton
class GeofenceManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val client: GeofencingClient,
) : GeofenceController {
    companion object {
        /** How quickly the system should respond after entering (ms). */
        const val RESPONSIVENESS_MS = 60_000
    }

    private val pendingIntent: PendingIntent by lazy {
        val intent = Intent(context, GeofenceReceiver::class.java)
        PendingIntent.getBroadcast(
            context,
            0,
            intent,
            // Geofencing requires a mutable PendingIntent on API 31+.
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE,
        )
    }

    private fun buildGeofence(zone: AlarmZone): Geofence = Geofence.Builder()
        .setRequestId(zone.id)
        .setCircularRegion(zone.latitude, zone.longitude, zone.radiusMeters)
        .setExpirationDuration(Geofence.NEVER_EXPIRE)
        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
        .setNotificationResponsiveness(RESPONSIVENESS_MS)
        .build()

    @SuppressLint("MissingPermission")
    override suspend fun register(zone: AlarmZone) {
        val request = GeofencingRequest.Builder()
            // Fire immediately if already inside when registered (good for testing).
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofence(buildGeofence(zone))
            .build()
        client.addGeofences(request, pendingIntent).await()
    }

    override suspend fun unregister(zoneId: String) {
        client.removeGeofences(listOf(zoneId)).await()
    }

    override suspend fun unregisterAll(zoneIds: List<String>) {
        if (zoneIds.isNotEmpty()) client.removeGeofences(zoneIds).await()
    }

    /** Re-registers every enabled zone (used after boot and on toggle-on). */
    @SuppressLint("MissingPermission")
    override suspend fun refresh(zones: List<AlarmZone>) {
        val enabled = zones.filter { it.isEnabled }
        unregisterAll(zones.map { it.id })
        if (enabled.isEmpty()) return
        val request = GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofences(enabled.map { buildGeofence(it) })
            .build()
        client.addGeofences(request, pendingIntent).await()
    }
}
