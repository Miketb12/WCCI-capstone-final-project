package com.codename_vp.serverside.Service;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

// Slows down password guessing by blocking a client after too many failed logins
@Service
public class LoginAttemptService {

    static final Duration WINDOW = Duration.ofMinutes(15);
    static final int MAX_FAILURES_PER_ACCOUNT = 5; // for one username from one address
    static final int MAX_FAILURES_PER_ADDRESS = 20; // across all usernames from one address

    private final Map<String, Deque<Instant>> failures = new ConcurrentHashMap<>();
    private final Clock clock;

    public LoginAttemptService() {
        this(Clock.systemUTC());
    }

    LoginAttemptService(Clock clock) {
        this.clock = clock;
    }

    public void checkAllowed(String clientAddress, String userName) {
        if (recentFailures(accountKey(clientAddress, userName)) >= MAX_FAILURES_PER_ACCOUNT
                || recentFailures(addressKey(clientAddress)) >= MAX_FAILURES_PER_ADDRESS) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Too many failed login attempts");
        }
    }

    public void recordFailure(String clientAddress, String userName) {
        Instant now = clock.instant();
        for (String key : new String[] { accountKey(clientAddress, userName), addressKey(clientAddress) }) {
            failures.compute(key, (k, times) -> {
                Deque<Instant> recent = times == null ? new ArrayDeque<>() : times;
                recent.addLast(now);
                return recent;
            });
        }
    }

    public void recordSuccess(String clientAddress, String userName) {
        failures.remove(accountKey(clientAddress, userName));
    }

    private int recentFailures(String key) {
        Instant cutoff = clock.instant().minus(WINDOW);
        Deque<Instant> remaining = failures.computeIfPresent(key, (k, times) -> {
            while (!times.isEmpty() && !times.peekFirst().isAfter(cutoff)) {
                times.removeFirst();
            }
            return times.isEmpty() ? null : times; // returning null drops the entry
        });
        return remaining == null ? 0 : remaining.size();
    }

    private static String accountKey(String clientAddress, String userName) {
        String name = userName == null ? "" : userName.toLowerCase(Locale.ROOT);
        return "account:" + clientAddress + ":" + name;
    }

    private static String addressKey(String clientAddress) {
        return "address:" + clientAddress;
    }
}
