# AGENTS.md · Ponytail adaptado al proyecto

Reglas de código para cualquier agente (Claude Code, Codex, Cursor, Copilot…). Basado en el
`AGENTS.md` de [Ponytail](https://github.com/DietrichGebert/ponytail) (MIT, Dietrich Gebert),
adaptado a este proyecto. Claude Code lo carga desde `CLAUDE.md`; otros agentes deben leer
también `CLAUDE.md`, que contiene las reglas no negociables del proyecto.

**Prioridad:** `CLAUDE.md`, `.claude/rules/` y `docs/` mandan sobre este archivo. Si el plugin
Ponytail está instalado y algo choca con lo de aquí, vale lo de aquí.

## Modo «senior vago»

El mejor código es el que no se escribe. Resuelve el problema entero con el mínimo código nuevo.
Termina cada respuesta con una o dos líneas: qué no hiciste o no comprobaste y qué riesgo debe
conocer la persona.

## Antes de escribir

Lee la tarea y el código que toca. Lista todo lo que el cambio debe alcanzar: llamadas, tests,
fixtures (y su espejo en otras piezas si hay un protocolo compartido), configuración
(`config.example.yaml` + su modelo de validación), exportaciones y documentación de `docs/`
(incluida la guía de arranque). Comprueba qué puede romper: datos de `data/` que destruiría o
expondría, mensajes entre piezas que dejan de encajar, comandos que la guía da por buenos. Eso es
el alcance; las funciones extra no.

## El cambio completo más pequeño

Toma la primera opción que funcione del todo:

1. ¿Hace falta? Omite funciones, opciones y flexibilidad que nadie ha pedido y nómbralas en una línea.
2. ¿Ya existe en el repositorio (un helper, una interfaz, un componente, un patrón)? Úsalo como el código de alrededor.
3. ¿Biblioteca estándar o función de la plataforma? Úsala.
4. ¿Una dependencia ya instalada? Úsala. **Nunca** añadas un paquete sin preguntar (ver `CLAUDE.md`).
5. ¿Cabe en una línea que se entiende de un vistazo? Una línea.
6. Si no: el mínimo código que funcione.

- Vago con la solución, nunca con el cambio: termina todo lo que la tarea necesita, incluidos
  llamadas, tests y fixtures que tu cambio rompe.
- Sin abstracciones, envoltorios, opciones, configuración ni código «para más adelante» que nadie
  ha pedido. Borrar gana a añadir. Respeta la estructura existente: capas, interfaces y convenciones.
- El diff más corto que funciona gana, una vez sabes todo lo que debe tocar. Una línea que hay que
  descifrar no es corta.
- Comenta solo el porqué que el código no muestra, en una línea y en castellano.
- Corrección de errores: antes de editar, busca todas las llamadas a la función y arregla la causa
  una sola vez en el código compartido.
- El código que mueves o fusionas conserva su manejo de errores y su validación.
- Entre dos opciones del mismo tamaño, la que acierta en los casos límite.
- Un atajo con un límite conocido lleva un comentario con este formato (el prefijo en inglés, para
  que `/ponytail-debt` lo encuentre): `shortcut: <el límite>, <cuándo mejorarlo>`.

## Lo que en este proyecto SÍ se ha pedido (no es abstracción de más)

Lo que Ponytail tomaría por exceso, pero el proyecto exige. Sustituye los ejemplos:

- Una interfaz (p. ej. `typing.Protocol`, interfaz de C#/TS) con doble de prueba por cada
  componente lento, caro o externo ({{RELLENAR: cuáles}}): son la forma de probar sin él.
- Validación tipada de configuración y de mensajes entre piezas ({{RELLENAR: Pydantic, zod…}});
  tipado en funciones públicas; docstrings breves en castellano.
- Configuración solo desde `config/config.yaml`: rutas, puertos, idiomas y dispositivos no se
  fijan en el código.
- {{RELLENAR o BORRAR: textos visibles para la persona usuaria en los idiomas <x, y, z>.}}

## Tests

Lo vago sin comprobar está sin terminar. **La regla del mínimo se aplica al código, nunca a las
pruebas:** la lógica nueva lleva tests completos (normales, límite y error) y, cuando aporte, una
prueba real en un entorno aislado (ver «Calidad sin escatimar» en `CLAUDE.md`). Un test que
protege un arreglo debe fallar sin él. Además, el proyecto exige siempre: {{RELLENAR: tests y
linter en verde de cada pieza; compilación limpia}}. {{RELLENAR o BORRAR: los cambios en el
camino crítico se miden con `tools/<medición>/`.}}

## Nunca recortar

Validación en las fronteras de confianza (red, configuración, entradas de la persona); manejo de
errores que evita perder datos; seguridad y privacidad (nada personal en los registros);
{{RELLENAR: la lógica crítica que debe ser determinista y probada, obligaciones legales del
producto}}; accesibilidad para el público del producto; la calibración que pide el hardware o el
entorno real; y todo lo que la persona haya pedido.
