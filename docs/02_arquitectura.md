# 02 · Arquitectura

## Visión general

```
{{RELLENAR: diagrama en texto de las piezas, dónde corre cada una y cómo se comunican
(protocolo, puertos). Un diagrama ASCII se lee en cualquier editor y en el terminal.}}
```

Principios (adapta los que apliquen):

- **Cada pieza se prueba sola.** Cada una funciona con un doble de prueba de las demás (cliente o
  servidor de mentira, grabaciones, fixtures), sin hardware ni servicios externos.
- **Interfaces sustituibles.** Lo lento, caro o externo (modelos, APIs, hardware, notificadores)
  va detrás de una interfaz y se elige por configuración.
- **Una sola fuente de configuración:** `config/config.yaml`.
- {{RELLENAR: reparto de responsabilidades, p. ej. «el servidor decide, la interfaz representa»}}

## Piezas

| Pieza | Arranca con | Escucha en | Notas |
| --- | --- | --- | --- |
| {{RELLENAR}} | {{RELLENAR: script de arranque}} | {{RELLENAR: 127.0.0.1:puerto}} | {{RELLENAR}} |

## Protocolo entre piezas (versión 1)

Mensajes en {{RELLENAR: JSON…}} con un sobre común `{ "type", "data", ... }`. La fuente de verdad es
{{RELLENAR: el archivo con los modelos de los mensajes}}; hay ejemplos de cada mensaje en
`tests/fixtures/protocol/` que usan los tests de **todas** las piezas. Cambiar el protocolo exige
subir la versión, actualizar los ejemplos y una ADR.

| Mensaje | Sentido | Datos | Cuándo |
| --- | --- | --- | --- |
| {{RELLENAR}} | {{RELLENAR}} | {{RELLENAR}} | {{RELLENAR}} |

## Estados

{{RELLENAR o BORRAR: máquina de estados, quién la cambia y qué ve la persona en cada estado.}}

## Flujo principal

{{RELLENAR: pasos numerados del caso de uso principal, de la entrada a la salida.}}

## Presupuesto de tiempo (o de recursos)

| Etapa | Objetivo | Medido |
| --- | --- | --- |
| {{RELLENAR}} | {{RELLENAR}} | {{RELLENAR: cifra, fecha y herramienta}} |
