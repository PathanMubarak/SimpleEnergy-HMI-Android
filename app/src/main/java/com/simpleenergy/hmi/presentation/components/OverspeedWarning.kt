
package com.simpleenergy.hmi.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private const val OVERSPEED_LIMIT = 50f

@Composable
fun OverspeedWarning(
    speedKmh: Float,
    modifier: Modifier = Modifier
) {
    if (speedKmh > OVERSPEED_LIMIT) {
        Surface(
            modifier = modifier.fillMaxWidth(),
            color = Color(0xFFB3261E),
            shape = RoundedCornerShape(10.dp)
        ) {
            Row(
                modifier = Modifier.padding(
                    horizontal = 8.dp,
                    vertical = 10.dp
                ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "⚠",
                    fontSize = 12.sp,
                    color = Color.White
                )

                Text(
                    text = "  OVERSPEED",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Text(
                    text = "  •  Reduce speed",
                    fontSize = 12.sp,
                    color = Color.White
                )
            }
        }
    }
}
