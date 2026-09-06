package com.jalrakshak.app.core.design.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.jalrakshak.app.ui.theme.JalRakshakTheme

@Composable
fun JalRakshakLogo(
    modifier: Modifier = Modifier,
    iconSize: Dp = 28.dp,
    showWordmark: Boolean = true
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Canvas(modifier = Modifier.size(iconSize)) {
            val width = size.width
            val height = size.height

            // Shield Outline + Droplet Symbol
            val shieldPath = Path().apply {
                moveTo(width * 0.5f, height * 0.05f)
                cubicTo(
                    width * 0.85f, height * 0.05f,
                    width * 0.95f, height * 0.3f,
                    width * 0.95f, height * 0.5f
                )
                cubicTo(
                    width * 0.95f, height * 0.8f,
                    width * 0.6f, height * 0.95f,
                    width * 0.5f, height * 0.98f
                )
                cubicTo(
                    width * 0.4f, height * 0.98f,
                    width * 0.05f, height * 0.8f,
                    width * 0.05f, height * 0.5f
                )
                cubicTo(
                    width * 0.05f, height * 0.3f,
                    width * 0.15f, height * 0.05f,
                    width * 0.5f, height * 0.05f
                )
                close()
            }

            drawPath(
                path = shieldPath,
                brush = Brush.linearGradient(
                    colors = listOf(primaryColor, secondaryColor),
                    start = Offset(0f, 0f),
                    end = Offset(width, height)
                )
            )

            // Inner Droplet Cutout
            val dropletPath = Path().apply {
                moveTo(width * 0.5f, height * 0.25f)
                cubicTo(
                    width * 0.7f, height * 0.55f,
                    width * 0.7f, height * 0.75f,
                    width * 0.5f, height * 0.78f
                )
                cubicTo(
                    width * 0.3f, height * 0.75f,
                    width * 0.3f, height * 0.55f,
                    width * 0.5f, height * 0.25f
                )
                close()
            }

            drawPath(
                path = dropletPath,
                color = Color.White.copy(alpha = 0.9f)
            )
        }

        if (showWordmark) {
            Spacer(modifier = Modifier.width(JalRakshakTheme.spacing.small))
            Text(
                text = "JalRakshak",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
