package brama.pressing_api.token;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Document(collection = "tokens")
@CompoundIndex(def = "{'userId': 1, 'tokenType': 1}")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Token {
    @Id
    private String id;

    @Field("token_value")
    @Indexed(unique = true, sparse = true) // sparse for non-unique FCM scenarios
    private String tokenValue;

    @Field("user_id")
    @Indexed
    private String userId;

    @Field("token_type")
    @Indexed
    private TokenType tokenType;

    @Field("expires_at")
    @Indexed(expireAfterSeconds = 0) // TTL index - MongoDB will auto-delete when expiresAt <= current time
    private LocalDateTime expiresAt;

    @Field("created_at")
    private LocalDateTime createdAt;

    @Field("updated_at")
    private LocalDateTime updatedAt;

    @Field("revoked")
    private boolean revoked;

    @Field("used")
    private boolean used;  // track if it really used or not

    // OTP specific fields
    @Field("purpose")
    private OtpPurpose purpose;

    @Field("attempts")
    private int attempts; // Track failed OTP attempts

    @Field("max_attempts")
    private int maxAttempts; // Max allowed attempts

    // Utility methods
    public boolean isExpired() {
        return expiresAt != null && LocalDateTime.now().isAfter(this.expiresAt);
    }

    public boolean isValid() {

        return !revoked && !used && !isExpired();
    }

    public boolean canAttempt() {
        return !revoked && !isExpired() && attempts < maxAttempts;
    }

    public void incrementAttempts() {
        this.attempts++;
        this.updatedAt = LocalDateTime.now();
    }

    public void markAsUsed() {
        this.used = true;
        this.updatedAt = LocalDateTime.now();
    }

    public void revoke() {
        this.revoked = true;
        this.updatedAt = LocalDateTime.now();
    }
}