package com.jalrakshak.app.feature.purification

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jalrakshak.app.R
import com.jalrakshak.app.core.design.components.JalRakshakCard
import com.jalrakshak.app.core.design.components.StatusChip
import com.jalrakshak.app.domain.model.OutletState
import com.jalrakshak.app.domain.model.SafetyState
import com.jalrakshak.app.domain.model.TreatmentStage
import com.jalrakshak.app.ui.theme.JalRakshakTheme

@Composable
fun PurificationScreen(
    modifier: Modifier = Modifier,
    viewModel: PurificationViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedStage = uiState.selectedStage
    val snapshot = uiState.waterSnapshot
    val safetyState = snapshot?.safetyState ?: SafetyState.NORMAL
    val outletState = snapshot?.outletState ?: OutletState.OPEN

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
                text = stringResource(R.string.purification_title),
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = stringResource(R.string.purification_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Stage Inspector Detail Card
            StageDetailCard(
                stage = selectedStage,
                safetyState = safetyState,
                outletState = outletState
            )

            Text(
                text = stringResource(R.string.purification_select_stage_hint),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // 10-Stage Pipeline Visualization
            PipelineVisualization(
                selectedStage = selectedStage,
                safetyState = safetyState,
                outletState = outletState,
                onSelectStage = { viewModel.selectStage(it) }
            )
        }
    }
}

@Composable
private fun StageDetailCard(
    stage: TreatmentStage,
    safetyState: SafetyState,
    outletState: OutletState
) {
    val (stageNum, nameRes, descRes) = getStageResources(stage)

    JalRakshakCard(
        modifier = Modifier.fillMaxWidth(),
        borderColor = MaterialTheme.colorScheme.primary
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
                    text = stringResource(R.string.purification_stage_step_label, stageNum),
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )

                if (stage == TreatmentStage.OUTLET_MONITORING) {
                    StatusChip(state = safetyState)
                } else if (stage == TreatmentStage.CONTROLLED_SOLENOID) {
                    Text(
                        text = if (outletState == OutletState.OPEN)
                            stringResource(R.string.outlet_status_open)
                        else
                            stringResource(R.string.outlet_status_closed),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = if (outletState == OutletState.OPEN)
                            JalRakshakTheme.safetyColors.normal
                        else
                            JalRakshakTheme.safetyColors.unsafe
                    )
                }
            }

            Text(
                text = stringResource(nameRes),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = stringResource(descRes),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun PipelineVisualization(
    selectedStage: TreatmentStage,
    safetyState: SafetyState,
    outletState: OutletState,
    onSelectStage: (TreatmentStage) -> Unit
) {
    val stages = TreatmentStage.entries

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        stages.forEachIndexed { index, stage ->
            val isSelected = stage == selectedStage
            val isLast = index == stages.size - 1

            PipelineNodeItem(
                stageNum = index + 1,
                stage = stage,
                isSelected = isSelected,
                safetyState = safetyState,
                outletState = outletState,
                onClick = { onSelectStage(stage) }
            )

            if (!isLast) {
                ConnectingFlowLine()
            }
        }
    }
}

@Composable
private fun PipelineNodeItem(
    stageNum: Int,
    stage: TreatmentStage,
    isSelected: Boolean,
    safetyState: SafetyState,
    outletState: OutletState,
    onClick: () -> Unit
) {
    val (_, nameRes, _) = getStageResources(stage)
    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
    val containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surface

    JalRakshakCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        borderColor = borderColor
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(containerColor)
                .padding(JalRakshakTheme.spacing.medium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Stage Number Badge
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondaryContainer,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$stageNum",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondaryContainer
                )
            }

            Spacer(modifier = Modifier.width(JalRakshakTheme.spacing.medium))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(nameRes),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Stage Status Indicator
            when (stage) {
                TreatmentStage.OUTLET_MONITORING -> StatusChip(state = safetyState)
                TreatmentStage.CONTROLLED_SOLENOID -> Text(
                    text = if (outletState == OutletState.OPEN) "OPEN" else "LOCKED",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = if (outletState == OutletState.OPEN) JalRakshakTheme.safetyColors.normal else JalRakshakTheme.safetyColors.unsafe
                )
                else -> Text(
                    text = "ACTIVE",
                    style = MaterialTheme.typography.labelSmall,
                    color = JalRakshakTheme.safetyColors.normal
                )
            }
        }
    }
}

@Composable
private fun ConnectingFlowLine() {
    val lineColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(width = 2.dp, height = 20.dp)) {
            drawLine(
                color = lineColor,
                start = Offset(x = size.width / 2, y = 0f),
                end = Offset(x = size.width / 2, y = size.height),
                strokeWidth = 4f
            )
        }
    }
}

private fun getStageResources(stage: TreatmentStage): Triple<Int, Int, Int> {
    return when (stage) {
        TreatmentStage.RAW_WATER -> Triple(1, R.string.stage_1_name, R.string.stage_1_desc)
        TreatmentStage.PP_SAND_FILTER -> Triple(2, R.string.stage_2_name, R.string.stage_2_desc)
        TreatmentStage.CALCITE_LIMESTONE -> Triple(3, R.string.stage_3_name, R.string.stage_3_desc)
        TreatmentStage.AERATION -> Triple(4, R.string.stage_4_name, R.string.stage_4_desc)
        TreatmentStage.FE_MN_MEDIA -> Triple(5, R.string.stage_5_name, R.string.stage_5_desc)
        TreatmentStage.ADSORPTION -> Triple(6, R.string.stage_6_name, R.string.stage_6_desc)
        TreatmentStage.UF -> Triple(7, R.string.stage_7_name, R.string.stage_7_desc)
        TreatmentStage.UV_C -> Triple(8, R.string.stage_8_name, R.string.stage_8_desc)
        TreatmentStage.OUTLET_MONITORING -> Triple(9, R.string.stage_9_name, R.string.stage_9_desc)
        TreatmentStage.CONTROLLED_SOLENOID -> Triple(10, R.string.stage_10_name, R.string.stage_10_desc)
    }
}
