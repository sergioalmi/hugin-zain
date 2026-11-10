# 06 · Registro de decisiones (ADR)

Formato: una entrada por decisión, la más reciente al final. Para cambiar una decisión no se
borra: se añade una nueva que la sustituye («Sustituye a ADR-00X») y se cambia el estado de la
antigua. Una decisión que se apoya en datos los incluye en «Medido».

```
## ADR-0XX · Título
- Fecha: AAAA-MM-DD · Estado: propuesta | aceptada (quién, cuándo) | sustituida por ADR-0YY
- Contexto: qué problema hay
- Decisión: qué se hace
- Medido: qué se probó, con qué, cuántas veces y el resultado (si aplica)
- Consecuencias: qué implica, qué se descarta, qué hay que vigilar
```

## ADR-001 · Qué modelo de Claude hace cada trabajo
- Fecha: 2026-11-10 · Estado: propuesta (pendiente de aceptar por el equipo)
- Contexto: el 09/11/2026 salió Fable 5.2. La guía de Anthropic «Which Claude model should you
  build with?» (09/11/2026) recomienda Opus 5.5 en `medium` como punto de partida; Fable 5.2 para
  lo más difícil (planificación de arquitectura, investigación a fondo, refactorizaciones grandes);
  modelos pequeños solo para trabajo fácil de comprobar (buscar archivos, ejecutar tests, leer
  logs). Precio por millón de tokens de entrada/salida: Fable 5.2 8/40 $, Opus 5.5 4/20 $,
  Haiku 5.5 0,10/0,50 $ (hasta 100K).
- Decisión: sesión principal con Opus 5.5 `medium` (`.claude/settings.json`), que implementa y
  escribe los tests. Agentes en `.claude/agents/`: `Plan`, `investigador` y `revisor` con Fable 5.2
  `high`; `Explore` y `pruebas` con Haiku 5.5. Escalado: comprobación automática → `/effort high`
  → Fable; nunca Opus en `xhigh`/`max` (la guía mide que `xhigh` da +1,4 puntos por 2,5 veces el
  coste). Resumen operativo en `CLAUDE.md`, «Modelos y esfuerzo».
- Medido: nada aún. La guía avisa de que repartir «planifica el fuerte, implementa el otro» hay
  que medirlo antes de darlo por bueno.
- Consecuencias: el paso de Fable a Opus pierde el razonamiento y la caché, así que el plan y las
  conclusiones se escriben en archivo. Hay que medir las 3 primeras incidencias con `/usage`
  (coste y si el `revisor` encontró fallos reales) y anotarlo aquí; si Fable no aporta, se vuelve
  a Opus `high` para revisar y planificar con una ADR nueva.

## Dudas abiertas

| Pregunta | Estado a {{RELLENAR: fecha}} | Cómo se trabaja mientras tanto |
| --- | --- | --- |
| {{RELLENAR}} | Sin respuesta (preguntar a …) | {{RELLENAR: configurable, interfaz con dos implementaciones…}} |
