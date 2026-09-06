package com.jalrakshak.app.domain.repository

import com.jalrakshak.app.domain.model.WaterSnapshot
import kotlinx.coroutines.flow.Flow

interface WaterRepository {
    val currentSnapshot: Flow<WaterSnapshot>
    suspend fun overrideOutlet(open: Boolean)
    suspend fun refreshData()
}
