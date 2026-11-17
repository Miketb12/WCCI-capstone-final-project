package com.codename_vp.serverside.Service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import com.codename_vp.serverside.Entity.User;
import com.codename_vp.serverside.Repository.UserRepo;

// Each test uses its own username, since the database is shared between tests
@SpringBootTest(properties = {
        "JWT_SECRET=test-secret-test-secret-test-secret-1234",
        "RAWG_API_KEY=test-key"
})
class RegistrationServiceTests {

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private LoginService loginService;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void savesNewUserWithHashedPassword() {
        registrationService.registerUser("newplayer", "secret-pass", "newplayer@example.com");

        User saved = userRepo.findByUserName("newplayer").orElseThrow();
        assertEquals("newplayer@example.com", saved.getEmail());
        assertNotEquals("secret-pass", saved.getPassword());
        assertTrue(passwordEncoder.matches("secret-pass", saved.getPassword()));
    }

    @Test
    void newUserCanLogIn() {
        registrationService.registerUser("freshlogin", "secret-pass", "freshlogin@example.com");

        assertEquals("freshlogin", loginService.loginUser("freshlogin", "secret-pass", "192.0.2.10").getUserName());
    }

    @Test
    void rejectsTakenUsername() {
        long usersBefore = userRepo.count();

        assertBadRequest("abc123", "another-pass");
        assertEquals(usersBefore, userRepo.count());
    }

    @Test
    void rejectsMissingOrBlankFields() {
        assertBadRequest(null, "secret-pass");
        assertBadRequest("   ", "secret-pass");
        assertBadRequest("nopassword", null);
        assertBadRequest("nopassword", "");
        assertTrue(userRepo.findByUserName("nopassword").isEmpty());
    }

    private void assertBadRequest(String userName, String password) {
        ResponseStatusException e = assertThrows(ResponseStatusException.class,
                () -> registrationService.registerUser(userName, password, "test@example.com"));
        assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
    }
}
