package brama.pressing_api.upload.service;

import brama.pressing_api.upload.dto.response.UploadResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface UploadService {
    UploadResponse uploadImage(MultipartFile file) throws IOException;

    UploadResponse uploadDocument(MultipartFile file) throws IOException;
}
