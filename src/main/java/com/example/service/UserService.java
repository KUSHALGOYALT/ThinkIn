package com.example.service;
import com.example.model.User;
import com.example.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepo repo;

    @Autowired
    private JWTService jwtService;

    @Autowired
    AuthenticationManager authManager;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    public User register(User user) {
        // Check if loginId already exists
        if (repo.existsByLoginId(user.getLoginId())) {
            throw new RuntimeException("Login ID already exists");
        }

        // Check if email already exists
        if (user.getEmail() != null && repo.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        // Validate required fields
        if (user.getLoginId() == null || user.getLoginId().trim().isEmpty()) {
            throw new RuntimeException("Login ID is required");
        }

        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            throw new RuntimeException("Password is required");
        }

        // Encode password
        user.setPassword(encoder.encode(user.getPassword()));

        // Set default values
        user.setEnabled(true);
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(true);

        return repo.save(user);
    }

    public String verify(String loginId, String password) {
        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginId, password)
        );

        if (authentication.isAuthenticated()) {
            return jwtService.generateToken(loginId);
        } else {
            throw new RuntimeException("Invalid credentials");
        }
    }

    public String extractUserFromToken(String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return jwtService.extractUserName(token);
    }

    public User getUserByLoginId(String loginId) {
        return repo.findByLoginId(loginId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}