package brama.pressing_api.auth;

import brama.pressing_api.auth.request.*;
import brama.pressing_api.auth.response.AuthenticationResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

/**
 * Authentication endpoints for login, registration, and token refresh.
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication API")
public class AuthenticationController {
    private final AuthenticationService service;

    /**
     * Authenticates a user and returns access/refresh tokens.
     */
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(
            @Valid
            @RequestBody
            final AuthenticationRequest request) {
        return ResponseEntity.ok(this.service.login(request));
    }

    /**
     * Registers a new user account.
     */
    @PostMapping("/register")
    public ResponseEntity<Void> register(
            @Valid
            @RequestBody
            final RegistrationRequest request) {
        this.service.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .build();
    }

    /**
     * Refreshes an access token using a refresh token.
     */
    @PostMapping("/refresh")
    public ResponseEntity<AuthenticationResponse> refresh(
            @RequestBody
            final RefreshRequest req) {
        return ResponseEntity.ok(this.service.refreshToken(req));
    }

    /**
     * Resets a user's password using their email.
     */
    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(
            @Valid
            @RequestBody
            final ResetPasswordRequest request) {
        this.service.resetPassword(request);
        return ResponseEntity.ok().build();
    }


    /**
     * Verifies user email via URL link (GET request from email).
     * Example: /verify-email?userId=xxx&code=123456
     */
    @GetMapping("/verify-email")
    public ResponseEntity<Void> verifyEmailViaLink(
            @RequestParam("userId") String userId,
            @RequestParam("code") String code) {
        this.service.verifyEmailByUserId(userId, code);

        // Redirect to frontend success page
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create("http://localhost:4200/login?verified=true"))
                .build();
    }

}
