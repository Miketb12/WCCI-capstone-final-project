package com.codename_vp.serverside.Service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.codename_vp.serverside.Entity.User;
import com.codename_vp.serverside.Repository.UserRepo;

import jakarta.annotation.PostConstruct;

@Service
public class LoginService {
    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private LoginAttemptService loginAttemptService;

    // Checked when the username doesn't exist, so that case takes as long as a wrong password
    private String unknownUserHash;

    @PostConstruct
    void createUnknownUserHash() {
        unknownUserHash = passwordEncoder.encode("unknown-user-placeholder");
    }

    public User loginUser(String userName, String password, String clientAddress) {
        loginAttemptService.checkAllowed(clientAddress, userName);

        Optional<User> user = userRepo.findByUserName(userName);
        String storedHash = user.map(User::getPassword).orElse(unknownUserHash);
        boolean matches = password != null && passwordEncoder.matches(password, storedHash);

        if (user.isPresent() && matches) {
            loginAttemptService.recordSuccess(clientAddress, userName);
            return user.get();
        }

        // Same response for an unknown user and a wrong password, so usernames can't be discovered
        loginAttemptService.recordFailure(clientAddress, userName);
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
    }
}
