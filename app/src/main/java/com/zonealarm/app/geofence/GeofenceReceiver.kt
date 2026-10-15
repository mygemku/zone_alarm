package com.zonealarm.app.geofence

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import com.zonealarm.app.alarm.AlarmNotifier
import com.zonealarm.app.domain.model.AlarmMode
import com.zonealarm.app.domain.repo.ZoneRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Receives geofence ENTER transitions while the app is closed.
 * One-shot zones are unregistered + disabled after firing; recurring zones
 * stay registered. Firing is delegated to [AlarmNotifier] (Phase 5 adds the
 * full-screen UI behind the same notification).
 */
@AndroidEntryPoint
class GeofenceReceiver : BroadcastReceiver() {
    @Inject lateinit var repository: ZoneRepository
    @Inject lateinit var geofenceManager: GeofenceManager
    @Inject lateinit var notifier: AlarmNotifier

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun onReceive(context: Context, intent: Intent) {
        val event = GeofencingEvent.fromIntent(intent) ?: return
        if (event.hasError()) return
        if (event.geofenceTransition != Geofence.GEOFENCE_TRANSITION_ENTER) return
        val ids = event.triggeringGeofences?.map { it.requestId }.orEmpty()
        if (ids.isEmpty()) return

        val pending = goAsync()
        scope.launch {
            try {
                for (id in ids) {
                    val zone = repository.getZone(id) ?: continue
                    if (!zone.isEnabled) continue
                    if (zone.mode == AlarmMode.ONE_SHOT) {
                        geofenceManager.unregister(id)
                        repository.setEnabled(id, false)
                    }
                    notifier.fire(zone)
                }
            } catch (e: SecurityException) {
                // Permission revoked between registration and delivery: nothing
                // can be monitored; the UI gate re-prompts on next launch.
            } finally {
                pending.finish()
            }
        }
    }
}
