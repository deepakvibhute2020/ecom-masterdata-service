# Customer API

Base URL: `http://localhost:8090/api/customers`

All requests and responses use `application/json`.

## Customer object

| Field | Type | Description |
|-------|------|-------------|
| id | number | Auto-generated identifier. Ignored on create and update. |
| name | string | Required; maximum 200 characters. |
| country | string | Optional; maximum 200 characters. |
| email | string | Required valid email address; maximum 100 characters. |
| contactNo | string | Optional; maximum 20 characters. |

## Endpoints

### Create customer

`POST /api/customers`

Request:

```json
{
  "name": "Taylor Smith",
  "country": "Canada",
  "email": "taylor@example.com",
  "contactNo": "555-0100"
}
```

Returns `201 Created` with the customer, including its generated `id`.
Invalid fields return `400 Bad Request`.

### List customers

`GET /api/customers?page=0&size=20&sort=name,asc`

Returns `200 OK` with a paginated response. `page` is zero-based, `size`
defaults to 20, and sorting supports `id`, `name`, and `email`.

### Get customer

`GET /api/customers/{id}`

Returns `200 OK` with the customer, or `404 Not Found` when the id does not
exist. A non-numeric id returns `400 Bad Request`.

### Update customer

`PUT /api/customers/{id}`

Accepts the same fields as create and returns `200 OK` with the updated
customer. Invalid fields return `400 Bad Request`; a missing customer returns
`404 Not Found`.

### Delete customer

`DELETE /api/customers/{id}`

Returns `204 No Content` when deleted, or `404 Not Found` when the id does not
exist.
