package com.airalo.api.client;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class OrderClient extends BaseClient {

    private static final String ORDERS_PATH = "/orders";

    public static Response submitOrder(String token, String packageId, int quantity) {
        return execute(() ->
                authenticatedRequest(token)
                        .contentType("multipart/form-data")
                        .multiPart("package_id", packageId)
                        .multiPart("quantity", quantity)
                        .post(buildUrl(ORDERS_PATH))
        );
    }

    public static Response submitOrder(String token, Map<String, Object> params) {
        return execute(() -> {
            RequestSpecification request = authenticatedRequest(token)
                    .contentType("multipart/form-data");

            params.forEach((key, value) -> request.multiPart(key, value.toString()));

            return request.post(buildUrl(ORDERS_PATH));
        });
    }

    public static Response submitOrderWithSharing(String token, String packageId, int quantity,
                                                    String toEmail, List<String> sharingOptions,
                                                    List<String> copyAddresses) {
        return execute(() -> {
            RequestSpecification request = authenticatedRequest(token)
                    .contentType("multipart/form-data")
                    .multiPart("package_id", packageId)
                    .multiPart("quantity", quantity)
                    .multiPart("to_email", toEmail);

            sharingOptions.forEach(option -> request.multiPart("sharing_option[]", option));
            copyAddresses.forEach(address -> request.multiPart("copy_address[]", address));

            return request.post(buildUrl(ORDERS_PATH));
        });
    }

    public static Response submitOrderWithoutAcceptHeader(String token) {
        return execute(() ->
                given()
                        .header("Authorization", "Bearer " + token)
                        .post(buildUrl(ORDERS_PATH))
        );
    }

    public static Response submitOrderWithoutAuth(String packageId, int quantity) {
        return execute(() ->
                baseRequest()
                        .contentType("multipart/form-data")
                        .multiPart("package_id", packageId)
                        .multiPart("quantity", quantity)
                        .post(buildUrl(ORDERS_PATH))
        );
    }

    public static Response submitOrderEmptyBody(String token) {
        return execute(() ->
                authenticatedRequest(token)
                        .post(buildUrl(ORDERS_PATH))
        );
    }
}
