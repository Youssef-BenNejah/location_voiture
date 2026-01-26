package brama.pressing_api.otp;

import brama.pressing_api.otp.request.GenerateOtpRequest;
import brama.pressing_api.otp.request.ResendOtpRequest;
import brama.pressing_api.otp.request.VerifyOtpRequest;
import brama.pressing_api.otp.response.OtpResponse;
import brama.pressing_api.otp.response.VerifyOtpResponse;

import brama.pressing_api.exception.BusinessException;
import brama.pressing_api.exception.ErrorCode;
import brama.pressing_api.token.TokenService;
import brama.pressing_api.user.User;
import brama.pressing_api.user.UserRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * OTP endpoints for generating, verifying, and resending codes.
 */
@RestController
@RequestMapping("/api/v1/otp")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Otp", description = "Otp API")
public class OtpController {
    private final TokenService tokenService;
    private final UserRepository userRepository;

    /**
     * Generates a new OTP for a user and purpose.
     */
    @PostMapping("/generate")
    public ResponseEntity<OtpResponse> generateOtp(@Valid @RequestBody GenerateOtpRequest request) {
        log.info("Generating OTP for email: {} with purpose: {}", request.getEmail(), request.getPurpose());

        final String userId = resolveUserId(request.getEmail());

         tokenService.generateOtpToken(
                userId,
                request.getPurpose());


        return ResponseEntity.ok(OtpResponse.builder()
                .message("OTP sent successfully to your registered email")
                .expiresInMinutes(10)
                .build());
    }

    /**
     * Verifies a submitted OTP code.
     */
    @PostMapping("/verify")
    public ResponseEntity<VerifyOtpResponse> verifyOtp(@Valid @RequestBody VerifyOtpRequest request) {
        log.info("Verifying OTP for email: {} with purpose: {}", request.getEmail(), request.getPurpose());

        final String userId = resolveUserId(request.getEmail());

        boolean isValid = tokenService.verifyOtpToken(
                userId,
                request.getOtpCode(),
                request.getPurpose()
        );

        return ResponseEntity.ok(VerifyOtpResponse.builder()
                .valid(isValid)
                .message(isValid ? "OTP verified successfully" : "Invalid OTP")
                .build());
    }

    /**
     * Resends a new OTP by revoking the existing one and issuing a new code.
     */
    @PostMapping("/resend")
    public ResponseEntity<OtpResponse> resendOtp(@Valid @RequestBody ResendOtpRequest request) {
        log.info("Resending OTP for email: {} with purpose: {}", request.getEmail(), request.getPurpose());

        final String userId = resolveUserId(request.getEmail());

        // Revoke existing OTP
        tokenService.revokeExistingOtpTokens(
                userId,
               request.getPurpose()
        );

        // Generate new OTP
        tokenService.generateOtpToken(
                userId,
               request.getPurpose()
        );

        return ResponseEntity.ok(OtpResponse.builder()
                .message("New OTP sent successfully to your registered email")
                .expiresInMinutes(10)
                .build());
    }

    private String resolveUserId(final String email) {
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        return user.getId();
    }
}
