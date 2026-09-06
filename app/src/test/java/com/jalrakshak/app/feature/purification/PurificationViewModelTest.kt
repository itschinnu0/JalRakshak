package com.jalrakshak.app.feature.purification

import com.jalrakshak.app.data.datasource.demo.DemoWaterDataSource
import com.jalrakshak.app.data.repository.WaterRepositoryImpl
import com.jalrakshak.app.domain.model.OutletState
import com.jalrakshak.app.domain.model.SafetyState
import com.jalrakshak.app.domain.model.TreatmentStage
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
class PurificationViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var simulationEngine: SimulationEngineImpl
    private lateinit var safetyEvaluator: SafetyEvaluatorImpl
    private lateinit var demoDataSource: DemoWaterDataSource
    private lateinit var repository: WaterRepositoryImpl
    private lateinit var viewModel: PurificationViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        simulationEngine = SimulationEngineImpl()
        safetyEvaluator = SafetyEvaluatorImpl()
        demoDataSource = DemoWaterDataSource(simulationEngine, safetyEvaluator)
        repository = WaterRepositoryImpl(demoDataSource)
        viewModel = PurificationViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `test initial PurificationUiState defaults to RAW_WATER stage`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertNotNull(state.waterSnapshot)
        assertEquals(TreatmentStage.RAW_WATER, state.selectedStage)
        assertEquals(SafetyState.NORMAL, state.waterSnapshot?.safetyState)
    }

    @Test
    fun `test selectStage updates selectedStage in PurificationUiState`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.selectStage(TreatmentStage.CALCITE_LIMESTONE)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(TreatmentStage.CALCITE_LIMESTONE, state.selectedStage)
    }

    @Test
    fun `test selecting CONTROLLED_SOLENOID stage reflects safety and outlet status`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()

        // Switch scenario to UNSAFE
        demoDataSource.setScenario(SimulationScenario.ACIDIC_MINING_WATER)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.selectStage(TreatmentStage.CONTROLLED_SOLENOID)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(TreatmentStage.CONTROLLED_SOLENOID, state.selectedStage)
        assertEquals(SafetyState.UNSAFE, state.waterSnapshot?.safetyState)
        assertEquals(OutletState.CLOSED, state.waterSnapshot?.outletState)
    }
}
