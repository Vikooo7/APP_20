# app19_jc_neon_bd_remoto · CRUD de Personas con Neon

Registro de personas (nombres, apellidos, fecha de nacimiento y DNI) guardado en PostgreSQL de **Neon** y consumido desde **Jetpack Compose** con **MVVM** y **Retrofit**.

```
App Android (View → ViewModel → Repository → Retrofit)  ──HTTP──>  API REST (Express)  ──>  Neon (PostgreSQL)
```

La app nunca se conecta a la base: la cadena de conexión solo está en el `.env` de la API.

## Carpetas

| Carpeta | Qué contiene |
|---|---|
| `app/` | App Android (`com.example.app19_jc_neon_bd_remoto`) |
| `apk/` | APK listo para instalar (`app19-Personas-Neon.apk`) |
| `V:\api-personas-neon` (fuera de este proyecto) | API REST en Node.js + Express + pg |

## Estructura MVVM

```
com.example.app19_jc_neon_bd_remoto
├── model/
│   ├── Persona.kt              ← data class (JSON ↔ Kotlin)
│   ├── PersonaApiService.kt    ← endpoints Retrofit
│   ├── RetrofitClient.kt       ← URL base y cliente HTTP
│   └── PersonaRepository.kt    ← único que llama a la API
├── viewmodel/
│   ├── PersonaUiState.kt       ← estado único de la pantalla
│   └── PersonaViewModel.kt     ← validación y operaciones CRUD
├── view/
│   ├── PantallaPersona.kt      ← Scaffold, lista y diálogo de eliminar
│   ├── FormularioPersona.kt    ← campos y botones
│   ├── PersonaItem.kt          ← Card de cada persona
│   ├── SelectorFecha.kt        ← DatePicker de Material 3
│   └── FormatoFecha.kt         ← conversión de fechas y edad
├── ui/theme/
└── MainActivity.kt
```

## Tabla `personas`

| Campo | Kotlin | PostgreSQL | Regla |
|---|---|---|---|
| Id | `id: Int` | `SERIAL` | Lo genera la base |
| Nombres | `nombres: String` | `VARCHAR(80)` | Obligatorio |
| Apellidos | `apellidos: String` | `VARCHAR(80)` | Obligatorio |
| Fecha de nacimiento | `fechaNacimiento: String` | `DATE` | `AAAA-MM-DD`, no futura |
| DNI | `dni: String` | `CHAR(8)` | 8 dígitos, único |

## Endpoints

| Método | Ruta | Operación | Respuesta |
|---|---|---|---|
| GET | `/api/personas` | Listar | 200 |
| GET | `/api/personas/:id` | Buscar una | 200 / 404 |
| POST | `/api/personas` | Registrar | 201 · 400 si falta un dato · 409 si el DNI ya existe |
| PUT | `/api/personas/:id` | Actualizar | 200 / 404 |
| DELETE | `/api/personas/:id` | Eliminar | 204 / 404 |

## Cómo ejecutarlo

1. **Neon:** crear el proyecto `personas-db` y ejecutar `V:\api-personas-neon\schema.sql` en el SQL Editor.
2. **API:** en `V:\api-personas-neon\.env`, reemplazar `DATABASE_URL` por la cadena de conexión de Neon.
   ```
   cd V:\api-personas-neon
   npm install
   npm start
   ```
3. **URL base:** en `model/RetrofitClient.kt`, poner en `BASE_URL` la dirección de la PC donde corre la API.
   Ver la sección siguiente: depende del dispositivo y de la red.
4. **App:** abrir la carpeta en Android Studio, sincronizar Gradle y pulsar Run.

## Conexión entre el celular y la PC (IP)

La API corre en la PC, así que la app necesita la **dirección IP de esa PC** para encontrarla. Esa dirección está escrita en `model/RetrofitClient.kt`:

```kotlin
private const val BASE_URL = "http://192.168.2.4:3000/"
```

| Dónde se ejecuta la app | `BASE_URL` | Requisito |
|---|---|---|
| Emulador de Android Studio | `http://10.0.2.2:3000/` | La API corriendo en la misma PC (10.0.2.2 es la PC vista desde el emulador) |
| Celular físico | `http://IP-DE-LA-PC:3000/` | Celular y PC en la **misma red Wi‑Fi** |

### La IP cambia con cada red

Cada red Wi‑Fi le asigna a la PC una IP distinta. Durante el desarrollo pasó esto:

| Red | IP de la PC |
|---|---|
| Red de la universidad | `172.16.136.76` |
| Red de casa (`SEATLEW2.0`) | `192.168.2.4` ← la que tiene la app ahora |

Si la PC se conecta a otra red, la app deja de encontrar la API y muestra **"No se pudo conectar con el servidor"**, aunque la API esté encendida. **El APK de la carpeta `apk/` solo funciona en la red `SEATLEW2.0`.**

### Para probar la app en otra red

1. En la PC, abrir PowerShell y ejecutar `ipconfig`. Anotar la **Dirección IPv4** de la sección **Wi‑Fi** (por ejemplo `192.168.1.50`).
2. Cambiar `BASE_URL` en `model/RetrofitClient.kt` por `http://ESA-IP:3000/` (sin olvidar `http://`, el puerto `:3000` y la `/` final).
3. Encender la API (`npm start`) y comprobar en el **navegador del celular** que abre `http://ESA-IP:3000/api/personas` y muestra las personas en JSON.
4. Volver a instalar la app con **Run**. El APK anterior conserva la IP antigua.

Lo más simple es probar en el **emulador** con `http://10.0.2.2:3000/`, que no depende de la red.

## Problemas que pueden surgir al probar

| Lo que se ve | Causa | Solución |
|---|---|---|
| "No se pudo conectar con el servidor" con la API encendida | La IP de `BASE_URL` no es la IP actual de la PC (se cambió de red) | Ver la IP con `ipconfig`, actualizar `BASE_URL` y volver a instalar |
| El navegador del celular no abre `http://IP:3000/api/personas` | Celular y PC en redes distintas, o celular con datos móviles | Conectar ambos a la misma Wi‑Fi |
| Misma Wi‑Fi y aún no abre | La red aísla los equipos entre sí (común en redes de universidad o de invitados) | Usar el celular como punto de acceso para la PC y revisar de nuevo la IP |
| Misma Wi‑Fi y aún no abre | El Firewall de Windows bloquea Node.js | Permitir Node.js en el Firewall (Windows lo pregunta la primera vez que se ejecuta `npm start`) |
| "No se pudo conectar" y en la PC `localhost:3000` tampoco abre | La API está apagada (se cerró la terminal o se pulsó Ctrl + C) | Ejecutar `npm start` y dejar la terminal abierta |
| La lista sale vacía, "Registros (0)", sin mensaje | Se abrió la app con la API apagada y el aviso ya desapareció | Encender la API y pulsar recargar |
| La primera carga tarda unos segundos | Neon suspende la base tras unos minutos sin uso | Pulsar recargar; la primera petición la despierta |
| "Error 500 del servidor" | La API no conecta con Neon (`DATABASE_URL` mal copiada en `.env`) | Copiar de nuevo la cadena desde **Connect** en Neon y reiniciar con `npm start` |
| `CLEARTEXT communication ... not permitted` | Falta permitir `http://` | Ya está resuelto: `android:usesCleartextTraffic="true"` en el Manifest |

## Pruebas

| Prueba | Acción | Resultado esperado |
|---|---|---|
| Listar | Abrir la app | Aparecen las 3 personas de ejemplo con su edad |
| Registrar | Completar los 4 campos y Guardar | "Persona registrada" y nueva fila en Neon |
| Editar | Lápiz, cambiar apellidos, Actualizar | Mismo id en Neon con el dato nuevo |
| Eliminar | Papelera y confirmar | La fila desaparece de Neon |
| DNI repetido | Registrar un DNI que ya existe | "Ya existe una persona con ese DNI" |
| Validación | Guardar con el DNI incompleto | "El DNI debe tener 8 dígitos", sin llamar a la API |
| Fecha futura | Abrir el calendario | Los días posteriores a hoy no se pueden elegir |
| Sin servidor | Detener la API y recargar | "No se pudo conectar con el servidor" |

Pruebas unitarias de `FormatoFecha.kt`: `gradlew testDebugUnitTest`.
