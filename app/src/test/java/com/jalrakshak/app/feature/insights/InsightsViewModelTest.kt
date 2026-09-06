package com.jalrakshak.app.feature.insights

import com.jalrakshak.app.data.datasource.demo.DemoWaterDataSource
import com.jalrakshak.app.data.repository.WaterRepositoryImpl
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
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class InsightsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var simulationEngine: SimulationEngineImpl
    private lateinit var safetyEvaluator: SafetyEvaluatorImpl
    private lateinit var demoDataSource: DemoWaterDataSource
    private lateinit var repository: WaterRepositoryImpl
    private lateinit var viewModel: InsightsViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        simulationEngine = SimulationEngineImpl()
        safetyEvaluator = SafetyEvaluatorImpl()
        demoDataSource = DemoWaterDataSource(simulationEngine, safetyEvaluator)
        repository = WaterRepositoryImpl(demoDataSource)
        viewModel = InsightsViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `test initial InsightsUiState loads history and defaults to PH parameter`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertNotNull(state.currentSnapshot)
        assertEquals(AnalyticsParameter.PH, state.selectedParameter)
        assertFalse(state.history.isEmpty())
        assertEquals(10, state.history.size)
    }

    @Test
    fun `test selecting parameter updates selectedParameter in InsightsUiState`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.selectParameter(AnalyticsParameter.TURBIDITY)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(AnalyticsParameter.TURBIDITY, state.selectedParameter)
    }

    @Test
    fun `test switching scenario updates history deterministically in InsightsViewModel`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()

        demoDataSource.setScenario(SimulationScenario.ACIDIC_MINING_WATER)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertNotNull(state.currentSnapshot)
        val lastSample = state.history.last()
        assertEquals(SafetyState.UNSAFE, lastSample.safetyState)
        assertEquals(3.4f, lastSample.reading.pH)
    }
}
