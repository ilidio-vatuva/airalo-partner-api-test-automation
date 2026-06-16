# BUG-001: 500 Internal Server Error when `Accept: application/json` header is missing

**Severity:** Low
**Endpoint:** POST /v2/orders
**Status:** Open

## Description

Sending a request to the Submit Order endpoint without the `Accept: application/json` header results in a 500 Internal Server Error with an HTML error page. The same request with the `Accept` header present returns a proper 422 JSON validation response.

## Steps to Reproduce

1. Obtain a valid OAuth2 token.
2. Send a POST request to `/v2/orders` with:
   - Header: `Authorization: Bearer {token}`
   - **No** `Accept: application/json` header
   - Body: no parameters sent (unchecked)
3. Observe the response.

## Expected Result

- Status: 422 Unprocessable Entity
- Body: JSON validation error indicating required fields are missing (same as when `Accept` header is present).

## Actual Result

- Status: 500
- Response: HTML error page (`<title>Airalo Partners – Server Error</title>`) instead of JSON.

## Comparison

| Accept header | Body params              | Status | Response format |
|---------------|--------------------------|--------|-----------------|
| Present       | Valid (quantity + package_id) | 200 | JSON (success) |
| Present       | None sent                | 422    | JSON (`{"code": 34, "reason": "..."}`) |
| Missing       | Valid (quantity + package_id) | 200 | Success (works fine) |
| Missing       | None sent                | 500    | HTML (Server Error page) |

## Notes

- The 500 only occurs when the `Accept` header is missing **and** the request triggers a validation error.
- The happy path (valid params) works fine without the `Accept` header.
- The server can't render validation/error responses without `Accept: application/json` — it crashes trying to format the error.
- An API should handle missing `Accept` headers gracefully — defaulting to JSON for an API endpoint, or returning a proper error in any format.
