package com.airalo.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Sim(
        Integer id,
        @JsonProperty("created_at") String createdAt,
        String iccid,
        String lpa,
        Object imsis,
        @JsonProperty("matching_id") String matchingId,
        String qrcode,
        @JsonProperty("qrcode_url") String qrcodeUrl,
        @JsonProperty("airalo_code") String airaloCode,
        @JsonProperty("apn_type") String apnType,
        @JsonProperty("apn_value") String apnValue,
        @JsonProperty("is_roaming") Boolean isRoaming,
        @JsonProperty("confirmation_code") String confirmationCode,
        Object apn,
        Object msisdn,
        @JsonProperty("direct_apple_installation_url") String directAppleInstallationUrl,
        @JsonProperty("voucher_code") String voucherCode,
        @JsonProperty("brand_settings_name") String brandSettingsName,
        Object sharing,
        Boolean recycled,
        @JsonProperty("recycled_at") String recycledAt,
        Order simable
) {
}
