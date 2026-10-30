
package com.simpleenergy.hmi.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val PanelBackground = Color(0xFF111B27)
private val PanelBorder = Color(0xFF263545)
private val PrimaryText = Color(0xFFF4FAFF)
private val SecondaryText = Color(0xFF8093A8)

private val BatteryCyan = Color(0xFF42E8FF)
private val BatteryAmber = Color(0xFFFFB547)
private val BatteryRed = Color(0xFFFF626D)

@Composable
fun RideMetricsPanel(
    batteryPercent: Int,
    estimatedRangeKm: Int,
    tripKm: Float,
    odometerKm: Int,
    averageSpeedKmh: Int,
    modifier: Modifier = Modifier
) {
    val battery = batteryPercent.coerceIn(0, 100)

    val targetColor = when {
        battery <= 10 -> BatteryRed
        battery <= 20 -> BatteryAmber
        else -> BatteryCyan
    }

    val animatedBattery by animateFloatAsState(
        targetValue = battery.toFloat(),
        animationSpec = spring(
            dampingRatio = 0.8f,
            stiffness = 65f
        ),
        label = "battery_level"
    )

    val animatedColor by animateColorAsState(
        targetValue = targetColor,
        animationSpec = tween(500),
        label = "battery_color"
    )

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(PanelBackground)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        // BATTERY HEADER
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    "BATTERY",
                    color = SecondaryText,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                )

                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        "$battery",
                        color = PrimaryText,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Text(
                        "%",
                        color = animatedColor,
                        fontSize = 15.sp,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "EST. RANGE",
                    color = SecondaryText,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        "$estimatedRangeKm",
                        color = animatedColor,
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        " km",
                        color = SecondaryText,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
            }
        }

        // BATTERY BAR
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(10.dp)
                    .clip(RoundedCornerShape(50))
                    .background(PanelBorder)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animatedBattery / 100f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(50))
                        .background(animatedColor)
                )
            }

            Spacer(Modifier.width(8.dp))

            Text(
                "▣",
                color = animatedColor,
                fontSize = 15.sp
            )
        }

        // LOW BATTERY WARNING
        if (battery <= 20) {
            Text(
                text = if (battery <= 10) {
                    "⚠ CRITICAL BATTERY"
                } else {
                    "⚠ LOW BATTERY"
                },
                color = animatedColor,
                fontSize = 9.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp
            )
        }

        // RIDE METRICS
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            MetricItem(
                label = "TRIP",
                value = String.format("%.1f", tripKm),
                unit = "km"
            )

            MetricItem(
                label = "ODOMETER",
                value = odometerKm.toString(),
                unit = "km"
            )

            MetricItem(
                label = "AVG. SPEED",
                value = averageSpeedKmh.toString(),
                unit = "km/h"
            )
        }
    }
}

@Composable
private fun MetricItem(
    label: String,
    value: String,
    unit: String
) {
    Column {
        Text(
            label,
            color = SecondaryText,
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )

        Spacer(Modifier.height(4.dp))

        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                value,
                color = PrimaryText,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                " $unit",
                color = SecondaryText,
                fontSize = 8.sp,
                modifier = Modifier.padding(bottom = 3.dp)
            )
        }
    }
}
