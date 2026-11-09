# Eventia

Aplicación Android para descubrir eventos y lugares, verlos en un mapa, guardar favoritos para consultarlos sin conexión e inscribirse a eventos con cupos limitados.

Proyecto final de *Programación de Aplicaciones Móviles 1*. El backend (API REST) lo proporciona la asignatura; aquí solo se desarrolla el cliente Android.

> Proyecto en desarrollo.

## Funcionalidades (planificadas)

- **Catálogo:** lista de eventos, detalle, búsqueda por nombre, filtro por categoría y actualización manual.
- **Lugares:** lista y detalle de lugares con sus próximos eventos.
- **Favoritos y sin conexión:** favoritos y caché del catálogo guardados con Room.
- **Cuenta e inscripciones:** registro, inicio y cierre de sesión, inscripción y cancelación a eventos, "mis inscripciones".
- **Mapa:** marcadores de eventos con coordenadas válidas, hoja de resumen e indicaciones en una app externa.

## Tecnologías

- Kotlin y Jetpack Compose (Material 3)
- MVVM con patrón Repository y `StateFlow`
- Retrofit + GSON
- Room y DataStore
- Hilt
- Coil
- Navigation Compose
- Biblioteca de mapas: por definir

## Versiones

| Elemento | Versión |
| --- | --- |
| Android Gradle Plugin | 9.3.3 |
| Gradle | 9.5.0 |
| Kotlin | 2.2.10 |
| Compose BOM | 2026.02.01 |
| compileSdk / targetSdk | 37 |
| minSdk | 31 |
| JDK (source/target) | 11 |

Versiones de Android Studio y JDK usadas para compilar: por documentar.

## Configuración

1. Clonar el repositorio.
2. Abrirlo con Android Studio.
3. Esperar la sincronización de Gradle y ejecutar la configuración `app` en un emulador o dispositivo.

URL base de la API y credenciales de prueba: por documentar.

## Arquitectura

Por documentar.

## Escenarios verificados

Por documentar.

## Limitaciones conocidas

Por documentar.
