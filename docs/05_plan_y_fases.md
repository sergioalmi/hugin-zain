# 05 · Plan y fases

Cada fase termina con algo que se puede **enseñar funcionando**. Marca las tareas al completarlas
(`[x]` con fecha, incidencia y la cifra clave) y actualiza la línea «Fase actual» de `CLAUDE.md`
al cambiar de fase. Cada fase es un hito de GitHub; cada tarea grande, una incidencia con ese hito.

| Fase | Fechas | Objetivos / acciones (`01_requisitos.md`) | Resultado visible |
| --- | --- | --- | --- |
| 0 · Arranque | {{RELLENAR}} | {{RELLENAR}} | Repositorio, entornos, decisiones de stack con datos, primera pieza funcionando |
| 1 · {{RELLENAR}} | {{RELLENAR}} | {{RELLENAR}} | {{RELLENAR}} |
| N · Cierre | {{RELLENAR}} | Entrega y difusión | {{RELLENAR: paquete instalado, vídeo, memoria}} |

Hitos de seguimiento: {{RELLENAR: fechas}}.

## Fase 0 · Arranque ({{RELLENAR: fechas}})

- [x] Instalar en el PC de desarrollo: Git, `gh` (CLI de GitHub), Claude Code, Node.js LTS (para
      los hooks del plugin Ponytail) (30/10/2026: Git 2.56.0, gh 2.103.3, Node.js 26.13.0 con winget)
- [ ] Instalar {{RELLENAR: lenguajes, gestores y herramientas del stack}}
- [ ] Plugins de Claude Code: aceptar Ponytail y frontend-design al confiar en la carpeta (o
      `/plugin install`); comprobar con `/plugin` y `/ponytail-help`
- [ ] Crear el repositorio en GitHub, proteger `main` (sin borrado ni push forzado), desactivar
      «squash merge», crear un hito por fase y subir este esqueleto
- [ ] Rellenar los `{{RELLENAR}}` de `CLAUDE.md`, `AGENTS.md` y `docs/` (sesión con `/grill-me`)
- [ ] Crear cada pieza con su gestor, tests y linter configurados, y `config/config.example.yaml`
      validado por un modelo tipado con tests
- [ ] {{RELLENAR: pruebas para decidir el stack con datos (ADR)}}
- [ ] Script de arranque (`scripts/start.ps1` o equivalente) y guía `docs/09_como_arrancar.md`

**Terminado cuando:** {{RELLENAR: criterio comprobable}}.

## Fase 1 · {{RELLENAR}}

- [ ] {{RELLENAR}}

**Terminado cuando:** {{RELLENAR}}.

## Registro para los seguimientos

Lleva en `docs/seguimiento/` un archivo por seguimiento con: tareas cerradas, indicadores
alcanzados, riesgos materializados, horas, personas implicadas y materiales generados. Al cerrar
cada fase: `/ponytail-audit` y `/ponytail-debt`, resumidos en ese archivo.

## Prompts sugeridos para Claude Code

Primera sesión (rellenar la plantilla):

> Lee PLANTILLA_LEEME.md, CLAUDE.md y docs/. Entrevístame con /grill-me para rellenar todos los
> {{RELLENAR}} del proyecto, empezando por CLAUDE.md. Busca antes en los documentos originales lo
> que ya esté decidido. No escribas código.

Primera tarea de código (modo plan):

> Lee CLAUDE.md y docs/05_plan_y_fases.md. Estamos en la Fase 0. Propón un plan para <tarea>,
> con sus tests. No escribas código hasta que apruebe el plan.

Funcionalidades grandes: `/grill-me` para que Claude te entreviste y escriba una especificación
(`docs/specs/<nombre>.md`) y después implementa en una sesión nueva con esa especificación.
