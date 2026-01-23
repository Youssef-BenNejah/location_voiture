package brama.pressing_api.chat.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatAttachmentRequest {
    private String publicId;
    private String url;
    private String secureUrl;
    private String format;
    private Long bytes;
    private String fileName;
    private String contentType;
}
