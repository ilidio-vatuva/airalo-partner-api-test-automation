package com.airalo.api.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ApiConfig {

    private static final Properties properties = new Properties();

    static {
        try (InputStream input = ApiConfig.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new RuntimeException("config.properties not found in classpath");
            }
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config.properties", e);
        }
    }

    public static String getBaseUrl() {
        return properties.getProperty("base.url");
    }

    public static String getClientId() {
        return properties.getProperty("client.id");
    }

    public static String getClientSecret() {
        return properties.getProperty("client.secret");
    }

    public static String getPackageId() {
        return properties.getProperty("package.id");
    }

    public static int getOrderQuantity() {
        return Integer.parseInt(properties.getProperty("order.quantity"));
    }
}
