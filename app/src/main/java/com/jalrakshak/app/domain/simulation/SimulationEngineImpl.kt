package com.jalrakshak.app.domain.simulation

import com.jalrakshak.app.domain.model.SensorReading

class SimulationEngineImpl : SimulationEngine {
    override var currentScenario: SimulationScenario = SimulationScenario.NORMAL
        private set

    override fun setScenario(scenario: SimulationScenario) {
        currentScenario = scenario
    }

    override fun generateReading(): SensorReading {
        // Values strictly based on docs/SIMULATION_SPEC.md
        return when (currentScenario) {
            SimulationScenario.NORMAL -> SensorReading(
                pH = 6.8f,
                turbidityNtu = 1.4f,
                tdsPpm = 286f,
                temperatureCelsius = 26.4f
            )
            SimulationScenario.ACIDIC_MINING_WATER -> SensorReading(
                pH = 3.4f,
                turbidityNtu = 18.7f,
                tdsPpm = 840f,
                temperatureCelsius = 27.1f
            )
            SimulationScenario.HIGH_TURBIDITY -> SensorReading(
                pH = 6.7f,
                turbidityNtu = 42.5f,
                tdsPpm = 410f,
                temperatureCelsius = 28.0f
            )
            SimulationScenario.SENSOR_FAULT -> SensorReading(
                pH = null, // Invalid/missing pH
                turbidityNtu = 2.1f,
                tdsPpm = 290f,
                temperatureCelsius = 26.7f
            )
        }
    }
}
