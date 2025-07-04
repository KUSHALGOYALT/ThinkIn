package com.example.service;

import com.example.model.User;
import com.example.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class OAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private UserRepo userRepo;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);

        // Extract user information from Google
        String email = oauth2User.getAttribute("email");
        String name = oauth2User.getAttribute("name");
        String googleId = oauth2User.getAttribute("sub");

        // Check if user exists in database
        Optional<User> existingUser = userRepo.findByEmail(email);

        User user;
        if (existingUser.isEmpty()) {
            // Create new user from Google OAuth2 data
            user = new User();
            user.setEmail(email);
            user.setFullName(name);
            user.setLoginId(email); // Use email as loginId for Google users
            user.setPassword(""); // No password for OAuth2 users
            user.setEnabled(true);
            user.setAccountNonExpired(true);
            user.setAccountNonLocked(true);
            user.setCredentialsNonExpired(true);

            user = userRepo.save(user);
        } else {
            user = existingUser.get();
            // Update user information if needed
            if (!name.equals(user.getFullName())) {
                user.setFullName(name);
                userRepo.save(user);
            }
        }

        return new OAuth2UserPrincipal(oauth2User, user);
    }
}