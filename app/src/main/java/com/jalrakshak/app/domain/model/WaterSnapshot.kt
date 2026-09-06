package com.jalrakshak.app.domain.model

data class WaterSnapshot(
    val reading: SensorReading,
    val safetyState: SafetyState,
    val outletState: OutletState,
    val riskProfile: SourceRiskProfile,
    val timestamp: Long = System.currentTimeMillis()
)
