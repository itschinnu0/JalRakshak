package com.jalrakshak.app.feature.system

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jalrakshak.app.data.datasource.demo.DemoWaterDataSource
import com.jalrakshak.app.data.repository.WaterRepositoryImpl
import com.jalrakshak.app.domain.model.OutletState
import com.jalrakshak.app.domain.model.SafetyState
import com.jalrakshak.app.domain.model.WaterSnapshot
import com.jalrakshak.app.domain.repository.WaterRepository
import com.jalrakshak.app.domain.safety.SafetyEvaluatorImpl
import com.jalrakshak.app.domain.simulation.SimulationEngineImpl
import com.jalrakshak.app.domain.simulation.SimulationScenario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SystemUiState(
    val waterSnapshot: WaterSnapshot? = null,
    val currentScenario: SimulationScenario = SimulationScenario.NORMAL,
    val isSimulatedOffline: Boolean = false
)

class SystemViewModel(
    private val repository: WaterRepository = defaultRepository,
    private val demoDataSource: DemoWaterDataSource? = defaultDemoDataSource
) : ViewModel() {

    private val _uiState = MutableStateFlow(SystemUiState())
    val uiState: StateFlow<SystemUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.currentSnapshot.collect { snapshot ->
                _uiState.value = _uiState.value.copy(
                    waterSnapshot = if (_uiState.value.isSimulatedOffline) {
                        snapshot.copy(
                            safetyState = SafetyState.SYSTEM_OFFLINE,
                            outletState = OutletState.CLOSED
                        )
                    } else {
                        snapshot
                    }
                )
            }
        }
    }

    fun switchScenario(scenario: SimulationScenario) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                currentScenario = scenario,
                isSimulatedOffline = false
            )
            demoDataSource?.setScenario(scenario)
        }
    }

    fun toggleOutlet(isOpen: Boolean) {
        viewModelScope.launch {
            if (!_uiState.value.isSimulatedOffline) {
                repository.overrideOutlet(isOpen)
            }
        }
    }

    fun simulateOffline(isOffline: Boolean) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSimulatedOffline = isOffline)
            val current = _uiState.value.waterSnapshot
            if (current != null && isOffline) {
                _uiState.value = _uiState.value.copy(
                    waterSnapshot = current.copy(
                        safetyState = SafetyState.SYSTEM_OFFLINE,
                        outletState = OutletState.CLOSED
                    )
                )
            } else if (current != null) {
                demoDataSource?.setScenario(_uiState.value.currentScenario)
            }
        }
    }

    companion object {
        private val defaultSimulationEngine = SimulationEngineImpl()
        private val defaultSafetyEvaluator = SafetyEvaluatorImpl()
        val defaultDemoDataSource = DemoWaterDataSource(defaultSimulationEngine, defaultSafetyEvaluator)
        val defaultRepository = WaterRepositoryImpl(defaultDemoDataSource)
    }
}
