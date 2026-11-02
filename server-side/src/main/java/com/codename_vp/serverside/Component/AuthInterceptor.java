package com.codename_vp.serverside.Component;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import com.codename_vp.serverside.Repository.UserRepo;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// Requires a valid token, and only lets users reach their own data
@Component
public class AuthInterceptor implements HandlerInterceptor {

    private static final String BEARER = "Bearer ";

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserRepo userRepo;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        if ("OPTIONS".equals(request.getMethod())) {
            return true;
        }

        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.startsWith(BEARER)
                || !jwtTokenUtil.validateToken(header.substring(BEARER.length()))) {
            response.sendError(HttpStatus.UNAUTHORIZED.value());
            return false;
        }

        String userName = jwtTokenUtil.extractUsername(header.substring(BEARER.length()));
        Long tokenUserId = userRepo.findByUserName(userName).map(user -> user.getId()).orElse(null);
        if (tokenUserId == null) {
            response.sendError(HttpStatus.UNAUTHORIZED.value());
            return false;
        }

        Map<?, ?> pathVariables = (Map<?, ?>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        if (pathVariables == null) {
            return true; // No matching route, so this ends in a 404
        }
        Object requestedUserName = pathVariables.get("username");
        Object requestedUserId = pathVariables.get("userId");
        if (requestedUserId == null) {
            requestedUserId = pathVariables.get("id");
        }
        if (requestedUserId == null) {
            requestedUserId = request.getParameter("userId");
        }

        boolean allowed = (requestedUserName == null || userName.equals(requestedUserName))
                && (requestedUserId == null || tokenUserId.toString().equals(requestedUserId));
        if (!allowed) {
            response.sendError(HttpStatus.FORBIDDEN.value());
        }
        return allowed;
    }
}
