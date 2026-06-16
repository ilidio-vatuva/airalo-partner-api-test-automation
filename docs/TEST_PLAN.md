# API Test Plan - Airalo Partner API

## Scope

This test plan covers **functional API testing** of the Airalo Partner API (`https://partners-api.airalo.com/v2`), including:

- OAuth2 authentication flow
- Submit Order endpoint (POST /v2/orders)
- Get eSIM Details endpoint (GET /v2/sims/{iccid})
- Input validation (negative/edge-case scenarios)
- Cross-endpoint data consistency
- Known bugs documented as expected-failure tests

### Test Types Covered

| Type | Description |
|------|-------------|
| Functional (Happy Path) | Verify endpoints return correct data for valid requests |
| Input Validation | Verify proper error handling for invalid/missing parameters |
| Data Integrity | Verify consistency between order and eSIM retrieval responses |
| Error Handling | Verify appropriate HTTP status codes and error messages |
| Authentication | Verify token-based access control works correctly |

### Out of Scope

| Type | Reason |
|------|--------|
| Performance / Load Testing | Not required by the exercise; would need dedicated tooling (k6, JMeter) |
| Security Testing | Beyond exercise scope (e.g., SQL injection, token manipulation, rate limiting) |
| Contract / Schema Testing | No OpenAPI spec provided to validate against |
| Integration Testing | No access to downstream systems (email delivery, Apple installation flow) |
| Idempotency Testing | No documentation on whether repeated orders are deduplicated |
| Concurrency Testing | Race conditions and parallel order submissions not in scope |
| Pagination / Filtering | Only retrieving individual eSIMs by ICCID; no list endpoints tested |
| Token Expiry / Refresh Flow | Only testing initial token acquisition, not lifecycle management |

---

## 1. Authentication (POST /v2/token)

### Happy Path

| # | Test Case | Expected |
|---|-----------|----------|
| 1.1 | Obtain access token with valid client_id and client_secret | 200 OK, response contains `access_token` and `token_type` |

### Negative Tests

| # | Test Case | Expected |
|---|-----------|----------|
| 1.2 | Request token with invalid client_id | 401/422 with error message |
| 1.3 | Request token with invalid client_secret | 401/422 with error message |
| 1.4 | Request token with empty credentials | 401/422 with error message |
| 1.5 | Request token with missing grant_type | 401/422 with error message |

---

## 2. Submit Order (POST /v2/orders)

### Happy Path

| # | Test Case | Expected |
|---|-----------|----------|
| 2.1 | Submit order for 6 eSIMs with package_id `moshi-moshi-7days-1gb` | 200 OK, response contains order with 6 sims in the `sims` array |
| 2.2 | Validate response contains correct `package_id` | `package_id` matches `moshi-moshi-7days-1gb` |
| 2.3 | Validate response contains correct `quantity` | `quantity` equals 6 |
| 2.4 | Validate each sim has a unique `iccid` | All 6 ICCIDs are distinct |
| 2.5 | Validate response `meta.message` is `"success"` | Message is appropriate |
| 2.6 | Validate order contains expected fields | `id`, `code`, `currency`, `type`, `esim_type`, `validity`, `package`, `data`, `price`, `created_at` are present |
| 2.7 | Validate each sim contains expected fields | `id`, `iccid`, `lpa`, `matching_id`, `qrcode`, `qrcode_url`, `apn_type`, `apn_value`, `is_roaming`, `direct_apple_installation_url` are present |
| 2.8 | Submit order with optional `description` field | 200 OK, description is reflected in response |
| 2.9 | Submit order with `to_email` and `sharing_option[]` | 200 OK, order is created successfully |

### Negative Tests

| # | Test Case | Expected |
|---|-----------|----------|
| 2.10 | Submit order without authorization header | 401 Unauthorized |
| 2.11 | Submit order with expired/invalid token | 401 Unauthorized |
| 2.12 | Submit order with invalid `package_id` | 422 with error message about invalid/out-of-stock package |
| 2.13 | Submit order with `quantity` = 0 | 422 with validation error |
| 2.14 | Submit order with `quantity` > 50 | 422 with validation error |
| 2.15 | Submit order with negative `quantity` | 422 with validation error |
| 2.16 | Submit order with missing `package_id` | 422 with validation error |
| 2.17 | Submit order with missing `quantity` | 422 with validation error |
| 2.18 | Submit order with invalid `brand_settings_name` | 422 with "The selected brand settings name is invalid." |
| 2.19 | Submit order with `to_email` but missing `sharing_option[]` | 422 with validation error |
| 2.20 | Submit order with invalid email format for `to_email` | 422 with validation error |

### Known Bugs (Expected Failures)

| # | Test Case | Actual Behavior | Expected Behavior |
|---|-----------|-----------------|-------------------|
| 2.21 | Submit order with empty body (auth header only) | 500 Internal Server Error | 422 with missing required fields message |
| 2.22 | Submit order with `to_email` = `valid_email@com` | Accepted as valid | 422 with invalid email error |
| 2.23 | Submit order with `type` = `esim` | Creates order as `sim` | Should reject invalid type or honor the value |

---

## 3. Get eSIM Details (GET /v2/sims/{iccid})

### Happy Path

| # | Test Case | Expected |
|---|-----------|----------|
| 3.1 | Get eSIM details for each ICCID from the order | 200 OK for each eSIM |
| 3.2 | Validate response contains correct `iccid` | Matches the requested ICCID |
| 3.3 | Validate `meta.message` is `"success"` | Message is appropriate |
| 3.4 | Validate eSIM contains expected fields | `id`, `iccid`, `lpa`, `matching_id`, `qrcode`, `qrcode_url`, `direct_apple_installation_url`, `apn_type`, `apn_value`, `is_roaming`, `apn`, `sharing` are present |
| 3.5 | Validate `simable` object contains order details | `id`, `code`, `package_id`, `quantity`, `type`, `description`, `esim_type`, `validity`, `package`, `data`, `price`, `status` are present |
| 3.6 | Validate `simable.status` is `"Completed"` | Order status reflects completion |
| 3.7 | Validate eSIM details match the order response | Fields like `iccid`, `lpa`, `matching_id`, `qrcode` are consistent between order and get-details responses |

### Negative Tests

| # | Test Case | Expected |
|---|-----------|----------|
| 3.8 | Get eSIM with invalid/non-existent ICCID | 404 or appropriate error |
| 3.9 | Get eSIM without authorization header | 401 Unauthorized |
| 3.10 | Get eSIM with expired/invalid token | 401 Unauthorized |
| 3.11 | Get eSIM with empty ICCID | 404 or 422 |

### Known Bugs (Expected Failures)

| # | Test Case | Actual Behavior | Expected Behavior |
|---|-----------|-----------------|-------------------|
| 3.12 | Order returns ICCID "abc" → navigate to `sharing.link` → eSIM details page shows same ICCID "abc" | `sharing.link` page displays a different ICCID "xyz" | ICCID on the sharing page should match the one returned in the order |
| 3.13 | Verify `direct_apple_installation_url` is functional | URL is broken | URL should be valid and accessible |

---

## 4. Cross-Endpoint Validation

| # | Test Case | Expected |
|---|-----------|----------|
| 4.1 | Order 6 eSIMs, then GET each one — all 6 should be retrievable | Each ICCID from the order returns valid eSIM details |
| 4.2 | Validate order `package_id` matches `simable.package_id` in GET response | Values are consistent |
| 4.3 | Validate order `quantity` matches number of sims retrievable | 6 eSIMs are individually accessible |

---

## Test Execution Strategy

1. **Setup:** Obtain OAuth2 token (test 1.1)
2. **Order:** Submit order for 6 eSIMs (test 2.1)
3. **Retrieve:** Loop through each ICCID and GET details (tests 3.1–3.7)
4. **Cross-validate:** Compare order response with individual eSIM responses (tests 4.1–4.3)
5. **Negative tests:** Run independently with fresh tokens as needed
6. **Known bugs:** Document as skipped/expected-failure with annotations explaining the defect
