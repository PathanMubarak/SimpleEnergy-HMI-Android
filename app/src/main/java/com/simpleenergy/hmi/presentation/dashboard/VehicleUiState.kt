
package com.simpleenergy.hmi.presentation.dashboard

import com.simpleenergy.hmi.domain.VehicleScenario

enum class SignalStatus {
    CONNECTED,
    INVALID,
    STALE,
    UNAVAILABLE
}

data class VehicleUiState(
    val speedKmh: Float = 0f,
    val motorOn: Boolean = false,
    val killSwitchActive: Boolean = false,
    val scenario: VehicleScenario? = null,
    val signalStatus: SignalStatus = SignalStatus.UNAVAILABLE,
    val isLoading: Boolean = true,

    // Additional dashboard signals
    val batteryPercent: Int = 82,
    val estimatedRangeKm: Int = 86,
    val tripKm: Float = 0f,
    val odometerKm: Int = 1324,
    val averageSpeedKmh: Int = 0,

    val leftIndicator: Boolean = false,
    val rightIndicator: Boolean = false,
    val highBeam: Boolean = false,
    val sideStandDown: Boolean = false,
    val regenActive: Boolean = false,
    val rideMode: String = "ECO"
)
