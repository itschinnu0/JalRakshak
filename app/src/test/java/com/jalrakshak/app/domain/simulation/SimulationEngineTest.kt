package com.jalrakshak.app.domain.simulation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class SimulationEngineTest {

    private lateinit var engine: SimulationEngine

    @Before
    fun setup() {
        engine = SimulationEngineImpl()
    }

    @Test
    fun `test engine starts in NORMAL scenario deterministically`() {
        assertEquals(SimulationScenario.NORMAL, engine.currentScenario)
        val reading1 = engine.generateReading()
        val reading2 = engine.generateReading()
        
        assertEquals(reading1, reading2) // Repeatable without random fluctuations
        assertEquals(6.8f, reading1.pH)
        assertEquals(1.4f, reading1.turbidityNtu)
    }

    @Test
    fun `test Acidic Mining Water scenario produces expected values`() {
        engine.setScenario(SimulationScenario.ACIDIC_MINING_WATER)
        val reading = engine.generateReading()
        
        assertEquals(3.4f, reading.pH)
        assertEquals(18.7f, reading.turbidityNtu)
        assertEquals(840f, reading.tdsPpm)
        // Ensure no fabricated claims like "Arsenic = 50ppb" exist in the data model.
    }

    @Test
    fun `test Sensor Fault scenario produces missing pH`() {
        engine.setScenario(SimulationScenario.SENSOR_FAULT)
        val reading = engine.generateReading()
        
        assertNull(reading.pH)
        assertEquals(2.1f, reading.turbidityNtu)
    }
}
