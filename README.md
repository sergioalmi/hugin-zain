# Hugin-Zain · proyecto de drones

{{RELLENAR: qué es, para quién y quién lo hace, en 2–3 líneas.}}

## Documentación

| Documento | Contenido |
| --- | --- |
| [`CLAUDE.md`](CLAUDE.md) | Instrucciones permanentes para Claude Code (y resumen para personas) |
| [`AGENTS.md`](AGENTS.md) | Reglas de código para cualquier agente (Ponytail adaptado) |
| [`docs/01_requisitos.md`](docs/01_requisitos.md) | Objetivos, entregables, indicadores y fechas |
| [`docs/02_arquitectura.md`](docs/02_arquitectura.md) | Piezas, protocolos, estados y presupuestos |
| [`docs/03_stack_verificado.md`](docs/03_stack_verificado.md) | Tecnologías, versiones, licencias y descartes |
| [`docs/04_entorno_y_rendimiento.md`](docs/04_entorno_y_rendimiento.md) | Máquinas, recursos y mediciones |
| [`docs/05_plan_y_fases.md`](docs/05_plan_y_fases.md) | Plan, tareas y criterios de terminado |
| [`docs/06_decisiones.md`](docs/06_decisiones.md) | Decisiones de diseño (ADR) y dudas abiertas |
| [`docs/07_privacidad_y_seguridad.md`](docs/07_privacidad_y_seguridad.md) | Datos, normativa y riesgos |
| [`docs/08_despliegue.md`](docs/08_despliegue.md) | Instalación en destino |
| [`docs/09_como_arrancar.md`](docs/09_como_arrancar.md) | **Cómo arrancar y usar todo**: comandos, configuración, planes B y averías |

## Puesta en marcha (PC de desarrollo)

Para **arrancar y usar** el proyecto una vez instalado: [`docs/09_como_arrancar.md`](docs/09_como_arrancar.md).

1. Instala: Git, [GitHub CLI](https://cli.github.com/) (`gh`),
   [Claude Code](https://code.claude.com/docs/en/setup), [Node.js LTS](https://nodejs.org/)
   (solo para los hooks del plugin Ponytail) y {{RELLENAR: lenguajes y herramientas del stack}}.
2. Clona el repositorio (pide acceso a `sergioalmi`): `gh auth login --web`, `gh auth setup-git`,
   `gh repo clone sergioalmi/hugin-zain` y, dentro, tu firma de commits:
   `git config user.name "<tu usuario>"` y `git config user.email "<id>+<usuario>@users.noreply.github.com"`.
3. Al abrir la carpeta en Claude Code y confiar en ella, acepta instalar los plugins Ponytail y
   frontend-design (declarados en `.claude/settings.json`). Compruébalo con `/plugin`.
4. Copia `config/config.example.yaml` a `config/config.yaml` y `.env.example` a `.env`.
   {{RELLENAR: variables de entorno y pasos propios del stack}}.
5. Abre una terminal aquí y ejecuta `claude`. En la primera sesión usa el modo plan
   (`Shift+Tab`) y el prompt de la Fase 0 de `docs/05_plan_y_fases.md`.

## Trabajo con Claude Code

- `CLAUDE.md` se carga en cada sesión (e importa `AGENTS.md`); `.claude/rules/` añade reglas al
  tocar ciertos archivos; `docs/` se lee cuando la tarea lo pide.
- Herramientas del equipo: `/grill-me` para especificar antes de planificar, `/ponytail-review`
  antes de cada commit, la skill frontend-design al diseñar interfaz (ver `CLAUDE.md`). Claude las
  usa también por iniciativa propia.
- `.claude/settings.json` define permisos compartidos: tests, lint y git local sin preguntar;
  añadir dependencias, `push` y borrados con confirmación; prohibido el push forzado y leer `.env`
  o `data/` con las herramientas de archivos. Son una guía, no una barrera de seguridad.
  Las preferencias personales van en `CLAUDE.local.md` o `.claude/settings.local.json` (ignorados).
- Comprueba qué ha cargado con `/context`.
- Una tarea por sesión; `/clear` entre tareas distintas (Claude lo propone cuando toca).

## Licencia

{{RELLENAR}}
