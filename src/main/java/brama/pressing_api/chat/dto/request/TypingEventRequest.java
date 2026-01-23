package brama.pressing_api.chat.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TypingEventRequest {
    @NotBlank
    private String conversationId;

    private boolean typing;
}
