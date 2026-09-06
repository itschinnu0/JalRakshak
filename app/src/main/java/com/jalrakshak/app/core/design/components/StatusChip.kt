package com.jalrakshak.app.core.design.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.jalrakshak.app.domain.model.SafetyState
import com.jalrakshak.app.ui.theme.JalRakshakTheme

@Composable
fun StatusChip(
    state: SafetyState,
    modifier: Modifier = Modifier
) {
    val (color, label) = when (state) {
        SafetyState.NORMAL -> JalRakshakTheme.safetyColors.normal to "NORMAL"
        SafetyState.WARNING -> JalRakshakTheme.safetyColors.warning to "WARNING"
        SafetyState.UNSAFE -> JalRakshakTheme.safetyColors.unsafe to "UNSAFE"
        SafetyState.SENSOR_FAULT -> JalRakshakTheme.safetyColors.fault to "FAULT"
        SafetyState.SYSTEM_OFFLINE -> JalRakshakTheme.safetyColors.offline to "OFFLINE"
    }

    Box(
        modifier = modifier
            .background(color = color.copy(alpha = 0.15f), shape = MaterialTheme.shapes.small)
            .padding(horizontal = JalRakshakTheme.spacing.small, vertical = JalRakshakTheme.spacing.extraSmall),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = color
        )
    }
}
