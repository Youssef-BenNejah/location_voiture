package brama.pressing_api.upload;

import brama.pressing_api.upload.dto.response.UploadResponse;
import brama.pressing_api.upload.service.UploadService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Upload endpoints for images and documents (Cloudinary backed).
 */
@RestController
@RequestMapping("/api/v1/uploads")
@RequiredArgsConstructor
@Tag(name = "Uploads", description = "Upload images and documents")
public class UploadController {
    private final UploadService uploadService;

    /**
     * Uploads an image file and returns its Cloudinary metadata.
     */
    @PostMapping("/images")
    public ResponseEntity<UploadResponse> uploadImage(@RequestPart("file") MultipartFile file) throws IOException {
        return ResponseEntity.status(HttpStatus.CREATED).body(uploadService.uploadImage(file));
    }

    /**
     * Uploads a document file and returns its Cloudinary metadata.
     */
    @PostMapping("/documents")
    public ResponseEntity<UploadResponse> uploadDocument(@RequestPart("file") MultipartFile file) throws IOException {
        return ResponseEntity.status(HttpStatus.CREATED).body(uploadService.uploadDocument(file));
    }
    /**
     * Upload multiple files for chat (supports both images and documents)
     */
    @PostMapping("/chat")
    public ResponseEntity<List<UploadResponse>> uploadChatFiles(
            @RequestPart("files") List<MultipartFile> files) throws IOException {

        List<UploadResponse> responses = new ArrayList<>();

        for (MultipartFile file : files) {
            // Determine if file is image or document based on content type
            String contentType = file.getContentType();
            UploadResponse response;

            if (contentType != null && contentType.startsWith("image/")) {
                response = uploadService.uploadImage(file);
            } else {
                response = uploadService.uploadDocument(file);
            }

            // Add fileName to response
            response.setFileName(file.getOriginalFilename());
            responses.add(response);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(responses);
    }
}
