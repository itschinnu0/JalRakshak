package com.jalrakshak.app.domain.repository

import com.jalrakshak.app.domain.model.WaterSnapshot
import kotlinx.coroutines.flow.Flow

interface WaterDataSource {
    val waterSnapshotFlow: Flow<WaterSnapshot>
    suspend fun setOutletState(isOpen: Boolean)
}
