package com.simpleenergy.hmi.data

import com.simpleenergy.hmi.data.model.VehicleData
import kotlinx.coroutines.flow.Flow

interface VehicleDataSource {

    fun observeVehicleData(): Flow<VehicleData>
}