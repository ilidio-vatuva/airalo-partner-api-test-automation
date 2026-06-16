# Automate API Requests and Verify Responses (Updated February 20, 2026)

**Objective:** Automate requests to the Airalo Partner API and validate the responses.

## Instructions

### 1. Explore the API

- Visit the Airalo Developer Portal to understand the available endpoints and their usage.
- Note the base URL you'll use in this exercise:
  - `https://partners-api.airalo.com/v2`

### 2. Authentication

- Obtain OAuth2 tokens to access the Airalo Partner API using the provided credentials:

  **client_id:** `***`

  **client_secret:** `***`

### 3. Endpoint 1

- Use the Submit order endpoint to POST an order for **6** eSIMs with package_id `moshi-moshi-7days-1gb`.
- Ensure you have a valid OAuth2 token before making the request.

### 4. Endpoint 2

- Use the Get eSIM endpoint to GET the eSIM details for each eSIM from your order.
- Use the correct query parameters to retrieve the details for each eSIM.

### 5. Create Automated Tests

- Write scripts using any language and framework of your choice to automate requests to these endpoints.

### 6. Verify Responses

- Validate the correctness of the responses, including:
  - **Status Codes:** Ensure each request receives the appropriate HTTP status code.
  - **Message:** Verify that each response message from the endpoint is appropriate.
  - **Response Body:** Confirm the response body contains the correct information for all eSIMs from the order, including order details and eSIM properties.

### 7. Expected Output

- A script that automates sending requests and verifying responses, with documentation.
