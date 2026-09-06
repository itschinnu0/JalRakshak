# ARCHITECTURE

Use MVVM + Repository + unidirectional state flow: DataSource -> Repository -> ViewModel -> UiState -> Composable. Packages: core/design, core/navigation, core/audio, core/haptics, core/localization, data/repository, data/datasource/demo, data/datasource/esp32, domain/model, domain/safety, domain/simulation, domain/repository, feature/home, feature/purification, feature/insights, feature/system. Use adaptive window size classes, not device-specific dimensions. Future ESP32 stays behind the data-source boundary.
