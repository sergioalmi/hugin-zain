---
paths:
  - "docs/**"
  - "README.md"
  - "CLAUDE.md"
  - "AGENTS.md"
---

# Reglas para la documentación

- En castellano, frases cortas, datos concretos (fechas absolutas `DD/MM/AAAA`, cifras con unidad,
  versiones exactas). Nada de «pronto», «ayer» ni «la semana que viene».
- Cada cifra de rendimiento o calidad dice **cuándo, dónde y cómo** se midió (máquina, comando,
  número de repeticiones). Lo no medido se marca como estimación.
- `docs/06_decisiones.md`: una ADR por decisión, la más reciente al final; nunca se borra una ADR,
  se sustituye con otra nueva («Sustituye a ADR-00X»). Contexto, decisión, lo medido y consecuencias.
- `docs/05_plan_y_fases.md`: al terminar una tarea, márcala `[x]` con fecha, incidencia y la cifra
  clave entre paréntesis. Al cambiar de fase, actualiza la línea «Fase actual» de `CLAUDE.md`.
- `docs/09_como_arrancar.md` es para personas, no para agentes: pasos que se pueden copiar y pegar,
  qué debe verse al hacerlos y qué hacer si falla. Se revisa contra el diff antes de cada PR.
- `docs/specs/<nombre>.md`: salen de `/grill-me`; recogen alcance, fuera de alcance, planes B y
  criterio de terminado. Si el alcance cambia, se cambia aquí primero.
- `CLAUDE.md` se carga en cada sesión: cada línea cuesta contexto. Lo que solo importa a veces va a
  `docs/` o a `.claude/rules/`; en `CLAUDE.md`, el enlace.
- Textos dirigidos a la persona usuaria en otros idiomas que el equipo no domina: márcalos con
  `REVISAR-<idioma>` hasta que los revise un hablante nativo, y anota quién y cuándo los revisó.
