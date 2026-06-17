package com.airalo.api.config;

import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

public class RateLimitHandler {

    private static final Logger log = LoggerFactory.getLogger(RateLimitHandler.class);
    private static final int MAX_RETRIES = 5;
    private static final long DEFAULT_BACKOFF_MS = 2000;
    private static final int BACKOFF_MULTIPLIER = 2;

    public static Response executeWithRetry(Supplier<Response> request) {
        Response response = request.get();

        int retries = 0;
        while (response.statusCode() == 429 && retries < MAX_RETRIES) {
            retries++;
            long waitMs = getWaitTime(response, retries);
            log.warn("Rate limited (429). Retry {}/{} after {}ms", retries, MAX_RETRIES, waitMs);
            try {
                Thread.sleep(waitMs);
            } catch (InterruptedException e) {
                log.warn("Retry sleep interrupted", e);
                Thread.currentThread().interrupt();
                break;
            }
            response = request.get();
        }

        return response;
    }

    private static long getWaitTime(Response response, int attempt) {
        String retryAfter = response.header("Retry-After");
        if (retryAfter != null) {
            try {
                long seconds = Long.parseLong(retryAfter);
                if (seconds > 0) {
                    return seconds * 1000;
                }
            } catch (NumberFormatException e) {
                log.debug("Non-numeric Retry-After header: '{}', using exponential backoff", retryAfter);
            }
        }
        return DEFAULT_BACKOFF_MS * (long) Math.pow(BACKOFF_MULTIPLIER, attempt - 1);
    }
}
