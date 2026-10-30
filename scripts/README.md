# scripts/

Instalación, descargas y arranque. **Es lo único del proyecto que puede usar internet** (descargas),
y lo único que el equipo lanza con doble clic.

- `download_*.ps1` / `.sh`: descargan a `models/` o `bin/` y comprueban el SHA-256; se pueden
  relanzar (reanudan y saltan lo verificado). Origen y licencia de cada descarga, en el README de
  su carpeta.
- `start.ps1` (envuelto por `start.bat` en la raíz): valida la configuración, arranca todas las
  piezas en orden en ventanas propias, espera a que cada una responda y salta las que ya están en
  marcha. Se documenta en `docs/09_como_arrancar.md`.
