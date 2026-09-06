package com.jalrakshak.app.domain.simulation

import com.jalrakshak.app.domain.model.SensorReading

/**
 * Deterministic data simulation engine.
 * Generates predictable scenarios (Normal, Mining Water, Faults) without random fluctuations.
 */
interface SimulationEngine {
    val currentScenario: SimulationScenario
    fun setScenario(scenario: SimulationScenario)
    fun generateReading(): SensorReading
}
