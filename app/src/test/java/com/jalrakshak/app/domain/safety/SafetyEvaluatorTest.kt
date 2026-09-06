package com.jalrakshak.app.domain.safety

import com.jalrakshak.app.domain.model.OutletState
import com.jalrakshak.app.domain.model.SafetyState
import com.jalrakshak.app.domain.model.SensorReading
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class SafetyEvaluatorTest {

    private lateinit var evaluator: SafetyEvaluator

    @Before
    fun setup() {
        evaluator = SafetyEvaluatorImpl()
    }

    @Test
    fun `test Normal valid readings result in NORMAL state`() {
        val reading = SensorReading(
            pH = 7.0f,
            turbidityNtu = 2.0f,
            tdsPpm = 300f,
            temperatureCelsius = 25.0f
        )
        val state = evaluator.evaluateSafety(reading)
        assertEquals(SafetyState.NORMAL, state)
        
        val outlet = evaluator.evaluateOutletState(state, OutletState.CLOSED, true)
        assertEquals(OutletState.OPEN, outlet)
    }

    @Test
    fun `test Warning condition maintains safety rules`() {
        val reading = SensorReading(
            pH = 6.2f, // Warning threshold for pH
            turbidityNtu = 4.0f,
            tdsPpm = 300f,
            temperatureCelsius = 25.0f
        )
        val state = evaluator.evaluateSafety(reading)
        assertEquals(SafetyState.WARNING, state)
        
        // Manual override allowed in warning
        val outlet = evaluator.evaluateOutletState(state, OutletState.CLOSED, true)
        assertEquals(OutletState.OPEN, outlet)
    }

    @Test
    fun `test Unsafe condition strictly locks outlet closed`() {
        val reading = SensorReading(
            pH = 3.4f, // Acidic Mining Water
            turbidityNtu = 18.7f,
            tdsPpm = 840f,
            temperatureCelsius = 27.1f
        )
        val state = evaluator.evaluateSafety(reading)
        assertEquals(SafetyState.UNSAFE, state)
        
        // Manual override attempting to open MUST BE IGNORED
        val outlet = evaluator.evaluateOutletState(state, OutletState.OPEN, true)
        assertEquals(OutletState.CLOSED, outlet)
    }

    @Test
    fun `test Missing critical sensor triggers FAULT and locks outlet`() {
        val reading = SensorReading(
            pH = null, // Missing critical sensor
            turbidityNtu = 2.0f,
            tdsPpm = 300f,
            temperatureCelsius = 25.0f
        )
        val state = evaluator.evaluateSafety(reading)
        assertEquals(SafetyState.SENSOR_FAULT, state)
        
        val outlet = evaluator.evaluateOutletState(state, OutletState.OPEN, true)
        assertEquals(OutletState.CLOSED, outlet)
    }

    @Test
    fun `test System Offline locks outlet`() {
        val outlet = evaluator.evaluateOutletState(SafetyState.SYSTEM_OFFLINE, OutletState.OPEN, true)
        assertEquals(OutletState.CLOSED, outlet)
    }
}
