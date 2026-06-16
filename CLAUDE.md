# CLAUDE.md — Airalo Partner API Tests

## Context
QA automation challenge — API layer only.
Stack: Java + RestAssured + JUnit 5.
Target: Airalo Partner API v2 (sandbox).

## Credentials & Config
- All credentials live in `src/test/resources/config.properties`
  or environment variables.
- Never hardcode CLIENT_ID, CLIENT_SECRET, or tokens in test files.
- Base URL, client_id and client_secret must be configurable
  without code changes.

## Authentication
- Auth endpoint is NOT a test case — it is a fixture.
- Obtain token once in `@BeforeAll` via `AuthFixture.getToken()`.
- Pass token as `Authorization: Bearer <token>` header in all
  subsequent requests.
- Auth endpoint: POST /v2/oauth/token (form-data, NOT JSON).
- If token fetch fails, fail fast — do not proceed with other tests.

## Architecture
- Use a client layer: one class per resource
  (OrderClient, SimClient, AuthFixture).
- Keep assertions in test files. Keep HTTP calls in client classes.
- No magic strings — use constants for endpoints, package IDs,
  field names.
- Response deserialization: use POJOs or JsonPath —
  never parse raw strings.
- Apply clean code principles.


## Test Writing Rules
- One assertion per test where possible.
  If multiple, they must be cohesive.
- Explicit waits only if polling is needed.
  No Thread.sleep() unless unavoidable.
- If a field is missing from the response, fail with a clear
  message — do not silently skip.
- Flag any ambiguous field name with a comment before asserting it.

## What You Must Not Do
- Do not re-authenticate per test — use the fixture token.
- Do not hardcode ICCIDs — derive them from the order response.
- Do not assert on values that change per run
  (timestamps, dynamic IDs) — assert structure instead.
- Do not add tests outside scope without flagging first.
- Do not mark a known bug as a test failure without a
  // KNOWN ISSUE comment explaining the discrepancy.

## My Role
I review every test before it is considered done.
When in doubt, stop and ask. I make the judgement call.