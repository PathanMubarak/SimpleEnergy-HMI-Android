
package com.simpleenergy.hmi.data.model

import com.simpleenergy.hmi.domain.VehicleScenario

data class VehicleData(
    val speedKmh: Float,
    val motorOn: Boolean,
    val killSwitchActive: Boolean,
    val scenario: VehicleScenario,
    val timestamp: Long,

    // Additional simulated dashboard signals
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
