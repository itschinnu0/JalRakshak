package com.jalrakshak.app.feature.insights

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jalrakshak.app.R
import com.jalrakshak.app.core.design.components.JalRakshakCard
import com.jalrakshak.app.core.design.components.StatusChip
import com.jalrakshak.app.domain.model.OutletState
import com.jalrakshak.app.domain.model.SafetyState
import com.jalrakshak.app.domain.model.WaterSnapshot
import com.jalrakshak.app.ui.theme.JalRakshakTheme

@Composable
fun InsightsScreen(
    modifier: Modifier = Modifier,
    viewModel: InsightsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val history = uiState.history
    val selectedParam = uiState.selectedParameter

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
                text = stringResource(R.string.insights_title),
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = stringResource(R.string.insights_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Parameter Switcher Tabs
            ParameterSelectorTabs(
                selectedParameter = selectedParam,
                onSelectParameter = { viewModel.selectParameter(it) }
            )

            // Parameter Trend Chart Card
            ParameterTrendCard(
                parameter = selectedParam,
                history = history
            )

            // Accessible Text Summary
            AccessibleTrendSummaryCard(
                parameter = selectedParam,
                history = history
            )

            // Operational Event Timeline Card
            OperationalEventTimelineCard(history = history)

            // Disclaimer Card
            DisclaimerCard()
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ParameterSelectorTabs(
    selectedParameter: AnalyticsParameter,
    onSelectParameter: (AnalyticsParameter) -> Unit
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(JalRakshakTheme.spacing.small),
        verticalArrangement = Arrangement.spacedBy(JalRakshakTheme.spacing.extraSmall)
    ) {
        AnalyticsParameter.entries.forEach { param ->
            val label = when (param) {
                AnalyticsParameter.PH -> stringResource(R.string.insights_param_ph)
                AnalyticsParameter.TURBIDITY -> stringResource(R.string.insights_param_turbidity)
                AnalyticsParameter.TDS -> stringResource(R.string.insights_param_tds)
                AnalyticsParameter.TEMPERATURE -> stringResource(R.string.insights_param_temp)
            }
            FilterChip(
                selected = param == selectedParameter,
                onClick = { onSelectParameter(param) },
                label = { Text(text = label) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    }
}

@Composable
private fun ParameterTrendCard(
    parameter: AnalyticsParameter,
    history: List<WaterSnapshot>
) {
    val (title, unit, minThreshold, maxThreshold) = getParamConfig(parameter)

    val points = history.mapIndexedNotNull { index, snapshot ->
        val value = when (parameter) {
            AnalyticsParameter.PH -> snapshot.reading.pH
            AnalyticsParameter.TURBIDITY -> snapshot.reading.turbidityNtu
            AnalyticsParameter.TDS -> snapshot.reading.tdsPpm
            AnalyticsParameter.TEMPERATURE -> snapshot.reading.temperatureCelsius
        }
        value?.let { index to it }
    }

    val latestValue = points.lastOrNull()?.second
    val isOutOfSpec = latestValue?.let { it < minThreshold || it > maxThreshold } ?: false

    JalRakshakCard(
        modifier = Modifier.fillMaxWidth(),
        borderColor = if (isOutOfSpec) JalRakshakTheme.safetyColors.warning else MaterialTheme.colorScheme.primary
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
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = latestValue?.let { "Latest: %.1f %s".format(it, unit) } ?: "No Data",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = if (isOutOfSpec) JalRakshakTheme.safetyColors.warning else JalRakshakTheme.safetyColors.normal
                )
            }

            Text(
                text = stringResource(R.string.insights_threshold_label, "%.1f - %.1f %s".format(minThreshold, maxThreshold, unit)),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(JalRakshakTheme.spacing.small))

            // Canvas Line Chart with Threshold Indicators
            TrendLineChart(
                points = points,
                minThreshold = minThreshold,
                maxThreshold = maxThreshold,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
            )
        }
    }
}

@Composable
private fun TrendLineChart(
    points: List<Pair<Int, Float>>,
    minThreshold: Float,
    maxThreshold: Float,
    modifier: Modifier = Modifier
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val warningColor = JalRakshakTheme.safetyColors.warning

    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            if (points.isEmpty()) return@Canvas

            val width = size.width
            val height = size.height
            val padding = 16f

            val allValues = points.map { it.second } + minThreshold + maxThreshold
            val minY = (allValues.minOrNull() ?: 0f) * 0.9f
            val maxY = (allValues.maxOrNull() ?: 10f) * 1.1f
            val rangeY = if (maxY - minY == 0f) 1f else maxY - minY

            val maxIndex = points.maxOfOrNull { it.first } ?: 1
            val stepX = if (maxIndex == 0) width else (width - 2 * padding) / maxIndex

            fun mapToY(valY: Float): Float {
                return height - padding - ((valY - minY) / rangeY) * (height - 2 * padding)
            }

            // Draw Threshold Lines
            val minThreshY = mapToY(minThreshold)
            val maxThreshY = mapToY(maxThreshold)

            val dashPathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)

            drawLine(
                color = warningColor.copy(alpha = 0.6f),
                start = Offset(padding, minThreshY),
                end = Offset(width - padding, minThreshY),
                strokeWidth = 2f,
                pathEffect = dashPathEffect
            )

            drawLine(
                color = warningColor.copy(alpha = 0.6f),
                start = Offset(padding, maxThreshY),
                end = Offset(width - padding, maxThreshY),
                strokeWidth = 2f,
                pathEffect = dashPathEffect
            )

            // Draw Trend Path
            val path = Path()
            points.forEachIndexed { i, pair ->
                val x = padding + pair.first * stepX
                val y = mapToY(pair.second)
                if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
            }

            drawPath(
                path = path,
                color = primaryColor,
                style = Stroke(width = 4f)
            )

            // Draw Point Markers
            points.forEach { pair ->
                val x = padding + pair.first * stepX
                val y = mapToY(pair.second)
                val isOut = pair.second < minThreshold || pair.second > maxThreshold
                drawCircle(
                    color = if (isOut) warningColor else primaryColor,
                    radius = 6f,
                    center = Offset(x, y)
                )
            }
        }
    }
}

@Composable
private fun AccessibleTrendSummaryCard(
    parameter: AnalyticsParameter,
    history: List<WaterSnapshot>
) {
    val (title, unit, minThreshold, maxThreshold) = getParamConfig(parameter)

    val values = history.mapIndexedNotNull { _, snapshot ->
        when (parameter) {
            AnalyticsParameter.PH -> snapshot.reading.pH
            AnalyticsParameter.TURBIDITY -> snapshot.reading.turbidityNtu
            AnalyticsParameter.TDS -> snapshot.reading.tdsPpm
            AnalyticsParameter.TEMPERATURE -> snapshot.reading.temperatureCelsius
        }
    }

    val avg = if (values.isNotEmpty()) values.average() else 0.0
    val minVal = values.minOrNull() ?: 0.0f
    val maxVal = values.maxOrNull() ?: 0.0f

    JalRakshakCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(JalRakshakTheme.spacing.medium),
            verticalArrangement = Arrangement.spacedBy(JalRakshakTheme.spacing.extraSmall)
        ) {
            Text(
                text = stringResource(R.string.insights_summary_title),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "$title History Summary (%d samples)".format(values.size),
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "Average: %.1f %s | Min: %.1f | Max: %.1f".format(avg, unit, minVal, maxVal),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = "Target Operating Range: %.1f – %.1f %s".format(minThreshold, maxThreshold, unit),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun OperationalEventTimelineCard(history: List<WaterSnapshot>) {
    JalRakshakCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(JalRakshakTheme.spacing.medium),
            verticalArrangement = Arrangement.spacedBy(JalRakshakTheme.spacing.small)
        ) {
            Text(
                text = stringResource(R.string.insights_timeline_title),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.primary
            )

            history.takeLast(5).reversed().forEachIndexed { index, snapshot ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(
                                    color = when (snapshot.safetyState) {
                                        SafetyState.NORMAL -> JalRakshakTheme.safetyColors.normal
                                        SafetyState.WARNING -> JalRakshakTheme.safetyColors.warning
                                        SafetyState.UNSAFE -> JalRakshakTheme.safetyColors.unsafe
                                        SafetyState.SENSOR_FAULT -> JalRakshakTheme.safetyColors.fault
                                        SafetyState.SYSTEM_OFFLINE -> JalRakshakTheme.safetyColors.offline
                                    },
                                    shape = CircleShape
                                )
                        )
                        Spacer(modifier = Modifier.width(JalRakshakTheme.spacing.small))
                        Text(
                            text = "Sample #${history.size - index}",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        StatusChip(state = snapshot.safetyState)
                        Spacer(modifier = Modifier.width(JalRakshakTheme.spacing.small))
                        Text(
                            text = if (snapshot.outletState == OutletState.OPEN) "OPEN" else "LOCKED",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (snapshot.outletState == OutletState.OPEN) JalRakshakTheme.safetyColors.normal else JalRakshakTheme.safetyColors.unsafe
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DisclaimerCard() {
    JalRakshakCard(modifier = Modifier.fillMaxWidth()) {
        Box(modifier = Modifier.padding(JalRakshakTheme.spacing.medium)) {
            Text(
                text = stringResource(R.string.insights_disclaimer),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private data class ParamConfig(
    val title: String,
    val unit: String,
    val minThreshold: Float,
    val maxThreshold: Float
)

private fun getParamConfig(parameter: AnalyticsParameter): ParamConfig {
    return when (parameter) {
        AnalyticsParameter.PH -> ParamConfig("pH Level", "pH", 6.5f, 8.5f)
        AnalyticsParameter.TURBIDITY -> ParamConfig("Turbidity", "NTU", 0.0f, 5.0f)
        AnalyticsParameter.TDS -> ParamConfig("TDS", "ppm", 0.0f, 500.0f)
        AnalyticsParameter.TEMPERATURE -> ParamConfig("Temperature", "°C", 10.0f, 30.0f)
    }
}
