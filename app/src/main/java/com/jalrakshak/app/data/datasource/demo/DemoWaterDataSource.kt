package com.jalrakshak.app.data.datasource.demo

import com.jalrakshak.app.domain.model.OutletState
import com.jalrakshak.app.domain.model.SensorReading
import com.jalrakshak.app.domain.model.SourceRiskProfile
import com.jalrakshak.app.domain.model.WaterSnapshot
import com.jalrakshak.app.domain.repository.WaterDataSource
import com.jalrakshak.app.domain.safety.SafetyEvaluator
import com.jalrakshak.app.domain.simulation.SimulationEngine
import com.jalrakshak.app.domain.simulation.SimulationScenario
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class DemoWaterDataSource(
    private val simulationEngine: SimulationEngine,
    private val safetyEvaluator: SafetyEvaluator
) : WaterDataSource {

    private val _snapshotFlow: MutableStateFlow<WaterSnapshot>
    override val waterSnapshotFlow: Flow<WaterSnapshot>

    private val _historyFlow: MutableStateFlow<List<WaterSnapshot>>
    override val historyFlow: Flow<List<WaterSnapshot>>

    init {
        val initialReading = simulationEngine.generateReading()
        val initialSafetyState = safetyEvaluator.evaluateSafety(initialReading)
        val initialOutletState = safetyEvaluator.evaluateOutletState(initialSafetyState, OutletState.OPEN, null)
        val initialRiskProfile = determineRiskProfile(simulationEngine.currentScenario)

        _snapshotFlow = MutableStateFlow(
            WaterSnapshot(
                reading = initialReading,
                safetyState = initialSafetyState,
                outletState = initialOutletState,
                riskProfile = initialRiskProfile
            )
        )
        waterSnapshotFlow = _snapshotFlow.asStateFlow()

        _historyFlow = MutableStateFlow(generateDeterministicHistory(simulationEngine.currentScenario))
        historyFlow = _historyFlow.asStateFlow()
    }

    private fun determineRiskProfile(scenario: SimulationScenario): SourceRiskProfile {
        return when (scenario) {
            SimulationScenario.ACIDIC_MINING_WATER -> SourceRiskProfile(hasHeavyMetals = true, requiresMicrobialTreatment = true)
            SimulationScenario.HIGH_TURBIDITY -> SourceRiskProfile(requiresMicrobialTreatment = true)
            else -> SourceRiskProfile()
        }
    }

    private fun generateDeterministicHistory(scenario: SimulationScenario): List<WaterSnapshot> {
        return when (scenario) {
            SimulationScenario.NORMAL -> (1..10).map { i ->
                val reading = SensorReading(
                    pH = 6.8f + (i % 3 - 1) * 0.05f,
                    turbidityNtu = 1.4f + (i % 2) * 0.1f,
                    tdsPpm = 286f + (i % 4) * 2f,
                    temperatureCelsius = 26.4f + (i % 2) * 0.2f
                )
                val safety = safetyEvaluator.evaluateSafety(reading)
                val outlet = safetyEvaluator.evaluateOutletState(safety, OutletState.OPEN, null)
                WaterSnapshot(reading, safety, outlet, SourceRiskProfile())
            }
            SimulationScenario.ACIDIC_MINING_WATER -> (1..10).map { i ->
                val ph = if (i <= 3) 6.8f else if (i <= 6) 5.2f else 3.4f
                val turbidity = if (i <= 3) 1.4f else if (i <= 6) 8.0f else 18.7f
                val tds = if (i <= 3) 286f else if (i <= 6) 550f else 840f
                val reading = SensorReading(ph, turbidity, tds, 27.1f)
                val safety = safetyEvaluator.evaluateSafety(reading)
                val outlet = safetyEvaluator.evaluateOutletState(safety, OutletState.OPEN, null)
                WaterSnapshot(reading, safety, outlet, SourceRiskProfile(hasHeavyMetals = true, requiresMicrobialTreatment = true))
            }
            SimulationScenario.HIGH_TURBIDITY -> (1..10).map { i ->
                val turbidity = if (i <= 3) 1.4f else if (i <= 6) 12.0f else 42.5f
                val reading = SensorReading(6.7f, turbidity, 410f, 28.0f)
                val safety = safetyEvaluator.evaluateSafety(reading)
                val outlet = safetyEvaluator.evaluateOutletState(safety, OutletState.OPEN, null)
                WaterSnapshot(reading, safety, outlet, SourceRiskProfile(requiresMicrobialTreatment = true))
            }
            SimulationScenario.SENSOR_FAULT -> (1..10).map { i ->
                val ph = if (i <= 7) 6.8f else null
                val reading = SensorReading(ph, 2.1f, 290f, 26.7f)
                val safety = safetyEvaluator.evaluateSafety(reading)
                val outlet = safetyEvaluator.evaluateOutletState(safety, OutletState.OPEN, null)
                WaterSnapshot(reading, safety, outlet, SourceRiskProfile())
            }
        }
    }

    private fun updateSnapshot(manualOutletOverride: Boolean? = null) {
        val reading = simulationEngine.generateReading()
        val safetyState = safetyEvaluator.evaluateSafety(reading)
        val currentOutlet = _snapshotFlow.value.outletState
        val newOutletState = safetyEvaluator.evaluateOutletState(safetyState, currentOutlet, manualOutletOverride)
        val riskProfile = determineRiskProfile(simulationEngine.currentScenario)

        _snapshotFlow.value = WaterSnapshot(
            reading = reading,
            safetyState = safetyState,
            outletState = newOutletState,
            riskProfile = riskProfile
        )
        _historyFlow.value = generateDeterministicHistory(simulationEngine.currentScenario)
    }

    fun setScenario(scenario: SimulationScenario) {
        simulationEngine.setScenario(scenario)
        updateSnapshot()
    }

    override suspend fun setOutletState(isOpen: Boolean) {
        updateSnapshot(manualOutletOverride = isOpen)
    }
}
