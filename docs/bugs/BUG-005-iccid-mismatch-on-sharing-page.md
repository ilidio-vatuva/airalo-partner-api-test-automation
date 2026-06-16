# BUG-005: ICCID mismatch on sharing page

**Severity:** Critical
**Endpoint:** POST /v2/orders, GET /v2/sims/{iccid}
**Fields:** `sims[].iccid`, `sharing.link`
**Status:** Open

## Description

The ICCID returned in the order response does not match the ICCID displayed on the eSIM details page accessed via `sharing.link`. This means the end user sees a different eSIM than the one assigned to their order.

## Steps to Reproduce

1. Obtain a valid OAuth2 token.
2. Send a POST request to `/v2/orders` with:
   - `package_id`: any valid package
   - `quantity`: 1
   - `to_email`: a valid email
   - `sharing_option[]`: `link`
3. Note the `iccid` from the response (e.g., `89852350625740021124`).
4. Navigate to the URL in `sharing.link`.
5. Observe the ICCID displayed on the eSIM details page.

## Expected Result

- The sharing page displays the same ICCID as returned in the order response.

## Actual Result

- The sharing page displays a different ICCID (e.g., order returns `89852350625740021124` but the page shows `89852350625740021140`).

## Additional Findings

- The mismatch is **deterministic** — the sharing pages always resolve to the same two hardcoded ICCIDs:
  - `sharing.link` (link page) → always shows ICCID `89852350625740021124`
  - `sharing.link` (PDF page) → always shows ICCID `89852350625740021140`
- These ICCIDs are fixed regardless of what eSIM was actually ordered.
- When ordering 6 eSIMs, all 6 share the **same** `sharing.link` and `access_code` — they are not unique per eSIM.
- The GET `/v2/sims/{iccid}` endpoint returns the correct ICCID in the response body, but its `sharing.link` still points to the same hardcoded eSIMs.

## Impact

- End users accessing their eSIM via the sharing link will see incorrect eSIM details.
- If multiple eSIMs share the same link, users cannot distinguish between their individual eSIMs.
