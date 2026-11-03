package com.codename_vp.serverside.Service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

class LoginAttemptServiceTests {

    private static final String ADDRESS = "203.0.113.5";

    private MutableClock clock;
    private LoginAttemptService attempts;

    @BeforeEach
    void setUp() {
        clock = new MutableClock(Instant.parse("2026-01-01T12:00:00Z"));
        attempts = new LoginAttemptService(clock);
    }

    @Test
    void blocksAccountAfterTooManyFailures() {
        failTimes(LoginAttemptService.MAX_FAILURES_PER_ACCOUNT - 1, "abc123");
        assertDoesNotThrow(() -> attempts.checkAllowed(ADDRESS, "abc123"));

        attempts.recordFailure(ADDRESS, "abc123");
        assertBlocked("abc123");
    }

    @Test
    void usernameMatchIgnoresCase() {
        failTimes(LoginAttemptService.MAX_FAILURES_PER_ACCOUNT, "ABC123");
        assertBlocked("abc123");
    }

    @Test
    void blockedAccountDoesNotBlockOtherUsernames() {
        failTimes(LoginAttemptService.MAX_FAILURES_PER_ACCOUNT, "abc123");
        assertDoesNotThrow(() -> attempts.checkAllowed(ADDRESS, "def123"));
    }

    @Test
    void blocksAddressAfterFailuresAcrossManyUsernames() {
        for (int i = 0; i < LoginAttemptService.MAX_FAILURES_PER_ADDRESS; i++) {
            attempts.recordFailure(ADDRESS, "user" + i);
        }
        assertBlocked("someone-new");
        assertDoesNotThrow(() -> attempts.checkAllowed("198.51.100.7", "someone-new"));
    }

    @Test
    void failuresExpireAfterTheWindow() {
        failTimes(LoginAttemptService.MAX_FAILURES_PER_ACCOUNT, "abc123");
        clock.advance(LoginAttemptService.WINDOW.plusSeconds(1));
        assertDoesNotThrow(() -> attempts.checkAllowed(ADDRESS, "abc123"));
    }

    @Test
    void successClearsAccountFailures() {
        failTimes(LoginAttemptService.MAX_FAILURES_PER_ACCOUNT - 1, "abc123");
        attempts.recordSuccess(ADDRESS, "abc123");
        attempts.recordFailure(ADDRESS, "abc123");
        assertDoesNotThrow(() -> attempts.checkAllowed(ADDRESS, "abc123"));
    }

    private void failTimes(int times, String userName) {
        for (int i = 0; i < times; i++) {
            attempts.recordFailure(ADDRESS, userName);
        }
    }

    private void assertBlocked(String userName) {
        ResponseStatusException e = assertThrows(ResponseStatusException.class,
                () -> attempts.checkAllowed(ADDRESS, userName));
        assertEquals(HttpStatus.TOO_MANY_REQUESTS, e.getStatusCode());
    }

    private static class MutableClock extends Clock {
        private Instant now;

        MutableClock(Instant now) {
            this.now = now;
        }

        void advance(Duration duration) {
            now = now.plus(duration);
        }

        @Override
        public Instant instant() {
            return now;
        }

        @Override
        public ZoneId getZone() {
            return ZoneOffset.UTC;
        }

        @Override
        public Clock withZone(ZoneId zone) {
            return this;
        }
    }
}
