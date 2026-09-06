package com.jalrakshak.app.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryTealDark,
    onPrimary = OnPrimaryTealDark,
    primaryContainer = PrimaryContainerDark,
    onPrimaryContainer = OnPrimaryContainerDark,
    secondary = SecondaryBlueDark,
    onSecondary = OnSecondaryBlueDark,
    secondaryContainer = SecondaryContainerDark,
    onSecondaryContainer = OnSecondaryContainerDark,
    background = BackgroundDark,
    onBackground = OnBackgroundDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = OnSurfaceVariantDark,
    error = SafetyUnsafeDark,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryTealLight,
    onPrimary = OnPrimaryTealLight,
    primaryContainer = PrimaryContainerLight,
    onPrimaryContainer = OnPrimaryContainerLight,
    secondary = SecondaryBlueLight,
    onSecondary = OnSecondaryBlueLight,
    secondaryContainer = SecondaryContainerLight,
    onSecondaryContainer = OnSecondaryContainerLight,
    background = BackgroundLight,
    onBackground = OnBackgroundLight,
    surface = SurfaceLight,
    onSurface = OnSurfaceLight,
    error = SafetyUnsafeLight,
    onError = Color.White
)

@Immutable
data class SafetyColors(
    val normal: Color,
    val warning: Color,
    val unsafe: Color,
    val fault: Color,
    val offline: Color
)

val LocalSafetyColors = staticCompositionLocalOf {
    SafetyColors(
        normal = Color.Unspecified,
        warning = Color.Unspecified,
        unsafe = Color.Unspecified,
        fault = Color.Unspecified,
        offline = Color.Unspecified
    )
}

@Composable
fun JalRakshakTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+, but for premium app feel we prefer our custom tech colors.
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    
    val safetyColors = if (darkTheme) {
        SafetyColors(
            normal = SafetyNormalDark,
            warning = SafetyWarningDark,
            unsafe = SafetyUnsafeDark,
            fault = SafetyFaultDark,
            offline = SafetyOfflineDark
        )
    } else {
        SafetyColors(
            normal = SafetyNormalLight,
            warning = SafetyWarningLight,
            unsafe = SafetyUnsafeLight,
            fault = SafetyFaultLight,
            offline = SafetyOfflineLight
        )
    }

    CompositionLocalProvider(
        LocalSpacing provides Spacing(),
        LocalSafetyColors provides safetyColors
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            shapes = JalRakshakShapes,
            content = content
        )
    }
}

object JalRakshakTheme {
    val safetyColors: SafetyColors
        @Composable
        get() = LocalSafetyColors.current
        
    val spacing: Spacing
        @Composable
        get() = LocalSpacing.current
}
