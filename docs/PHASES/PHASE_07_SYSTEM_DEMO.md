# Phase 07 — System, Alerts & Demo Mode

## Objective
Complete operational/system experience and make the judge flow reliable.

## Implement
System: controller/ESP32 placeholder, sensor health, power, local/offline status, outlet, DEMO MODE, limitation.
Alerts: in-app history, meaningful state-change treatment, SoundPool short sounds, restrained haptics.
Demo scenarios: Normal, Acidic Mining Water, High Turbidity, Sensor Fault, Recovery.

## Judge flow
Normal -> Purification -> Acidic Mining Water -> UNSAFE -> Outlet Locked -> Sensor Fault -> Insights -> System -> limitation -> Recovery.

No fake connected hardware and no internet dependency.


## Checkpoint
Run the required build/tests, report changed files, results, deviations and known issues, then STOP and wait for human approval.
