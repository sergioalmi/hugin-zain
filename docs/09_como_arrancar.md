# Cómo arrancar Hugin-Zain

Guía práctica para poner en marcha todo lo hecho hasta ahora (estado a {{RELLENAR: fecha}}). Está
pensada para quien olvidó cómo se hacía o llega nuevo al proyecto: pasos que se copian y pegan,
qué debe verse al hacerlos y qué hacer si falla. **Se revisa contra el diff antes de cada PR**
(`CLAUDE.md`, «Cómo trabajamos»).
Si es la primera vez en un PC nuevo, haz antes la instalación del `README.md`.
Todos los comandos se lanzan **desde la raíz del repositorio**.

## 1. Las piezas

| # | Pieza | Qué hace | Dónde escucha |
| --- | --- | --- | --- |
| 1 | {{RELLENAR}} | {{RELLENAR}} | {{RELLENAR}} |

Todas leen el mismo archivo `config/config.yaml`.

## 2. Una sola vez en cada PC

1. {{RELLENAR: descargas (modelos, binarios) con su script}}
2. {{RELLENAR: instalar dependencias de cada pieza}}
3. **Configuración:** copia `config/config.example.yaml` a `config/config.yaml` y ajusta lo de la
   sección 5. `config.yaml` no se sube a git.
4. {{RELLENAR: lo que no está en git y se copia a mano, compilaciones}}

## 3. Arrancar (cada vez)

{{RELLENAR: el comando o doble clic que lo arranca todo, qué hace (orden, esperas, lo que salta si
ya está en marcha), sus opciones y cómo se para. Después, «A mano, una terminal por pieza», con el
mensaje de «listo» que hay que esperar en cada una.}}

### Comprobar que todo responde

```
{{RELLENAR: comprobaciones de salud (URL /health, puertos, consumo de recursos)}}
```

## 4. Usar el producto

| Acción | Qué pasa |
| --- | --- |
| {{RELLENAR}} | {{RELLENAR}} |

## 5. `config/config.yaml`: qué se puede cambiar

Tras cambiar algo, reinicia las piezas que leen esa sección (columna «Lo lee»).

### Lo que se toca de verdad

| Opción | Valores | Lo lee | Para qué |
| --- | --- | --- | --- |
| {{RELLENAR}} | {{RELLENAR}} | {{RELLENAR}} | {{RELLENAR}} |

### Lo que no hay que tocar (o aún no hace nada)

- {{RELLENAR: opciones reservadas para fases futuras, las que rompen la compatibilidad entre piezas}}

## 6. Planes B

| Problema | Qué hacer |
| --- | --- |
| {{RELLENAR}} | {{RELLENAR}} |

## 7. Herramientas de prueba

| Para | Comando |
| --- | --- |
| Probar el despegue a 2 m del Mini 3 Pro (botón en el DJI RC Pro) | Ver `tools/prueba-despegue/README.md`: instalación, App Key, compilar, instalar en el mando y procedimiento (primero en simulador) |

## 8. Si algo falla

| Síntoma | Causa probable |
| --- | --- |
| {{RELLENAR}} | {{RELLENAR}} |
