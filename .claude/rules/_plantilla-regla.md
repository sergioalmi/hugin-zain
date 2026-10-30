---
paths:
  - "{{RELLENAR: carpeta/**/*.ext}}"
---

# Reglas para {{RELLENAR: lenguaje, pieza o tipo de archivo}}

<!--
Cómo funcionan estas reglas: Claude Code carga un archivo de .claude/rules/ solo cuando trabaja con
archivos que encajan con `paths` (patrones glob desde la raíz). Sin `paths`, se carga siempre.
Crea un archivo por lenguaje o pieza (p. ej. python.md, frontend.md, contenido.md) copiando este,
y borra esta plantilla al terminar. Escribe reglas concretas y comprobables, no consejos generales:
lo general ya está en CLAUDE.md y AGENTS.md.
-->

- Versión exacta del lenguaje o motor y cómo se fija ({{RELLENAR}}). No cambiarla sin ADR.
- Cómo se crea y gestiona el proyecto: gestor de paquetes, comando para añadir dependencias (solo
  con permiso), archivos de bloqueo que se versionan.
- Estructura: dónde va el código, dónde los tests, un módulo o paquete por área, espacios de
  nombres (y los nombres prohibidos porque chocan con la plataforma).
- Bibliotecas elegidas para cada necesidad (configuración, mensajes, red, interfaz) y las
  prohibidas, con el motivo en una línea.
- Trampas de la plataforma que no hay que repetir (APIs obsoletas, hilos, rendimiento en bucles).
- Qué no se edita a mano (archivos generados, escenas, metadatos) y qué hacer en su lugar.
- Cómo se prueba sin hardware ni servicios: dobles de prueba y fixtures compartidas.
- Registros: con el logger del módulo; nunca datos personales ni contenido de la persona usuaria.
- Antes de terminar, en verde: {{RELLENAR: comandos exactos de lint, formato, compilación y tests}}.
