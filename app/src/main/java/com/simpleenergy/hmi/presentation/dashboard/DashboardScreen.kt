
package com.simpleenergy.hmi.presentation.dashboard

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.simpleenergy.hmi.domain.VehicleScenario
import com.simpleenergy.hmi.presentation.components.DateTimeDisplay
import com.simpleenergy.hmi.presentation.components.OverspeedWarning
import com.simpleenergy.hmi.presentation.components.RideMetricsPanel
import com.simpleenergy.hmi.presentation.components.SettingsDrawer
import com.simpleenergy.hmi.presentation.theme.HmiColors
import com.simpleenergy.hmi.presentation.components.VehicleIndicatorsPanel
import com.simpleenergy.hmi.ui.components.HmiSpeedometer

@Composable
fun DashboardScreen(
    viewModel: VehicleViewModel
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var settingsVisible by remember { mutableStateOf(false) }
    var overspeedEnabled by remember { mutableStateOf(true) }
    var speedUnit by remember { mutableStateOf("km/h") }

    BackHandler(enabled = settingsVisible) {
        settingsVisible = false
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(HmiColors.Background)
            .padding(horizontal = 22.dp, vertical = 8.dp)
    ) {

        // HEADER
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(38.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "SIMPLE",
                fontSize = 23.sp,
                fontWeight = FontWeight.ExtraBold,
                color = HmiColors.Primary,
                letterSpacing = 1.sp
            )
            DateTimeDisplay()
            IconButton(
                onClick = { settingsVisible = true }
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Open settings",
                    tint = HmiColors.Cyan
                )
            }
            Text(
                text = when (state.signalStatus) {
                    SignalStatus.CONNECTED -> "● VEHICLE CONNECTED"
                    SignalStatus.INVALID -> "● INVALID SIGNAL"
                    SignalStatus.STALE -> "● SIGNAL STALE"
                    SignalStatus.UNAVAILABLE -> "● SIGNAL UNAVAILABLE"
                },
                color = when (state.signalStatus) {
                    SignalStatus.CONNECTED -> HmiColors.Success
                    SignalStatus.INVALID,
                    SignalStatus.STALE,
                    SignalStatus.UNAVAILABLE -> HmiColors.Danger
                },
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }

        // MAIN RIDE AREA
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // LEFT: MOTOR AND SAFETY
            Column(
                modifier = Modifier.weight(0.85f),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                VehicleStatus(
                    title = "MOTOR STATUS",
                    value = if (state.motorOn) "ON" else "OFF",
                    active = state.motorOn
                )

                VehicleStatus(
                    title = "KILL SWITCH",
                    value = if (state.killSwitchActive) "ACTIVE" else "INACTIVE",
                    active = !state.killSwitchActive
                )

                Column(
                    modifier = Modifier
                        .background(
                            HmiColors.Surface,
                            RoundedCornerShape(12.dp)
                        )
                        .padding(13.dp)
                ) {
                    Text(
                        "CURRENT SCENARIO",
                        color = HmiColors.TextSecondary,
                        fontSize = 9.sp,
                        letterSpacing = 1.sp
                    )

                    Spacer(Modifier.height(5.dp))

                    Text(
                        state.scenario?.name?.replace("_", " ") ?: "WAITING",
                        color = HmiColors.Cyan,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // CENTRE: SPEEDOMETER
            Box(
                modifier = Modifier.weight(1.5f),
                contentAlignment = Alignment.Center
            ) {
                HmiSpeedometer(
                    speedKmh = state.speedKmh,
                    modifier = Modifier.size(260.dp)
                )
                if (overspeedEnabled) {
                    OverspeedWarning(
                        speedKmh = state.speedKmh
                    )
                }

            }

            // RIGHT: RIDE INFORMATION
            Column(
                modifier = Modifier.weight(0.85f),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    "RIDE MODE",
                    color = HmiColors.TextSecondary,
                    fontSize = 10.sp,
                    letterSpacing = 1.5.sp
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf("ECO", "RIDE", "SONIC").forEach { mode ->

                        val selected = state.rideMode == mode

                        Button(
                            onClick = {
                                viewModel.selectRideMode(mode)
                            },
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(
                                horizontal = 2.dp,
                                vertical = 5.dp
                            ),
                            shape = RoundedCornerShape(6.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selected) {
                                    HmiColors.Primary
                                } else {
                                    HmiColors.Surface
                                }
                            )
                        ) {
                            Text(
                                text = mode,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                HorizontalDivider(
                    modifier = Modifier.width(120.dp),
                    color = HmiColors.Surface
                )

                Text(
                    "VEHICLE SPEED",
                    color = HmiColors.TextSecondary,
                    fontSize = 9.sp
                )

                Text(
                    "${state.speedKmh.toInt()} km/h",
                    color = HmiColors.TextPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // ADDITIONAL DASHBOARD PANELS
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            horizontalArrangement = Arrangement.spacedBy(150.dp)
        ) {
            RideMetricsPanel(
                batteryPercent = state.batteryPercent,
                estimatedRangeKm = state.estimatedRangeKm,
                tripKm = state.tripKm,
                odometerKm = state.odometerKm,
                averageSpeedKmh = state.averageSpeedKmh,
                modifier = Modifier.weight(1.1f)
            )
            VehicleIndicatorsPanel(
                leftIndicator = state.leftIndicator,
                rightIndicator = state.rightIndicator,
                highBeam = state.highBeam,
                sideStandDown = state.sideStandDown,
                regenActive = state.regenActive,
                rideMode = state.rideMode,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(8.dp))

        // MANUAL SCENARIOS + AUTO
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            horizontalArrangement = Arrangement.spacedBy(7.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {


            VehicleScenario.entries.forEach { scenario ->

                val selected = state.scenario == scenario
                val isAccelerating = scenario.name.contains("ACCELERATING")

                if (isAccelerating) {

                    // Press-and-hold throttle control
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(
                                color = if (selected) {
                                    HmiColors.Primary
                                } else {
                                    HmiColors.Surface
                                },
                                shape = RoundedCornerShape(9.dp)
                            )
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onPress = {
                                        viewModel.startThrottle()

                                        try {
                                            tryAwaitRelease()
                                        } finally {
                                            viewModel.stopThrottle()
                                        }
                                    }
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "ACCELERATE\n(TAP & HOLD)",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 11.sp,
                            color = HmiColors.TextPrimary
                        )
                    }

                } else {

                    Button(
                        onClick = {
                            viewModel.selectScenario(scenario)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        contentPadding = PaddingValues(
                            horizontal = 3.dp,
                            vertical = 4.dp
                        ),
                        shape = RoundedCornerShape(9.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selected) {
                                HmiColors.Primary
                            } else {
                                HmiColors.Surface
                            },
                            contentColor = HmiColors.TextPrimary
                        )
                    ) {
                        Text(
                            text = scenario.name.replace("_", "\n"),
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 10.sp
                        )
                    }
                }
            }



            Button(
                onClick = {
                    viewModel.resumeAutomaticSimulation()
                },
                modifier = Modifier
                    .width(85.dp)
                    .fillMaxHeight(),
                shape = RoundedCornerShape(9.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = HmiColors.Success,
                    contentColor = HmiColors.Background
                ),
                contentPadding = PaddingValues(4.dp)
            ) {
                Text(
                    "▶ AUTO",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }

    SettingsDrawer(
        visible = settingsVisible,
        onDismiss = { settingsVisible = false },
        overspeedEnabled = overspeedEnabled,
        onOverspeedChanged = { overspeedEnabled = it },
        speedUnit = speedUnit,
        onSpeedUnitChanged = { speedUnit = it }
    )
}

@Composable
private fun VehicleStatus(
    title: String,
    value: String,
    active: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                HmiColors.Surface,
                RoundedCornerShape(12.dp)
            )
            .padding(13.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            title,
            fontSize = 9.sp,
            color = HmiColors.TextSecondary,
            letterSpacing = 1.sp
        )

        Text(
            value,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = if (active) {
                HmiColors.Success
            } else {
                HmiColors.Danger
            }
        )
    }
}
