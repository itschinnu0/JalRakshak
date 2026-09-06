# Phase 03 — Domain, Deterministic Simulation & Safety

## Objective
Implement core product logic independently of UI.

## Implement
SensorReading, WaterSnapshot, SafetyState, OutletState, TreatmentStage, SourceRiskProfile, events; deterministic SimulationEngine; centralized demo thresholds; SafetyEvaluator; outlet fail-safe rules; state-transition events; recovery; scenario selection.

## Required scenarios
Normal: pH 6.8, turbidity 1.4 NTU, TDS 286 ppm, temp 26.4 C.
Acidic Mining Water: pH 3.4, turbidity 18.7 NTU, TDS 840 ppm, temp 27.1 C.
High Turbidity: pH 6.7, turbidity 42.5 NTU, TDS 410 ppm, temp 28.0 C.
Sensor Fault: invalid/missing pH, turbidity 2.1 NTU, TDS 290 ppm, temp 26.7 C.

UNSAFE, SENSOR_FAULT and SYSTEM_OFFLINE close the outlet. Never fabricate unsupported contaminant readings.


## Checkpoint
Run the required build/tests, report changed files, results, deviations and known issues, then STOP and wait for human approval.
