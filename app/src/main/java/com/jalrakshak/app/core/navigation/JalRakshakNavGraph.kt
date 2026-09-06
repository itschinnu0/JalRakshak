package com.jalrakshak.app.core.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.jalrakshak.app.feature.home.HomeScreen
import com.jalrakshak.app.feature.insights.InsightsScreen
import com.jalrakshak.app.feature.purification.PurificationScreen
import com.jalrakshak.app.feature.system.SystemScreen

/**
 * Navigation 3 foundation for the JalRakshak app shell.
 */
@Composable
fun JalRakshakNavGraph(
    backStack: MutableList<Any>,
    modifier: Modifier = Modifier
) {
    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        modifier = modifier.fillMaxSize(),
        entryProvider = { route ->
            when (route) {
                is Route.Home -> NavEntry(route) { HomeScreen() }
                is Route.Purification -> NavEntry(route) { PurificationScreen() }
                is Route.Insights -> NavEntry(route) { InsightsScreen() }
                is Route.System -> NavEntry(route) { SystemScreen() }
                else -> error("Unknown route: $route")
            }
        }
    )
}
