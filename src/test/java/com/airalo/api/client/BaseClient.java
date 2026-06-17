package com.airalo.api.client;

import com.airalo.api.config.ApiConfig;
import com.airalo.api.config.RateLimitHandler;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

import static io.restassured.RestAssured.given;

public abstract class BaseClient {

    private static final Logger log = LoggerFactory.getLogger(BaseClient.class);
    private static final String ACCEPT_JSON = "application/json";

    protected static String buildUrl(String path) {
        return ApiConfig.getBaseUrl() + path;
    }

    protected static RequestSpecification authenticatedRequest(String token) {
        return given()
                .header("Accept", ACCEPT_JSON)
                .header("Authorization", "Bearer " + token);
    }

    protected static RequestSpecification baseRequest() {
        return given()
                .header("Accept", ACCEPT_JSON);
    }

    protected static Response execute(Supplier<Response> request) {
        Response response = RateLimitHandler.executeWithRetry(request);
        log.debug("Response: {} {}", response.statusCode(), response.statusLine());
        return response;
    }
}
