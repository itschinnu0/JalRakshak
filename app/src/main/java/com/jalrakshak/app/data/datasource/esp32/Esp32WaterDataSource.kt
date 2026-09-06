package com.jalrakshak.app.data.datasource.esp32

import com.jalrakshak.app.domain.model.WaterSnapshot
import com.jalrakshak.app.domain.repository.WaterDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

/**
 * Future scope: BLE ESP32 Data Source.
 * Keeps hardware implementation isolated from domain and UI.
 */
class Esp32WaterDataSource : WaterDataSource {
    // To be implemented in later phases with actual BLE payload parsing
    override val waterSnapshotFlow: Flow<WaterSnapshot> = emptyFlow()

    override suspend fun setOutletState(isOpen: Boolean) {
        // Implementation for BLE override (e.g., transmitting payload)
    }
}
