# BUG-002: Invalid email format accepted

**Severity:** Medium
**Endpoint:** POST /v2/orders
**Parameters:** `to_email`, `copy_address[]`
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

## Additional Affected Parameter: `copy_address[]`

The same weak validation applies to the `copy_address[]` parameter. Sending `user@yopmailcom` (missing dot before TLD) is accepted as valid.

### Steps to Reproduce

1. Obtain a valid OAuth2 token.
2. Send a POST request to `/v2/orders` with:
   - `package_id`: any valid package
   - `quantity`: 1
   - `to_email`: `test@example.com`
   - `sharing_option[]`: `link`
   - `copy_address[]`: `user@yopmailcom`
3. Observe the response.

### Actual Result

- Status: 200 OK
- Order is created and email is attempted to the malformed address.

## Notes

- The email validation appears to only check for the presence of `@` and a domain part, without validating the TLD.
- Both `to_email` and `copy_address[]` share the same weak validation logic.
