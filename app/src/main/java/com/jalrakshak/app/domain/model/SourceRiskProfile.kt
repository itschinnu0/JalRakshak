package com.jalrakshak.app.domain.model

data class SourceRiskProfile(
    val hasKnownArsenic: Boolean = false,
    val hasKnownFluoride: Boolean = false,
    val hasHeavyMetals: Boolean = false,
    val requiresMicrobialTreatment: Boolean = true
)
