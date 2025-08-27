package brama.pressing_api.token;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TokenRepository extends MongoRepository<Token, String> {
    long countByUserIdAndTokenTypeAndRevokedFalse(String userId, TokenType tokenType);

    List<Token> findByUserIdAndTokenTypeAndRevokedFalse(String userId, TokenType tokenType);

    Optional<Token> findByTokenValueAndTokenType(String refreshToken, TokenType tokenType);
    @Query(value = "{ 'userId': ?0, 'createdAt': { $gte: ?1 } }", count = true)

    long countOtpTokensInTimeFrame(String userId, LocalDateTime localDateTime);

    Optional<Token> findByUserIdAndTokenTypeAndPurposeAndRevokedFalse(String userId, TokenType tokenType, OtpPurpose purpose);

    @Query("{'userId': ?0, 'tokenType': 'OTP_TOKEN', 'purpose': ?1, 'revoked': false, 'used': false, 'expiresAt': {$gt: ?2}}")
    Optional<Token> findValidOtpToken(String userId, OtpPurpose purpose, LocalDateTime now);
}
