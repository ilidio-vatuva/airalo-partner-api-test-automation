package com.airalo.api.tests;

import com.airalo.api.client.AuthClient;
import com.airalo.api.config.ApiConfig;
import com.airalo.api.model.ApiResponse;
import com.airalo.api.model.TokenData;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Epic("Airalo Partner API")
@Feature("Authentication")
@DisplayName("Authentication Tests")
class AuthenticationTest extends BaseTest {

    @Test
    @Tag("happy-path")
    @DisplayName("1.1 - Should obtain access token with valid credentials")
    void shouldObtainAccessTokenWithValidCredentials() {
        Response response = AuthClient.requestToken(
                ApiConfig.getClientId(),
                ApiConfig.getClientSecret()
        );

        assertEquals(200, response.statusCode());
        ApiResponse<TokenData> body = response.as(new TypeRef<ApiResponse<TokenData>>() {});
        assertNotNull(body.data().accessToken());
        assertEquals("Bearer", body.data().tokenType());
    }

    @Test
    @Tag("negative")
    @DisplayName("1.2 - Should reject invalid client_id")
    void shouldRejectInvalidClientId() {
        Response response = AuthClient.requestToken("invalid_client_id", ApiConfig.getClientSecret());

        assertTrue(response.statusCode() == 401 || response.statusCode() == 422);
    }

    @Test
    @Tag("negative")
    @DisplayName("1.3 - Should reject invalid client_secret")
    void shouldRejectInvalidClientSecret() {
        Response response = AuthClient.requestToken(ApiConfig.getClientId(), "invalid_secret");

        assertEquals(401, response.statusCode());
        ApiResponse<Object> body = response.as(new TypeRef<ApiResponse<Object>>() {});
        assertEquals("Client authentication failed", body.meta().message());
    }

    @Test
    @Tag("negative")
    @DisplayName("1.4 - Should reject empty credentials")
    void shouldRejectEmptyCredentials() {
        Response response = AuthClient.requestToken("", "");

        assertTrue(response.statusCode() == 401 || response.statusCode() == 422);
    }

    @Test
    @Tag("known-bug")
    @DisplayName("1.5 - BUG: Should reject request with missing grant_type (returns 200)")
    void shouldRejectRequestWithMissingGrantType() {
        // KNOWN ISSUE: API issues a token even without grant_type
        Response response = AuthClient.requestTokenWithoutGrantType(
                ApiConfig.getClientId(),
                ApiConfig.getClientSecret()
        );

        assertTrue(response.statusCode() == 400 || response.statusCode() == 401 || response.statusCode() == 422,
                "Expected 400, 401, or 422 but got: " + response.statusCode());
    }
}
