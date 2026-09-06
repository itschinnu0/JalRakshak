package com.jalrakshak.app.domain.repository

import com.jalrakshak.app.data.repository.WaterRepositoryImpl
import com.jalrakshak.app.data.datasource.demo.DemoWaterDataSource
import com.jalrakshak.app.domain.model.OutletState
import com.jalrakshak.app.domain.model.SafetyState
import com.jalrakshak.app.domain.safety.SafetyEvaluatorImpl
import com.jalrakshak.app.domain.simulation.SimulationEngineImpl
import com.jalrakshak.app.domain.simulation.SimulationScenario
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class WaterRepositoryTest {

    @Test
    fun `test Repository yields initial Demo state`() = runBlocking {
        val engine = SimulationEngineImpl()
        val evaluator = SafetyEvaluatorImpl()
        val dataSource = DemoWaterDataSource(engine, evaluator)
        val repository = WaterRepositoryImpl(dataSource)
        
        val snapshot = repository.currentSnapshot.first()
        assertEquals(OutletState.OPEN, snapshot.outletState)
        assertEquals(SafetyState.NORMAL, snapshot.safetyState)
    }

    @Test
    fun `test Repository overrides outlet when NORMAL`() = runBlocking {
        val engine = SimulationEngineImpl()
        val evaluator = SafetyEvaluatorImpl()
        val dataSource = DemoWaterDataSource(engine, evaluator)
        val repository = WaterRepositoryImpl(dataSource)
        
        repository.overrideOutlet(false)
        val snapshot = repository.currentSnapshot.first()
        assertEquals(OutletState.CLOSED, snapshot.outletState)
    }

    @Test
    fun `test Source Risk Profile maps correctly`() = runBlocking {
        val engine = SimulationEngineImpl()
        val evaluator = SafetyEvaluatorImpl()
        val dataSource = DemoWaterDataSource(engine, evaluator)
        
        dataSource.setScenario(SimulationScenario.ACIDIC_MINING_WATER)
        val snapshot = dataSource.waterSnapshotFlow.first()
        
        // The domain logically identifies heavy metal risk in mining water profile, 
        // without spoofing specific readings.
        assertTrue(snapshot.riskProfile.hasHeavyMetals)
        assertEquals(SafetyState.UNSAFE, snapshot.safetyState)
        assertEquals(OutletState.CLOSED, snapshot.outletState)
    }
}
