package com.codename_vp.serverside.Component;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;

import com.codename_vp.serverside.Repository.UserRepo;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

// Sends real requests through the interceptor, using the seed users from Populator
@SpringBootTest(properties = {
        "JWT_SECRET=" + AuthInterceptorTests.SECRET,
        "RAWG_API_KEY=test-key"
})
@AutoConfigureMockMvc
class AuthInterceptorTests {

    static final String SECRET = "test-secret-test-secret-test-secret-1234";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserRepo userRepo;

    private Long abcId;
    private Long defId;

    @BeforeEach
    void findSeedUsers() {
        abcId = userRepo.findByUserName("abc123").orElseThrow().getId();
        defId = userRepo.findByUserName("def123").orElseThrow().getId();
    }

    private String bearer(String token) {
        return "Bearer " + token;
    }

    private String abcToken() {
        return bearer(jwtTokenUtil.generateToken("abc123"));
    }

    @Test
    void missingTokenReturns401() throws Exception {
        mockMvc.perform(get("/user/id/" + abcId))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void malformedTokenReturns401() throws Exception {
        mockMvc.perform(get("/user/id/" + abcId).header(HttpHeaders.AUTHORIZATION, bearer("not-a-jwt")))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(get("/user/id/" + abcId).header(HttpHeaders.AUTHORIZATION, "Token " + jwtTokenUtil.generateToken("abc123")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void tokenSignedWithAnotherKeyReturns401() throws Exception {
        String forged = Jwts.builder()
                .subject("abc123")
                .signWith(Keys.hmacShaKeyFor("some-other-secret-some-other-secret".getBytes(StandardCharsets.UTF_8)))
                .compact();

        mockMvc.perform(get("/user/id/" + abcId).header(HttpHeaders.AUTHORIZATION, bearer(forged)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void expiredTokenReturns401() throws Exception {
        String expired = Jwts.builder()
                .subject("abc123")
                .issuedAt(new Date(System.currentTimeMillis() - 7_200_000))
                .expiration(new Date(System.currentTimeMillis() - 3_600_000))
                .signWith(Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8)))
                .compact();

        mockMvc.perform(get("/user/id/" + abcId).header(HttpHeaders.AUTHORIZATION, bearer(expired)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void tokenForUnknownUserReturns401() throws Exception {
        mockMvc.perform(get("/user/id/" + abcId).header(HttpHeaders.AUTHORIZATION, bearer(jwtTokenUtil.generateToken("ghost"))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void ownDataReturns200() throws Exception {
        mockMvc.perform(get("/user/id/" + abcId).header(HttpHeaders.AUTHORIZATION, abcToken()))
                .andExpect(status().isOk());
        mockMvc.perform(get("/user/" + abcId + "/wishlist").header(HttpHeaders.AUTHORIZATION, abcToken()))
                .andExpect(status().isOk());
        mockMvc.perform(get("/user/username/abc123").header(HttpHeaders.AUTHORIZATION, abcToken()))
                .andExpect(status().isOk());
    }

    @Test
    void anotherUsersDataReturns403() throws Exception {
        mockMvc.perform(get("/user/id/" + defId).header(HttpHeaders.AUTHORIZATION, abcToken()))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/user/" + defId + "/ownedlist").header(HttpHeaders.AUTHORIZATION, abcToken()))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/user/username/def123").header(HttpHeaders.AUTHORIZATION, abcToken()))
                .andExpect(status().isForbidden());
    }

    @Test
    void userIdQueryParameterIsChecked() throws Exception {
        // Stopped by the interceptor, so RAWG is never called
        mockMvc.perform(post("/user/add-to-wish-list/3328").param("userId", defId.toString())
                .header(HttpHeaders.AUTHORIZATION, abcToken()))
                .andExpect(status().isForbidden());
    }

    @Test
    void corsPreflightIsNotBlocked() throws Exception {
        mockMvc.perform(options("/user/id/" + abcId)
                .header(HttpHeaders.ORIGIN, "http://localhost:5173")
                .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "GET")
                .header(HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS, "authorization"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "http://localhost:5173"));
    }

    @Test
    void unknownRouteUnderUserReturns404() throws Exception {
        mockMvc.perform(get("/user/users").header(HttpHeaders.AUTHORIZATION, abcToken()))
                .andExpect(status().isNotFound());
    }

    @Test
    void routesOutsideUserDoNotNeedAToken() throws Exception {
        mockMvc.perform(get("/platform"))
                .andExpect(status().isOk());
    }
}
