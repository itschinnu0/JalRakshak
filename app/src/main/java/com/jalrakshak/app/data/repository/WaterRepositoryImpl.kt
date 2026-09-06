package com.jalrakshak.app.data.repository

import com.jalrakshak.app.domain.model.WaterSnapshot
import com.jalrakshak.app.domain.repository.WaterDataSource
import com.jalrakshak.app.domain.repository.WaterRepository
import kotlinx.coroutines.flow.Flow

class WaterRepositoryImpl(
    private val dataSource: WaterDataSource
) : WaterRepository {
    override val currentSnapshot: Flow<WaterSnapshot> = dataSource.waterSnapshotFlow
    override val snapshotHistory: Flow<List<WaterSnapshot>> = dataSource.historyFlow

    override suspend fun overrideOutlet(open: Boolean) {
        dataSource.setOutletState(open)
    }

    override suspend fun refreshData() {
        // To be implemented or mapped to BLE connection refresh if needed
    }
}
