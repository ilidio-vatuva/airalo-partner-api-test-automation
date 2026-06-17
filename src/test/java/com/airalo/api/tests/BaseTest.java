package com.airalo.api.tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseTest {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    @BeforeEach
    void logTestStart(TestInfo testInfo) {
        log.info("Running: {}", testInfo.getDisplayName());
    }
}
