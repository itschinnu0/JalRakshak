package com.jalrakshak.app.core.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay

/**
 * Navigation 3 foundation for the JalRakshak app shell.
 */
@Composable
fun JalRakshakNavGraph() {
    val backStack = remember { mutableStateListOf<Any>(Route.Home) }
    
    // Stub implementation to establish Navigation 3 foundation.
    // Further phases will use SceneStrategies, Adaptive components, etc.
    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = { route ->
            when (route) {
                is Route.Home -> NavEntry(route) { Text("Home Screen Stub") }
                is Route.Purification -> NavEntry(route) { Text("Purification Screen Stub") }
                is Route.Insights -> NavEntry(route) { Text("Insights Screen Stub") }
                is Route.System -> NavEntry(route) { Text("System Screen Stub") }
                else -> error("Unknown route: $route")
            }
        }
    )
}
