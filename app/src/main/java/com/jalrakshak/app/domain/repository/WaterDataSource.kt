package com.jalrakshak.app.domain.repository

import com.jalrakshak.app.domain.model.WaterSnapshot
import kotlinx.coroutines.flow.Flow

interface WaterDataSource {
    val waterSnapshotFlow: Flow<WaterSnapshot>
    val historyFlow: Flow<List<WaterSnapshot>>
    suspend fun setOutletState(isOpen: Boolean)
}
