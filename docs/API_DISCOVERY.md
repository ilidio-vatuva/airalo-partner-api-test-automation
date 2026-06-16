# Airalo Partner API - Discovery Notes

## Endpoint 1: Submit Order

**Description:**
This endpoint allows you to submit an order to the Airalo Partner API by providing the required parameters, such as `package_id` and `quantity`. You may also include an optional `description` field to track your internal order ID or add any relevant notes related to the order.

When submitting the order, the response includes the field `direct_apple_installation_url` with installation instructions that provide Apple's universal link, enabling direct installation on devices running iOS 17.4 or later.

You can also provide your user's email address using the `to_email` parameter, which will send an email asynchronously. The email uses a white-label template powered by the eSIM cloud feature, providing users with a link to access and install the eSIM.

**Endpoint:** `POST /v2/orders`

### Headers

| Header          | Value                |
|-----------------|----------------------|
| `Authorization` | Bearer {access_token}|
| `Accept`        | application/json     |

### Body Parameters

| Parameter             | Type    | Required | Description |
|-----------------------|---------|----------|-------------|
| `package_id`          | string  | Yes      | The package ID associated with the order. |
| `quantity`            | integer | Yes      | Number of eSIMs to order. Min: 1; Max: 50. |
| `description`         | string  | No       | Internal order ID or any relevant notes. |
| `to_email`            | string  | No       | Email address of the user to receive the eSIM installation instructions. |
| `type`                | string  | No       | The type of order. Currently the API ignores this and always creates as `sim`. |
| `brand_settings_name` | string  | No       | Nullable. The brand under which the eSIM should be shared. Null for unbranded. No valid values found for the test credentials. |
| `sharing_option[]`    | array   | No*      | Sharing options for the eSIM. Values: `link`, `pdf`. *Required if `to_email` is provided. |
| `copy_address[]`      | array   | No       | Email addresses to which the eSIM will be copied. |

### Sample Response (200 OK)

```json
{
    "data": {
        "id": 74506,
        "code": "20260616-074506",
        "currency": "USD",
        "package_id": "kallur-digital-7days-1gb",
        "quantity": 1,
        "type": "sim",
        "description": "1 sim kallur-digital-7days-1gb",
        "esim_type": "local",
        "validity": 7,
        "package": "Kallur Digital-1 GB - 7 days",
        "data": "1 GB",
        "price": 4.5,
        "pricing_model": "net_pricing",
        "created_at": "2026-06-16 18:19:00",
        "manual_installation": "<p>...</p>",
        "qrcode_installation": "<p>...</p>",
        "installation_guides": {
            "en": "https://www.airalo.com/help/getting-started-with-airalo"
        },
        "text": null,
        "voice": null,
        "net_price": 3.2,
        "brand_settings_name": null,
        "sims": [
            {
                "id": 77566,
                "created_at": "2026-06-16 18:19:00",
                "iccid": "8900000461442300635",
                "lpa": "lpa.airalo.com",
                "imsis": null,
                "matching_id": "DUMMY-2605220200-TtqGx-78782",
                "qrcode": "LPA:1$lpa.airalo.com$DUMMY-2605220200-TtqGx-78782",
                "qrcode_url": "https://sandbox.airalo.com/qr?expires=1867947540&id=429029&signature=...",
                "airalo_code": null,
                "apn_type": "automatic",
                "apn_value": "N/A",
                "is_roaming": true,
                "confirmation_code": null,
                "apn": {
                    "ios": {
                        "apn_type": "automatic",
                        "apn_value": "N/A"
                    },
                    "android": {
                        "apn_type": "automatic",
                        "apn_value": "N/A"
                    }
                },
                "msisdn": null,
                "direct_apple_installation_url": "https://esimsetup.apple.com/esim_qrcode_provisioning?carddata=LPA:1$lpa.airalo.com$DUMMY-2605220200-TtqGx-78782"
            }
        ]
    },
    "meta": {
        "message": "success"
    }
}
```

---

## Endpoint 2: Get eSIM Details

**Description:**
This endpoint allows you to retrieve the details of a specific eSIM using its ICCID. Only eSIM orders made via the API are retrievable via this endpoint. You can include related data in the response by specifying optional parameters.

The `direct_apple_installation_url` field supports direct installation on iOS devices (iOS 17.4+) using Apple's Universal Links.

**Endpoint:** `GET /v2/sims/{sim_iccid}`

### Path Parameters

| Parameter    | Type   | Required | Description |
|--------------|--------|----------|-------------|
| `sim_iccid`  | string | Yes      | The ICCID of the eSIM to retrieve details for. |

### Headers

| Header          | Value                |
|-----------------|----------------------|
| `Authorization` | Bearer {access_token}|
| `Accept`        | application/json     |

### Sample Response (200 OK)

```json
{
    "data": {
        "id": 77566,
        "created_at": "2026-06-16 18:19:00",
        "iccid": "8900000461442300635",
        "lpa": "lpa.airalo.com",
        "imsis": null,
        "matching_id": "DUMMY-2605220200-TtqGx-78782",
        "qrcode": "LPA:1$lpa.airalo.com$DUMMY-2605220200-TtqGx-78782",
        "qrcode_url": "https://sandbox.airalo.com/qr?expires=1867947540&id=429029&signature=...",
        "direct_apple_installation_url": "https://esimsetup.apple.com/esim_qrcode_provisioning?carddata=LPA:1$lpa.airalo.com$DUMMY-2605220200-TtqGx-78782",
        "voucher_code": null,
        "airalo_code": null,
        "apn_type": "automatic",
        "apn_value": "N/A",
        "is_roaming": true,
        "confirmation_code": null,
        "brand_settings_name": null,
        "msisdn": null,
        "apn": {
            "ios": {
                "apn_type": "automatic",
                "apn_value": "N/A"
            },
            "android": {
                "apn_type": "automatic",
                "apn_value": "N/A"
            }
        },
        "sharing": {
            "link": null,
            "access_code": null
        },
        "recycled": false,
        "recycled_at": null,
        "simable": {
            "id": 74506,
            "code": "20260616-074506",
            "package_id": "kallur-digital-7days-1gb",
            "currency": "USD",
            "quantity": 1,
            "type": "sim",
            "description": "1 sim kallur-digital-7days-1gb",
            "esim_type": "local",
            "validity": "7",
            "package": "Kallur Digital-1 GB - 7 days",
            "data": "1 GB",
            "price": "4.50",
            "created_at": "2026-06-16 18:19:00",
            "manual_installation": "",
            "qrcode_installation": "",
            "installation_guides": {
                "en": "https://www.airalo.com/help/getting-started-with-airalo"
            },
            "text": null,
            "voice": null,
            "net_price": 3.2,
            "status": {
                "name": "Completed",
                "slug": "completed"
            },
            "user": {
                "id": 71355,
                "name": "Technical user for Airalo Hiring Test",
                "email": "technical_user_dummy_email_6998364a25902@airalo.com",
                "mobile": null,
                "address": null,
                "state": null,
                "city": null,
                "postal_code": null,
                "country_id": null,
                "company": null,
                "created_at": "2026-02-20 10:24:10"
            }
        }
    },
    "meta": {
        "message": "success"
    }
}
```

---

## Issues Found During Exploration

### 1. 500 error on null body

Sending the authorization header with a **null body** returns a 500 Internal Server Error. When the body is empty but not null, it correctly returns 422.

- **Trigger:** Body is null (not just empty)
- **Actual:** 500 Internal Server Error
- **Expected:** 422 with a message indicating required fields are missing

### 2. Invalid email accepted

`valid_email@com` is accepted as a valid email address.

- **Actual:** Order created successfully
- **Expected:** 422 with a validation error for invalid email format

### 3. `type` parameter ignored

Whatever value is sent for `type`, the API always creates the order as `sim`. If only `sim` is supported, the parameter should either be removed or the API should reject unsupported values.

### 4. PDF attachment missing

When `sharing_option` includes `pdf`, the email is sent without the PDF attachment. The expected behavior is that the email should include the PDF attachment.

### 5. ICCID mismatch on sharing page

The ICCID returned in the order response does not match the ICCID displayed on the eSIM details page accessed via `sharing.link`.

- **Expected:** Order returns ICCID "abc" → navigating to `sharing.link` shows the same ICCID "abc".
- **Actual:** Order returns ICCID "abc" → navigating to `sharing.link` shows a different ICCID "xyz".

**Additional findings:**
- The mismatch is not consistent — different ICCIDs appear on the link page vs. the PDF page (e.g., `89852350625740021124` on link, `89852350625740021140` on PDF).
- When ordering 6 eSIMs, all 6 share the same `sharing.link` and `access_code` — they are not unique per eSIM.
- The GET `/v2/sims/{iccid}` endpoint returns the correct ICCID, but its `sharing.link` still points to the wrong eSIM details page.

### 6. Broken `direct_apple_installation_url`

The URL in the response is non-functional. Affects all eSIMs regardless of package. The URL structure uses sandbox/dummy data (`DUMMY-...`) which suggests this is a sandbox environment limitation, but the URL should still either work or not be returned.

Example: `https://esimsetup.apple.com/esim_qrcode_provisioning?carddata=LPA:1$lpa.airalo.com$DUMMY-2605230200-BUn8D-68535`

### 7. `sharing.link` populated regardless of `sharing_option[]`

The `sharing.link` field is populated even when only `pdf` is sent as the sharing option. Expected: `sharing.link` should only be populated when `link` is explicitly included in `sharing_option[]`.

---

## Additional Observations

| Area | Finding |
|------|---------|
| Quantity boundary | `quantity: 50` succeeds (max). `quantity: 51` correctly returns 422: "quantity must be less than or equal to 50". |
| Partial body | Sending only `package_id` or only `quantity` returns 422 (not 500), so the 500 is specific to null body. |
| Token longevity | Token remained valid for 3+ hours of testing without expiring. |
| `copy_address[]` | Emails are actually delivered to the addresses specified. |
