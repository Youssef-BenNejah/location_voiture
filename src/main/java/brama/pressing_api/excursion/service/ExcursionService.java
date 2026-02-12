package brama.pressing_api.excursion.service;

import brama.pressing_api.excursion.dto.request.CreateExcursionRequest;
import brama.pressing_api.excursion.dto.request.UpdateExcursionRequest;
import brama.pressing_api.excursion.dto.response.ExcursionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ExcursionService {
    ExcursionResponse create(CreateExcursionRequest request);

    ExcursionResponse update(String excursionId, UpdateExcursionRequest request);

    ExcursionResponse getById(String excursionId);

    ExcursionResponse getPublicById(String excursionId);

    void delete(String excursionId);

    List<ExcursionResponse> listPublic(ExcursionSearchCriteria criteria);

    Page<ExcursionResponse> listAdmin(ExcursionSearchCriteria criteria, Pageable pageable);

    ExcursionResponse setEnabled(String excursionId, boolean enabled);
    ExcursionResponse uploadImages(String excursionId, List<MultipartFile> images) throws IOException;

}
