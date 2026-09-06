package com.jalrakshak.app.core.navigation

import kotlinx.serialization.Serializable

sealed interface Route {
    @Serializable
    data object Home : Route
    
    @Serializable
    data object Purification : Route
    
    @Serializable
    data object Insights : Route
    
    @Serializable
    data object System : Route
}
