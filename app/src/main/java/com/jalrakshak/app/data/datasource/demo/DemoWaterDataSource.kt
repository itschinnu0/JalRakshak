package com.jalrakshak.app.data.datasource.demo

import com.jalrakshak.app.domain.model.OutletState
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

    init {
        // Initial state creation without referencing uninitialized Flow
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
    }

    private fun determineRiskProfile(scenario: SimulationScenario): SourceRiskProfile {
        return when (scenario) {
            SimulationScenario.ACIDIC_MINING_WATER -> SourceRiskProfile(hasHeavyMetals = true, requiresMicrobialTreatment = true)
            SimulationScenario.HIGH_TURBIDITY -> SourceRiskProfile(requiresMicrobialTreatment = true)
            else -> SourceRiskProfile()
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
    }

    /**
     * Advances the simulation deterministically to a new scenario.
     */
    fun setScenario(scenario: SimulationScenario) {
        simulationEngine.setScenario(scenario)
        updateSnapshot()
    }

    override suspend fun setOutletState(isOpen: Boolean) {
        // Attempt to override the outlet state.
        // SafetyEvaluator will strictly reject this if the state is UNSAFE or FAULT.
        updateSnapshot(manualOutletOverride = isOpen)
    }
}
