# MS Persona

Microservicio de gestión de personas con arquitectura hexagonal usando Spring Boot WebFlux, R2DBC y RouterFunctions.

## Requisitos

- Java 17
- Gradle
- Docker y Docker Compose

## Dependencias

Este microservicio depende de:
- **ms-bootcamp**: Microservicio de gestión de bootcamps
  - Endpoint requerido: `GET /bootcamps` para validar existencia de bootcamps
  - URL configurable mediante variable de entorno `MS_BOOTCAMP_URL` (default: `http://localhost:8082`)
  - Si el endpoint no está disponible, se retornará error 404

## Configuración de Base de Datos

Levantar MySQL con Docker Compose:

```bash
docker-compose up -d
```

La base de datos se inicializará automáticamente con las tablas necesarias:
- `persona`: Almacena información de personas
- `inscripcion`: Almacena inscripciones de personas en bootcamps

## Variables de Entorno

- `MS_BOOTCAMP_URL`: URL del microservicio ms-bootcamp (default: `http://localhost:8082`)

## Ejecutar la Aplicación

```bash
./gradlew bootRun
```

La aplicación estará disponible en `http://localhost:8083`

API Documentation: `http://localhost:8083/swagger-ui.html`

## Endpoints

### POST /personas/{personaId}/inscripciones

Inscribe una persona en un bootcamp.

**Request Body:**
```json
{
  "bootcampId": 10
}
```

**Validaciones:**
- `personaId` debe existir (404 si no existe)
- `bootcampId` debe existir en ms-bootcamp (404 si no existe)
- La persona no puede estar inscrita dos veces en el mismo bootcamp (409)
- Máximo 5 bootcamps simultáneos según solape de fechas (409)
- No se permite inscribir si el nuevo bootcamp se cruza con alguno ya inscrito (409)

**Response 201:**
```json
{
  "id": 1,
  "personaId": 1,
  "bootcampId": 10,
  "fechaInscripcion": "2024-01-15T10:30:00"
}
```

## Tests

```bash
./gradlew test
```

