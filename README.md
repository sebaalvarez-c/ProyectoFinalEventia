# Eventia

Android app to discover events and venues, view them on a map, save favorites for offline access and sign up for events with limited spots.

Final project for *Programación de Aplicaciones Móviles 1*. The backend (REST API) is provided by the course; only the Android client is built here.

> Work in progress.

## Features (planned)

- **Catalog:** event list, detail, search by name, category filter and manual refresh.
- **Venues:** venue list and detail with upcoming events.
- **Favorites and offline:** favorites and catalog cache stored with Room.
- **Account and sign-ups:** register, login, logout, sign up for / cancel events, "my sign-ups".
- **Map:** markers for events with valid coordinates, summary sheet and external directions.

## Tech stack

- Kotlin and Jetpack Compose (Material 3)
- MVVM with Repository pattern, `StateFlow`
- Retrofit + GSON
- Room and DataStore
- Hilt
- Coil
- Navigation Compose
- Maps library: to be defined

## Versions

| Item | Version |
| --- | --- |
| Android Gradle Plugin | 9.3.3 |
| Gradle | 9.5.0 |
| Kotlin | 2.2.10 |
| Compose BOM | 2026.02.01 |
| compileSdk / targetSdk | 37 |
| minSdk | 31 |
| JDK (source/target) | 11 |

Android Studio and JDK versions used to build: to be documented.

## Setup

1. Clone the repository.
2. Open it with Android Studio.
3. Let Gradle sync and run the `app` configuration on an emulator or device.

API base URL and test credentials: to be documented.

## Architecture

To be documented.

## Verified scenarios

To be documented.

## Known limitations

To be documented.
