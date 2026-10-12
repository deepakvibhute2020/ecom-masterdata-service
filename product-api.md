# Product API

Base URL: `http://localhost:8090/api/products`

Swagger UI: `http://localhost:8091/actuator/swagger-ui`
OpenAPI JSON: `http://localhost:8091/actuator/openapi`

All requests and responses use `application/json`.

## Product object

| Field | Type   | Description                                              |
|-------|--------|----------------------------------------------------------|
| id    | number | Auto-generated identifier. Ignored on create and update. |
| name  | string | Required, not blank, max 200 characters.                 |

```json
{ "id": 1, "name": "Laptop" }
```

## Endpoints

### Create product
`POST /api/products`

Request:
```json
{ "name": "Laptop" }
```

Response `201 Created`:
```json
{ "id": 1, "name": "Laptop" }
```

Errors: `400` if `name` is missing, blank or longer than 200 characters, or the body is malformed JSON.

### Get all products
`GET /api/products`

Response `200 OK`:
```json
[
  { "id": 1, "name": "Laptop" },
  { "id": 2, "name": "Smart Phone" }
]
```

Returns an empty list when there are no products.

### Get product by id
`GET /api/products/{id}`

`id` must be numeric; a non-numeric value such as `abc` returns `400`.

Response `200 OK`:
```json
{ "id": 1, "name": "Laptop" }
```

Errors: `404` if no product has that id.

### Search products by name
`GET /api/products/name/{name}`

Returns products whose name contains the given text, ignoring case (SQL `LIKE %name%`).
Example: `GET /api/products/name/phone` matches "Smart Phone" and "iPhone".

Response `200 OK`:
```json
[
  { "id": 2, "name": "Smart Phone" },
  { "id": 3, "name": "iPhone" }
]
```

Returns an empty list when nothing matches.

Note: a value made only of digits is treated as an id, so numeric names cannot be searched with this endpoint.

### Update product
`PUT /api/products/{id}`

Request:
```json
{ "name": "Gaming Laptop" }
```

Response `200 OK`:
```json
{ "id": 1, "name": "Gaming Laptop" }
```

Errors: `400` for an invalid `name` (same rules as create); `404` if no product has that id.

## Errors

Errors are returned by a central `@RestControllerAdvice` in one format:

```json
{
  "timestamp": "2026-10-12T14:00:00Z",
  "status": 404,
  "error": "Not Found",
  "message": "Product not found: 99999"
}
```

Handled cases: `404` product not found, `400` validation failure, malformed body, or invalid path value.

## Configuration

| Property                      | Purpose                                         |
|-------------------------------|-------------------------------------------------|
| `server.port`                 | API port (8090)                                 |
| `management.server.port`      | Port serving Swagger UI (8091)                  |
| `app.cors.allowed-origins`    | Origins allowed to call `/api/**` (Swagger UI)  |
| `DB_USERNAME` / `DB_PASSWORD` | Environment variables for the MySQL credentials |

## Example (curl)

```
curl -X POST http://localhost:8090/api/products -H "Content-Type: application/json" -d "{\"name\":\"Laptop\"}"
curl http://localhost:8090/api/products
curl http://localhost:8090/api/products/1
curl http://localhost:8090/api/products/name/lap
curl -X PUT http://localhost:8090/api/products/1 -H "Content-Type: application/json" -d "{\"name\":\"Gaming Laptop\"}"
```
