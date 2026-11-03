package com.codename_vp.serverside.Service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

// Uses the seed users from Populator
@SpringBootTest(properties = {
        "JWT_SECRET=test-secret-test-secret-test-secret-1234",
        "RAWG_API_KEY=test-key"
})
class LoginServiceTests {

    @Autowired
    private LoginService loginService;

    @Test
    void logsInWithCorrectPassword() {
        assertEquals("abc123", loginService.loginUser("abc123", "1234", "192.0.2.1").getUserName());
    }

    @Test
    void unknownUserAndWrongPasswordGiveTheSameError() {
        ResponseStatusException unknownUser = assertThrows(ResponseStatusException.class,
                () -> loginService.loginUser("nobody", "1234", "192.0.2.2"));
        ResponseStatusException wrongPassword = assertThrows(ResponseStatusException.class,
                () -> loginService.loginUser("abc123", "wrong", "192.0.2.2"));

        assertEquals(HttpStatus.UNAUTHORIZED, unknownUser.getStatusCode());
        assertEquals(unknownUser.getStatusCode(), wrongPassword.getStatusCode());
        assertEquals(unknownUser.getReason(), wrongPassword.getReason());
    }

    @Test
    void blocksCorrectPasswordAfterTooManyFailures() {
        String address = "192.0.2.3";
        for (int i = 0; i < LoginAttemptService.MAX_FAILURES_PER_ACCOUNT; i++) {
            assertThrows(ResponseStatusException.class, () -> loginService.loginUser("def123", "wrong", address));
        }

        ResponseStatusException e = assertThrows(ResponseStatusException.class,
                () -> loginService.loginUser("def123", "1234", address));
        assertEquals(HttpStatus.TOO_MANY_REQUESTS, e.getStatusCode());
    }
}
