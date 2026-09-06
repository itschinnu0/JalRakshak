package com.jalrakshak.app.data.datasource.demo

import com.jalrakshak.app.domain.model.OutletState
import com.jalrakshak.app.domain.model.SafetyState
import com.jalrakshak.app.domain.model.SensorReading
import com.jalrakshak.app.domain.model.SourceRiskProfile
import com.jalrakshak.app.domain.model.WaterSnapshot
import com.jalrakshak.app.domain.repository.WaterDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class DemoWaterDataSource : WaterDataSource {
    private val _snapshotFlow = MutableStateFlow(
        WaterSnapshot(
            reading = SensorReading(
                pH = 6.8f,
                turbidityNtu = 1.4f,
                tdsPpm = 286f,
                temperatureCelsius = 26.4f
            ),
            safetyState = SafetyState.NORMAL,
            outletState = OutletState.OPEN,
            riskProfile = SourceRiskProfile()
        )
    )
    override val waterSnapshotFlow: Flow<WaterSnapshot> = _snapshotFlow.asStateFlow()

    override suspend fun setOutletState(isOpen: Boolean) {
        val currentState = _snapshotFlow.value
        _snapshotFlow.value = currentState.copy(
            outletState = if (isOpen) OutletState.OPEN else OutletState.CLOSED
        )
    }
}
