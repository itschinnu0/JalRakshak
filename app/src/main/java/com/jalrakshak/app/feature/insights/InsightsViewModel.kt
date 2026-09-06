package com.jalrakshak.app.feature.insights

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jalrakshak.app.data.datasource.demo.DemoWaterDataSource
import com.jalrakshak.app.data.repository.WaterRepositoryImpl
import com.jalrakshak.app.domain.model.WaterSnapshot
import com.jalrakshak.app.domain.repository.WaterRepository
import com.jalrakshak.app.domain.safety.SafetyEvaluatorImpl
import com.jalrakshak.app.domain.simulation.SimulationEngineImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class AnalyticsParameter {
    PH,
    TURBIDITY,
    TDS,
    TEMPERATURE
}

data class InsightsUiState(
    val history: List<WaterSnapshot> = emptyList(),
    val currentSnapshot: WaterSnapshot? = null,
    val selectedParameter: AnalyticsParameter = AnalyticsParameter.PH,
    val isLoading: Boolean = false
)

class InsightsViewModel(
    private val repository: WaterRepository = defaultRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(InsightsUiState())
    val uiState: StateFlow<InsightsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.currentSnapshot.collect { snapshot ->
                _uiState.value = _uiState.value.copy(currentSnapshot = snapshot)
            }
        }
        viewModelScope.launch {
            repository.snapshotHistory.collect { history ->
                _uiState.value = _uiState.value.copy(history = history)
            }
        }
    }

    fun selectParameter(param: AnalyticsParameter) {
        _uiState.value = _uiState.value.copy(selectedParameter = param)
    }

    companion object {
        private val defaultSimulationEngine = SimulationEngineImpl()
        private val defaultSafetyEvaluator = SafetyEvaluatorImpl()
        val defaultDemoDataSource = DemoWaterDataSource(defaultSimulationEngine, defaultSafetyEvaluator)
        val defaultRepository = WaterRepositoryImpl(defaultDemoDataSource)
    }
}
