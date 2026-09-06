package com.jalrakshak.app.feature.system

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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SystemViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var simulationEngine: SimulationEngineImpl
    private lateinit var safetyEvaluator: SafetyEvaluatorImpl
    private lateinit var demoDataSource: DemoWaterDataSource
    private lateinit var repository: WaterRepositoryImpl
    private lateinit var viewModel: SystemViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        simulationEngine = SimulationEngineImpl()
        safetyEvaluator = SafetyEvaluatorImpl()
        demoDataSource = DemoWaterDataSource(simulationEngine, safetyEvaluator)
        repository = WaterRepositoryImpl(demoDataSource)
        viewModel = SystemViewModel(repository, demoDataSource)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `test initial SystemUiState reflects NORMAL water snapshot`() = runTest {
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
    fun `test simulating system offline sets SYSTEM_OFFLINE state and locks outlet`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.simulateOffline(true)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.isSimulatedOffline)
        assertEquals(SafetyState.SYSTEM_OFFLINE, state.waterSnapshot?.safetyState)
        assertEquals(OutletState.CLOSED, state.waterSnapshot?.outletState)

        // Attempt manual open override
        viewModel.toggleOutlet(true)
        testDispatcher.scheduler.advanceUntilIdle()

        val stateAfterOverride = viewModel.uiState.value
        assertEquals(OutletState.CLOSED, stateAfterOverride.waterSnapshot?.outletState)
    }
}
