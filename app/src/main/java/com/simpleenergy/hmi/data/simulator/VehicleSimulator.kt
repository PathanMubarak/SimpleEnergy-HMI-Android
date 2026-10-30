
package com.simpleenergy.hmi.data.simulator

import com.simpleenergy.hmi.data.VehicleDataSource
import com.simpleenergy.hmi.data.model.VehicleData
import com.simpleenergy.hmi.domain.VehicleScenario
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.math.roundToInt

class VehicleSimulator : VehicleDataSource {

    companion object {
        private const val UPDATE_INTERVAL = 1_000L
        private const val INITIAL_BATTERY = 82f
        private const val INITIAL_ODOMETER = 1324f
        private const val RANGE_PER_BATTERY_PERCENT = 1.05f
    }

    private val scenarios = VehicleScenario.entries

    override fun observeVehicleData(): Flow<VehicleData> = flow {

        var batterySoc = INITIAL_BATTERY
        var tripDistance = 0f
        var odometer = INITIAL_ODOMETER
        var elapsedSeconds = 0
        var movingSeconds = 0
        var movingDistance = 0f
        var tick = 0

        while (true) {
            for (scenario in scenarios) {

                val speed = scenario.speedKmh.coerceIn(0f, 200f)

                // One second of simulated distance

                val scenarioName = scenario.name


                if (speed > 0f) {
                    val distanceDelta = speed / 3600f

                    tripDistance += distanceDelta
                    odometer += distanceDelta
                    movingDistance += distanceDelta
                    movingSeconds++
                }

                val batteryChange = when {
                    scenarioName.contains("DECELERATING") -> 0.3f
                    speed >= 50f -> -0.5f
                    speed >= 25f -> -0.25f
                    else -> 0f
                }

                batterySoc = (batterySoc + batteryChange).coerceIn(0f, 100f)


                elapsedSeconds++
                tick++


                val isParked = scenarioName.contains("PARKED")
                val isReady = scenarioName.contains("READY")
                val isRiding = scenarioName.contains("RIDING")
                val isAccelerating = scenarioName.contains("ACCELERATING")
                val isMotorDisabled = scenarioName.contains("DISABLED")

                val estimatedRange = (
                        batterySoc * RANGE_PER_BATTERY_PERCENT
                        ).roundToInt()

                val averageSpeed = if (movingSeconds > 0) {
                    (movingDistance * 3600f / movingSeconds).roundToInt()
                } else {
                    0
                }

                emit(
                    VehicleData(
                        speedKmh = speed,
                        motorOn = scenario.motorOn,
                        killSwitchActive = scenario.killSwitchActive,
                        scenario = scenario,
                        timestamp = System.currentTimeMillis(),

                        batteryPercent = batterySoc.roundToInt(),
                        estimatedRangeKm = estimatedRange,
                        tripKm = tripDistance,
                        odometerKm = odometer.roundToInt(),
                        averageSpeedKmh = averageSpeed,

                        // Illustrative indicator signals
                        leftIndicator = isReady,
                        rightIndicator = isRiding,
                        highBeam = isRiding || isAccelerating,
                        sideStandDown = isParked,
                        regenActive = scenarioName.contains("DECELERATING"),

                        rideMode = when {
                            isParked -> "ECO"
                            isAccelerating -> "SONIC"
                            isRiding -> "RIDE"
                            else -> "ECO"
                        }
                    )
                )

                delay(UPDATE_INTERVAL)
            }
        }
    }
}
