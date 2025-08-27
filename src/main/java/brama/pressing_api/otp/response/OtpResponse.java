package brama.pressing_api.otp.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OtpResponse {
    private String message;
    private int expiresInMinutes;
}
