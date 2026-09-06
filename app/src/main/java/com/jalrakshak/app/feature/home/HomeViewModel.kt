package com.jalrakshak.app.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jalrakshak.app.data.datasource.demo.DemoWaterDataSource
import com.jalrakshak.app.data.repository.WaterRepositoryImpl
import com.jalrakshak.app.domain.model.WaterSnapshot
import com.jalrakshak.app.domain.repository.WaterRepository
import com.jalrakshak.app.domain.safety.SafetyEvaluatorImpl
import com.jalrakshak.app.domain.simulation.SimulationEngineImpl
import com.jalrakshak.app.domain.simulation.SimulationScenario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HomeUiState(
    val waterSnapshot: WaterSnapshot? = null,
    val currentScenario: SimulationScenario = SimulationScenario.NORMAL,
    val isLoading: Boolean = false
)

class HomeViewModel(
    private val repository: WaterRepository = defaultRepository,
    private val demoDataSource: DemoWaterDataSource? = defaultDemoDataSource
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.currentSnapshot.collect { snapshot ->
                _uiState.value = _uiState.value.copy(
                    waterSnapshot = snapshot,
                    isLoading = false
                )
            }
        }
    }

    fun toggleOutlet(isOpen: Boolean) {
        viewModelScope.launch {
            repository.overrideOutlet(isOpen)
        }
    }

    fun switchScenario(scenario: SimulationScenario) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(currentScenario = scenario)
            demoDataSource?.setScenario(scenario)
        }
    }

    companion object {
        private val defaultSimulationEngine = SimulationEngineImpl()
        private val defaultSafetyEvaluator = SafetyEvaluatorImpl()
        val defaultDemoDataSource = DemoWaterDataSource(defaultSimulationEngine, defaultSafetyEvaluator)
        val defaultRepository = WaterRepositoryImpl(defaultDemoDataSource)
    }
}
