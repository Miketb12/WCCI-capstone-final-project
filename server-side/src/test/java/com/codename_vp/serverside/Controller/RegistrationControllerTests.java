package com.codename_vp.serverside.Controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

// Sends real requests to POST /register, which needs no token
@SpringBootTest(properties = {
        "JWT_SECRET=test-secret-test-secret-test-secret-1234",
        "RAWG_API_KEY=test-key"
})
@AutoConfigureMockMvc
class RegistrationControllerTests {

    @Autowired
    private MockMvc mockMvc;

    private String body(String userName, String password) {
        return """
                {"userName": "%s", "password": "%s", "email": "%s@example.com"}
                """.formatted(userName, password, userName);
    }

    @Test
    void returns201WithoutThePassword() throws Exception {
        mockMvc.perform(post("/register").contentType(MediaType.APPLICATION_JSON).content(body("webplayer", "secret-pass")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userName").value("webplayer"))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void takenUsernameReturns400() throws Exception {
        mockMvc.perform(post("/register").contentType(MediaType.APPLICATION_JSON).content(body("abc123", "secret-pass")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void blankPasswordReturns400() throws Exception {
        mockMvc.perform(post("/register").contentType(MediaType.APPLICATION_JSON).content(body("blankpass", "")))
                .andExpect(status().isBadRequest());
    }
}
