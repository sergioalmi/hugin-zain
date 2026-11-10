# Prueba de despegue a 2 m (Mini 3 Pro + DJI RC Pro)

App Android mínima para probar el MSDK V5 5.18.0. Tiene dos botones:

- **DESPEGAR A 2 m**: despegue automático. Después sube con joysticks virtuales hasta 2 m sobre el
  punto de despegue y se queda ahí.
- **ATERRIZAR**: aterrizaje automático. Confirma sola la parada a unos 0,5 m del suelo.

También tiene la casilla **Modo simulador (motores parados)**: con ella marcada, el dron vuela solo
dentro del simulador del MSDK.

La app se instala **en el propio DJI RC Pro**, que es un Android con pantalla. No hace falta móvil.
Con un RC-N1 iría en un móvil Android arm64 conectado al mando por USB.

Abortos automáticos (aterriza sola):

| Caso | Límite |
| --- | --- |
| Altura | > 3 m |
| Sin dato de altura | > 1 s |
| Despegue o subida sin terminar | > 15 s |
| Fallo al activar los joysticks virtuales | — |

Si el piloto recupera el control con el mando (botón de pausa), la app deja de enviar órdenes. El
botón ATERRIZAR sigue funcionando.

## 1. Una sola vez en cada PC

1. Instala las herramientas (≈1,5 GB, en `%LOCALAPPDATA%\hugin-zain-toolchain`). En Git Bash,
   desde la raíz del repositorio:

   ```bash
   bash scripts/instalar_android.sh
   ```

   Al final debe verse `Listo. JDK: openjdk version "17…"`.
2. Pide la **App Key** en https://developer.dji.com/user/apps (tipo *Mobile SDK*, Android). La App
   Key solo vale para el nombre de paquete con el que la registres. Añade las dos cosas a
   `tools/prueba-despegue/local.properties` (no se sube a git):

   ```
   dji.appKey=TU_APP_KEY
   dji.applicationId=el.paquete.de.tu.app.key
   ```

   Sin `dji.applicationId` se usa `com.huginzain.pruebadespegue`.

## 2. Compilar y probar

```bash
export JAVA_HOME="$LOCALAPPDATA/hugin-zain-toolchain/jdk"
cd tools/prueba-despegue && ./gradlew :app:testDebugUnitTest :app:lintDebug :app:assembleDebug
```

Debe terminar con `BUILD SUCCESSFUL`. El APK queda en `app/build/outputs/apk/debug/app-debug.apk`
(≈160 MB, por las librerías del MSDK).

## 3. Instalar en el DJI RC Pro

Usa una de estas dos formas:

- Copia el APK al mando por USB-C y ábrelo con su gestor de archivos. Hay que permitir la
  instalación de apps de origen desconocido.
- Si el mando tiene la depuración USB activada:

  ```bash
  "$LOCALAPPDATA/hugin-zain-toolchain/sdk/platform-tools/adb.exe" install -r app/build/outputs/apk/debug/app-debug.apk
  ```

**Cierra DJI Fly antes de abrir la app.** La primera vez el mando necesita internet (Wi-Fi) para
registrar la App Key.

## 4. Procedimiento de prueba

**Primero, en el simulador:**

1. Pon el dron sobre una superficie plana y enciende el dron y el mando.
2. Abre la app. Espera a ver `MSDK: App Key registrada` y `Dron: conectado`.
3. Marca **Modo simulador** y espera a ver `Simulador: SÍ`. Los motores no arrancan en el simulador.
4. Pulsa **DESPEGAR A 2 m**. La fase debe pasar por
   `TAKING_OFF → CLIMBING → HOLDING`, con la altura subiendo hasta 2,00 m ± 0,15 m.
5. Pulsa **ATERRIZAR**. La fase pasa a `LANDING` y después a `ON_GROUND`.
6. Repite la prueba 3 veces. Prueba también el botón de pausa del mando durante la subida: la fase
   debe pasar a `MANUAL`.

**Después, de verdad:**

- Desmarca el simulador.
- Necesitas un espacio despejado con al menos 4 m de altura libre, sin personas debajo y con el
  suelo con textura (los sensores de abajo lo necesitan en interior).
- El piloto debe tener el mando en la mano, listo para el botón de pausa o para aterrizar a mano.

Repite los pasos 4 y 5.

## 5. Si algo falla

| Síntoma | Causa probable |
| --- | --- |
| `Fallo al registrar la App Key` | Falta `dji.appKey`, la App Key es de otro nombre de paquete o el mando no tiene internet |
| `Dron: NO conectado` | DJI Fly sigue abierto, o el dron está apagado o sin emparejar (emparejar con DJI Fly) |
| Error «binding device mismatch» | Se cambió de mando: vuelve a emparejar con DJI Fly |
| `Despegue falló: …` | Brújula sin calibrar, batería baja, motores bloqueados o zona restringida; lee el mensaje |
| Se queda en `CLIMBING` y aterriza a los 15 s | El dron no acepta la altura por joysticks virtuales: revisar en el simulador el modo `VerticalControlMode.POSITION` |
| Mensajes del MSDK | En el PC: `adb logcat -s PruebaDespegue` |
| La app se cierra nada más abrirla (`NoClassDefFoundError` en `adb logcat -b crash`) | Se compiló como depurable: el protector del MSDK no arranca así. `app/build.gradle.kts` debe tener `debug { isDebuggable = false }` |
