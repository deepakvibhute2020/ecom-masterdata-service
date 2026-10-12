# Product API Test Cases

## GET /api/products

### Pagination

Query parameters:
- `page`: zero-based page number; defaults to `0`
- `size`: requested number of products per page; defaults to `20`
- `sort`: optional, `property,direction`; allowed properties are `id` and `name`.
  Any other property returns HTTP 400 Bad Request.

Expected:
- HTTP 200 OK
- A paginated response containing no more than the requested `size` products in `content`
- Pagination metadata, including `totalElements` and `totalPages`

## GET /api/products/name/{name}

Uses the same `page` and `size` parameters and response format as
`GET /api/products`.

## GET /api/products/{id}

### Scenario 1: Product exists

Input:
- Product ID: 101

Expected:
- HTTP 200 OK
- Product details returned

### Scenario 2: Product does not exist

Input:
- Product ID: 9999

Expected:
- HTTP 404 Not Found

### Scenario 3: Invalid product ID

Input:
- Product ID: abc

Expected:
- HTTP 400 Bad Request, if rejected by the API's routing and validation design