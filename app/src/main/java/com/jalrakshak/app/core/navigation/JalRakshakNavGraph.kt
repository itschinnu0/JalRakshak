package com.jalrakshak.app.core.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.jalrakshak.app.feature.home.HomeScreen
import com.jalrakshak.app.feature.purification.PurificationScreen

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
                is Route.Insights -> NavEntry(route) { PlaceholderScreen("Insights Screen Stub") }
                is Route.System -> NavEntry(route) { PlaceholderScreen("System Screen Stub") }
                else -> error("Unknown route: $route")
            }
        }
    )
}

@Composable
private fun PlaceholderScreen(title: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = title, style = MaterialTheme.typography.headlineMedium)
    }
}
