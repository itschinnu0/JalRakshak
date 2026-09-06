# AGENTS.md — JalRakshak Coding Rules

You are the primary Android implementation agent for JalRakshak.

## Non-negotiable priorities

1. Follow `docs/IMPLEMENTATION_PLAN.md` and the referenced specifications.
2. Preserve technical honesty.
3. Build a polished, premium environmental-tech UI.
4. Keep the demo deterministic and reliable.
5. Keep safety/outlet decisions centralized and testable.
6. Keep the architecture ready for future ESP32 integration.
7. Compile and test incrementally after meaningful changes.
8. Do not silently invent missing product requirements.

## Stack

- Kotlin
- Jetpack Compose
- Material 3 stable APIs
- Navigation 3 stable line
- Material 3 Adaptive / Window Size Classes
- Vico for charts
- Lottie only for high-value decorative/brand motion
- Native Compose animation APIs for core interaction
- DataStore only where persistence is justified
- SoundPool for short alert sounds
- Android/Compose haptics

Use the versions specified by the final implementation plan. Do not upgrade libraries independently unless explicitly required and documented in `docs/DECISIONS.md`.

## Architecture

Use MVVM + Repository + unidirectional state flow:

DataSource -> Repository -> ViewModel -> UiState -> Composable

Keep domain safety evaluation independent from UI.

The current data source is deterministic demo simulation. A future ESP32 source must fit behind the existing data-source boundary rather than forcing UI rewrites.

## UI rules

The app must not look like a stock Material dashboard.

Use the JalRakshak design system:
- premium environmental-tech + futuristic monitoring
- light and dark themes
- adaptive layouts
- custom hybrid navigation
- safety hero on Home
- interactive treatment pipeline
- interactive parameter charts
- event timeline
- meaningful motion, haptics and short sounds
- visible DEMO MODE

Do not hard-code layouts for the Infinix X6711. Use adaptive/window-size logic.

## Water-safety truth boundary

The onboard demo parameters are pH, turbidity, TDS/EC-related dissolved-solids indication and temperature.

These parameters do NOT identify individual heavy metals or bacteria.

Never display fabricated live values for arsenic, lead, cadmium, chromium, fluoride, or microbiological contamination.

Use a source-risk profile for such hazards.

Never claim “100% safe”, “guaranteed drinking water”, or certification without validation.

## Treatment terminology

Use the final treatment sequence:

Raw water -> PP/Sand -> Calcite/Limestone -> Aeration -> Fe/Mn Media -> Appropriate Adsorption -> UF -> UV-C -> Outlet Monitoring -> Solenoid

Activated carbon may be described for many organics/taste/odor; do not claim it universally removes every heavy metal.

Arsenic, fluoride and other metals require contaminant-appropriate media and validation.

## Hardware consistency

The prototype BOM uses a 10 W solar panel.

Do not reintroduce SIM800L GSM as a current BOM component.

Do not change hardware claims without updating the documentation.

## Coding discipline

- No random values in UI.
- No business logic hidden inside Composables.
- No duplicated safety rules.
- No magic thresholds scattered across screens.
- Strings belong in resources.
- Theme tokens belong in the design system.
- Lifecycle-aware collection/coroutines.
- Prefer simple, maintainable code over clever abstractions.
- Add tests for safety, simulation transitions and important ViewModel behavior.
- Keep placeholders clearly marked.

## Phase Discipline

The project is implemented phase-by-phase.

Before starting a phase:
1. Read AGENTS.md.
2. Read DOCUMENTATION_INDEX.md.
3. Read the phase-specific specification.
4. Inspect the current repository state.
5. Confirm the previous checkpoint is intact.

During a phase:
- Work only on the requested phase.
- Do not implement future phases.
- Do not introduce unrelated dependencies.
- Do not redesign established architecture without justification.
- Keep the project buildable.
- Prefer incremental changes.

At the end:
1. Run required tests/build.
2. Report files changed.
3. Report tests/build results.
4. Report deviations from specification.
5. Do not proceed to the next phase automatically.

The human must approve the checkpoint before the next phase begins.

## Change control

When a meaningful architectural/product decision changes:
1. update the relevant specification,
2. record the rationale in `docs/DECISIONS.md`,
3. add an entry to `docs/CHANGELOG.md`.
