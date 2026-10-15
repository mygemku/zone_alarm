package com.zonealarm.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zonealarm.app.ui.theme.Amber400
import com.zonealarm.app.ui.theme.Amber500
import com.zonealarm.app.ui.theme.Amber700
import com.zonealarm.app.ui.theme.Indigo400
import com.zonealarm.app.ui.theme.Indigo500
import com.zonealarm.app.ui.theme.Slate900
import com.zonealarm.app.ui.theme.Slate950

/**
 * ZoneAlarm official emblem mark:
 * Fuses a GPS Location Pin, Wake-up Alarm Bell, and Geofence Sonar Waves.
 */
@Composable
fun ZoneAlarmLogoMark(
    modifier: Modifier = Modifier,
    size: Dp = 44.dp,
    showBackground: Boolean = true,
) {
    val amberLight = Amber400
    val amberMid = Amber500
    val amberDark = Amber700
    val indigoAccent = Indigo400
    val indigoOuter = Indigo500.copy(alpha = 0.5f)

    Box(
        modifier = modifier
            .size(size)
            .then(
                if (showBackground) {
                    Modifier.background(
                        color = Slate900,
                        shape = RoundedCornerShape(size * 0.24f)
                    )
                } else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier.size(size * 0.82f)
        ) {
            val w = this.size.width
            val h = this.size.height
            val scale = w / 108f

            // Ground radar ripple
            val ripplePath = Path().apply {
                moveTo(39f * scale, 76f * scale)
                cubicTo(39f * scale, 73.8f * scale, 69f * scale, 73.8f * scale, 69f * scale, 76f * scale)
                cubicTo(69f * scale, 78.2f * scale, 39f * scale, 78.2f * scale, 39f * scale, 76f * scale)
                close()
            }
            drawPath(
                path = ripplePath,
                color = indigoAccent.copy(alpha = 0.6f),
                style = Stroke(width = 1.4f * scale)
            )

            val innerRipplePath = Path().apply {
                moveTo(45f * scale, 76f * scale)
                cubicTo(45f * scale, 74.5f * scale, 63f * scale, 74.5f * scale, 63f * scale, 76f * scale)
                cubicTo(63f * scale, 77.5f * scale, 45f * scale, 77.5f * scale, 45f * scale, 76f * scale)
                close()
            }
            drawPath(
                path = innerRipplePath,
                color = amberMid.copy(alpha = 0.85f),
                style = Stroke(width = 1.2f * scale)
            )

            // Outer acoustic waves (Indigo)
            val leftOuterWave = Path().apply {
                moveTo(33f * scale, 36f * scale)
                cubicTo(27.5f * scale, 42.5f * scale, 27.5f * scale, 53.5f * scale, 33f * scale, 60f * scale)
            }
            drawPath(
                path = leftOuterWave,
                color = indigoOuter,
                style = Stroke(width = 2.2f * scale, cap = StrokeCap.Round)
            )

            val rightOuterWave = Path().apply {
                moveTo(75f * scale, 36f * scale)
                cubicTo(80.5f * scale, 42.5f * scale, 80.5f * scale, 53.5f * scale, 75f * scale, 60f * scale)
            }
            drawPath(
                path = rightOuterWave,
                color = indigoOuter,
                style = Stroke(width = 2.2f * scale, cap = StrokeCap.Round)
            )

            // Inner acoustic waves (Bright Indigo)
            val leftInnerWave = Path().apply {
                moveTo(38f * scale, 40f * scale)
                cubicTo(34f * scale, 44.5f * scale, 34f * scale, 51.5f * scale, 38f * scale, 56f * scale)
            }
            drawPath(
                path = leftInnerWave,
                color = indigoAccent,
                style = Stroke(width = 2.6f * scale, cap = StrokeCap.Round)
            )

            val rightInnerWave = Path().apply {
                moveTo(70f * scale, 40f * scale)
                cubicTo(74f * scale, 44.5f * scale, 74f * scale, 51.5f * scale, 70f * scale, 56f * scale)
            }
            drawPath(
                path = rightInnerWave,
                color = indigoAccent,
                style = Stroke(width = 2.6f * scale, cap = StrokeCap.Round)
            )

            // Crown Hanger Ring
            drawCircle(
                color = amberLight,
                radius = 3.5f * scale,
                center = Offset(54f * scale, 27.5f * scale),
                style = Stroke(width = 2f * scale)
            )

            // Pin-Bell Fusion Body with Sunset Amber Gradient
            val pinBellPath = Path().apply {
                moveTo(54f * scale, 30f * scale)
                cubicTo(62f * scale, 30f * scale, 67f * scale, 37f * scale, 67f * scale, 46f * scale)
                cubicTo(67f * scale, 51f * scale, 69f * scale, 53f * scale, 70f * scale, 54f * scale)
                cubicTo(66f * scale, 61f * scale, 58f * scale, 69f * scale, 54f * scale, 75f * scale)
                cubicTo(50f * scale, 69f * scale, 42f * scale, 61f * scale, 38f * scale, 54f * scale)
                cubicTo(39f * scale, 53f * scale, 41f * scale, 51f * scale, 41f * scale, 46f * scale)
                cubicTo(41f * scale, 37f * scale, 46f * scale, 30f * scale, 54f * scale, 30f * scale)
                close()
            }
            drawPath(
                path = pinBellPath,
                brush = Brush.verticalGradient(
                    colors = listOf(amberLight, amberMid, amberDark),
                    startY = 30f * scale,
                    endY = 75f * scale
                )
            )

            // Negative space clapper cavity
            drawCircle(
                color = Slate950,
                radius = 6f * scale,
                center = Offset(54f * scale, 47.5f * scale)
            )

            // GPS Focal Beacon Clapper
            drawCircle(
                color = Color.White,
                radius = 3f * scale,
                center = Offset(54f * scale, 47.5f * scale)
            )
        }
    }
}

/**
 * Full horizontal logo lockup: Emblem + Stylized Wordmark + Tagline.
 */
@Composable
fun ZoneAlarmLogoLockup(
    modifier: Modifier = Modifier,
    markSize: Dp = 44.dp,
    showSubtitle: Boolean = true,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ZoneAlarmLogoMark(size = markSize)
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Zone",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
                Text(
                    text = "Alarm",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                )
            }
            if (showSubtitle) {
                Text(
                    text = "GEOFENCE WAKE ALARM",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.2.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }
    }
}
