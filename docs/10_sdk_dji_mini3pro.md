# 10 · SDK de DJI para el Mini 3 Pro

Comprobado online el **03/11/2026** en la documentación oficial de DJI. Antes de fijar o subir de
versión, vuelve a mirar las [notas de versión](https://developer.dji.com/doc/mobile-sdk-tutorial/en/).

## Qué SDK sirve

El Mini 3 Pro **solo** se programa con el **Mobile SDK V5 (MSDK) para Android**. No lo admiten el
MSDK V4, el Onboard SDK, el Payload SDK, el Windows SDK ni el MSDK para iOS. El SDK corre en un
móvil Android conectado por USB al mando; no hay código dentro del dron.

| Dato | Valor |
| --- | --- |
| Versión actual | **5.18.0** (22/05/2026; añade páginas de 16 KB y OID) |
| Mini 3 Pro admitido desde | 5.3.0 (07/04/2023) |
| Mandos admitidos | **DJI RC-N1** (con móvil) y **DJI RC Pro**. El DJI RC (pantalla de 5,5″) **no** |
| Firmware probado por DJI con 5.18.0 | Dron 01.00.09.00 · RC-N1 04.14.07.00 · RC Pro 03.02.09.00 |
| Licencia | Propietaria de DJI; enlaza FFmpeg sin modificar (LGPL-2.1) |
| Requisito | Cuenta de desarrollador DJI y **App Key** ligada al nombre de paquete de la app |

## Enlaces oficiales

- Tutoriales y notas de versión: https://developer.dji.com/doc/mobile-sdk-tutorial/en/
- Referencia de la API Android: https://developer.dji.com/api-reference-v5/android-api/Components/IWaypointMissionManager/IWaypointMissionManager.html
  (menú lateral con todas las clases: `ISDKManager`, `IKeyManager`, `IVirtualStickManager`,
  `ISimulatorManager`, `IMediaDataCenter`, `IPerceptionManager`, `IFlyZoneManager`,
  `IUASRemoteIDManager`, `IFlightLogManager`…)
- Repositorio con la app de ejemplo y la API en local (`Docs/Android_API`):
  https://github.com/dji-sdk/Mobile-SDK-Android-V5 (rama `dev-sdk-main`; la app de ejemplo está en
  `SampleCode-V5/`)
- Integrar el MSDK en un proyecto vacío: https://developer.dji.com/doc/mobile-sdk-tutorial/en/quick-start/user-project-caution.html
- App Key: https://developer.dji.com/user/apps
- Foro de soporte (pide iniciar sesión): https://sdk-forum.dji.net/hc/en-us

Tutoriales útiles para este proyecto (todos bajo `…/mobile-sdk-tutorial/en/`):
`tutorials/keymanager.html` (telemetría y órdenes), `tutorials/virtual-stick.html`,
`tutorials/simulator.html`, `tutorials/video-stream/video-stream2.html`,
`basic-introduction/basic-concepts/flight-control.html` (ejes y marcos de coordenadas),
`quick-start/version-differences.html` (diferencias V4 → V5: no copies código V4 de tutoriales).

## Integración (versiones que recomienda DJI para 5.18.0)

Java 17 · Kotlin 2.1.0 · Gradle 8.12 · AGP 8.7.0 · `minSdk 24` · `compileSdk`/`targetSdk 35`.

```groovy
android {
    defaultConfig {
        ndk { abiFilters 'arm64-v8a' }   // el MSDK solo trae arm64-v8a
    }
    packagingOptions {
        pickFirst 'lib/arm64-v8a/libc++_shared.so'
        // + la lista de doNotStrip "*/*/lib….so" del tutorial de integración
    }
}
dependencies {
    implementation  "com.dji:dji-sdk-v5-aircraft:5.18.0"
    compileOnly     "com.dji:dji-sdk-v5-aircraft-provided:5.18.0"
    runtimeOnly     "com.dji:dji-sdk-v5-networkImp:5.18.0"  // red (registro, mapas…); sin ella el control funciona
}
```

- `AndroidManifest.xml`: `android:extractNativeLibs="true"` en `<application>` (obligatorio desde
  5.17.0), `uses-feature android.hardware.usb.accessory` y la App Key como `meta-data`
  `com.dji.sdk.API_KEY`. La App Key no se sube a git.
- En `Application.attachBaseContext` va `com.cySdkyc.clx.Helper.install(this)` (desde 5.10.0;
  el antiguo `com.secneo.sdk.Helper` ya no arranca el MSDK).
- No sirve en emulador x86: hace falta un móvil arm64 real.

## Qué ofrece para el Mini 3 Pro

| Función | Estado en el Mini 3 Pro | API |
| --- | --- | --- |
| Telemetría (batería, posición, actitud, estado de vuelo, señal) | Sí | `KeyManager` + `FlightControllerKey`, `BatteryKey`… |
| Despegue, aterrizaje, vuelta a casa | Sí | `FlightControllerKey.KeyStartTakeoff`, `KeyStartAutoLanding`, `KeyStartGoHome` |
| Joysticks virtuales | Sí (camino previsto en el plan) | `IVirtualStickManager` |
| Simulador | Sí | `ISimulatorManager` |
| Vídeo en directo | Sí | `IMediaDataCenter` / `ICameraStreamManager` |
| Datos de obstáculos | Sí desde 5.9.0 | `IPerceptionManager` |
| Rutas KMZ/WPML (`IWaypointMissionManager`) | **No.** DJI responde en su foro que Mini 3 y Mini 3 Pro no admiten misiones de waypoints | — |
| Vuelo inteligente (track, POI…) | **No** (en 5.14.0 solo Mini 4 Pro y modelos Enterprise) | — |

Sin rutas nativas, todo desplazamiento autónomo se hace con joysticks virtuales desde la app; si
se pierde el enlace móvil–mando–dron, la misión se interrumpe (lo que hace el dron entonces lo
decide su acción de pérdida de señal, no la app).

### Joysticks virtuales (lo que dice la documentación)

- `enableVirtualStick()` toma el control; `disableVirtualStick()` lo devuelve al mando. Si el
  mando recupera el control, hay que volver a llamar a `enableVirtualStick()`.
- El dron no puede estar en otra tarea automática y el mando debe estar en su modo normal.
- Modo avanzado (`setVirtualStickAdvancedModeEnabled(true)` + `sendVirtualStickAdvancedParam`):
  velocidades/ángulos en roll-pitch, velocidad angular en yaw y velocidad o altura en vertical, en
  marco del cuerpo o de tierra. Con él activo, `getLeftStick()`/`getRightStick()` se ignoran.
- Frecuencia de envío recomendada: **5–25 Hz** (el plan usa 10 Hz).
- Marco de tierra NED; en el del cuerpo, pitch positivo = hacia atrás. Confirmar ejes en el
  simulador antes de volar (tarea P1).

## Problemas conocidos del Mini 3 Pro con el MSDK

- Al cambiar de mando, el dron puede quedarse con el error «binding device mismatch or under
  verification»: hay que volver a emparejar con **DJI Fly**.
- `isVirtualStickEnable` daba estado erróneo (arreglado en 5.7.0) y tras reconectar (5.11.0).
- Se cerraba al arrancar en la UE (arreglado en 5.10.0). Si reaparece, el atajo oficial era
  `UASRemoteIDManager` con estrategia de EE. UU.
- No obtenía la información del firmware (arreglado en 5.11.0).
- El plan recoge además, del foro, que tras despegar hay que volver a llamar a
  `enableVirtualStick()` (tarea C1.6). No aparece en las notas oficiales: verificarlo en P0/P1.

## Pendiente de comprobar con el dron real

- Firmware real del dron y del RC-N1 frente al probado por DJI (tabla de arriba).
- Apagado automático en reposo y comportamiento al perder el enlace (P0).
- Que la App Key se registra con el nombre de paquete definitivo.
