package com.airalo.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Order(
        Integer id,
        String code,
        @JsonProperty("package_id") String packageId,
        Integer quantity,
        String currency,
        String type,
        @JsonProperty("esim_type") String esimType,
        Integer validity,
        @JsonProperty("package") String packageName,
        String data,
        Object price,
        String description,
        @JsonProperty("created_at") String createdAt,
        @JsonProperty("net_price") Object netPrice,
        @JsonProperty("brand_settings_name") String brandSettingsName,
        Status status,
        List<Sim> sims
) {
}
