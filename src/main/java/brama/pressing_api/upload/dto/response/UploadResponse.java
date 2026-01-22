package brama.pressing_api.upload.dto.response;

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
public class UploadResponse {
    private String publicId;
    private String url;
    private String secureUrl;
    private String format;
    private Long bytes;
}
