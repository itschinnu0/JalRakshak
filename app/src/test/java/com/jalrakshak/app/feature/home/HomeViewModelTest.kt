package com.jalrakshak.app.feature.home

import com.jalrakshak.app.data.datasource.demo.DemoWaterDataSource
import com.jalrakshak.app.data.repository.WaterRepositoryImpl
import com.jalrakshak.app.domain.model.OutletState
import com.jalrakshak.app.domain.model.SafetyState
import com.jalrakshak.app.domain.safety.SafetyEvaluatorImpl
import com.jalrakshak.app.domain.simulation.SimulationEngineImpl
import com.jalrakshak.app.domain.simulation.SimulationScenario
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var simulationEngine: SimulationEngineImpl
    private lateinit var safetyEvaluator: SafetyEvaluatorImpl
    private lateinit var demoDataSource: DemoWaterDataSource
    private lateinit var repository: WaterRepositoryImpl
    private lateinit var viewModel: HomeViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        simulationEngine = SimulationEngineImpl()
        safetyEvaluator = SafetyEvaluatorImpl()
        demoDataSource = DemoWaterDataSource(simulationEngine, safetyEvaluator)
        repository = WaterRepositoryImpl(demoDataSource)
        viewModel = HomeViewModel(repository, demoDataSource)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `test initial HomeUiState reflects NORMAL water snapshot`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertNotNull(state.waterSnapshot)
        assertEquals(SafetyState.NORMAL, state.waterSnapshot?.safetyState)
        assertEquals(OutletState.OPEN, state.waterSnapshot?.outletState)
    }

    @Test
    fun `test switching to Acidic Mining Water scenario updates state to UNSAFE and locks outlet`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.switchScenario(SimulationScenario.ACIDIC_MINING_WATER)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(SimulationScenario.ACIDIC_MINING_WATER, state.currentScenario)
        assertEquals(SafetyState.UNSAFE, state.waterSnapshot?.safetyState)
        assertEquals(OutletState.CLOSED, state.waterSnapshot?.outletState)
    }

    @Test
    fun `test switching to Sensor Fault scenario updates state to FAULT and locks outlet`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.switchScenario(SimulationScenario.SENSOR_FAULT)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(SimulationScenario.SENSOR_FAULT, state.currentScenario)
        assertEquals(SafetyState.SENSOR_FAULT, state.waterSnapshot?.safetyState)
        assertEquals(OutletState.CLOSED, state.waterSnapshot?.outletState)
    }

    @Test
    fun `test attempting to open outlet while UNSAFE keeps outlet CLOSED`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()

        // First transition to UNSAFE
        viewModel.switchScenario(SimulationScenario.ACIDIC_MINING_WATER)
        testDispatcher.scheduler.advanceUntilIdle()

        // Attempt manual open override
        viewModel.toggleOutlet(true)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(OutletState.CLOSED, state.waterSnapshot?.outletState)
    }
}
