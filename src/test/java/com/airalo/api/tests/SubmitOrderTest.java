package com.airalo.api.tests;

import com.airalo.api.client.AuthClient;
import com.airalo.api.client.OrderClient;
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

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@Epic("Airalo Partner API")
@Feature("Submit Order")
@DisplayName("Submit Order Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SubmitOrderTest extends BaseTest {

    private static final TypeRef<ApiResponse<Order>> ORDER_TYPE = new TypeRef<>() {};

    private static String token;

    @BeforeAll
    static void setUp() {
        token = AuthClient.getValidToken();
        assertNotNull(token, "Failed to obtain access token");
    }

    @Test
    @Tag("happy-path")
    @org.junit.jupiter.api.Order(1)
    @DisplayName("2.1 - Should submit order for 6 eSIMs successfully")
    void shouldSubmitOrderForSixEsims() {
        Response response = OrderClient.submitOrder(
                token,
                ApiConfig.getPackageId(),
                ApiConfig.getOrderQuantity()
        );

        assertEquals(200, response.statusCode());
        ApiResponse<Order> body = response.as(ORDER_TYPE);
        assertEquals("success", body.meta().message());
        assertEquals(ApiConfig.getOrderQuantity(), body.data().sims().size());
    }

    @Test
    @Tag("happy-path")
    @org.junit.jupiter.api.Order(2)
    @DisplayName("2.2 - Should return correct package_id in response")
    void shouldReturnCorrectPackageId() {
        Response response = OrderClient.submitOrder(token, ApiConfig.getPackageId(), 1);

        assertEquals(200, response.statusCode());
        ApiResponse<Order> body = response.as(ORDER_TYPE);
        assertEquals(ApiConfig.getPackageId(), body.data().packageId());
    }

    @Test
    @Tag("happy-path")
    @org.junit.jupiter.api.Order(3)
    @DisplayName("2.3 - Should return correct quantity in response")
    void shouldReturnCorrectQuantity() {
        int quantity = ApiConfig.getOrderQuantity();
        Response response = OrderClient.submitOrder(token, ApiConfig.getPackageId(), quantity);

        assertEquals(200, response.statusCode());
        ApiResponse<Order> body = response.as(ORDER_TYPE);
        assertEquals(quantity, body.data().quantity());
    }

    @Test
    @Tag("happy-path")
    @org.junit.jupiter.api.Order(4)
    @DisplayName("2.4 - Should return unique ICCIDs for each sim")
    void shouldReturnUniqueIccids() {
        Response response = OrderClient.submitOrder(
                token,
                ApiConfig.getPackageId(),
                ApiConfig.getOrderQuantity()
        );

        assertEquals(200, response.statusCode());
        ApiResponse<Order> body = response.as(ORDER_TYPE);

        List<String> iccids = body.data().sims().stream()
                .map(Sim::iccid)
                .toList();
        Set<String> uniqueIccids = new HashSet<>(iccids);
        assertEquals(iccids.size(), uniqueIccids.size(), "All ICCIDs should be unique");
    }

    @Test
    @Tag("happy-path")
    @org.junit.jupiter.api.Order(5)
    @DisplayName("2.5 - Should return success message")
    void shouldReturnSuccessMessage() {
        Response response = OrderClient.submitOrder(token, ApiConfig.getPackageId(), 1);

        assertEquals(200, response.statusCode());
        ApiResponse<Order> body = response.as(ORDER_TYPE);
        assertEquals("success", body.meta().message());
    }

    @Test
    @Tag("happy-path")
    @org.junit.jupiter.api.Order(6)
    @DisplayName("2.6 - Should contain expected order fields")
    void shouldContainExpectedOrderFields() {
        Response response = OrderClient.submitOrder(token, ApiConfig.getPackageId(), 1);

        assertEquals(200, response.statusCode());
        ApiResponse<Order> body = response.as(ORDER_TYPE);
        Order order = body.data();

        assertNotNull(order.id(), "id should not be null");
        assertNotNull(order.code(), "code should not be null");
        assertNotNull(order.currency(), "currency should not be null");
        assertNotNull(order.type(), "type should not be null");
        assertNotNull(order.esimType(), "esim_type should not be null");
        assertNotNull(order.validity(), "validity should not be null");
        assertNotNull(order.packageName(), "package should not be null");
        assertNotNull(order.data(), "data should not be null");
        assertNotNull(order.price(), "price should not be null");
        assertNotNull(order.createdAt(), "created_at should not be null");
    }

    @Test
    @Tag("happy-path")
    @org.junit.jupiter.api.Order(7)
    @DisplayName("2.7 - Should contain expected sim fields")
    void shouldContainExpectedSimFields() {
        Response response = OrderClient.submitOrder(token, ApiConfig.getPackageId(), 1);

        assertEquals(200, response.statusCode());
        ApiResponse<Order> body = response.as(ORDER_TYPE);
        Sim sim = body.data().sims().get(0);

        assertNotNull(sim.id(), "id should not be null");
        assertNotNull(sim.iccid(), "iccid should not be null");
        assertNotNull(sim.lpa(), "lpa should not be null");
        assertNotNull(sim.matchingId(), "matching_id should not be null");
        assertNotNull(sim.qrcode(), "qrcode should not be null");
        assertNotNull(sim.qrcodeUrl(), "qrcode_url should not be null");
        assertNotNull(sim.apnType(), "apn_type should not be null");
        assertNotNull(sim.apnValue(), "apn_value should not be null");
        assertNotNull(sim.isRoaming(), "is_roaming should not be null");
        assertNotNull(sim.directAppleInstallationUrl(), "direct_apple_installation_url should not be null");
    }

    @Test
    @Tag("happy-path")
    @org.junit.jupiter.api.Order(8)
    @DisplayName("2.8 - Should reflect description in response")
    void shouldReflectDescriptionInResponse() {
        Map<String, Object> params = Map.of(
                "package_id", ApiConfig.getPackageId(),
                "quantity", 1,
                "description", "Test order description"
        );

        Response response = OrderClient.submitOrder(token, params);

        assertEquals(200, response.statusCode());
        ApiResponse<Order> body = response.as(ORDER_TYPE);
        assertEquals("Test order description", body.data().description());
    }

    @Test
    @Tag("happy-path")
    @org.junit.jupiter.api.Order(9)
    @DisplayName("2.9 - Should submit order with to_email and sharing_option")
    void shouldSubmitOrderWithEmailAndSharingOption() {
        Map<String, Object> params = Map.of(
                "package_id", ApiConfig.getPackageId(),
                "quantity", 1,
                "to_email", "test@example.com",
                "sharing_option[]", "link"
        );

        Response response = OrderClient.submitOrder(token, params);

        assertEquals(200, response.statusCode());
    }

    @Test
    @Tag("negative")
    @org.junit.jupiter.api.Order(10)
    @DisplayName("2.10 - Should reject order without authorization")
    void shouldRejectOrderWithoutAuthorization() {
        Response response = OrderClient.submitOrderWithoutAuth(ApiConfig.getPackageId(), 1);

        assertEquals(401, response.statusCode());
    }

    @Test
    @Tag("negative")
    @org.junit.jupiter.api.Order(10)
    @DisplayName("2.11 - Should reject order with invalid token")
    void shouldRejectOrderWithInvalidToken() {
        Response response = OrderClient.submitOrder("invalid_token", ApiConfig.getPackageId(), 1);

        assertEquals(401, response.statusCode());
    }

    @Test
    @Tag("negative")
    @org.junit.jupiter.api.Order(11)
    @DisplayName("2.12 - Should reject order with invalid package_id")
    void shouldRejectOrderWithInvalidPackageId() {
        Response response = OrderClient.submitOrder(token, "non-existent-package", 1);

        assertEquals(422, response.statusCode());
    }

    @Test
    @Tag("negative")
    @org.junit.jupiter.api.Order(12)
    @DisplayName("2.13 - Should reject order with quantity 0")
    void shouldRejectOrderWithQuantityZero() {
        Response response = OrderClient.submitOrder(token, ApiConfig.getPackageId(), 0);

        assertEquals(422, response.statusCode());
    }

    @Test
    @Tag("negative")
    @org.junit.jupiter.api.Order(13)
    @DisplayName("2.14 - Should reject order with quantity above 50")
    void shouldRejectOrderWithQuantityAbove50() {
        Response response = OrderClient.submitOrder(token, ApiConfig.getPackageId(), 51);

        assertEquals(422, response.statusCode());
    }

    @Test
    @Tag("negative")
    @org.junit.jupiter.api.Order(14)
    @DisplayName("2.15 - Should reject order with negative quantity")
    void shouldRejectOrderWithNegativeQuantity() {
        Response response = OrderClient.submitOrder(token, ApiConfig.getPackageId(), -1);

        assertEquals(422, response.statusCode());
    }

    @Test
    @Tag("negative")
    @org.junit.jupiter.api.Order(15)
    @DisplayName("2.16 - Should reject order with missing package_id")
    void shouldRejectOrderWithMissingPackageId() {
        Map<String, Object> params = Map.of("quantity", 1);

        Response response = OrderClient.submitOrder(token, params);

        assertEquals(422, response.statusCode());
    }

    @Test
    @Tag("negative")
    @org.junit.jupiter.api.Order(16)
    @DisplayName("2.17 - Should reject order with missing quantity")
    void shouldRejectOrderWithMissingQuantity() {
        Map<String, Object> params = Map.of("package_id", ApiConfig.getPackageId());

        Response response = OrderClient.submitOrder(token, params);

        assertEquals(422, response.statusCode());
    }

    @Test
    @Tag("negative")
    @org.junit.jupiter.api.Order(17)
    @DisplayName("2.18 - Should reject order with invalid brand_settings_name")
    void shouldRejectOrderWithInvalidBrandSettingsName() {
        Map<String, Object> params = Map.of(
                "package_id", ApiConfig.getPackageId(),
                "quantity", 1,
                "brand_settings_name", "invalid-brand"
        );

        Response response = OrderClient.submitOrder(token, params);

        assertEquals(422, response.statusCode());
    }

    @Test
    @Tag("negative")
    @org.junit.jupiter.api.Order(18)
    @DisplayName("2.19 - Should reject order with to_email but missing sharing_option")
    void shouldRejectOrderWithEmailButMissingSharingOption() {
        Map<String, Object> params = Map.of(
                "package_id", ApiConfig.getPackageId(),
                "quantity", 1,
                "to_email", "test@example.com"
        );

        Response response = OrderClient.submitOrder(token, params);

        assertEquals(422, response.statusCode());
    }

    @Test
    @Tag("negative")
    @org.junit.jupiter.api.Order(19)
    @DisplayName("2.20 - Should reject order with invalid email format")
    void shouldRejectOrderWithInvalidEmailFormat() {
        Map<String, Object> params = Map.of(
                "package_id", ApiConfig.getPackageId(),
                "quantity", 1,
                "to_email", "not-an-email",
                "sharing_option[]", "link"
        );

        Response response = OrderClient.submitOrder(token, params);

        assertEquals(422, response.statusCode());
    }

    @Test
    @Tag("known-bug")
    @org.junit.jupiter.api.Order(20)
    @DisplayName("2.21 - BUG: Should return 422 when body is empty (returns 422 with Accept header)")
    void shouldReturn422WhenBodyIsEmpty() {
        Response response = OrderClient.submitOrderEmptyBody(token);

        assertEquals(422, response.statusCode());
    }

    @Test
    @Tag("known-bug")
    @org.junit.jupiter.api.Order(21)
    @DisplayName("2.22 - BUG: Should return 500 when Accept header is missing and body is empty")
    void shouldReturn500WhenAcceptHeaderMissingAndBodyEmpty() {
        Response response = OrderClient.submitOrderWithoutAcceptHeader(token);

        assertEquals(500, response.statusCode());
    }

    @Test
    @Tag("happy-path")
    @org.junit.jupiter.api.Order(22)
    @DisplayName("2.24 - Should submit order with multiple sharing options and copy addresses")
    void shouldSubmitOrderWithMultipleSharingOptionsAndCopyAddresses() {
        Response response = OrderClient.submitOrderWithSharing(
                token,
                ApiConfig.getPackageId(),
                1,
                "test@example.com",
                List.of("pdf", "link"),
                List.of("copy1@example.com", "copy2@example.com")
        );

        assertEquals(200, response.statusCode());
    }

    @Test
    @Tag("negative")
    @org.junit.jupiter.api.Order(23)
    @DisplayName("2.25 - Should reject order with invalid copy_address format")
    void shouldRejectOrderWithInvalidCopyAddressFormat() {
        Response response = OrderClient.submitOrderWithSharing(
                token,
                ApiConfig.getPackageId(),
                1,
                "test@example.com",
                List.of("link"),
                List.of("invalid-email")
        );

        assertEquals(422, response.statusCode());
    }

    @Test
    @Tag("known-bug")
    @org.junit.jupiter.api.Order(24)
    @DisplayName("2.22 - BUG: Should reject invalid email format (valid_email@com accepted)")
    void shouldRejectInvalidEmailWithoutTld() {
        // KNOWN ISSUE: API accepts "valid_email@com" as valid email
        Map<String, Object> params = Map.of(
                "package_id", ApiConfig.getPackageId(),
                "quantity", 1,
                "to_email", "valid_email@com",
                "sharing_option[]", "link"
        );

        Response response = OrderClient.submitOrder(token, params);

        assertEquals(422, response.statusCode());
    }

    @Test
    @Tag("known-bug")
    @org.junit.jupiter.api.Order(25)
    @DisplayName("2.26 - BUG: Should reject invalid copy_address format (missing TLD accepted)")
    void shouldRejectInvalidCopyAddressWithoutTld() {
        // KNOWN ISSUE: API accepts "user@domain" without TLD as valid email
        Response response = OrderClient.submitOrderWithSharing(
                token,
                ApiConfig.getPackageId(),
                1,
                "test@example.com",
                List.of("link"),
                List.of("user@yopmailcom")
        );

        assertEquals(422, response.statusCode());
    }

    @Test
    @Tag("known-bug")
    @org.junit.jupiter.api.Order(26)
    @DisplayName("2.23 - BUG: Should reject or honor type parameter (type=esim creates sim)")
    void shouldRejectOrHonorTypeParameter() {
        // KNOWN ISSUE: type=esim is ignored, order is created as "sim"
        Map<String, Object> params = Map.of(
                "package_id", ApiConfig.getPackageId(),
                "quantity", 1,
                "type", "esim"
        );

        Response response = OrderClient.submitOrder(token, params);
        ApiResponse<Order> body = response.as(ORDER_TYPE);

        assertEquals(200, response.statusCode());
        assertEquals("esim", body.data().type());
    }
}
