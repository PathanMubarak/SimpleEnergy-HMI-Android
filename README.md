# Simple Energy – Android HMI Dashboard

## Overview

A simulated Android Vehicle Human-Machine Interface (HMI) dashboard developed as part of the Simple Energy Android HMI assignment.

The application provides a landscape-oriented vehicle dashboard that displays essential vehicle information, including speed, motor status, kill-switch status, ride metrics, and vehicle indicators.

It uses a simulated vehicle data source to demonstrate how vehicle information can be continuously received, processed, validated, and presented through a reactive Android UI.

The implementation focuses on clean architecture, reactive state management, separation of concerns, input validation, and a responsive dashboard experience.

## Features

### Vehicle Dashboard

1. Landscape-oriented dashboard interface.
2. Digital speedometer with simulated speed updates.
3. Vehicle motor status.
4. Kill-switch status.
5. Vehicle scenario and status indicators.
6. Organized dashboard layout for essential riding information.

### Vehicle Simulation

The application supports the five required vehicle scenarios:

| Scenario | Speed | Motor | Kill Switch |
|---|---:|---|---|
| Parked | 0 km/h | OFF | Inactive |
| Ready | 0 km/h | ON | Inactive |
| Riding | 25 km/h | ON | Inactive |
| Accelerating | 50 km/h | ON | Inactive |
| Motor Disabled | 0 km/h | OFF | Active |

The application supports automatic simulation and manual scenario selection.

### 3. Ride Modes

Three selectable ride modes:

12.  **ECO** – Designed for a more gradual acceleration response.
13.   **RIDE** – Provides a balanced acceleration response.
14.   **SONIC** – Provides a more aggressive acceleration response.

The manual throttle simulation uses the selected ride mode to determine acceleration behavior and its configured speed limit.

### 4. Vehicle Metrics

The dashboard includes vehicle-related information such as:
15. Battery state of charge.
16. Estimated range.
17. Trip distance.
18. Odometer.
19. Average speed.
20. Vehicle indicators.

### 5. Safety and Status Information

21. Overspeed warning.
22. Motor status indication.
23. Kill-switch status indication.
24. Vehicle signal/data availability status.
25. Handling of invalid and stale vehicle information.

### 6. Dashboard Enhancements

26. Date and time display.
27. Settings drawer.
28. Speed display preferences.
29. Additional vehicle status indicators.

## Technology Stack

| Technology | Purpose |
|---|---|
| Kotlin | Application development |
| Jetpack Compose | Declarative UI |
| Material 3 | UI components and styling |
| Kotlin Coroutines | Asynchronous operations |
| StateFlow | Reactive state management |
| ViewModel | UI state and business logic |
| Android SDK | Android platform integration |
| Gradle | Build and dependency management |

## Architecture

The application follows the **MVVM (Model–View–ViewModel)** architecture with a separated data layer.

```text
com.simpleenergy.hmi
│
├── data
│   ├── model
│   │   └── VehicleData.kt
│   │
│   ├── simulator
│   │   └── VehicleSimulator.kt
│   │
│   └── VehicleDataSource.kt
│
├── domain
│   └── VehicleScenario.kt
│
├── presentation
│   ├── dashboard
│   │   ├── DashboardScreen.kt
│   │   ├── VehicleViewModel.kt
│   │   └── VehicleUiState.kt
│   │
│   └── components
│       ├── RideMetricsPanel.kt
│       ├── VehicleIndicatorsPanel.kt
│       ├── OverspeedWarning.kt
│       ├── DateTimeDisplay.kt
│       └── SettingsDrawer.kt
│
├── ui
│   └── components
│       └── HmiSpeedometer.kt
│
└── MainActivity.kt
```

*The tree illustrates the main application modules; supporting theme and resource files are also included in the repository.*

### Data Layer

Responsible for providing vehicle information.

- `VehicleData.kt` defines the vehicle data model.
- `VehicleDataSource.kt` defines the data-source contract.
- `VehicleSimulator.kt` provides simulated vehicle data.

The data-source abstraction separates the dashboard from the implementation of the vehicle data provider.

### Domain Layer

`VehicleScenario.kt` defines the supported vehicle scenarios and their corresponding states.

This keeps scenario definitions separate from UI implementation.

### Presentation Layer

The presentation layer contains the dashboard UI and its state management.

- `VehicleViewModel.kt` processes vehicle data and manages dashboard state.
- `VehicleUiState.kt` represents the state consumed by the UI.
- `DashboardScreen.kt` renders the dashboard using Jetpack Compose.
- Reusable components handle the speedometer, metrics, indicators, warnings, date/time, and settings.

### State Management and Data Flow

The application uses Kotlin Coroutines and StateFlow for reactive state updates.

```text
Vehicle Simulator
       |
       v
VehicleDataSource
       |
       v
VehicleViewModel
       |
       v
VehicleUiState (StateFlow)
       |
       v
DashboardScreen (Compose)
       |
       v
Updated Dashboard UI
```

1. The simulator emits vehicle information.
2. The ViewModel collects the data.
3. Incoming values are validated and processed.
4. The ViewModel updates the UI state.
5. StateFlow emits the updated state.
6. Jetpack Compose observes the state and recomposes the relevant UI.


## Vehicle Data Handling

The application uses a structured approach to process simulated vehicle information and keep the dashboard synchronized with the latest available data.

- Processes vehicle information through a dedicated data-source layer.
- Uses Kotlin Coroutines and Flow for asynchronous data updates.
- Maintains observable UI state using StateFlow.
- Validates incoming vehicle information before updating the dashboard.
- Monitors data freshness to maintain up-to-date vehicle status.
- Handles unavailable data gracefully to ensure a stable user experience.
- Separates data processing from UI rendering using the MVVM architecture.

## Build and Run Instructions

### Prerequisites

- Android Studio
- JDK 17
- Android SDK
- Android emulator or physical Android device

### Clone the Repository

```bash
git clone https://github.com/PathanMubarak/SimpleEnergy-HMI-Android.git
```

### Open the Project

1. Open Android Studio.
2. Select **Open**.
3. Navigate to the cloned `SimpleEnergy-HMI-Android` directory.
4. Allow Gradle synchronization to complete.
5. Start an Android emulator or connect a physical device.

### Run the Application

Select the application run configuration in Android Studio and click **Run**.

The dashboard is designed to run in landscape orientation.

### Build a Debug APK

From the project root, execute:

**Windows PowerShell:**

```powershell
.\gradlew.bat assembleDebug
```

**macOS / Linux:**

```bash
./gradlew assembleDebug
```

The generated APK is located at:

```text
app/build/outputs
