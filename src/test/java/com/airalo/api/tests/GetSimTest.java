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
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Epic("Airalo Partner API")
@Feature("Get eSIM Details")
@DisplayName("Get eSIM Details Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class GetSimTest extends BaseTest {

    private static final TypeRef<ApiResponse<Order>> ORDER_TYPE = new TypeRef<>() {};
    private static final TypeRef<ApiResponse<Sim>> SIM_TYPE = new TypeRef<>() {};

    private static String token;
    private static List<String> orderIccids;
    private static Order orderData;

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
        assertFalse(orderIccids.isEmpty(), "Order should contain at least one eSIM");
    }

    @Test
    @Tag("happy-path")
    @org.junit.jupiter.api.Order(1)
    @DisplayName("3.1 - Should get eSIM details for each ICCID from order")
    void shouldGetEsimDetailsForEachIccid() {
        for (String iccid : orderIccids) {
            Response response = SimClient.getSimByIccid(token, iccid);
            assertEquals(200, response.statusCode(),
                    "Failed to get eSIM details for ICCID: " + iccid);
        }
    }

    @Test
    @Tag("happy-path")
    @org.junit.jupiter.api.Order(2)
    @DisplayName("3.2 - Should return correct ICCID in response")
    void shouldReturnCorrectIccid() {
        String iccid = orderIccids.get(0);
        Response response = SimClient.getSimByIccid(token, iccid);

        assertEquals(200, response.statusCode());
        ApiResponse<Sim> body = response.as(SIM_TYPE);
        assertEquals(iccid, body.data().iccid());
    }

    @Test
    @Tag("happy-path")
    @org.junit.jupiter.api.Order(3)
    @DisplayName("3.3 - Should return success message")
    void shouldReturnSuccessMessage() {
        Response response = SimClient.getSimByIccid(token, orderIccids.get(0));

        assertEquals(200, response.statusCode());
        ApiResponse<Sim> body = response.as(SIM_TYPE);
        assertEquals("success", body.meta().message());
    }

    @Test
    @Tag("happy-path")
    @org.junit.jupiter.api.Order(4)
    @DisplayName("3.4 - Should contain expected eSIM fields")
    void shouldContainExpectedEsimFields() {
        Response response = SimClient.getSimByIccid(token, orderIccids.get(0));

        assertEquals(200, response.statusCode());
        ApiResponse<Sim> body = response.as(SIM_TYPE);
        Sim sim = body.data();

        assertNotNull(sim.id(), "id should not be null");
        assertNotNull(sim.iccid(), "iccid should not be null");
        assertNotNull(sim.lpa(), "lpa should not be null");
        assertNotNull(sim.matchingId(), "matching_id should not be null");
        assertNotNull(sim.qrcode(), "qrcode should not be null");
        assertNotNull(sim.qrcodeUrl(), "qrcode_url should not be null");
        assertNotNull(sim.directAppleInstallationUrl(), "direct_apple_installation_url should not be null");
        assertNotNull(sim.apnType(), "apn_type should not be null");
        assertNotNull(sim.apnValue(), "apn_value should not be null");
        assertNotNull(sim.isRoaming(), "is_roaming should not be null");
        assertNotNull(sim.apn(), "apn should not be null");
        assertNotNull(sim.sharing(), "sharing should not be null");
    }

    @Test
    @Tag("known-bug")
    @org.junit.jupiter.api.Order(5)
    @DisplayName("3.5 - BUG: Should contain simable object with order details (status is null)")
    void shouldContainSimableObjectWithOrderDetails() {
        Response response = SimClient.getSimByIccid(token, orderIccids.get(0));

        assertEquals(200, response.statusCode());
        ApiResponse<Sim> body = response.as(SIM_TYPE);
        Order simable = body.data().simable();

        assertNotNull(simable.id(), "simable.id should not be null");
        assertNotNull(simable.code(), "simable.code should not be null");
        assertNotNull(simable.packageId(), "simable.package_id should not be null");
        assertNotNull(simable.quantity(), "simable.quantity should not be null");
        assertNotNull(simable.type(), "simable.type should not be null");
        assertNotNull(simable.esimType(), "simable.esim_type should not be null");
        assertNotNull(simable.validity(), "simable.validity should not be null");
        assertNotNull(simable.packageName(), "simable.package should not be null");
        assertNotNull(simable.data(), "simable.data should not be null");
        assertNotNull(simable.price(), "simable.price should not be null");
        // KNOWN ISSUE: API does not populate status in simable object via GET /sims/{iccid}
        assertNotNull(simable.status(), "simable.status should not be null");
        assertNotNull(simable.createdAt(), "simable.created_at should not be null");
    }

    @Test
    @Tag("known-bug")
    @org.junit.jupiter.api.Order(6)
    @DisplayName("3.6 - BUG: Should have simable status as Completed (status is null)")
    void shouldHaveSimableStatusAsCompleted() {
        // KNOWN ISSUE: API does not populate status in simable object via GET /sims/{iccid}
        Response response = SimClient.getSimByIccid(token, orderIccids.get(0));

        assertEquals(200, response.statusCode());
        ApiResponse<Sim> body = response.as(SIM_TYPE);
        assertNotNull(body.data().simable().status(), "simable.status should not be null");
        assertEquals("Completed", body.data().simable().status().name());
    }

    @Test
    @Tag("happy-path")
    @org.junit.jupiter.api.Order(7)
    @DisplayName("3.6b - Should have correct package_id in simable")
    void shouldHaveCorrectPackageIdInSimable() {
        Response response = SimClient.getSimByIccid(token, orderIccids.get(0));

        assertEquals(200, response.statusCode());
        ApiResponse<Sim> body = response.as(SIM_TYPE);
        assertEquals(ApiConfig.getPackageId(), body.data().simable().packageId());
    }

    @Test
    @Tag("happy-path")
    @org.junit.jupiter.api.Order(8)
    @DisplayName("3.7 - Should have consistent data between order and get-details responses")
    void shouldHaveConsistentDataBetweenOrderAndGetDetails() {
        String iccid = orderIccids.get(0);
        Response simResponse = SimClient.getSimByIccid(token, iccid);

        assertEquals(200, simResponse.statusCode());
        ApiResponse<Sim> body = simResponse.as(SIM_TYPE);
        Sim sim = body.data();

        assertEquals(iccid, sim.iccid());

        Sim orderSim = orderData.sims().get(0);
        assertEquals(orderSim.lpa(), sim.lpa(), "LPA should be consistent");
        assertEquals(orderSim.matchingId(), sim.matchingId(), "Matching ID should be consistent");
        assertEquals(orderSim.qrcode(), sim.qrcode(), "QR code should be consistent");
    }

    @Test
    @Tag("negative")
    @org.junit.jupiter.api.Order(9)
    @DisplayName("3.8 - Should return error for invalid ICCID")
    void shouldReturnErrorForInvalidIccid() {
        Response response = SimClient.getSimByIccid(token, "0000000000000000000");

        assertTrue(response.statusCode() == 404 || response.statusCode() == 422,
                "Expected 404 or 422 but got: " + response.statusCode());
    }

    @Test
    @Tag("negative")
    @org.junit.jupiter.api.Order(10)
    @DisplayName("3.9 - Should reject request without authorization")
    void shouldRejectRequestWithoutAuthorization() {
        Response response = SimClient.getSimWithoutAuth(orderIccids.get(0));

        assertEquals(401, response.statusCode());
    }

    @Test
    @Tag("negative")
    @org.junit.jupiter.api.Order(11)
    @DisplayName("3.10 - Should reject request with invalid token")
    void shouldRejectRequestWithInvalidToken() {
        Response response = SimClient.getSimByIccid("invalid_token", orderIccids.get(0));

        assertEquals(401, response.statusCode());
    }

    @Test
    @Tag("known-bug")
    @org.junit.jupiter.api.Order(12)
    @DisplayName("3.11 - BUG: Empty ICCID should return error (returns 200)")
    void shouldReturnErrorForEmptyIccid() {
        // KNOWN ISSUE: API returns 200 with a list instead of rejecting empty ICCID
        Response response = SimClient.getSimByIccid(token, "");

        assertTrue(response.statusCode() == 404 || response.statusCode() == 422,
                "Expected 404 or 422 but got: " + response.statusCode());
    }

    @Test
    @Tag("manual")
    @org.junit.jupiter.api.Order(13)
    @DisplayName("3.12 - MANUAL: Sharing link page should show correct ICCID")
    @Disabled("Requires browser verification - sharing.link page displays a different ICCID than expected")
    void sharingLinkShouldShowCorrectIccid() {
    }

    @Test
    @Tag("manual")
    @org.junit.jupiter.api.Order(14)
    @DisplayName("3.13 - MANUAL: direct_apple_installation_url should be functional")
    @Disabled("Requires browser verification - URL accessibility cannot be validated via API alone")
    void directAppleInstallationUrlShouldBeFunctional() {
    }

    @Test
    @Tag("manual")
    @org.junit.jupiter.api.Order(15)
    @DisplayName("3.14 - MANUAL: PDF attachment should be present in sharing email")
    @Disabled("Requires email inbox verification - PDF attachment not received when sharing_option[]=pdf")
    void pdfAttachmentShouldBePresentInEmail() {
    }

    @Test
    @Tag("manual")
    @org.junit.jupiter.api.Order(16)
    @DisplayName("3.15 - MANUAL: sharing.link should only be populated when sharing_option includes link")
    @Disabled("Requires creating orders with different sharing_option values and verifying sharing.link field via browser")
    void sharingLinkShouldOnlyPopulateWhenOptionSelected() {
    }
}
