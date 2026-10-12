# Product API Test Cases

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