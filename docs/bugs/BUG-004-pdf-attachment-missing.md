# BUG-004: PDF attachment missing from email

**Severity:** Medium
**Endpoint:** POST /v2/orders
**Parameter:** `sharing_option[]`
**Status:** Open

## Description

When `sharing_option[]` includes `pdf`, the email sent to the user does not contain the PDF attachment. The email is delivered but the PDF with eSIM installation details is absent.

## Steps to Reproduce

1. Obtain a valid OAuth2 token.
2. Send a POST request to `/v2/orders` with:
   - `package_id`: any valid package
   - `quantity`: 1
   - `to_email`: a valid email address
   - `sharing_option[]`: `pdf`
3. Check the email received at the specified address.

## Expected Result

- Email is received with a PDF attachment containing eSIM installation details (QR code, LPA, activation instructions).

## Actual Result

- Email is received but without any PDF attachment.

## Notes

- The email itself is delivered successfully — only the PDF attachment is missing.
- The `link` sharing option works correctly (provides a functional sharing link).
