
package com.simpleenergy.hmi.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

private val DrawerBackground = Color(0xFF0B111B)
private val DrawerCard = Color(0xFF151E2B)
private val DrawerCyan = Color(0xFF42DDF0)
private val DrawerMuted = Color(0xFF8D9BAD)

@Composable
fun SettingsDrawer(
    visible: Boolean,
    onDismiss: () -> Unit,
    overspeedEnabled: Boolean,
    onOverspeedChanged: (Boolean) -> Unit,
    speedUnit: String,
    onSpeedUnitChanged: (String) -> Unit
) {
    if (!visible) return

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0x99000000)),
            horizontalArrangement = Arrangement.End
        ) {
            // Tap outside the drawer to close it.
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clickable { onDismiss() }
            )

            AnimatedVisibility(
                visible = visible,
                enter = slideInHorizontally { it },
                exit = slideOutHorizontally { it }
            ) {
                Column(
                    modifier = Modifier
                        .width(340.dp)
                        .fillMaxHeight()
                        .background(DrawerBackground)
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                "SETTINGS",
                                color = Color.White,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 2.sp
                            )
                            Text(
                                "Dashboard preferences",
                                color = DrawerMuted,
                                fontSize = 11.sp
                            )
                        }

                        TextButton(onClick = onDismiss) {
                            Text(
                                "CLOSE  ✕",
                                color = DrawerCyan,
                                fontSize = 11.sp
                            )
                        }
                    }

                    HorizontalDivider(color = DrawerCard)

                    Text(
                        "SAFETY",
                        color = DrawerCyan,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                DrawerCard,
                                RoundedCornerShape(12.dp)
                            )
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                "Overspeed warning",
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                "Alert above 80 km/h",
                                color = DrawerMuted,
                                fontSize = 11.sp
                            )
                        }

                        Switch(
                            checked = overspeedEnabled,
                            onCheckedChange = onOverspeedChanged,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = DrawerCyan
                            )
                        )
                    }

                    Text(
                        "DISPLAY",
                        color = DrawerCyan,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                DrawerCard,
                                RoundedCornerShape(12.dp)
                            )
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            "Speed unit",
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("km/h", "mph").forEach { unit ->
                                val selected = speedUnit == unit

                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable {
                                            onSpeedUnitChanged(unit)
                                        },
                                    color = if (selected) {
                                        DrawerCyan.copy(alpha = 0.18f)
                                    } else {
                                        DrawerBackground
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    border = if (selected) {
                                        androidx.compose.foundation.BorderStroke(
                                            1.dp,
                                            DrawerCyan
                                        )
                                    } else {
                                        null
                                    }
                                ) {
                                    Box(
                                        modifier = Modifier.padding(12.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            unit,
                                            color = if (selected) {
                                                DrawerCyan
                                            } else {
                                                DrawerMuted
                                            },
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(Modifier.weight(1f))

                    HorizontalDivider(color = DrawerCard)

                    Column(
                        verticalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Text(
                            "SIMPLE ENERGY HMI",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                        Text(
                            "Dashboard Demo • Version 1.0",
                            color = DrawerMuted,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }
    }
}
