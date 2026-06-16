# BUG-002: Invalid email format accepted

**Severity:** Medium
**Endpoint:** POST /v2/orders
**Parameter:** `to_email`
**Status:** Open

## Description

The API accepts `valid_email@com` as a valid email address when submitting an order. This format is missing a valid TLD and should be rejected.

## Steps to Reproduce

1. Obtain a valid OAuth2 token.
2. Send a POST request to `/v2/orders` with:
   - `package_id`: any valid package
   - `quantity`: 1
   - `to_email`: `valid_email@com`
   - `sharing_option[]`: `link`
3. Observe the response.

## Expected Result

- Status: 422 Unprocessable Entity
- Body: Validation error indicating the email format is invalid.

## Actual Result

- Status: 200 OK
- Order is created successfully with the invalid email.

## Notes

- The email validation appears to only check for the presence of `@` and a domain part, without validating the TLD.
