package brama.pressing_api.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BanUserRequest {
    @NotBlank
    private String reason;
}
