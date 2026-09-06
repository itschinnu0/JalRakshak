package com.jalrakshak.app.core.design.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.jalrakshak.app.R
import com.jalrakshak.app.ui.theme.JalRakshakTheme

@Composable
fun DemoModeBanner(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(JalRakshakTheme.safetyColors.warning.copy(alpha = 0.2f))
            .padding(vertical = JalRakshakTheme.spacing.extraSmall),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.demo_mode_active),
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = JalRakshakTheme.safetyColors.warning,
            textAlign = TextAlign.Center
        )
    }
}
