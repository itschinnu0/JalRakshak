package com.jalrakshak.app.feature.system

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jalrakshak.app.R
import com.jalrakshak.app.core.design.components.JalRakshakCard
import com.jalrakshak.app.core.design.components.StatusChip
import com.jalrakshak.app.domain.model.OutletState
import com.jalrakshak.app.domain.model.SafetyState
import com.jalrakshak.app.domain.simulation.SimulationScenario
import com.jalrakshak.app.ui.theme.JalRakshakTheme

@Composable
fun SystemScreen(
    modifier: Modifier = Modifier,
    viewModel: SystemViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snapshot = uiState.waterSnapshot
    val safetyState = snapshot?.safetyState ?: SafetyState.NORMAL
    val outletState = snapshot?.outletState ?: OutletState.OPEN
    val currentScenario = uiState.currentScenario
    val isOffline = uiState.isSimulatedOffline

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = JalRakshakTheme.spacing.medium)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(vertical = JalRakshakTheme.spacing.medium),
            verticalArrangement = Arrangement.spacedBy(JalRakshakTheme.spacing.medium)
        ) {
            // Header
            Text(
                text = stringResource(R.string.system_title),
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = stringResource(R.string.system_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // 1. Active Operational Alert
            ActiveAlertCard(
                safetyState = safetyState,
                outletState = outletState
            )

            // 2. Hardware & Controller Specification Card
            HardwareSpecCard()

            // 3. Sensor Diagnostics Grid
            SensorDiagnosticsGrid(
                safetyState = safetyState,
                currentScenario = currentScenario
            )

            // 4. Judge Demo Scenario Controls
            DemoScenarioCard(
                currentScenario = currentScenario,
                isOffline = isOffline,
                onSelectScenario = { viewModel.switchScenario(it) },
                onToggleOffline = { viewModel.simulateOffline(it) }
            )

            // 5. System Limitations & Disclaimers Card
            LimitationsCard()
        }
    }
}

@Composable
private fun ActiveAlertCard(
    safetyState: SafetyState,
    outletState: OutletState
) {
    val statusColor = when (safetyState) {
        SafetyState.NORMAL -> JalRakshakTheme.safetyColors.normal
        SafetyState.WARNING -> JalRakshakTheme.safetyColors.warning
        SafetyState.UNSAFE -> JalRakshakTheme.safetyColors.unsafe
        SafetyState.SENSOR_FAULT -> JalRakshakTheme.safetyColors.fault
        SafetyState.SYSTEM_OFFLINE -> JalRakshakTheme.safetyColors.offline
    }

    val (what, why, action) = when (safetyState) {
        SafetyState.NORMAL -> Triple(
            "Water parameters inside normal threshold",
            "Monitored pH, Turbidity, TDS, and Temp meet JalRakshak rules",
            if (outletState == OutletState.OPEN) "Solenoid valve OPEN for distribution" else "Solenoid valve CLOSED by manual command"
        )
        SafetyState.WARNING -> Triple(
            "Parameters elevated above nominal baseline",
            "Filtration and multi-barrier system actively treating intake",
            "System monitoring actively; Solenoid OPEN"
        )
        SafetyState.UNSAFE -> Triple(
            "Operating safety threshold exceeded",
            "Untreated water poses health hazard in current condition",
            "Fail-safe solenoid valve LOCKED CLOSED immediately"
        )
        SafetyState.SENSOR_FAULT -> Triple(
            "Critical sensor missing or disconnected",
            "Cannot evaluate water safety accurately without sensor telemetry",
            "Fail-safe engaged: Solenoid valve LOCKED CLOSED"
        )
        SafetyState.SYSTEM_OFFLINE -> Triple(
            "Power or hardware communication loss",
            "System in default fail-safe unpowered state",
            "Electromechanical solenoid defaulted to CLOSED"
        )
    }

    JalRakshakCard(
        modifier = Modifier.fillMaxWidth(),
        borderColor = statusColor
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(JalRakshakTheme.spacing.medium),
            verticalArrangement = Arrangement.spacedBy(JalRakshakTheme.spacing.small)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.system_alert_active_title),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = statusColor
                )
                StatusChip(state = safetyState)
            }

            Text(
                text = stringResource(R.string.system_alert_what, what),
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = stringResource(R.string.system_alert_why, why),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = stringResource(R.string.system_alert_action, action),
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = statusColor
            )
        }
    }
}

@Composable
private fun HardwareSpecCard() {
    JalRakshakCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(JalRakshakTheme.spacing.medium),
            verticalArrangement = Arrangement.spacedBy(JalRakshakTheme.spacing.small)
        ) {
            Text(
                text = stringResource(R.string.system_card_power_title),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = stringResource(R.string.system_power_desc),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(JalRakshakTheme.spacing.extraSmall))

            Text(
                text = stringResource(R.string.system_card_controller_title),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = stringResource(R.string.system_controller_mode),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = stringResource(R.string.system_controller_future_ble),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SensorDiagnosticsGrid(
    safetyState: SafetyState,
    currentScenario: SimulationScenario
) {
    val isPhFault = safetyState == SafetyState.SENSOR_FAULT || currentScenario == SimulationScenario.SENSOR_FAULT

    JalRakshakCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(JalRakshakTheme.spacing.medium),
            verticalArrangement = Arrangement.spacedBy(JalRakshakTheme.spacing.small)
        ) {
            Text(
                text = stringResource(R.string.system_sensor_health_title),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.system_sensor_ph_status),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = if (isPhFault) stringResource(R.string.system_sensor_status_fault) else stringResource(R.string.system_sensor_status_online),
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = if (isPhFault) JalRakshakTheme.safetyColors.fault else JalRakshakTheme.safetyColors.normal
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.system_sensor_turbidity_status),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = stringResource(R.string.system_sensor_status_online),
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = JalRakshakTheme.safetyColors.normal
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.system_sensor_tds_status),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = stringResource(R.string.system_sensor_status_online),
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = JalRakshakTheme.safetyColors.normal
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.system_sensor_temp_status),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = stringResource(R.string.system_sensor_status_online),
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = JalRakshakTheme.safetyColors.normal
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DemoScenarioCard(
    currentScenario: SimulationScenario,
    isOffline: Boolean,
    onSelectScenario: (SimulationScenario) -> Unit,
    onToggleOffline: (Boolean) -> Unit
) {
    JalRakshakCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(JalRakshakTheme.spacing.medium),
            verticalArrangement = Arrangement.spacedBy(JalRakshakTheme.spacing.small)
        ) {
            Text(
                text = stringResource(R.string.demo_controls_title),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.secondary
            )

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(JalRakshakTheme.spacing.small),
                verticalArrangement = Arrangement.spacedBy(JalRakshakTheme.spacing.extraSmall)
            ) {
                FilterChip(
                    selected = !isOffline && currentScenario == SimulationScenario.NORMAL,
                    onClick = { onSelectScenario(SimulationScenario.NORMAL) },
                    label = { Text(stringResource(R.string.demo_scenario_normal)) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
                FilterChip(
                    selected = !isOffline && currentScenario == SimulationScenario.ACIDIC_MINING_WATER,
                    onClick = { onSelectScenario(SimulationScenario.ACIDIC_MINING_WATER) },
                    label = { Text(stringResource(R.string.demo_scenario_mining)) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
                FilterChip(
                    selected = !isOffline && currentScenario == SimulationScenario.HIGH_TURBIDITY,
                    onClick = { onSelectScenario(SimulationScenario.HIGH_TURBIDITY) },
                    label = { Text(stringResource(R.string.demo_scenario_turbidity)) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
                FilterChip(
                    selected = !isOffline && currentScenario == SimulationScenario.SENSOR_FAULT,
                    onClick = { onSelectScenario(SimulationScenario.SENSOR_FAULT) },
                    label = { Text(stringResource(R.string.demo_scenario_fault)) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
                FilterChip(
                    selected = isOffline,
                    onClick = { onToggleOffline(!isOffline) },
                    label = { Text("Simulate System Offline") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = JalRakshakTheme.safetyColors.offline.copy(alpha = 0.3f),
                        selectedLabelColor = JalRakshakTheme.safetyColors.offline
                    )
                )
            }
        }
    }
}

@Composable
private fun LimitationsCard() {
    JalRakshakCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(JalRakshakTheme.spacing.medium),
            verticalArrangement = Arrangement.spacedBy(JalRakshakTheme.spacing.small)
        ) {
            Text(
                text = stringResource(R.string.system_limitations_title),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = stringResource(R.string.system_limitations_desc),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
