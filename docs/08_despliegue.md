# 08 · Despliegue

## Destino

{{RELLENAR: dónde y en qué se instala (máquina, servidor, dispositivo), con qué restricciones
(sin internet, pantalla, permisos) y quién lo mantiene después.}}

## Paquete de entrega

```
{{RELLENAR: qué carpetas y archivos componen la instalación, p. ej.:}}
  <pieza>/          compilado o con su entorno (sin instalar nada a mano)
  config/config.yaml
  data/             vacío al instalar
  scripts/start.ps1 arranca todas las piezas en orden y espera a cada una
  start.bat         solo llama a scripts/start.ps1 (doble clic o inicio del sistema)
```

## Lista para el día de la instalación

- [ ] {{RELLENAR: comprobaciones en destino (red, permisos, calibraciones, prueba sin internet si aplica)}}
- [ ] Arranque desde cero con `start.bat` y prueba del caso de uso principal
- [ ] Guía `docs/09_como_arrancar.md` entregada a quien lo mantendrá

## Reglas visuales o de plataforma

{{RELLENAR o BORRAR: lo que impone el dispositivo de destino (resolución, contraste, zonas
seguras) y que manda sobre la estética.}}
