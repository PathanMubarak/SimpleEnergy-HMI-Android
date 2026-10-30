
package com.simpleenergy.hmi.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simpleenergy.hmi.data.VehicleDataSource
import com.simpleenergy.hmi.data.model.VehicleData
import com.simpleenergy.hmi.data.simulator.VehicleSimulator
import com.simpleenergy.hmi.domain.VehicleScenario
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class VehicleViewModel(
    private val dataSource: VehicleDataSource = VehicleSimulator()
) : ViewModel() {

    companion object {
        private const val STALE_TIMEOUT = 2500L
        private const val CHECK_INTERVAL = 500L
        private const val MAX_SPEED = 200f

        private const val THROTTLE_INTERVAL = 200L

        private const val BATTERY_DRAIN = 0.1f
        private const val REGEN_CHARGE = 0.03f
        private const val RANGE_PER_PERCENT = 1.05f
    }

    private val _uiState = MutableStateFlow(VehicleUiState())
    val uiState: StateFlow<VehicleUiState> = _uiState.asStateFlow()

    private var isManualMode = false
    private var lastReceivedTimestamp: Long? = null

    private var simulationJob: Job? = null
    private var throttleJob: Job? = null

    private var throttleHeld = false
    private var batterySoc = 82f

    init {
        monitorSignalFreshness()
    }

    // ---------------- RIDE MODE ----------------

    fun selectRideMode(mode: String) {
        val selectedMode = mode.uppercase()

        if (selectedMode !in listOf("ECO", "RIDE", "SONIC")) return

        _uiState.value = _uiState.value.copy(
            rideMode = selectedMode
        )
    }

    private fun getThrottleIncrement(mode: String): Float {
        return when (mode.uppercase()) {
            "ECO" -> 1f
            "RIDE" -> 2.5f
            "SONIC" -> 4f
            else -> 2.5f
        }
    }

    private fun getMaximumThrottleSpeed(mode: String): Float {
        return when (mode.uppercase()) {
            "ECO" -> 60f
            "RIDE" -> 90f
            "SONIC" -> 120f
            else -> 90f
        }
    }

    // ---------------- AUTO SIMULATION ----------------

    fun startAutomaticSimulation() {
        throttleHeld = false
        throttleJob?.cancel()
        throttleJob = null

        isManualMode = false
        lastReceivedTimestamp = null

        simulationJob?.cancel()

        simulationJob = viewModelScope.launch {
            dataSource.observeVehicleData()
                .catch {
                    _uiState.value = _uiState.value.copy(
                        speedKmh = 0f,
                        signalStatus = SignalStatus.UNAVAILABLE,
                        isLoading = false
                    )
                }
                .collect { data ->
                    processVehicleData(data)
                }
        }
    }

    fun resumeAutomaticSimulation() {
        startAutomaticSimulation()
    }

    // ---------------- PRESS AND HOLD THROTTLE ----------------

    fun startThrottle() {
        if (throttleHeld) return

        val initialState = _uiState.value

        // Throttle is available only when the vehicle is ready to ride.
        if (!initialState.motorOn || initialState.killSwitchActive) return

        isManualMode = true
        simulationJob?.cancel()
        lastReceivedTimestamp = null

        throttleHeld = true
        batterySoc = initialState.batteryPercent.toFloat()

        if (throttleJob?.isActive == true) return

        throttleJob = viewModelScope.launch {
            while (true) {
                delay(THROTTLE_INTERVAL)

                val current = _uiState.value
                val oldSpeed = current.speedKmh

                val increment = getThrottleIncrement(current.rideMode)
                val maximumSpeed = getMaximumThrottleSpeed(current.rideMode)

                val newSpeed = if (throttleHeld) {
                    (oldSpeed + increment).coerceAtMost(maximumSpeed)
                } else {
                    (oldSpeed - increment).coerceAtLeast(0f)
                }

                val batteryChange = if (throttleHeld) {
                    -BATTERY_DRAIN
                } else if (newSpeed > 0f) {
                    REGEN_CHARGE
                } else {
                    0f
                }

                batterySoc = (batterySoc + batteryChange)
                    .coerceIn(0f, 100f)

                val distanceDelta =
                    newSpeed * (THROTTLE_INTERVAL / 3_600_000f)

                val trip = current.tripKm + distanceDelta
                val odometer = current.odometerKm + distanceDelta

                val acceleratingScenario =
                    VehicleScenario.entries.firstOrNull {
                        it.name.contains("ACCELERATING")
                    }

                val ridingScenario =
                    VehicleScenario.entries.firstOrNull {
                        it.name.contains("RIDING")
                    }

                val readyScenario =
                    VehicleScenario.entries.firstOrNull {
                        it.name.contains("READY")
                    }

                val selectedScenario = when {
                    throttleHeld -> acceleratingScenario
                    newSpeed > 0f -> ridingScenario
                    else -> readyScenario
                } ?: current.scenario

                _uiState.value = current.copy(
                    speedKmh = newSpeed,
                    motorOn = true,
                    killSwitchActive = false,
                    scenario = selectedScenario,
                    signalStatus = SignalStatus.CONNECTED,
                    isLoading = false,

                    batteryPercent = batterySoc.toInt(),
                    estimatedRangeKm =
                        (batterySoc * RANGE_PER_PERCENT).toInt(),

                    tripKm = trip,
                    odometerKm = odometer.toInt(),

                    leftIndicator = !throttleHeld && newSpeed <= 0f,
                    rightIndicator = false,
                    highBeam = throttleHeld,
                    sideStandDown = false,
                    regenActive = !throttleHeld && newSpeed > 0f

                    // Keep the user's selected ride mode unchanged.
                )

                if (!throttleHeld && newSpeed <= 0f) {
                    throttleJob = null
                    break
                }
            }
        }
    }

    fun stopThrottle() {
        throttleHeld = false
    }

    // ---------------- MANUAL ASSIGNMENT SCENARIOS ----------------

    fun selectScenario(scenario: VehicleScenario) {
        throttleHeld = false
        throttleJob?.cancel()
        throttleJob = null

        simulationJob?.cancel()

        isManualMode = true
        lastReceivedTimestamp = null

        val current = _uiState.value
        val name = scenario.name

        val isParked = name.contains("PARKED")
        val isReady = name.contains("READY")
        val isRiding = name.contains("RIDING")
        val isAccelerating = name.contains("ACCELERATING")

        _uiState.value = current.copy(
            speedKmh = scenario.speedKmh.coerceIn(0f, MAX_SPEED),
            motorOn = scenario.motorOn,
            killSwitchActive = scenario.killSwitchActive,
            scenario = scenario,
            signalStatus = SignalStatus.CONNECTED,
            isLoading = false,

            leftIndicator = isReady,
            rightIndicator = isRiding,
            highBeam = isRiding || isAccelerating,
            sideStandDown = isParked,
            regenActive = false,

            // Preserve the currently selected ride mode.
            rideMode = current.rideMode
        )
    }

    // ---------------- VEHICLE DATA VALIDATION ----------------

    private fun processVehicleData(data: VehicleData) {
        val valid =
            data.speedKmh.isFinite() &&
                    data.speedKmh in 0f..MAX_SPEED &&
                    data.timestamp > 0L &&
                    data.timestamp <= System.currentTimeMillis() + 5000L &&
                    data.batteryPercent in 0..100 &&
                    data.estimatedRangeKm >= 0 &&
                    data.tripKm.isFinite() &&
                    data.tripKm >= 0f &&
                    data.odometerKm >= 0 &&
                    data.averageSpeedKmh in 0..MAX_SPEED.toInt()

        if (!valid) {
            _uiState.value = _uiState.value.copy(
                speedKmh = 0f,
                signalStatus = SignalStatus.INVALID,
                isLoading = false
            )
            return
        }

        lastReceivedTimestamp = System.currentTimeMillis()
        batterySoc = data.batteryPercent.toFloat()

        _uiState.value = VehicleUiState(
            speedKmh = data.speedKmh,
            motorOn = data.motorOn,
            killSwitchActive = data.killSwitchActive,
            scenario = data.scenario,
            signalStatus = SignalStatus.CONNECTED,
            isLoading = false,

            batteryPercent = data.batteryPercent,
            estimatedRangeKm = data.estimatedRangeKm,
            tripKm = data.tripKm,
            odometerKm = data.odometerKm,
            averageSpeedKmh = data.averageSpeedKmh,

            leftIndicator = data.leftIndicator,
            rightIndicator = data.rightIndicator,
            highBeam = data.highBeam,
            sideStandDown = data.sideStandDown,
            regenActive = data.regenActive,
            rideMode = data.rideMode
        )
    }

    // ---------------- SIGNAL MONITOR ----------------

    private fun monitorSignalFreshness() {
        viewModelScope.launch {
            while (true) {
                delay(CHECK_INTERVAL)

                if (isManualMode) continue

                val lastReceived = lastReceivedTimestamp ?: continue

                if (System.currentTimeMillis() - lastReceived > STALE_TIMEOUT) {
                    _uiState.value = _uiState.value.copy(
                        speedKmh = 0f,
                        signalStatus = SignalStatus.STALE,
                        isLoading = false
                    )
                }
            }
        }
    }

    override fun onCleared() {
        throttleHeld = false
        throttleJob?.cancel()
        simulationJob?.cancel()
        super.onCleared()
    }
}
