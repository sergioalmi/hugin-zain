# Plantilla de proyecto con Claude Code · cómo usarla

Esqueleto para empezar un proyecto nuevo con la misma forma de trabajar que funcionó en otro:
`CLAUDE.md` como contrato con Claude, `AGENTS.md` (Ponytail adaptado), plugins y skills que Claude
usa por iniciativa propia, documentación numerada en `docs/`, flujo incidencia → rama → PR, ADR
para cada decisión y una guía de arranque que se mantiene al día en cada PR.

**Este archivo se borra cuando el proyecto ya está rellenado.**

## Qué hay

| Archivo / carpeta | Qué es | Qué hay que hacer |
| --- | --- | --- |
| `CLAUDE.md` | Instrucciones permanentes: reglas no negociables, arquitectura, stack, comandos, forma de trabajar, contexto, plugins | Rellenar los `{{RELLENAR}}`. Las secciones sin marcas son genéricas y valen tal cual |
| `AGENTS.md` | Reglas de código «senior vago» (Ponytail) para cualquier agente | Rellenar «Lo que SÍ se ha pedido», «Tests» y «Nunca recortar» |
| `.claude/settings.json` | Plugins (Ponytail, frontend-design) y permisos compartidos | Añadir los comandos de test y lint del proyecto a `allow` (ver abajo) |
| `.claude/rules/` | Reglas que se cargan solo al tocar ciertos archivos | Copiar `_plantilla-regla.md` por lenguaje o pieza; `documentacion.md` vale tal cual |
| `.claude/skills/` | `/grill-me` y `grilling` (entrevista antes de planificar; MIT, ver `TERCEROS.md`) | Nada |
| `.github/pull_request_template.md` | Lista de comprobación de cada PR | Ajustar las casillas si el proyecto pide otras |
| `docs/01`–`09` | Requisitos, arquitectura, stack, entorno, plan, decisiones, privacidad, despliegue, guía de arranque | Rellenar a medida que se decide; empezar por 01, 05 y 06 |
| `docs/specs/_plantilla.md` | Formato de las especificaciones que salen de `/grill-me` | Nada (Claude la copia) |
| `config/config.example.yaml` | Única fuente de configuración (esquema versionado) | Rellenar con las secciones del proyecto |
| `.gitignore`, `.gitattributes`, `.env.example` | Secretos, datos y descargas fuera de git; finales de línea | Añadir lo propio del stack |
| `README.md` | Puesta en marcha para personas | Rellenar |

Busca lo pendiente con: `git grep -n "RELLENAR"` (o la búsqueda del editor).

## Pasos

1. **Requisitos previos** en el PC: Git, [GitHub CLI](https://cli.github.com/) (`gh`),
   [Claude Code](https://code.claude.com/docs/en/setup) y [Node.js LTS](https://nodejs.org/)
   (solo lo usan los hooks del plugin Ponytail).
2. **Copia la plantilla** a la carpeta del proyecto nuevo (descomprime el .zip y renombra la
   carpeta). Comprueba que se copiaron las carpetas ocultas `.claude/` y `.github/` y los archivos
   `.gitignore`, `.gitattributes` y `.env.example`.
3. **Repositorio** (desde la carpeta):

   ```bash
   git init -b main
   git add -A
   git commit -m "chore: esqueleto del proyecto desde la plantilla"
   gh repo create <usuario>/<proyecto> --private --source . --push
   ```

   En GitHub → Settings: en *General → Pull Requests* desmarca «Allow squash merging»; en
   *Rules → Rulesets* (o *Branches*) protege `main` contra borrado y push forzado. Crea un hito
   por fase (*Issues → Milestones*).
4. **Abre Claude Code** en la carpeta y **confía en ella**: te ofrecerá instalar los plugins
   declarados (Ponytail y frontend-design). Acepta y comprueba con `/plugin` y `/ponytail-help`.
5. **Rellena con Claude.** Si tienes documentos originales (enunciado, ficha, diseños) fuera del
   repositorio, añádelos primero con `/add-dir "<ruta>"`. Después, en modo plan:

   > Lee PLANTILLA_LEEME.md, CLAUDE.md y docs/. Entrevístame con /grill-me para rellenar todos
   > los {{RELLENAR}} del proyecto, empezando por CLAUDE.md (reglas no negociables, arquitectura,
   > stack) y siguiendo por docs/01, 05 y 06. Busca antes en los documentos originales lo que ya
   > esté decidido. No escribas código.

6. **Permisos de los comandos del proyecto.** Cuando existan los comandos de test y lint, añádelos a
   `allow` en `.claude/settings.json` (en `Bash(...)` y `PowerShell(...)` si usas Windows), y los
   que añaden dependencias a `ask`. Ejemplos: `"Bash(uv run --project <pieza> pytest *)"`,
   `"Bash(npm test *)"`, `"Bash(uv add *)"` en `ask`, `"Bash(npm install *)"` en `ask`. Si el
   proyecto descarga modelos o binarios a `models/` o `bin/`, añade `Edit(./models/**)` a `deny`.
7. **Reglas por lenguaje:** copia `.claude/rules/_plantilla-regla.md` a `python.md`, `web.md`…
   con su `paths`, y bórrala.
8. Borra este archivo, `git grep RELLENAR` debe salir vacío, y primer commit `docs:`.

## Cómo se trabaja después (resumen; el detalle está en `CLAUDE.md`)

- Cada tarea: incidencia con hito → rama `feat/<tema>` → PR con la plantilla y `Closes #n` →
  fusión con merge commit. Claude revisa con `/ponytail-review` antes de cada commit y la guía de
  arranque antes de cada PR.
- Funcionalidad grande o decisión con ADR: `/grill-me` → `docs/specs/<nombre>.md` → modo plan.
- Al cambiar de funcionalidad, Claude deja un traspaso en la incidencia siguiente y propone
  `/clear`; dentro de una funcionalidad larga, propone `/compact` con foco.
- Al cerrar una fase: `/ponytail-audit` y `/ponytail-debt` → `docs/seguimiento/`.

## Avisos prácticos

- **Fusionar PR con Claude en modo automático:** el clasificador de permisos bloquea
  `gh pr merge` como «fusión sin revisión» aunque se pida en el chat. O fusionas tú (botón «Create
  a merge commit» o `gh pr merge <n> --merge`), o usas otro modo de permisos y apruebas el aviso,
  o añades `Bash(gh pr merge *)` a `allow` en `.claude/settings.local.json` (solo tu PC).
- **Windows y la app de escritorio de Claude:** lo que se instala en `AppData` desde la app puede
  quedar virtualizado. Si usas gestores que guardan cachés ahí (uv, pip, npm), apunta sus cachés y
  entornos a `%USERPROFILE%\.<herramienta>\` con variables de entorno de usuario.
- **PowerShell 5.1** (el de Windows): `Start-Process` no entrecomilla argumentos (rutas con
  espacios) y estropea las comillas al pasar código a programas externos; guarda los `.ps1` con
  BOM UTF-8 si llevan tildes.
- La memoria personal de Claude (`~/.claude/projects/...`) no viaja con la plantilla: lo que deba
  saber cualquier sesión del proyecto va en `CLAUDE.md`, `.claude/rules/` o `docs/`.
