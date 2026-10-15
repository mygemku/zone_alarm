package com.zonealarm.app.geofence

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.zonealarm.app.domain.repo.ZoneRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/** Re-registers enabled geofences after a reboot (geofences don't survive it). */
@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {
    @Inject lateinit var repository: ZoneRepository
    @Inject lateinit var geofenceManager: GeofenceManager

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return
        val pending = goAsync()
        scope.launch {
            try {
                val zones = repository.observeZones().first()
                geofenceManager.refresh(zones)
            } catch (e: SecurityException) {
                // Permission revoked; UI gate re-prompts on next launch.
            } finally {
                pending.finish()
            }
        }
    }
}
