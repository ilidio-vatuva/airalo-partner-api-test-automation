# BUG-007: `sharing.link` populated regardless of `sharing_option[]`

**Severity:** Low
**Endpoint:** POST /v2/orders, GET /v2/sims/{iccid}
**Fields:** `sharing.link`, `sharing_option[]`
**Status:** Open

## Description

The `sharing.link` field in the eSIM response is populated even when only `pdf` is specified in `sharing_option[]`. The link should only be generated when `link` is explicitly requested.

## Steps to Reproduce

1. Obtain a valid OAuth2 token.
2. Send a POST request to `/v2/orders` with:
   - `package_id`: any valid package
   - `quantity`: 1
   - `to_email`: a valid email
   - `sharing_option[]`: `pdf` (without `link`)
3. Retrieve the eSIM via GET `/v2/sims/{iccid}`.
4. Check the `sharing.link` field.

## Expected Result

- `sharing.link` is `null` since only `pdf` was requested.

## Actual Result

- `sharing.link` is populated with a URL even though `link` was not included in `sharing_option[]`.

## Notes

- This suggests the API always generates a sharing link regardless of the requested sharing options.
- While not harmful, it contradicts the purpose of `sharing_option[]` as a control mechanism for what sharing methods are provided.
