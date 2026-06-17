package com.airalo.api.tests;

import com.airalo.api.client.AuthClient;
import com.airalo.api.client.OrderClient;
import com.airalo.api.client.SimClient;
import com.airalo.api.config.ApiConfig;
import com.airalo.api.model.ApiResponse;
import com.airalo.api.model.Order;
import com.airalo.api.model.Sim;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Epic("Airalo Partner API")
@Feature("Cross-Endpoint Validation")
@DisplayName("Cross-Endpoint Validation Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CrossEndpointValidationTest extends BaseTest {

    private static final TypeRef<ApiResponse<Order>> ORDER_TYPE = new TypeRef<>() {};
    private static final TypeRef<ApiResponse<Sim>> SIM_TYPE = new TypeRef<>() {};

    private static String token;
    private static Order orderData;
    private static List<String> orderIccids;

    @BeforeAll
    static void setUp() {
        token = AuthClient.getValidToken();
        assertNotNull(token, "Failed to obtain access token");

        Response orderResponse = OrderClient.submitOrder(
                token,
                ApiConfig.getPackageId(),
                ApiConfig.getOrderQuantity()
        );
        assertEquals(200, orderResponse.statusCode(), "Failed to create order for test setup");

        ApiResponse<Order> body = orderResponse.as(ORDER_TYPE);
        orderData = body.data();
        orderIccids = orderData.sims().stream()
                .map(Sim::iccid)
                .toList();
    }

    @Test
    @Tag("happy-path")
    @org.junit.jupiter.api.Order(1)
    @DisplayName("4.1 - All 6 eSIMs from order should be retrievable individually")
    void allEsimsFromOrderShouldBeRetrievable() {
        assertEquals(ApiConfig.getOrderQuantity(), orderIccids.size(),
                "Order should contain " + ApiConfig.getOrderQuantity() + " eSIMs");

        for (String iccid : orderIccids) {
            Response response = SimClient.getSimByIccid(token, iccid);
            assertEquals(200, response.statusCode(),
                    "eSIM with ICCID " + iccid + " should be retrievable");

            ApiResponse<Sim> body = response.as(SIM_TYPE);
            assertEquals(iccid, body.data().iccid(),
                    "Returned ICCID should match requested ICCID");
        }
    }

    @Test
    @Tag("happy-path")
    @org.junit.jupiter.api.Order(2)
    @DisplayName("4.2 - Order package_id should match simable.package_id in GET response")
    void orderPackageIdShouldMatchSimablePackageId() {
        String orderPackageId = orderData.packageId();

        Response simResponse = SimClient.getSimByIccid(token, orderIccids.get(0));

        assertEquals(200, simResponse.statusCode());
        ApiResponse<Sim> body = simResponse.as(SIM_TYPE);
        assertEquals(orderPackageId, body.data().simable().packageId());
    }

    @Test
    @Tag("happy-path")
    @org.junit.jupiter.api.Order(3)
    @DisplayName("4.3 - Order quantity should match number of retrievable sims")
    void orderQuantityShouldMatchRetrievableSims() {
        int expectedQuantity = orderData.quantity();
        int retrievableCount = 0;

        for (String iccid : orderIccids) {
            Response response = SimClient.getSimByIccid(token, iccid);
            if (response.statusCode() == 200) {
                retrievableCount++;
            }
        }

        assertEquals(expectedQuantity, retrievableCount,
                "All ordered eSIMs should be individually retrievable");
    }
}
