package com.jalrakshak.app.domain.repository

import com.jalrakshak.app.data.repository.WaterRepositoryImpl
import com.jalrakshak.app.data.datasource.demo.DemoWaterDataSource
import com.jalrakshak.app.domain.model.OutletState
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class WaterRepositoryTest {

    @Test
    fun testRepositoryYieldsInitialDemoState() = runBlocking {
        val dataSource = DemoWaterDataSource()
        val repository = WaterRepositoryImpl(dataSource)
        
        val snapshot = repository.currentSnapshot.first()
        assertEquals(OutletState.OPEN, snapshot.outletState)
    }

    @Test
    fun testRepositoryOverridesOutlet() = runBlocking {
        val dataSource = DemoWaterDataSource()
        val repository = WaterRepositoryImpl(dataSource)
        
        repository.overrideOutlet(false)
        val snapshot = repository.currentSnapshot.first()
        assertEquals(OutletState.CLOSED, snapshot.outletState)
    }
}
