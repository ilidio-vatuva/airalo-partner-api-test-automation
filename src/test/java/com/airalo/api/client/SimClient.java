package com.airalo.api.client;

import com.airalo.api.model.ApiResponse;
import com.airalo.api.model.Sim;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimClient extends BaseClient {

    private static final Logger log = LoggerFactory.getLogger(SimClient.class);
    private static final String SIMS_PATH = "/sims";
    private static final TypeRef<ApiResponse<Sim>> SIM_TYPE = new TypeRef<>() {};

    private static final int POLL_MAX_ATTEMPTS = 5;

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

    public static void waitForOrderStatus(String token, String iccid) {
        for (int attempt = 1; attempt <= POLL_MAX_ATTEMPTS; attempt++) {
            Response response = getSimByIccid(token, iccid);
            if (response.statusCode() == 200) {
                ApiResponse<Sim> body = response.as(SIM_TYPE);
                if (body.data().simable() != null && body.data().simable().status() != null) {
                    log.info("Order status populated after {} attempt(s): {}",
                            attempt, body.data().simable().status().name());
                    return;
                }
            }
            log.info("Polling order status (attempt {}/{})", attempt, POLL_MAX_ATTEMPTS);
        }
        log.warn("Order status not populated after {} attempts", POLL_MAX_ATTEMPTS);
    }
}
