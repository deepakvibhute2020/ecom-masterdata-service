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

### Pagination and sorting
`GET /api/products` and `GET /api/products/name/{name}` are paginated and accept these query parameters:

| Parameter | Default | Description                                                                 |
|-----------|---------|-----------------------------------------------------------------------------|
| `page`    | `0`     | Zero-based page number.                                                     |
| `size`    | `20`    | Number of products per page, supplied by the caller.                        |
| `sort`    | none    | `column,direction`. Columns: `id`, `name`. Direction: `asc` or `desc`.      |

Sort rules:
- `direction` is case-insensitive (`asc`, `ASC`, `desc`, `DESC`) and defaults to `asc` when omitted.
- Repeat `sort` to sort by several columns; they apply in the order given.
- Any other column returns `400` with `Invalid sort property: <column>`.

| Request                                         | Result                                |
|-------------------------------------------------|---------------------------------------|
| `?sort=id,asc`                                  | Oldest id first                       |
| `?sort=id,desc`                                 | Newest id first                       |
| `?sort=name,asc`                                | Name A to Z                           |
| `?sort=name,desc`                               | Name Z to A                           |
| `?sort=name,asc&sort=id,desc`                   | Name A to Z, then id newest first     |
| `?sort=id,desc&sort=name,asc`                   | Id newest first, then name A to Z     |
| `?page=1&size=5&sort=name,desc`                 | Second page of 5, name Z to A         |
| `?sort=price,asc`                               | `400` invalid sort property           |

In Swagger UI, fill `page`, `size` and `sort` as separate fields. Clear the placeholder `string` in `sort`, which is rejected with `400`.

Paginated response `200 OK`:
```json
{
  "content": [
    { "id": 1, "name": "Laptop" },
    { "id": 2, "name": "Smart Phone" }
  ],
  "page": { "size": 20, "number": 0, "totalElements": 2, "totalPages": 1 }
}
```

`content` is an empty list when there are no matches.

### Get all products
`GET /api/products?page=0&size=20&sort=name,asc`

Response `200 OK`: paginated response as shown above.

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

Supports the same `page`, `size` and `sort` parameters, e.g. `GET /api/products/name/phone?sort=name,desc`.

Response `200 OK`: paginated response (see above), e.g.
```json
{
  "content": [
    { "id": 2, "name": "Smart Phone" },
    { "id": 3, "name": "iPhone" }
  ],
  "page": { "size": 20, "number": 0, "totalElements": 2, "totalPages": 1 }
}
```

`content` is an empty list when nothing matches.

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

Handled cases: `404` product not found, `400` validation failure, malformed body, invalid path value, or invalid sort property.

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
curl "http://localhost:8090/api/products?page=0&size=20"
curl "http://localhost:8090/api/products?sort=name,asc"
curl "http://localhost:8090/api/products?sort=name,desc"
curl "http://localhost:8090/api/products?sort=id,desc&size=5"
curl "http://localhost:8090/api/products?sort=id,desc&sort=name,asc"
curl http://localhost:8090/api/products/1
curl "http://localhost:8090/api/products/name/lap?sort=name,desc"
curl -X PUT http://localhost:8090/api/products/1 -H "Content-Type: application/json" -d "{\"name\":\"Gaming Laptop\"}"
```
