package com.jalrakshak.app.core.design

import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import com.jalrakshak.app.core.navigation.JalRakshakNavGraph

/**
 * Adaptive Design-system foundation needed by the app shell.
 */
@Composable
fun AppShell() {
    // Basic Navigation Suite Scaffold structure.
    // In Phase 02, we'll implement adaptive items and the full navigation rails/bars.
    NavigationSuiteScaffold(
        navigationSuiteItems = {
            // Stub for navigation items
            item(
                selected = true,
                onClick = { /* Handle click */ },
                icon = { Text("H") },
                label = { Text("Home") }
            )
        }
    ) {
        JalRakshakNavGraph()
    }
}
