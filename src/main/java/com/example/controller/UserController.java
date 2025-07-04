package com.example.controller;
import com.example.model.User;
import com.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*") // Configure this properly for your React frontend
public class UserController {

    @Autowired
    private UserService service;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            User registeredUser = service.register(user);
            return ResponseEntity.ok(Map.of(
                    "message", "User registered successfully",
                    "userId", registeredUser.getId(),
                    "loginId", registeredUser.getLoginId()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", e.getMessage()
            ));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            String token = service.verify(loginRequest.getLoginId(), loginRequest.getPassword());
            return ResponseEntity.ok(Map.of(
                    "message", "Login successful",
                    "token", token,
                    "loginId", loginRequest.getLoginId()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", e.getMessage()
            ));
        }
    }

    @GetMapping("/google")
    public ResponseEntity<?> googleLogin() {
        return ResponseEntity.ok(Map.of(
                "message", "Redirect to Google OAuth2",
                "url", "/oauth2/authorization/google"
        ));
    }

    @GetMapping("/user")
    public ResponseEntity<?> getCurrentUser(@RequestHeader("Authorization") String token) {
        try {
            String loginId = service.extractUserFromToken(token);
            User user = service.getUserByLoginId(loginId);
            return ResponseEntity.ok(Map.of(
                    "user", Map.of(
                            "id", user.getId(),
                            "loginId", user.getLoginId(),
                            "email", user.getEmail(),
                            "fullName", user.getFullName(),
                            "phoneNumber", user.getPhoneNumber() != null ? user.getPhoneNumber() : ""
                    )
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", e.getMessage()
            ));
        }
    }

    // Inner class for login request
    public static class LoginRequest {
        private String loginId;
        private String password;

        public String getLoginId() {
            return loginId;
        }

        public void setLoginId(String loginId) {
            this.loginId = loginId;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}