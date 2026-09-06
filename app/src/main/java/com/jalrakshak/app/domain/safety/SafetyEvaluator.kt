package com.jalrakshak.app.domain.safety

import com.jalrakshak.app.domain.model.OutletState
import com.jalrakshak.app.domain.model.SafetyState
import com.jalrakshak.app.domain.model.SensorReading

/**
 * Domain safety evaluator which determines the SafetyState from a given SensorReading,
 * and controls the deterministic fail-safe outlet rules.
 * Safety evaluation belongs purely in the domain layer.
 */
interface SafetyEvaluator {
    fun evaluateSafety(reading: SensorReading): SafetyState
    
    fun evaluateOutletState(
        safetyState: SafetyState, 
        currentOutletState: OutletState, 
        manualOverride: Boolean? = null
    ): OutletState
}
