package com.airalo.api.client;

import com.airalo.api.config.ApiConfig;
import com.airalo.api.model.ApiResponse;
import com.airalo.api.model.TokenData;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class AuthClient extends BaseClient {

    private static final String TOKEN_PATH = "/token";

    public static Response requestToken(String clientId, String clientSecret) {
        return execute(() ->
                given()
                        .contentType("application/x-www-form-urlencoded")
                        .header("Accept", "application/json")
                        .formParam("client_id", clientId)
                        .formParam("client_secret", clientSecret)
                        .formParam("grant_type", "client_credentials")
                        .post(buildUrl(TOKEN_PATH))
        );
    }

    public static Response requestTokenWithoutGrantType(String clientId, String clientSecret) {
        return execute(() ->
                given()
                        .contentType("application/x-www-form-urlencoded")
                        .header("Accept", "application/json")
                        .formParam("client_id", clientId)
                        .formParam("client_secret", clientSecret)
                        .post(buildUrl(TOKEN_PATH))
        );
    }

    public static String getValidToken() {
        Response response = requestToken(ApiConfig.getClientId(), ApiConfig.getClientSecret());
        if (response.statusCode() != 200) {
            return null;
        }
        ApiResponse<TokenData> body = response.as(new TypeRef<ApiResponse<TokenData>>() {});
        return body.data().accessToken();
    }
}
