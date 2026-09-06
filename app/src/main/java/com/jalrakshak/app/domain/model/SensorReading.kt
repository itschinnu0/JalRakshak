package com.jalrakshak.app.domain.model

data class SensorReading(
    val pH: Float?,
    val turbidityNtu: Float?,
    val tdsPpm: Float?,
    val temperatureCelsius: Float?
) {
    val hasValidSensors: Boolean
        get() = pH != null && turbidityNtu != null && tdsPpm != null && temperatureCelsius != null
}
