# Product Service Architecture

## Overview

The Product Service manages product information.

## Architecture

```text
Angular Frontend
       |
       v
REST Controller
       |
       v
Product Service
       |
       v
Product Repository
       |
       v
MySQL Database
```

## Responsibilities

### Controller

- Handles HTTP requests.
- Validates request input.
- Returns HTTP responses.

### Service

- Implements business logic.
- Coordinates repository operations.
- Applies business validations.

### Repository

- Performs database operations.
- Uses Spring Data JPA.

## Exception Handling

Use a centralized exception handler implemented with
@RestControllerAdvice.