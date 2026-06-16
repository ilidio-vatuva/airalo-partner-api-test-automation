# BUG-003: `type` parameter is ignored

**Severity:** Low
**Endpoint:** POST /v2/orders
**Parameter:** `type`
**Status:** Open

## Description

Whatever value is sent for the `type` parameter, the API always creates the order as `sim`. The parameter has no effect on the response.

## Steps to Reproduce

1. Obtain a valid OAuth2 token.
2. Send a POST request to `/v2/orders` with:
   - `package_id`: any valid package
   - `quantity`: 1
   - `type`: `esim` (or any other value)
3. Observe the `type` field in the response.

## Expected Result

Either:
- The API creates the order with the specified type, OR
- The API returns a 422 rejecting unsupported type values, OR
- The parameter is removed from the documentation/accepted parameters.

## Actual Result

- Status: 200 OK
- Response `type` is always `"sim"` regardless of input value.

## Notes

- If the business logic only supports `sim`, the parameter should be deprecated or removed from the API contract to avoid confusion.
