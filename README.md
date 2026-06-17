# Airalo Partner API Test Automation

Automated test suite for the Airalo Partner API v2, covering authentication, order submission, eSIM retrieval, and cross-endpoint validation.

## Tech Stack

- Java 17
- JUnit 5
- RestAssured 5.4
- Jackson (JSON deserialization)
- Allure (reporting)
- Maven

## Project Structure

```
src/test/java/com/airalo/api/
├── client/             # HTTP client layer (one class per resource)
│   ├── BaseClient.java
│   ├── AuthClient.java
│   ├── OrderClient.java
│   └── SimClient.java
├── config/
│   ├── ApiConfig.java          # Reads config.properties
│   └── RateLimitHandler.java   # 429 retry with exponential backoff
├── model/              # Response POJOs (Java records)
│   ├── ApiResponse.java
│   ├── Order.java
│   ├── Sim.java
│   ├── Status.java
│   ├── Meta.java
│   └── TokenData.java
└── tests/
    ├── BaseTest.java
    ├── AuthenticationTest.java         # 5 tests
    ├── SubmitOrderTest.java            # 27 tests
    ├── GetSimTest.java                 # 16 tests (4 manual/skipped)
    └── CrossEndpointValidationTest.java # 3 tests
```

## Test Coverage

| Suite | Tests | Description |
|-------|-------|-------------|
| Authentication | 5 | Token acquisition, invalid credentials, missing grant_type |
| Submit Order | 27 | Happy path, validation, sharing options, edge cases |
| Get eSIM | 16 | ICCID lookup, field validation, error handling |
| Cross-Endpoint | 3 | Order → SIM consistency checks |

**Total: 51 tests** (44 pass, 7 known API bugs documented)

## Prerequisites

- Java 17+
- Maven 3.8+
- Airalo Partner API credentials

## Setup

1. Clone the repository:
   ```bash
   git clone https://github.com/ilidio-vatuva/airalo-partner-api-test-automation.git
   cd airalo-partner-api-test-automation
   ```

2. Create `src/test/resources/config.properties`:
   ```properties
   base.url=https://partners-api.airalo.com/v2
   client.id=YOUR_CLIENT_ID
   client.secret=YOUR_CLIENT_SECRET
   package.id=moshi-moshi-7days-1gb
   order.quantity=6
   ```

## Running Tests

```bash
# Run all tests
mvn test

# Run a specific test class
mvn test -Dtest=AuthenticationTest

# Run tests by tag
mvn test -Dgroups=happy-path
mvn test -Dgroups=negative
mvn test -Dgroups=known-bug
```

## Allure Reporting

```bash
# Generate and open report
mvn allure:report
mvn allure:serve
```

## CI/CD

Tests run via GitHub Actions (`workflow_dispatch`). Credentials are injected from repository secrets:

- `CLIENT_ID`
- `CLIENT_SECRET`

Trigger manually from the Actions tab. The base URL defaults to `https://partners-api.airalo.com/v2`.

## Architecture Decisions

- **Client layer separation**: HTTP calls live in client classes; assertions stay in test files.
- **Token as fixture**: Authentication happens once in `@BeforeAll`, not per test.
- **Rate limit handling**: Automatic retry with exponential backoff on 429 responses.
- **POJO deserialization**: Responses are deserialized into Java records — no raw string parsing.
- **Known bugs documented**: Tests that expose API defects are tagged `@Tag("known-bug")` and prefixed with `BUG:` in their display name.

## Known API Issues

| Test | Issue |
|------|-------|
| 1.5 | API issues token without `grant_type` parameter (returns 200) |
| 2.22 | API accepts `valid_email@com` (no TLD) as valid email |
| 2.23 | `type=esim` parameter is ignored, order created as "sim" |
| 2.26 | API accepts `user@domain` (no TLD) as valid copy_address |
| 3.5 | `simable.status` not populated in GET /sims response |
| 3.6 | Same as 3.5 — status field is null |
| 3.11 | Empty ICCID returns 200 with a list instead of 404/422 |
