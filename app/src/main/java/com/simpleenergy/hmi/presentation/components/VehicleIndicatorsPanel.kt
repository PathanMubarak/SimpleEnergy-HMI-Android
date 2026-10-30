
package com.simpleenergy.hmi.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val PanelBg = Color(0xFF111B27)
private val PanelBorder = Color(0xFF263545)
private val Muted = Color(0xFF8093A8)
private val White = Color(0xFFF4FAFF)
private val Cyan = Color(0xFF42E8FF)
private val Green = Color(0xFF45E6A5)
private val Amber = Color(0xFFFFB547)
private val Red = Color(0xFFFF626D)

@Composable
fun VehicleIndicatorsPanel(
    leftIndicator: Boolean,
    rightIndicator: Boolean,
    highBeam: Boolean,
    sideStandDown: Boolean,
    regenActive: Boolean,
    rideMode: String,
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "indicator_blink")

    val blinkAlpha by transition.animateFloat(
        initialValue = 0.25f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(500),
            repeatMode = RepeatMode.Reverse
        ),
        label = "blink_alpha"
    )

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(PanelBg)
            .border(1.dp, PanelBorder, RoundedCornerShape(20.dp))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            "VEHICLE INDICATORS",
            color = Muted,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Indicator(
                symbol = "←",
                label = "LEFT",
                active = leftIndicator,
                color = Amber,
                blinkAlpha = blinkAlpha
            )

            Indicator(
                symbol = "→",
                label = "RIGHT",
                active = rightIndicator,
                color = Amber,
                blinkAlpha = blinkAlpha
            )

            Indicator(
                symbol = "☀",
                label = "HIGH BEAM",
                active = highBeam,
                color = Cyan
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Indicator(
                symbol = "S",
                label = "SIDE STAND",
                active = sideStandDown,
                color = Red
            )

            Indicator(
                symbol = "R",
                label = "REGEN",
                active = regenActive,
                color = Green
            )

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "RIDE MODE",
                    color = Muted,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    rideMode.uppercase(),
                    color = Cyan,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun Indicator(
    symbol: String,
    label: String,
    active: Boolean,
    color: Color,
    blinkAlpha: Float = 1f
) {
    val animatedColor by animateColorAsState(
        targetValue = if (active) color else Muted,
        animationSpec = tween(250),
        label = "indicator_color"
    )

    val alpha = if (active) blinkAlpha else 0.45f

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .alpha(alpha)
                .clip(RoundedCornerShape(10.dp))
                .background(
                    if (active) color.copy(alpha = 0.15f)
                    else PanelBorder.copy(alpha = 0.4f)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                symbol,
                color = animatedColor,
                fontSize = 21.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Text(
            label,
            color = if (active) White else Muted,
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            if (active) "ON" else "OFF",
            color = animatedColor,
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
