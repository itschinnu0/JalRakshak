package com.jalrakshak.app.domain.safety

import com.jalrakshak.app.domain.model.OutletState
import com.jalrakshak.app.domain.model.SafetyState
import com.jalrakshak.app.domain.model.SensorReading

class SafetyEvaluatorImpl : SafetyEvaluator {

    override fun evaluateSafety(reading: SensorReading): SafetyState {
        // 1. Check for missing/invalid critical sensors
        if (!reading.hasValidSensors) {
            return SafetyState.SENSOR_FAULT
        }

        // We can safely non-null assert here because hasValidSensors is true
        val ph = reading.pH!!
        val turbidity = reading.turbidityNtu!!
        val tds = reading.tdsPpm!!

        // 2. Evaluate specific thresholds (Demonstration operating rules)
        // These are not medical/certification limits, just deterministic rules for the prototype.
        var currentState = SafetyState.NORMAL

        // pH rules
        if (ph < 5.5f || ph > 9.5f) {
            return SafetyState.UNSAFE
        } else if (ph < 6.5f || ph > 8.5f) {
            currentState = SafetyState.WARNING
        }

        // Turbidity rules
        if (turbidity > 20.0f) {
            return SafetyState.UNSAFE
        } else if (turbidity > 5.0f) {
            // escalate to warning if not already unsafe
            if (currentState == SafetyState.NORMAL) currentState = SafetyState.WARNING
        }

        // TDS rules
        if (tds > 1000f) {
            return SafetyState.UNSAFE
        } else if (tds > 500f) {
            if (currentState == SafetyState.NORMAL) currentState = SafetyState.WARNING
        }

        return currentState
    }

    override fun evaluateOutletState(
        safetyState: SafetyState,
        currentOutletState: OutletState,
        manualOverride: Boolean?
    ): OutletState {
        // Critical Rule: Outlet MUST be CLOSED for UNSAFE, FAULT, or OFFLINE.
        // Fail-safe behavior overrides any manual preference.
        if (safetyState == SafetyState.UNSAFE || 
            safetyState == SafetyState.SENSOR_FAULT || 
            safetyState == SafetyState.SYSTEM_OFFLINE) {
            return OutletState.CLOSED
        }

        // If safe (NORMAL or WARNING), respect manual override if provided
        if (manualOverride != null) {
            return if (manualOverride) OutletState.OPEN else OutletState.CLOSED
        }

        // Otherwise maintain current state or default to OPEN if NORMAL
        return currentOutletState
    }
}
