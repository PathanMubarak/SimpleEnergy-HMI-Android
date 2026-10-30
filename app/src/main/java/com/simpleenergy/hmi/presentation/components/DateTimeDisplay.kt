
package com.simpleenergy.hmi.presentation.components

import androidx.compose.runtime.*
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun DateTimeDisplay(
    modifier: Modifier = Modifier
) {
    var currentTime by remember {
        mutableStateOf(LocalDateTime.now())
    }

    LaunchedEffect(Unit) {
        while (true) {
            currentTime = LocalDateTime.now()
            delay(1000L)
        }
    }

    val time = currentTime.format(
        DateTimeFormatter.ofPattern("hh:mm a")
    )

    val date = currentTime.format(
        DateTimeFormatter.ofPattern("EEE, dd MMM yyyy")
    )

    androidx.compose.foundation.layout.Column(
        modifier = modifier,
        horizontalAlignment = androidx.compose.ui.Alignment.End
    ) {
        Text(
            text = time,
            color = Color.White,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = date.uppercase(),
            color = Color(0xFF9AA8B8),
            fontSize = 9.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
