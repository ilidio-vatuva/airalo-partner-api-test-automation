package com.airalo.api.client;

import io.restassured.response.Response;

public class SimClient extends BaseClient {

    private static final String SIMS_PATH = "/sims";

    public static Response getSimByIccid(String token, String iccid) {
        return execute(() ->
                authenticatedRequest(token)
                        .queryParam("include", "order")
                        .get(buildUrl(SIMS_PATH + "/" + iccid))
        );
    }

public static Response getSimWithoutAuth(String iccid) {
        return execute(() ->
                baseRequest()
                        .get(buildUrl(SIMS_PATH + "/" + iccid))
        );
    }
}
