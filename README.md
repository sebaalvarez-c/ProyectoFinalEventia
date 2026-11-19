# Eventia

Aplicación Android para descubrir eventos y lugares, desarrollada como proyecto final de Programación de Aplicaciones Móviles 1.

## Versiones

| Herramienta / Librería       | Versión        |
|------------------------------|----------------|
| Android Studio               | Meerkat 2025.1 o superior (requiere AGP 9.x) |
| JDK                          | 17 o superior  |
| Android Gradle Plugin (AGP)  | 9.3.3          |
| Kotlin                       | 2.2.10         |
| compileSdk / targetSdk       | 37             |
| minSdk                       | 31 (Android 12)|
| Jetpack Compose BOM           | 2026.02.01     |
| Hilt                         | 2.60.1         |
| Retrofit                     | 2.11.0         |
| OkHttp                       | 4.12.0         |
| Room                         | 2.7.1          |
| DataStore Preferences        | 1.1.5          |
| Navigation Compose           | 2.9.0          |
| Coil                         | 2.7.0          |
| osmdroid                     | 6.1.20         |
| Coroutines                   | 1.10.2         |

## Configuración de la API

El backend es una API Node.js/Express que corre en la PC de desarrollo en el puerto 8000.

### Emulador Android

Usar el tunnel de ADB para que el emulador acceda a `localhost` de la PC:

```bash
adb reverse tcp:8000 tcp:8000
```

La URL base en `di/NetworkModule.kt` está configurada como:

```kotlin
private const val BASE_URL = "http://localhost:8000/api/"
```

### Dispositivo físico

Reemplazar `localhost` con la IP local de la PC (ej. `192.168.1.X`) y actualizar `BASE_URL`.

**Credenciales de prueba:**
- Email: `ana@eventia.test`
- Contraseña: `eventia123`

Las solicitudes autenticadas envían `Authorization: Bearer <token>` automáticamente mediante `AuthInterceptor`. Si el servidor responde `401`, la sesión local se limpia y la app redirige al inicio de sesión sin cerrarse.

## Configuración del mapa

El mapa usa **osmdroid** con un proxy de tiles propio en el backend. Los tiles se sirven en `GET /tiles/:z/:x/:y` y el backend los obtiene de ESRI World Street Map (gratuito, sin API key).

Requiere que el backend esté corriendo y el tunnel ADB activo. No se necesita ninguna clave de API externa.

## Arquitectura

El proyecto sigue **Clean Architecture** con **MVVM** y separación en tres capas:

```
ui/          → Composables + ViewModels (StateFlow)
domain/      → Modelos de dominio + interfaces de repositorio
data/        → Implementaciones de repositorio, Room, Retrofit, DataStore
```

Los DTOs de red nunca llegan a la UI: se transforman a modelos de dominio en la capa de datos. Los ViewModels exponen un único `UiState` via `StateFlow` y no dependen de Retrofit ni Room directamente.

```
com.example.proyecto_final_eventia/
├── data/
│   ├── local/          Room (EventiaDatabase, DAOs, entidades, DataStore)
│   ├── remote/         Retrofit (EventiaApi, DTOs, AuthInterceptor)
│   └── repository/     Implementaciones de repositorio
├── di/                 Módulos Hilt (NetworkModule, DatabaseModule, RepositoryModule)
├── domain/
│   ├── model/          Event, Venue, VenueSummary, Category, User
│   └── repository/     Interfaces EventRepository, VenueRepository, AuthRepository
├── navigation/         EventiaNavHost, Screen (rutas)
├── ui/
│   ├── auth/           Login, Register
│   ├── components/     EventCard, VenueCard, UiStateWrapper
│   ├── eventdetail/    Detalle de evento + inscripción
│   ├── explore/        Catálogo (Eventos / Lugares) + búsqueda + filtros
│   ├── favorites/      Favoritos offline
│   ├── map/            Mapa con marcadores (osmdroid)
│   ├── profile/        Perfil + mis inscripciones
│   ├── theme/          Color, Type, Theme
│   └── venuedetail/    Detalle de lugar + próximos eventos
└── util/               NetworkMonitor, DateFormatter
```

## Cómo compilar y ejecutar

1. Asegurarse de que el backend esté corriendo (`php artisan serve`).
2. Abrir el proyecto en Android Studio Meerkat o superior.
3. Verificar que la `BASE_URL` en `NetworkModule.kt` corresponda al entorno (emulador o dispositivo físico).
4. Ejecutar en un emulador API 31+ o dispositivo físico con Android 12+.

No se requiere ninguna clave de API ni archivo de configuración externo.

## Escenarios verificados manualmente

| Escenario | Resultado esperado |
|-----------|-------------------|
| Inicio con sesión guardada | App abre directamente en Explorar |
| Inicio sin sesión | App abre en Login |
| Búsqueda + filtro de categoría combinados | Lista muestra solo coincidencias de ambos criterios |
| Limpiar filtros | Lista vuelve al catálogo completo |
| Sin conexión (modo avión) | Catálogo muestra datos en caché con aviso "Sin conexión · datos de hace Xh" |
| Sin conexión — actualizar catálogo | Mantiene datos en caché, informa el error sin borrar la lista |
| Favorito persiste al cerrar app | El favorito reaparece al relanzar |
| Quitar favorito desde la lista | Aparece opción "Deshacer" |
| Favorito accesible sin conexión | La pantalla Favoritos muestra el evento; su detalle básico también |
| Inscribirse a evento con cupos | Cupo disponible se reduce; evento aparece en Perfil → Próximos |
| Inscribirse a evento sin cupos (409) | Diálogo informa "Sin cupos disponibles" |
| Cancelar inscripción | Cupo se restaura; evento desaparece de Perfil |
| Sin sesión → botón Asistir | Redirige a Login; al iniciar sesión vuelve al detalle del evento |
| Token expirado (401) | Sesión se limpia automáticamente y redirige a Login |
| Rotación de dispositivo | Estado de la pantalla se conserva |
| Evento sin imagen | Se muestra placeholder |
| Lugar sin imagen | Se muestra placeholder |

## Limitaciones conocidas

- **Inscripciones sin conexión**: la pantalla Perfil requiere internet para cargar "Mis inscripciones". Los datos de asistencia no se cachean localmente.
- **Tiles del mapa**: osmdroid descarga los tiles de OpenStreetMap bajo demanda. Sin conexión, solo se ven los tiles ya almacenados en caché por el sistema.
- **Imágenes de lugares**: los datos de prueba del backend no incluyen `imageUrl` para lugares; se muestra un placeholder en todos los casos.
- **RF-10 (Eventos cercanos)**: requisito opcional; no implementado.
- **Dispositivo físico**: cambiar `BASE_URL` a la IP LAN de la PC que corre el backend.
