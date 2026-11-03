# Hugin-Zain · proyecto de drones

{{RELLENAR: qué es el proyecto en 2–4 líneas: qué hace, para quién y dónde funciona. Quién lo
encarga o financia y las fechas de inicio y fin, si las hay.}}
Equipo: {{RELLENAR: personas, rol y usuario de GitHub}}. Repositorio: https://github.com/sergioalmi/hugin-zain (privado)
(incidencias con hito por fase; `main` sin borrado ni push forzado; sin «squash merge»).

**Fase actual: 0 · Arranque** (ver `docs/05_plan_y_fases.md`). Actualiza esta línea al cambiar de fase.
{{RELLENAR o BORRAR: **Prioridad absoluta hasta el <fecha>: <entrega>** (`docs/specs/<entrega>.md`,
ADR-00X). Ante cualquier duda de alcance, gana lo que la acerque a esa entrega.}}

## Reglas no negociables

Lo que nunca se negocia por comodidad, plazo o rendimiento. Cada regla, en una línea clara y
comprobable; las más graves con **IMPORTANT:**, que Claude respeta con más fuerza. Ejemplos del
tipo de regla que va aquí (sustitúyelos por las del proyecto):

- {{RELLENAR: **IMPORTANT:** restricción técnica de base (p. ej. «sin internet en ejecución»,
  «todo servicio escucha en 127.0.0.1», «compatible con el navegador X»).}}
- {{RELLENAR: **Privacidad / datos:** qué datos personales hay, dónde viven (`data/`) y qué no se
  guarda nunca (registros sin datos personales).}}
- {{RELLENAR: **Normativa:** obligaciones legales del producto (RGPD, accesibilidad, Ley de IA…).}}
- {{RELLENAR: **Seguridad de la persona usuaria o del sistema:** lo que el software nunca hace y
  qué lógica debe ser determinista y probada.}}
- {{RELLENAR: **Trampas conocidas de la tecnología:** APIs obsoletas que no hay que copiar de
  tutoriales, opciones que hay que desactivar, versiones prohibidas.}}
- {{RELLENAR: **Objetivo de rendimiento** medible (latencia, memoria, tiempo de carga).}}
  Toda optimización se justifica con una medición, no con una suposición.

## Arquitectura

{{RELLENAR: las piezas (programas, servicios, procesos), qué hace cada una, dónde corre y cómo se
comunican (protocolo, formato, puertos). Una línea por pieza, con su carpeta.}}

Protocolos, estados y presupuestos (de tiempo, memoria…): `docs/02_arquitectura.md`.
Cambiar un protocolo entre piezas exige subir su versión y documentarlo en `docs/06_decisiones.md`.

## Stack decidido (verificado el {{RELLENAR: fecha}})

| Pieza | Elección | Dónde corre |
| --- | --- | --- |
| {{RELLENAR}} | {{RELLENAR: tecnología y versión}} | {{RELLENAR}} |

Detalle, versiones, licencias y alternativas descartadas (con el motivo): `docs/03_stack_verificado.md`.

## Estructura del repositorio

```
{{RELLENAR: una carpeta por pieza o proyecto}}/   Código de cada pieza, con sus tests al lado
  tests/          Tests; datos de prueba en tests/fixtures/
tools/            Herramientas de desarrollo: medición, simuladores, dobles de piezas externas
scripts/          Instalación, descargas y arranque (lo único que puede usar internet)
config/           config.example.yaml (versionado) → config.yaml (local, ignorado)
data/  logs/      Datos personales y registros (ignorados por git)
docs/             Documentación de referencia (leer bajo demanda)
  specs/          Especificaciones de funcionalidades (salen de /grill-me)
  seguimiento/    Informes de cada hito (auditoría y deuda)
.claude/          Configuración compartida de Claude Code: permisos, plugins, reglas y skills
.github/          Plantilla de PR
```

Si una carpeta aún no existe, créala siguiendo esta estructura (la Fase 0 explica cómo).

## Comandos

Desde la raíz del repositorio. {{RELLENAR: en qué terminales funcionan (PowerShell, Bash…).}}

- Entornos: {{RELLENAR: comando para instalar dependencias de cada pieza}}
- Tests: {{RELLENAR: todos}} · uno: {{RELLENAR: un solo archivo}}
- Lint y formato: {{RELLENAR}}
- Arrancar cada pieza: {{RELLENAR}}
- Todo junto: {{RELLENAR: script de arranque, p. ej. `scripts/start.ps1` envuelto por `start.bat`}}
- Herramientas: {{RELLENAR: `tools/<herramienta>/…`, una línea por herramienta estable}}
  - Prueba de despegue a 2 m (Android, Git Bash): `bash scripts/instalar_android.sh` (una vez) y
    `export JAVA_HOME="$LOCALAPPDATA/hugin-zain-toolchain/jdk"; cd tools/prueba-despegue && ./gradlew :app:testDebugUnitTest :app:lintDebug :app:assembleDebug`

Cuando añadas un comando nuevo y estable, apúntalo aquí (y en la guía de arranque si lo usa una persona).

## Cómo trabajamos

- **Explorar → planificar → implementar → verificar.** Usa el modo plan para cambios que toquen
  varios archivos o varias piezas. Si el cambio cabe en una frase, hazlo directamente.
- **IMPORTANT: Calidad sin escatimar.** El resultado debe ser excelente: no ahorres tokens, tiempo
  ni pruebas. Prueba todo lo que hagas con tests automáticos completos (casos normales, límite y de
  error) y, cuando aporte, también de verdad en un entorno aislado que elijas tú (servidor real en
  un puerto efímero, cliente de prueba, carpeta temporal, ruta con espacios y tildes, procesos en
  segundo plano…). Si algo se puede comprobar a la vista, di a la persona exactamente cómo: qué
  comando ejecutar y qué debe ver. Ponytail recorta código, nunca verificación.
- **Verificación obligatoria:** todo cambio termina con tests y linter en verde
  ({{RELLENAR: qué exige cada pieza: compilar sin avisos, tests de interfaz…}}). Muestra la
  salida, no la resumas. Un test nuevo que corrige un fallo debe fallar sin el arreglo: compruébalo.
- **Prueba sin dependencias externas:** cada módulo se puede ejecutar con `tests/fixtures/` y
  dobles de prueba de lo que es lento, caro o externo ({{RELLENAR: modelos, APIs, hardware,
  bases de datos}}). Los tests unitarios no dependen de red, hardware ni servicios arrancados.
- **Diagnóstico con datos:** ante un fallo, primero reprodúcelo y mídelo; descarta hipótesis con
  pruebas, no con razonamientos. Cuenta a la persona lo que descartaste y cómo.
- **Rendimiento:** {{RELLENAR o BORRAR: todo cambio en el camino crítico se mide con
  `tools/<medición>/` y el resultado se anota en el commit.}}
- **Dependencias:** no añadas paquetes sin preguntar. Comprueba licencia ({{RELLENAR: licencias
  admitidas y prohibidas}}), soporte de la plataforma y de la versión del lenguaje, y que no
  arrastre dependencias pesadas sin necesidad.
- **Decisiones:** cada decisión de diseño se añade a `docs/06_decisiones.md` (formato ADR), con
  lo medido que la justifica.
- **Git:** commits pequeños y frecuentes, en castellano, con prefijo `feat:`, `fix:`, `docs:`,
  `test:`, `refactor:` o `chore:`, y cuerpo con qué se probó y lo medido. Rama principal `main`;
  una rama por funcionalidad. Nunca subas `data/`, `config/config.yaml`, `.env` ni archivos
  generados o descargados.
  Flujo: incidencia de GitHub (con hito de su fase) → rama `feat/<tema>` o `fix/<tema>` → PR con
  la plantilla de `.github/` y `Closes #n` (GitHub solo cierra con palabras clave en inglés; si el
  PR no termina la incidencia, «Parte de #n») → fusión con merge commit. Documentación menor,
  directa a `main`.
- **Antes de abrir cada PR, revisa `docs/09_como_arrancar.md`** (la guía de arranque para el
  equipo) contra el diff: comandos, orden de arranque, opciones de configuración (y si ya hacen
  algo o siguen reservadas), atajos, planes B, herramientas y averías. Si algo cambió, actualízala
  en el mismo PR; si no, márcalo como «no aplica» en la plantilla.
- **Si dudas sobre un requisito**, consulta `docs/01_requisitos.md` antes de preguntar.

## Gestión del contexto (`/clear` y `/compact`)

Claude no puede ejecutar estos comandos: **los detecta y los propone**, con el comando exacto en
un bloque al final de su respuesta, y la persona solo lo lanza. La unidad de trabajo es la
funcionalidad (una incidencia o un PR).

- **`/clear` al cambiar de funcionalidad:** cuando un PR se fusiona y lo siguiente es otra
  incidencia, o cuando la conversación cambia a un tema sin relación. Antes de proponerlo, Claude
  comprueba que no queda nada solo en la conversación: commits subidos, PR fusionado, decisiones en
  ADR, plan (`docs/05`) y especificación al día, y deja un **traspaso** como comentario en la
  incidencia siguiente (estado, pendientes, cifras medidas, avisos). Después da la frase con la que
  empezar la nueva sesión (p. ej. «Seguimos con #11: lee su traspaso y `docs/specs/<x>.md`»).
- **`/compact` dentro de una funcionalidad larga:** cuando la conversación ya acumula mucha
  depuración, salidas de tests o mediciones y queda trabajo de esa misma funcionalidad. Claude lo
  propone con foco: `/compact Conserva: <qué se está haciendo, archivos tocados, cifras, pendientes>`.
  Si lo que queda es independiente de lo hablado, propone `/clear` (el resumen pierde detalle).
- **Nunca** propone `/clear` con trabajo sin commit, pruebas a medias o decisiones sin registrar.
- La compactación automática puede ocurrir sola; para ella vale la regla de conservación de abajo.

## Plugins y skills: úsalos por iniciativa propia

Declarados en `.claude/settings.json` y `.claude/skills/`. **IMPORTANT:** invócalos tú con la
herramienta Skill cuando se cumpla su disparador; la persona no tiene que pedirlos. Si un plugin
no está instalado, aplica igualmente la regla que describe y avisa en una línea.

| Cuándo | Qué invocas tú | Resultado |
| --- | --- | --- |
| Al empezar cualquier tarea de código | `ponytail:ponytail` en nivel `full` (el que trae por defecto); `lite` si la tarea es solo documentación | Cambio mínimo completo; cierra con lo omitido y los riesgos |
| Antes de proponer o hacer un commit que incluya código | `ponytail:ponytail-review` sobre el diff | Corrige los fallos antes del commit; menciona el resto |
| Al cerrar una fase o un hito de seguimiento | `ponytail:ponytail-audit` y `ponytail:ponytail-debt` | Resumen en `docs/seguimiento/` |
| Al diseñar o rehacer interfaz (pantallas, componentes, estilos, materiales de difusión) | `frontend-design:frontend-design` | Dirección visual con los límites de abajo |
| Se pide una funcionalidad que toca varios módulos o varias piezas, o una decisión que merece ADR, y no existe su `docs/specs/<nombre>.md` | `grilling` (la versión de la persona es `/grill-me`) | Entrevista antes del modo plan |

- **Ponytail:** sus reglas, adaptadas al proyecto, se cargan siempre, aunque el plugin esté apagado:
  @AGENTS.md
- **frontend-design:** por encima de su estética mandan {{RELLENAR: las reglas visuales del
  producto (marca, plataforma, documento de diseño), la accesibilidad de su público (tamaño de
  letra, contraste, objetivos táctiles, nada que dependa solo del color) y las restricciones
  técnicas (p. ej. sin CDN ni Google Fonts si funciona sin internet: tipografías e iconos
  empaquetados)}}.
- **grilling:** pregunta en castellano; antes de preguntar, busca en `docs/` lo que ya está
  decidido. Al acabar, escribe `docs/specs/<nombre>.md` (y la ADR si toca) y no implementes hasta
  que la persona confirme. No la uses para tareas de una frase ni para correcciones.

## Convenciones de código

- Identificadores y código en inglés; comentarios, docstrings, documentación y commits en
  castellano. {{RELLENAR o BORRAR: textos que ve la persona usuaria: en los idiomas <x, y, z>.}}
- {{RELLENAR: por lenguaje: tipado, validación de configuración y mensajes, concurrencia,
  registros (nunca `print`, sin datos personales), rutas y codificación explícita.}}
- Configuración solo desde `config/config.yaml` (esquema en `config/config.example.yaml`); es la
  única fuente de rutas, puertos, idiomas y dispositivos. Nada de eso se fija en el código.
- El proyecto debe funcionar en una ruta con espacios y tildes.
- Detalles por lenguaje o carpeta en `.claude/rules/` (se cargan al tocar esos archivos).

## Entorno de desarrollo y despliegue (diseña para el más limitado)

{{RELLENAR o BORRAR: máquinas de desarrollo y de destino (CPU, memoria, GPU, sistema), el
presupuesto de recursos que no se puede pasar y cómo se mide.}}

## Dudas abiertas (no bloquean: trabaja con ambos casos)

- {{RELLENAR: pregunta pendiente → cómo se trabaja mientras tanto (configurable, interfaz con
  dos implementaciones…)}}

## Documentación de referencia (léela cuando la tarea lo pida)

- `docs/01_requisitos.md` · objetivos, entregables, indicadores y fechas comprometidas
- `docs/02_arquitectura.md` · piezas, protocolos, estados, flujo y presupuestos
- `docs/03_stack_verificado.md` · versiones, licencias, enlaces y alternativas descartadas
- `docs/04_entorno_y_rendimiento.md` · máquinas, recursos, cómo medir y lo medido
- `docs/05_plan_y_fases.md` · fases, tareas, criterios de terminado y prompts sugeridos
- `docs/06_decisiones.md` · registro de decisiones (ADR) y dudas abiertas
- `docs/07_privacidad_y_seguridad.md` · datos, normativa, riesgos y contenido
- `docs/08_despliegue.md` · instalación en destino y lista del día de la entrega
- `docs/09_como_arrancar.md` · cómo arrancar y usar todo, opciones de configuración y averías
  (mantenla al día al cambiar comandos u opciones)
- `docs/10_sdk_dji_mini3pro.md` · SDK de DJI (MSDK V5) para el Mini 3 Pro: enlaces, integración y límites
- `docs/specs/` · especificaciones · `docs/seguimiento/` · informes de cada hito

{{RELLENAR o BORRAR: originales fuera del repositorio (ficha, diseños) en `<ruta>`: para leerlos,
pide a la persona que ejecute `/add-dir "<ruta>"`.}}

Al compactar la conversación, conserva siempre: la fase actual, los archivos modificados,
los comandos de verificación usados y sus resultados, y las decisiones tomadas.
