# BUG-006: Broken `direct_apple_installation_url`

**Severity:** Low
**Endpoint:** POST /v2/orders, GET /v2/sims/{iccid}
**Field:** `direct_apple_installation_url`
**Status:** Open

## Description

The `direct_apple_installation_url` returned in eSIM responses is non-functional. The URL contains dummy/sandbox data that cannot be used for actual eSIM installation on iOS devices.

## Steps to Reproduce

1. Obtain a valid OAuth2 token.
2. Submit an order or retrieve an eSIM via GET `/v2/sims/{iccid}`.
3. Copy the `direct_apple_installation_url` from the response.
4. Attempt to open the URL on an iOS 17.4+ device or verify it in a browser.

## Expected Result

- The URL is functional and triggers eSIM installation on compatible iOS devices, OR
- The field is not returned if the environment does not support it.

## Actual Result

- The URL is broken/non-functional.
- Example: `https://esimsetup.apple.com/esim_qrcode_provisioning?carddata=LPA:1$lpa.airalo.com$DUMMY-2605230200-BUn8D-68535`

## Notes

- Affects all eSIMs regardless of package.
- The URL contains `DUMMY-` prefixed matching IDs, suggesting this is sandbox data.
- The domain (`esimsetup.apple.com`) is Apple's legitimate eSIM provisioning endpoint, but the `carddata` payload uses dummy values that Apple cannot resolve.
- This may be expected behavior for a sandbox/test environment, but the API should either return a functional test URL or omit the field entirely.
