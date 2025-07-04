package com.example.handler;
import com.example.service.JWTService;
import com.example.service.OAuth2UserPrincipal;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private JWTService jwtService;

    @Value("${frontend.url:http://localhost:3000}")
    private String frontendUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        OAuth2UserPrincipal oauth2User = (OAuth2UserPrincipal) authentication.getPrincipal();

        // Generate JWT token for the OAuth2 user
        String token = jwtService.generateToken(oauth2User.getUser().getLoginId());

        // Get user details
        String userEmail = oauth2User.getEmail();
        String userName = oauth2User.getName();

        // Redirect to frontend with token and user info
        String redirectUrl = String.format("%s/auth/callback?token=%s&email=%s&name=%s",
                frontendUrl,
                URLEncoder.encode(token, StandardCharsets.UTF_8),
                URLEncoder.encode(userEmail, StandardCharsets.UTF_8),
                URLEncoder.encode(userName, StandardCharsets.UTF_8));

        response.sendRedirect(redirectUrl);
    }
}