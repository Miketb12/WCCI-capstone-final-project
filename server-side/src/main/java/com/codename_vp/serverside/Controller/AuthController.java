package com.codename_vp.serverside.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codename_vp.serverside.Component.JwtTokenUtil;
import com.codename_vp.serverside.Entity.AuthRequest;
import com.codename_vp.serverside.Service.LoginService;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private LoginService loginService;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody AuthRequest authRequest) {
        // Throws a 401 or 404 response unless the credentials are valid
        loginService.loginUser(authRequest.getUsername(), authRequest.getPassword());
        String token = jwtTokenUtil.generateToken(authRequest.getUsername());
        return ResponseEntity.ok(token);
    }
}
