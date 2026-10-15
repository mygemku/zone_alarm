package com.zonealarm.app.alarm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.zonealarm.app.domain.repo.ZoneRepository
import com.zonealarm.app.ui.components.formatRadius
import com.zonealarm.app.ui.theme.ZoneAlarmTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.launch

/**
 * Full-screen ringing screen, styled after `location_alarm_app.html`:
 * radar waves, zone name, Stop + 10-minute Snooze. Follows the system
 * theme via [ZoneAlarmTheme].
 */
@AndroidEntryPoint
class AlarmActivity : ComponentActivity() {
    @Inject lateinit var repository: ZoneRepository

    private var zoneName by mutableStateOf("Alarm")
    private var zoneRadius by mutableStateOf("")
    private var zoneId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        zoneId = intent.getStringExtra(AlarmNotifier.EXTRA_ZONE_ID)
        lifecycleScope.launch {
            zoneId?.let { repository.getZone(it) }?.let { zone ->
                zoneName = zone.name
                zoneRadius = formatRadius(zone.radiusMeters)
            }
        }
        zoneId?.let { AlarmService.start(this, it) }

        setContent {
            ZoneAlarmTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        RadarWaves()
                        Column(
                            modifier = Modifier.fillMaxSize().padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Spacer(Modifier.height(64.dp))
                                Text(
                                    "You have arrived!",
                                    style = MaterialTheme.typography.headlineMedium,
                                )
                                Text(
                                    zoneName,
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.primary,
                                )
                                if (zoneRadius.isNotEmpty()) {
                                    Text(
                                        "Within $zoneRadius of target",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                            }
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Button(
                                    onClick = { dismiss() },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.error,
                                    ),
                                    modifier = Modifier.fillMaxWidth().height(56.dp),
                                ) {
                                    Text("Stop")
                                }
                                Spacer(Modifier.height(12.dp))
                                OutlinedButton(
                                    onClick = { snooze() },
                                    modifier = Modifier.fillMaxWidth().height(56.dp),
                                ) {
                                    Text("Snooze 10 min")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun dismiss() {
        AlarmService.dismiss(this)
        finish()
    }

    private fun snooze() {
        zoneId?.let { SnoozeWorker.schedule(this, it) }
        AlarmService.dismiss(this)
        finish()
    }
}

/** Three expanding radar rings, looping (mirrors the mockup animation). */
@Composable
private fun RadarWaves() {
    val transition = rememberInfiniteTransition(label = "radar")
    val progress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = LinearEasing),
        ),
        label = "wave",
    )
    val color = MaterialTheme.colorScheme.primary
    Canvas(modifier = Modifier.size(300.dp)) {
        for (i in 0 until 3) {
            val phase = (progress + i / 3f) % 1f
            drawCircle(
                color = color.copy(alpha = 0.5f * (1f - phase)),
                radius = size.minDimension * 0.2f + phase * size.minDimension * 0.3f,
                style = Stroke(width = 6f),
            )
        }
    }
}
