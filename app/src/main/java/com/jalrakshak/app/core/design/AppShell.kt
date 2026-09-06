package com.jalrakshak.app.core.design

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.jalrakshak.app.R
import com.jalrakshak.app.core.design.components.DemoModeBanner
import com.jalrakshak.app.core.navigation.JalRakshakNavGraph
import com.jalrakshak.app.core.navigation.Route
import com.jalrakshak.app.ui.theme.JalRakshakTheme

@Composable
fun AppShell() {
    val backStack = remember { mutableStateListOf<Any>(Route.Home) }
    val currentRoute = backStack.lastOrNull() ?: Route.Home

    // Extension helper for navigation rules: if selecting the same route, pop to root.
    val navigateTo = { targetRoute: Route ->
        if (currentRoute == targetRoute) {
            backStack.clear()
            backStack.add(targetRoute)
        } else {
            backStack.clear()
            backStack.add(targetRoute)
        }
    }

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            item(
                selected = currentRoute is Route.Home,
                onClick = { navigateTo(Route.Home) },
                icon = { Text("⌂") },
                label = { Text(stringResource(R.string.nav_home)) }
            )
            item(
                selected = currentRoute is Route.Purification,
                onClick = { navigateTo(Route.Purification) },
                icon = { Text("≈") },
                label = { Text(stringResource(R.string.nav_purification)) }
            )
            item(
                selected = currentRoute is Route.Insights,
                onClick = { navigateTo(Route.Insights) },
                icon = { Text("◷") },
                label = { Text(stringResource(R.string.nav_insights)) }
            )
            item(
                selected = currentRoute is Route.System,
                onClick = { navigateTo(Route.System) },
                icon = { Text("⚙") },
                label = { Text(stringResource(R.string.nav_system)) }
            )
        },
        navigationSuiteColors = NavigationSuiteDefaults.colors(
            navigationBarContainerColor = MaterialTheme.colorScheme.surface,
            navigationBarContentColor = MaterialTheme.colorScheme.onSurface,
            navigationRailContainerColor = MaterialTheme.colorScheme.surface,
            navigationRailContentColor = MaterialTheme.colorScheme.onSurface,
        )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            DemoModeBanner()
            
            Box(modifier = Modifier.weight(1f)) {
                JalRakshakNavGraph(backStack = backStack)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AppShellPreview() {
    JalRakshakTheme {
        AppShell()
    }
}
