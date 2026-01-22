package brama.pressing_api.upload.service.impl;

import brama.pressing_api.config.CloudinaryProperties;
import brama.pressing_api.exception.BusinessException;
import brama.pressing_api.exception.ErrorCode;
import brama.pressing_api.upload.dto.response.UploadResponse;
import brama.pressing_api.upload.service.UploadService;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CloudinaryUploadService implements UploadService {
    private static final Set<String> IMAGE_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/webp",
            "image/gif"
    );
    private static final Set<String> DOC_TYPES = Set.of(
            "application/pdf",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "image/jpeg",
            "image/png"
    );

    private final Cloudinary cloudinary;
    private final CloudinaryProperties properties;

    @Value("${app.upload.max-bytes:10485760}")
    private long maxUploadBytes;

    @Override
    public UploadResponse uploadImage(final MultipartFile file) throws IOException {
        validateFile(file, IMAGE_TYPES);
        String folder = properties.getFolder() + "/images";
        Map<?, ?> result = cloudinary.uploader().upload(file.getBytes(),
                ObjectUtils.asMap("folder", folder, "resource_type", "image"));
        return toResponse(result);
    }

    @Override
    public UploadResponse uploadDocument(final MultipartFile file) throws IOException {
        validateFile(file, DOC_TYPES);
        String folder = properties.getFolder() + "/documents";
        Map<?, ?> result = cloudinary.uploader().upload(file.getBytes(),
                ObjectUtils.asMap("folder", folder, "resource_type", "raw"));
        return toResponse(result);
    }

    private void validateFile(final MultipartFile file, final Set<String> allowedTypes) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ErrorCode.FILE_REQUIRED);
        }
        if (file.getSize() > maxUploadBytes) {
            throw new BusinessException(ErrorCode.FILE_TOO_LARGE);
        }
        String contentType = file.getContentType();
        if (contentType == null || !allowedTypes.contains(contentType)) {
            throw new BusinessException(ErrorCode.FILE_TYPE_NOT_ALLOWED);
        }
    }

    private UploadResponse toResponse(final Map<?, ?> result) {
        return UploadResponse.builder()
                .publicId(asString(result.get("public_id")))
                .url(asString(result.get("url")))
                .secureUrl(asString(result.get("secure_url")))
                .format(asString(result.get("format")))
                .bytes(asLong(result.get("bytes")))
                .build();
    }

    private String asString(final Object value) {
        return value != null ? value.toString() : null;
    }

    private Long asLong(final Object value) {
        if (value instanceof Number number) {
            return number.longValue();
        }
        return null;
    }
}
