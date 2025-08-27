package brama.pressing_api.otp;

import brama.pressing_api.otp.request.GenerateOtpRequest;
import brama.pressing_api.otp.request.ResendOtpRequest;
import brama.pressing_api.otp.request.VerifyOtpRequest;
import brama.pressing_api.otp.response.OtpResponse;
import brama.pressing_api.otp.response.VerifyOtpResponse;


import brama.pressing_api.token.TokenService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/otp")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Otp", description = "Otp API")
public class OtpController {
    private final TokenService tokenService;

    @PostMapping("/generate")
    public ResponseEntity<OtpResponse> generateOtp(@Valid @RequestBody GenerateOtpRequest request) {
        log.info("Generating OTP for user: {} with purpose: {}", request.getUserId(), request.getPurpose());

         tokenService.generateOtpToken(
                request.getUserId(),
                request.getPurpose());


        return ResponseEntity.ok(OtpResponse.builder()
                .message("OTP sent successfully to your registered email")
                .expiresInMinutes(10)
                .build());
    }

    @PostMapping("/verify")
    public ResponseEntity<VerifyOtpResponse> verifyOtp(@Valid @RequestBody VerifyOtpRequest request) {
        log.info("Verifying OTP for user: {} with purpose: {}", request.getUserId(), request.getPurpose());

        boolean isValid = tokenService.verifyOtpToken(
                request.getUserId(),
                request.getOtpCode(),
                request.getPurpose()
        );

        return ResponseEntity.ok(VerifyOtpResponse.builder()
                .valid(isValid)
                .message(isValid ? "OTP verified successfully" : "Invalid OTP")
                .build());
    }

    @PostMapping("/resend")
    public ResponseEntity<OtpResponse> resendOtp(@Valid @RequestBody ResendOtpRequest request) {
        log.info("Resending OTP for user: {} with purpose: {}", request.getUserId(), request.getPurpose());

        // Revoke existing OTP
        tokenService.revokeExistingOtpTokens(
                request.getUserId(),
               request.getPurpose()
        );

        // Generate new OTP
        tokenService.generateOtpToken(
                request.getUserId(),
               request.getPurpose()
        );

        return ResponseEntity.ok(OtpResponse.builder()
                .message("New OTP sent successfully to your registered email")
                .expiresInMinutes(10)
                .build());
    }
}
