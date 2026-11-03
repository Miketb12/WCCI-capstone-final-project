package com.codename_vp.serverside.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import jakarta.servlet.http.HttpServletRequest;

import com.codename_vp.serverside.Component.JwtTokenUtil;
import com.codename_vp.serverside.Entity.LoginResponse;
import com.codename_vp.serverside.Entity.User;
import com.codename_vp.serverside.Service.LoginService;

@RestController
@RequestMapping("/login")
@CrossOrigin(origins = "http://localhost:5173")
public class LoginController {
    @Autowired
    private LoginService loginService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @PostMapping
    public ResponseEntity<LoginResponse> loginUser(@RequestBody User user, HttpServletRequest request) {
        try {
            User loggedInUser = loginService.loginUser(user.getUserName(), user.getPassword(),
                    request.getRemoteAddr());
            String token = jwtTokenUtil.generateToken(loggedInUser.getUserName());
            return ResponseEntity.ok(new LoginResponse(loggedInUser.getId(), loggedInUser.getUserName(),
                    loggedInUser.getEmail(), token));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).build();
        }
    }
}
