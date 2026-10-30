
package com.simpleenergy.hmi.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simpleenergy.hmi.presentation.components.OverspeedWarning
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

private val GaugeBackground = Color(0xFF111B27)
private val GaugeTrack = Color(0xFF263545)
private val GaugeCyan = Color(0xFF42E8FF)
private val GaugeBlue = Color(0xFF258BFF)
private val GaugeWhite = Color(0xFFF4FAFF)
private val GaugeMuted = Color(0xFF8093A8)

@Composable
fun HmiSpeedometer(
    speedKmh: Float,
    modifier: Modifier = Modifier
) {
    val safeSpeed = speedKmh.coerceIn(0f, 120f)

    val animatedSpeed by animateFloatAsState(
        targetValue = safeSpeed,
        animationSpec = spring(
            dampingRatio = 0.82f,
            stiffness = 85f
        ),
        label = "speed_animation"
    )

    val progress = animatedSpeed / 120f

    Box(
        modifier = modifier.size(330.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasCenter = Offset(
                x = size.width / 2f,
                y = size.height / 2f
            )

            val radius = size.minDimension * 0.405f
            val strokeWidth = size.minDimension * 0.035f

            val startAngle = 135f
            val totalSweep = 270f
            val activeSweep = totalSweep * progress

            // Outer subtle ring
            drawCircle(
                color = GaugeCyan.copy(alpha = 0.07f),
                radius = radius + strokeWidth * 2.2f,
                center = canvasCenter,
                style = Stroke(width = 1.dp.toPx())
            )

            // Background speed track
            drawArc(
                color = GaugeTrack,
                startAngle = startAngle,
                sweepAngle = totalSweep,
                useCenter = false,
                topLeft = Offset(
                    canvasCenter.x - radius,
                    canvasCenter.y - radius
                ),
                size = androidx.compose.ui.geometry.Size(
                    radius * 2f,
                    radius * 2f
                ),
                style = Stroke(
                    width = strokeWidth,
                    cap = StrokeCap.Round
                )
            )

            // Active illuminated speed arc
            if (activeSweep > 0.5f) {
                drawArc(
                    brush = Brush.sweepGradient(
                        colors = listOf(
                            GaugeBlue,
                            GaugeCyan,
                            Color(0xFFB7F8FF)
                        ),
                        center = canvasCenter
                    ),
                    startAngle = startAngle,
                    sweepAngle = activeSweep,
                    useCenter = false,
                    topLeft = Offset(
                        canvasCenter.x - radius,
                        canvasCenter.y - radius
                    ),
                    size = androidx.compose.ui.geometry.Size(
                        radius * 2f,
                        radius * 2f
                    ),
                    style = Stroke(
                        width = strokeWidth,
                        cap = StrokeCap.Round
                    )
                )
            }

            // Precision tick marks: 0 to 120 km/h
            for (index in 0..60) {
                val angle = Math.toRadians(
                    (startAngle + index * (totalSweep / 60f)).toDouble()
                )

                val isMajor = index % 10 == 0
                val innerRadius = radius - if (isMajor) {
                    strokeWidth * 1.9f
                } else {
                    strokeWidth * 1.25f
                }

                val outerRadius = radius - strokeWidth * 0.65f

                val start = Offset(
                    x = canvasCenter.x + cos(angle).toFloat() * innerRadius,
                    y = canvasCenter.y + sin(angle).toFloat() * innerRadius
                )

                val end = Offset(
                    x = canvasCenter.x + cos(angle).toFloat() * outerRadius,
                    y = canvasCenter.y + sin(angle).toFloat() * outerRadius
                )

                drawLine(
                    color = if (index <= progress * 60f) {
                        GaugeCyan.copy(alpha = if (isMajor) 0.95f else 0.55f)
                    } else {
                        GaugeMuted.copy(alpha = if (isMajor) 0.8f else 0.35f)
                    },
                    start = start,
                    end = end,
                    strokeWidth = if (isMajor) 2.2.dp.toPx() else 1.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }

            // Animated needle endpoint
            val needleAngle = Math.toRadians(
                (startAngle + activeSweep).toDouble()
            )

            val needleLength = radius * 0.72f

            val needleEnd = Offset(
                x = canvasCenter.x + cos(needleAngle).toFloat() * needleLength,
                y = canvasCenter.y + sin(needleAngle).toFloat() * needleLength
            )

            // Needle glow
            drawLine(
                color = GaugeCyan.copy(alpha = 0.20f),
                start = canvasCenter,
                end = needleEnd,
                strokeWidth = 12.dp.toPx(),
                cap = StrokeCap.Round
            )

            // Needle
            drawLine(
                color = GaugeWhite,
                start = canvasCenter,
                end = needleEnd,
                strokeWidth = 3.dp.toPx(),
                cap = StrokeCap.Round
            )

            // Needle tip
            drawCircle(
                color = GaugeCyan,
                radius = 6.dp.toPx(),
                center = needleEnd
            )

            // Center hub
            drawCircle(
                color = GaugeBackground,
                radius = 11.dp.toPx(),
                center = canvasCenter
            )

            drawCircle(
                color = GaugeCyan,
                radius = 5.dp.toPx(),
                center = canvasCenter
            )
        }

        // Speed readout
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(22.dp))

            Text(
                text = animatedSpeed.roundToInt().toString(),
                color = GaugeWhite,
                fontSize = 66.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-2).sp
            )

            Text(
                text = "km/h",
                color = GaugeCyan,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 3.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "VEHICLE SPEED",
                color = GaugeMuted,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
        }
    }
}
