package brama.pressing_api.otp.request;

import brama.pressing_api.token.OtpPurpose;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResendOtpRequest {
    @NotBlank(message = "User ID is required")
    private String userId;

    
    private OtpPurpose purpose;
}
