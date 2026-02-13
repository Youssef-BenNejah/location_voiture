package brama.pressing_api.token;

import brama.pressing_api.email.EmailService;
import brama.pressing_api.exception.BusinessException;
import brama.pressing_api.exception.ErrorCode;

import brama.pressing_api.user.User;
import brama.pressing_api.user.UserRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import java.security.SecureRandom;
import java.time.LocalDateTime;

import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenService {

    @Value("${app.security.otp.length}")
    private int otpLength;

    @Value("${app.security.otp.expiry-minutes}")
    private int otpExpiryMinutes;

    @Value("${app.security.otp.max-attempts}")
    private int otpMaxAttempts;

    @Value("${app.security.otp.max-per-hour}")
    private int maxOtpPerHour;
    private final TokenRepository tokenRepository;
    private final EmailService emailService;
    private final UserRepository userRepository;
    private final Random secureRandom = new SecureRandom();


    @Transactional
    public Token storeRefreshToken(String userId, String jwtToken,LocalDateTime expirationTime) {


        revokeAllUserRefreshTokens(userId);


        Token refreshToken = Token.builder()
                .tokenValue(jwtToken)
                .userId(userId)
                .tokenType(TokenType.REFRESH_TOKEN)
                .expiresAt(expirationTime)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .revoked(false)
                .used(false)
                .build();

        log.info("Stored refresh token metadata for user: {} with JTI: {}", userId, jwtToken);
        return tokenRepository.save(refreshToken);
    }


    private void revokeAllUserRefreshTokens(String userId) {
        List<Token> existingTokens = tokenRepository.findByUserIdAndTokenTypeAndRevokedFalse(
                userId, TokenType.REFRESH_TOKEN);

        existingTokens.forEach(Token::revoke);

        if (!existingTokens.isEmpty()) {
            tokenRepository.saveAll(existingTokens);
            log.info("Revoked {} existing refresh tokens for user: {}", existingTokens.size(), userId);
        }
    }

    // Add validation method
    public void validateRefreshToken(String refreshToken) {
        tokenRepository.findByTokenValueAndTokenType(refreshToken, TokenType.REFRESH_TOKEN)
                .ifPresent(token -> {
                    if (token.isRevoked()) {
                        throw new BusinessException(ErrorCode.REFRESH_TOKEN_REVOKED);
                    }
                    if (token.isExpired()) {
                        throw new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN);
                    }
                });
    }

    // ===== OTP TOKEN METHODS =====
    @Transactional
    public Token generateOtpToken(String userId, OtpPurpose purpose) {
        // Rate limiting check
        long recentOtpCount = tokenRepository.countOtpTokensInTimeFrame(
                userId, LocalDateTime.now().minusHours(1));

        if (recentOtpCount >= maxOtpPerHour) {
            throw new BusinessException(ErrorCode.OTP_RATE_LIMIT_EXCEEDED);
        }

        // Revoke any existing valid OTP for this purpose
        revokeExistingOtpTokens(userId, purpose);

        String otpCode = generateOtpCode();

        Token otpToken = Token.builder()
                .tokenValue(otpCode)
                .userId(userId)
                .tokenType(TokenType.OTP_TOKEN)
                .purpose(purpose)
                .expiresAt(LocalDateTime.now().plusMinutes(otpExpiryMinutes))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .revoked(false)
                .used(false)
                .attempts(0)
                .maxAttempts(otpMaxAttempts)
                .build();

        // Save the token first
        Token savedToken = tokenRepository.save(otpToken);
        log.info("Generated OTP token for user: {} with purpose: {}", userId, purpose);


        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

            String displayName = user.getFirstName() != null && !user.getFirstName().isBlank()
                    ? user.getFirstName()
                    : user.getEmail();
            if (purpose == OtpPurpose.EMAIL_VERIFICATION) {
                String verificationUrl = UriComponentsBuilder.fromHttpUrl("http://localhost:8080")
                        .path("/api/v1/auth/verify-email")
                        .queryParam("userId", userId)
                        .queryParam("code", otpCode)
                        .build()
                        .toUriString();
                emailService.sendEmailVerificationEmail(
                        user.getEmail(),
                        displayName,
                        otpCode,
                        verificationUrl,
                        String.valueOf(otpExpiryMinutes)
                );
            } else {
                emailService.sendOtpEmail(
                        user.getEmail(),
                        displayName,
                        otpCode,
                        purpose.name(),
                        String.valueOf(otpExpiryMinutes)
                );
            }
            log.info("OTP email sent successfully to user: {}", userId);
        } catch (MessagingException e) {
            log.error("Failed to send OTP email to user: {}, but OTP was generated successfully", userId, e);
            // Note: OTP is still valid even if email fails
            // You might want to implement a retry mechanism or notification system
        } catch (BusinessException e) {
            // User not found - you might want to revoke the token
            savedToken.revoke();
            tokenRepository.save(savedToken);
            throw e;
        }

        return savedToken;
    }
    @Transactional

    public void revokeExistingOtpTokens(String userId, OtpPurpose purpose) {
        tokenRepository.findByUserIdAndTokenTypeAndPurposeAndRevokedFalse(
                        userId, TokenType.OTP_TOKEN, purpose)
                .ifPresent(token -> {
                    token.revoke();
                    tokenRepository.save(token);
                });
    }

    private String generateOtpCode() {
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < otpLength; i++) {
            otp.append(secureRandom.nextInt(10));
        }
        return otp.toString();
    }
    @Transactional

    public boolean verifyOtpToken(String userId, String otpCode, OtpPurpose purpose) {
        Token otpToken = tokenRepository.findValidOtpToken(userId, purpose, LocalDateTime.now())
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_OR_EXPIRED_OTP));

        // Check if already used
        if (otpToken.isUsed()) {
            log.warn("Attempt to use already used OTP for user: {} with purpose: {}", userId, purpose);
            throw new BusinessException(ErrorCode.INVALID_OR_EXPIRED_OTP);
        }

        // Check max attempts
        if (!otpToken.canAttempt()) {
            log.warn("Max OTP attempts exceeded for user: {} with purpose: {}", userId, purpose);
            throw new BusinessException(ErrorCode.OTP_MAX_ATTEMPTS_EXCEEDED);
        }

        // Verify the code
        if (!otpToken.getTokenValue().equals(otpCode)) {
            otpToken.incrementAttempts();

            // Revoke if max attempts reached
            if (otpToken.getAttempts() >= otpToken.getMaxAttempts()) {
                otpToken.revoke();
                log.warn("OTP revoked due to max attempts for user: {} with purpose: {}", userId, purpose);
            }

            tokenRepository.save(otpToken);

            log.warn("Invalid OTP attempt for user: {} (attempt {}/{})",
                    userId, otpToken.getAttempts(), otpToken.getMaxAttempts());

            // Return specific error based on attempts
            if (otpToken.isRevoked()) {
                throw new BusinessException(ErrorCode.OTP_MAX_ATTEMPTS_EXCEEDED);
            } else {
                throw new BusinessException(ErrorCode.INVALID_OTP_CODE);
            }
        }

        // OTP is valid - mark as used
        otpToken.markAsUsed();
        tokenRepository.save(otpToken);

        if (purpose == OtpPurpose.EMAIL_VERIFICATION) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
            if (!user.isEmailVerified()) {
                user.setEmailVerified(true);
                userRepository.save(user);
            }
        }

        log.info("OTP verified successfully for user: {} with purpose: {}", userId, purpose);
        return true;
    }
}
