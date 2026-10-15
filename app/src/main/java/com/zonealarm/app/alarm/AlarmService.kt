package com.zonealarm.app.alarm

import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Build
import android.os.IBinder
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.zonealarm.app.domain.repo.ZoneRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

/**
 * Foreground service that loops the alarm sound + vibration until dismissed.
 * Started by [AlarmActivity] (foreground context, so background-start limits
 * don't apply). Stops itself on dismiss.
 */
@AndroidEntryPoint
class AlarmService : Service() {
    @Inject lateinit var repository: ZoneRepository
    @Inject lateinit var notifier: AlarmNotifier

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var player: MediaPlayer? = null
    private var vibrator: Vibrator? = null

    companion object {
        const val ACTION_START = "com.zonealarm.app.alarm.START"
        const val ACTION_DISMISS = "com.zonealarm.app.alarm.DISMISS"

        fun start(context: Context, zoneId: String) {
            val intent = Intent(context, AlarmService::class.java).apply {
                action = ACTION_START
                putExtra(AlarmNotifier.EXTRA_ZONE_ID, zoneId)
            }
            context.startForegroundService(intent)
        }

        fun dismiss(context: Context) {
            val intent = Intent(context, AlarmService::class.java).apply {
                action = ACTION_DISMISS
            }
            context.startService(intent)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                val zoneId = intent.getStringExtra(AlarmNotifier.EXTRA_ZONE_ID) ?: return START_NOT_STICKY
                scope.launch {
                    val zone = repository.getZone(zoneId)
                    // Fall back to a generic alert if the zone vanished.
                    startForeground(
                        AlarmNotifier.NOTIFICATION_ID,
                        zone?.let { notifier.buildAlert(it) } ?: return@launch stopSelf(),
                    )
                    startSound(zone?.soundUri)
                    startVibration()
                }
            }
            ACTION_DISMISS -> stopRinging()
        }
        return START_NOT_STICKY
    }

    private fun startSound(soundUri: String?) {
        stopSound()
        try {
            val uri = soundUri?.let { android.net.Uri.parse(it) }
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                ?: return
            player = MediaPlayer().apply {
                setDataSource(this@AlarmService, uri)
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build(),
                )
                isLooping = true
                prepare()
                start()
            }
        } catch (_: Exception) {
            // Sound is best-effort; vibration + notification still fire.
            player = null
        }
    }

    private fun startVibration() {
        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            (getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager).defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
        val pattern = longArrayOf(0, 500, 500)
        vibrator?.vibrate(VibrationEffect.createWaveform(pattern, 0))
    }

    private fun stopSound() {
        player?.run { try { stop() } catch (_: Exception) {} ; release() }
        player = null
    }

    private fun stopRinging() {
        stopSound()
        vibrator?.cancel()
        notifier.cancel()
        stopSelf()
    }

    override fun onDestroy() {
        stopSound()
        vibrator?.cancel()
        scope.cancel()
        super.onDestroy()
    }
}
