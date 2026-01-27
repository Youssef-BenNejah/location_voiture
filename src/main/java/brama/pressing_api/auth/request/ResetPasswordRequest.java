package brama.pressing_api.auth.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResetPasswordRequest {
    @NotBlank(message = "VALIDATION.RESET_PASSWORD.EMAIL.BLANK")
    @Email(message = "VALIDATION.RESET_PASSWORD.EMAIL.FORMAT")
    @Schema(example = "attia@mail.com")
    String email;

    @NotBlank(message = "VALIDATION.RESET_PASSWORD.NEW_PASSWORD.BLANK")
    @Size(min = 8,
            max = 72,
            message = "VALIDATION.RESET_PASSWORD.NEW_PASSWORD.SIZE"
    )
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*\\W).*$",
            message = "VALIDATION.RESET_PASSWORD.NEW_PASSWORD.WEAK"
    )
    @Schema(example = "pAssword1!_")
    String newPassword;

    @NotBlank(message = "VALIDATION.RESET_PASSWORD.CONFIRM_PASSWORD.BLANK")
    @Size(min = 8,
            max = 72,
            message = "VALIDATION.RESET_PASSWORD.CONFIRM_PASSWORD.SIZE"
    )
    @Schema(example = "pAssword1!_")
    String confirmPassword;
}
