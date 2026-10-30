# 07 · Privacidad, seguridad y contenido

{{RELLENAR: quién usa el producto y por qué estas reglas pesan más que cualquier funcionalidad.}}

## Datos personales

- **Dónde viven:** solo en `data/` (fuera de git). {{RELLENAR: si salen de la máquina, a dónde y
  con qué base legal}}.
- **Mínimo necesario:** {{RELLENAR: qué se guarda y por qué}}.
- **Lo que nunca se guarda:** {{RELLENAR: p. ej. imágenes, audio, contraseñas en claro}}.
- **Registros (`logs/`):** sin datos personales ni contenido de la persona usuaria; solo tiempos,
  errores y eventos técnicos.
- **Derechos:** cómo se consultan y borran los datos de una persona.
- **Consentimiento:** {{RELLENAR: cuándo hace falta y cómo se recoge}}.

## Normativa aplicable

| Norma | Qué exige al producto | Dónde se cumple (código o proceso) |
| --- | --- | --- |
| {{RELLENAR: RGPD, accesibilidad, Ley de IA…}} | {{RELLENAR}} | {{RELLENAR}} |

## Riesgos para la persona usuaria o el sistema

{{RELLENAR: situaciones de riesgo, qué hace el software (respuesta fija, aviso, bloqueo) y por qué
esa lógica es determinista y probada, no generada. Quién revisa los textos y las listas.}}

## Contenido

{{RELLENAR o BORRAR: tono, temas prohibidos, revisión de textos por personas, idiomas.}}

## Seguridad técnica

- Servicios escuchando solo en `127.0.0.1` salvo lo que deba verse en red (y entonces, cuál y por qué).
- Secretos en `.env` (nunca en git ni en `config.yaml`); `.env.example` con las claves vacías.
- Validación de toda entrada en las fronteras de confianza (red, archivos, configuración).
