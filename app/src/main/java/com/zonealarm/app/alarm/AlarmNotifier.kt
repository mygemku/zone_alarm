package com.zonealarm.app.alarm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import com.zonealarm.app.domain.model.AlarmZone
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Posts the high-priority alarm notification. Carries the full-screen intent
 * to [AlarmActivity] (built in Phase 5) so the alarm surfaces over lock
 * screen / other apps once USE_FULL_SCREEN_INTENT is granted.
 */
@Singleton
class AlarmNotifier @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    companion object {
        const val CHANNEL_ID = "zonealarm_alarms"
        const val NOTIFICATION_ID = 1001
        const val EXTRA_ZONE_ID = "zone_id"
    }

    private val manager: NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    fun ensureChannel() {
        val sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Alarms",
            NotificationManager.IMPORTANCE_HIGH,
        ).apply {
            description = "Location alarm alerts"
            setSound(sound, null)
            enableVibration(true)
            lockscreenVisibility = android.app.Notification.VISIBILITY_PUBLIC
        }
        manager.createNotificationChannel(channel)
    }

    /** Notification used both for firing alerts and the playback service. */
    fun buildAlert(zone: AlarmZone): android.app.Notification {
        ensureChannel()
        val fullScreen = Intent(context, AlarmActivity::class.java).apply {
            putExtra(EXTRA_ZONE_ID, zone.id)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val fullScreenPi = PendingIntent.getActivity(
            context,
            zone.id.hashCode(),
            fullScreen,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        val dismiss = Intent(context, AlarmService::class.java).apply {
            action = AlarmService.ACTION_DISMISS
        }
        val dismissPi = PendingIntent.getService(
            context,
            zone.id.hashCode(),
            dismiss,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle(zone.name)
            .setContentText("You have arrived — within ${formatRadius(zone.radiusMeters)} of target.")
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(false)
            .setOngoing(true)
            .setFullScreenIntent(fullScreenPi, true)
            .addAction(
                android.R.drawable.ic_menu_close_clear_cancel,
                "Dismiss",
                dismissPi,
            )
            .build()
    }

    fun fire(zone: AlarmZone) {
        manager.notify(NOTIFICATION_ID, buildAlert(zone))
    }

    fun cancel() {
        manager.cancel(NOTIFICATION_ID)
    }

    private fun formatRadius(meters: Float): String =
        if (meters >= 1000) "${"%.1f".format(meters / 1000)} km" else "${meters.toInt()} m"
}
