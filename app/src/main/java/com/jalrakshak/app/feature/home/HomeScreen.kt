package com.jalrakshak.app.feature.home

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jalrakshak.app.R
import com.jalrakshak.app.core.design.components.JalRakshakButton
import com.jalrakshak.app.core.design.components.JalRakshakCard
import com.jalrakshak.app.core.design.components.StatusChip
import com.jalrakshak.app.domain.model.OutletState
import com.jalrakshak.app.domain.model.SafetyState
import com.jalrakshak.app.domain.model.SensorReading
import com.jalrakshak.app.domain.model.SourceRiskProfile
import com.jalrakshak.app.domain.model.WaterSnapshot
import com.jalrakshak.app.domain.simulation.SimulationScenario
import com.jalrakshak.app.ui.theme.JalRakshakTheme

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val snapshot = uiState.waterSnapshot
    val safetyState = snapshot?.safetyState ?: SafetyState.NORMAL
    val outletState = snapshot?.outletState ?: OutletState.OPEN
    val reading = snapshot?.reading
    val riskProfile = snapshot?.riskProfile ?: SourceRiskProfile()

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
            // 1. Safety Hero Header & Outlet Status
            SafetyHeroCard(
                safetyState = safetyState,
                outletState = outletState,
                onToggleOutlet = { viewModel.toggleOutlet(it) }
            )

            // 2. Sensor Readings Grid
            SensorReadingsSection(reading = reading)

            // 3. Source Context & Risk Profile Card
            SourceRiskProfileCard(riskProfile = riskProfile)

            // 4. Treatment Sequence Summary
            TreatmentSummaryCard()

            // 5. Demo Scenario Switcher (Judge Controls)
            DemoScenarioSwitcherCard(
                currentScenario = uiState.currentScenario,
                onSelectScenario = { viewModel.switchScenario(it) }
            )
        }
    }
}

@Composable
private fun SafetyHeroCard(
    safetyState: SafetyState,
    outletState: OutletState,
    onToggleOutlet: (Boolean) -> Unit
) {
    val (statusColor, titleRes, descRes) = when (safetyState) {
        SafetyState.NORMAL -> Triple(
            JalRakshakTheme.safetyColors.normal,
            R.string.safety_state_normal,
            R.string.safety_state_normal_desc
        )
        SafetyState.WARNING -> Triple(
            JalRakshakTheme.safetyColors.warning,
            R.string.safety_state_warning,
            R.string.safety_state_warning_desc
        )
        SafetyState.UNSAFE -> Triple(
            JalRakshakTheme.safetyColors.unsafe,
            R.string.safety_state_unsafe,
            R.string.safety_state_unsafe_desc
        )
        SafetyState.SENSOR_FAULT -> Triple(
            JalRakshakTheme.safetyColors.fault,
            R.string.safety_state_fault,
            R.string.safety_state_fault_desc
        )
        SafetyState.SYSTEM_OFFLINE -> Triple(
            JalRakshakTheme.safetyColors.offline,
            R.string.safety_state_offline,
            R.string.safety_state_offline_desc
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
                StatusChip(state = safetyState)

                Text(
                    text = if (outletState == OutletState.OPEN)
                        stringResource(R.string.outlet_status_open)
                    else
                        stringResource(R.string.outlet_status_closed),
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = if (outletState == OutletState.OPEN) JalRakshakTheme.safetyColors.normal else JalRakshakTheme.safetyColors.unsafe
                )
            }

            Text(
                text = stringResource(titleRes),
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = statusColor
            )

            Text(
                text = stringResource(descRes),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(JalRakshakTheme.spacing.extraSmall))

            val isLockedBySafety = safetyState == SafetyState.UNSAFE || safetyState == SafetyState.SENSOR_FAULT || safetyState == SafetyState.SYSTEM_OFFLINE

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isLockedBySafety) {
                    Text(
                        text = stringResource(R.string.outlet_locked_safety_reason),
                        style = MaterialTheme.typography.labelSmall,
                        color = JalRakshakTheme.safetyColors.unsafe,
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    Text(
                        text = stringResource(R.string.outlet_label),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(JalRakshakTheme.spacing.small))
                    JalRakshakButton(
                        text = if (outletState == OutletState.OPEN)
                            stringResource(R.string.outlet_action_close)
                        else
                            stringResource(R.string.outlet_action_open),
                        onClick = { onToggleOutlet(outletState != OutletState.OPEN) },
                        containerColor = if (outletState == OutletState.OPEN)
                            JalRakshakTheme.safetyColors.unsafe
                        else
                            JalRakshakTheme.safetyColors.normal
                    )
                }
            }
        }
    }
}

@Composable
private fun SensorReadingsSection(reading: SensorReading?) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(JalRakshakTheme.spacing.small)
    ) {
        SensorCard(
            title = stringResource(R.string.param_ph_title),
            value = reading?.pH?.let { "%.1f".format(it) } ?: "--",
            unit = stringResource(R.string.param_ph_unit),
            subtitle = stringResource(R.string.param_ph_normal_range),
            isOutSpec = reading?.pH?.let { it < 6.5f || it > 8.5f } ?: false,
            modifier = Modifier.weight(1f)
        )
        SensorCard(
            title = stringResource(R.string.param_turbidity_title),
            value = reading?.turbidityNtu?.let { "%.1f".format(it) } ?: "--",
            unit = stringResource(R.string.param_turbidity_unit),
            subtitle = stringResource(R.string.param_turbidity_normal_range),
            isOutSpec = reading?.turbidityNtu?.let { it > 5.0f } ?: false,
            modifier = Modifier.weight(1f)
        )
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(JalRakshakTheme.spacing.small)
    ) {
        SensorCard(
            title = stringResource(R.string.param_tds_title),
            value = reading?.tdsPpm?.let { "%.0f".format(it) } ?: "--",
            unit = stringResource(R.string.param_tds_unit),
            subtitle = stringResource(R.string.param_tds_normal_range),
            isOutSpec = reading?.tdsPpm?.let { it > 500f } ?: false,
            modifier = Modifier.weight(1f)
        )
        SensorCard(
            title = stringResource(R.string.param_temp_title),
            value = reading?.temperatureCelsius?.let { "%.1f".format(it) } ?: "--",
            unit = stringResource(R.string.param_temp_unit),
            subtitle = stringResource(R.string.param_temp_normal_range),
            isOutSpec = false,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun SensorCard(
    title: String,
    value: String,
    unit: String,
    subtitle: String,
    isOutSpec: Boolean,
    modifier: Modifier = Modifier
) {
    val accentColor = if (isOutSpec) JalRakshakTheme.safetyColors.warning else MaterialTheme.colorScheme.primary

    JalRakshakCard(
        modifier = modifier,
        borderColor = if (isOutSpec) accentColor else null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(JalRakshakTheme.spacing.medium)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(JalRakshakTheme.spacing.extraSmall))

            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = if (isOutSpec) accentColor else MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = unit,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(JalRakshakTheme.spacing.extraSmall))

            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
private fun SourceRiskProfileCard(riskProfile: SourceRiskProfile) {
    JalRakshakCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(JalRakshakTheme.spacing.medium),
            verticalArrangement = Arrangement.spacedBy(JalRakshakTheme.spacing.small)
        ) {
            Text(
                text = stringResource(R.string.source_risk_title),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.primary
            )

            val profileText = when {
                riskProfile.hasHeavyMetals -> stringResource(R.string.source_risk_heavy_metals)
                riskProfile.requiresMicrobialTreatment -> stringResource(R.string.source_risk_microbial)
                else -> stringResource(R.string.source_risk_normal)
            }

            Text(
                text = profileText,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = stringResource(R.string.source_risk_disclaimer),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun TreatmentSummaryCard() {
    JalRakshakCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(JalRakshakTheme.spacing.medium),
            verticalArrangement = Arrangement.spacedBy(JalRakshakTheme.spacing.extraSmall)
        ) {
            Text(
                text = stringResource(R.string.treatment_summary_title),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = stringResource(R.string.treatment_summary_active),
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = stringResource(R.string.treatment_stages_count),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DemoScenarioSwitcherCard(
    currentScenario: SimulationScenario,
    onSelectScenario: (SimulationScenario) -> Unit
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
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.secondary
            )

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(JalRakshakTheme.spacing.small),
                verticalArrangement = Arrangement.spacedBy(JalRakshakTheme.spacing.extraSmall)
            ) {
                ScenarioChip(
                    label = stringResource(R.string.demo_scenario_normal),
                    isSelected = currentScenario == SimulationScenario.NORMAL,
                    onClick = { onSelectScenario(SimulationScenario.NORMAL) }
                )
                ScenarioChip(
                    label = stringResource(R.string.demo_scenario_mining),
                    isSelected = currentScenario == SimulationScenario.ACIDIC_MINING_WATER,
                    onClick = { onSelectScenario(SimulationScenario.ACIDIC_MINING_WATER) }
                )
                ScenarioChip(
                    label = stringResource(R.string.demo_scenario_turbidity),
                    isSelected = currentScenario == SimulationScenario.HIGH_TURBIDITY,
                    onClick = { onSelectScenario(SimulationScenario.HIGH_TURBIDITY) }
                )
                ScenarioChip(
                    label = stringResource(R.string.demo_scenario_fault),
                    isSelected = currentScenario == SimulationScenario.SENSOR_FAULT,
                    onClick = { onSelectScenario(SimulationScenario.SENSOR_FAULT) }
                )
            }
        }
    }
}

@Composable
private fun ScenarioChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = { Text(text = label) },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    )
}
