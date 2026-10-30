package com.simpleenergy.hmi.presentation.dashboard

import com.simpleenergy.hmi.MainDispatcherRule
import com.simpleenergy.hmi.data.VehicleDataSource
import com.simpleenergy.hmi.data.model.VehicleData
import com.simpleenergy.hmi.domain.VehicleScenario
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.runCurrent
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class VehicleViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private class FakeVehicleDataSource : VehicleDataSource {

        val vehicleData = MutableSharedFlow<VehicleData>(
            replay = 1
        )

        override fun observeVehicleData(): Flow<VehicleData> {
            return vehicleData
        }
    }

    @Test
    fun `valid vehicle data updates dashboard state`() = runTest {

        val source = FakeVehicleDataSource()
        val viewModel = VehicleViewModel(source)

        source.vehicleData.emit(
            VehicleData(
                speedKmh = 25f,
                motorOn = true,
                killSwitchActive = false,
                scenario = VehicleScenario.RIDING,
                timestamp = System.currentTimeMillis()
            )
        )

        runCurrent()

        val state = viewModel.uiState.value

        assertEquals(25f, state.speedKmh)
        assertTrue(state.motorOn)
        assertFalse(state.killSwitchActive)
        assertEquals(SignalStatus.CONNECTED, state.signalStatus)
    }

    @Test
    fun `negative speed is rejected`() = runTest {

        val source = FakeVehicleDataSource()
        val viewModel = VehicleViewModel(source)

        source.vehicleData.emit(
            VehicleData(
                speedKmh = -10f,
                motorOn = true,
                killSwitchActive = false,
                scenario = VehicleScenario.RIDING,
                timestamp = System.currentTimeMillis()
            )
        )

        runCurrent()

        val state = viewModel.uiState.value

        assertEquals(0f, state.speedKmh)
        assertEquals(SignalStatus.INVALID, state.signalStatus)
    }
}