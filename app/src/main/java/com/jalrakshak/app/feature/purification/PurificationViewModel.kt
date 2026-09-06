package com.jalrakshak.app.feature.purification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jalrakshak.app.data.datasource.demo.DemoWaterDataSource
import com.jalrakshak.app.data.repository.WaterRepositoryImpl
import com.jalrakshak.app.domain.model.TreatmentStage
import com.jalrakshak.app.domain.model.WaterSnapshot
import com.jalrakshak.app.domain.repository.WaterRepository
import com.jalrakshak.app.domain.safety.SafetyEvaluatorImpl
import com.jalrakshak.app.domain.simulation.SimulationEngineImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class PurificationUiState(
    val waterSnapshot: WaterSnapshot? = null,
    val selectedStage: TreatmentStage = TreatmentStage.RAW_WATER,
    val isLoading: Boolean = false
)

class PurificationViewModel(
    private val repository: WaterRepository = defaultRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PurificationUiState())
    val uiState: StateFlow<PurificationUiState> = _uiState.asStateFlow()

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

    fun selectStage(stage: TreatmentStage) {
        _uiState.value = _uiState.value.copy(selectedStage = stage)
    }

    companion object {
        private val defaultSimulationEngine = SimulationEngineImpl()
        private val defaultSafetyEvaluator = SafetyEvaluatorImpl()
        val defaultDemoDataSource = DemoWaterDataSource(defaultSimulationEngine, defaultSafetyEvaluator)
        val defaultRepository = WaterRepositoryImpl(defaultDemoDataSource)
    }
}
