package brama.pressing_api.otp.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VerifyOtpResponse {
    private boolean valid;
    private String message;
}
