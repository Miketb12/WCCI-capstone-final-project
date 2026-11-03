package com.codename_vp.serverside.Component;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

// Stops startup with a readable message when server-side/.env is missing or incomplete
@Component
public class RequiredSettings implements InitializingBean {

    static final int MIN_JWT_SECRET_BYTES = 32;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${rawg.api.key}")
    private String rawgApiKey;

    static List<String> findProblems(String jwtSecret, String rawgApiKey) {
        List<String> problems = new ArrayList<>();
        if (jwtSecret == null || jwtSecret.isBlank()) {
            problems.add("JWT_SECRET is not set");
        } else if (jwtSecret.getBytes(StandardCharsets.UTF_8).length < MIN_JWT_SECRET_BYTES) {
            problems.add("JWT_SECRET must be at least " + MIN_JWT_SECRET_BYTES + " characters");
        }
        if (rawgApiKey == null || rawgApiKey.isBlank()) {
            problems.add("RAWG_API_KEY is not set");
        }
        return problems;
    }

    @Override
    public void afterPropertiesSet() {
        List<String> problems = findProblems(jwtSecret, rawgApiKey);
        if (!problems.isEmpty()) {
            throw new IllegalStateException("Server settings are missing or invalid: " + String.join("; ", problems)
                    + ". Copy server-side/.env.example to server-side/.env and fill it in.");
        }
    }
}
