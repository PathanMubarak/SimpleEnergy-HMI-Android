package com.simpleenergy.hmi.domain

enum class VehicleScenario(
    val speedKmh: Float,
    val motorOn: Boolean,
    val killSwitchActive: Boolean
) {
    PARKED(
        speedKmh = 0f,
        motorOn = false,
        killSwitchActive = false
    ),

    READY(
        speedKmh = 0f,
        motorOn = true,
        killSwitchActive = false
    ),

    RIDING(
        speedKmh = 25f,
        motorOn = true,
        killSwitchActive = false
    ),

    ACCELERATING(
        speedKmh = 50f,
        motorOn = true,
        killSwitchActive = false
    ),

    MOTOR_DISABLED(
        speedKmh = 0f,
        motorOn = false,
        killSwitchActive = true
    )
}