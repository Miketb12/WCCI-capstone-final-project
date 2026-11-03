package com.codename_vp.serverside.Component;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

class RequiredSettingsTests {

    private static final String VALID_SECRET = "a".repeat(RequiredSettings.MIN_JWT_SECRET_BYTES);

    @Test
    void acceptsValidSettings() {
        assertTrue(RequiredSettings.findProblems(VALID_SECRET, "key").isEmpty());
    }

    @Test
    void reportsEverythingThatIsMissing() {
        assertEquals(List.of("JWT_SECRET is not set", "RAWG_API_KEY is not set"),
                RequiredSettings.findProblems("", " "));
    }

    @Test
    void rejectsShortJwtSecret() {
        assertEquals(List.of("JWT_SECRET must be at least 32 characters"),
                RequiredSettings.findProblems("too-short", "key"));
    }
}
