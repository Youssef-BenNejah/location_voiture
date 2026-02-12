package brama.pressing_api.excursion.service.impl;

import brama.pressing_api.excursion.ExcursionMapper;
import brama.pressing_api.excursion.domain.model.Excursion;
import brama.pressing_api.excursion.dto.request.CreateExcursionRequest;
import brama.pressing_api.excursion.dto.request.UpdateExcursionRequest;
import brama.pressing_api.excursion.dto.response.ExcursionResponse;
import brama.pressing_api.excursion.repo.ExcursionRepository;
import brama.pressing_api.excursion.service.ExcursionSearchCriteria;
import brama.pressing_api.excursion.service.ExcursionService;
import brama.pressing_api.exception.BusinessException;
import brama.pressing_api.exception.EntityNotFoundException;
import brama.pressing_api.exception.ErrorCode;
import brama.pressing_api.upload.dto.response.UploadResponse;
import brama.pressing_api.upload.service.UploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExcursionServiceImpl implements ExcursionService {
    private final ExcursionRepository excursionRepository;
    private final UploadService uploadService;

    @Override
    public ExcursionResponse create(final CreateExcursionRequest request) {
        Excursion excursion = ExcursionMapper.toEntity(request);
        ensureCapacityValid(excursion.getTotalCapacity(), excursion.getBookedSeats());
        return ExcursionMapper.toResponse(excursionRepository.save(excursion));
    }

    @Override
    public ExcursionResponse update(final String excursionId, final UpdateExcursionRequest request) {
        Excursion excursion = excursionRepository.findById(excursionId)
                .orElseThrow(() -> new EntityNotFoundException("Excursion not found"));
        Integer currentBooked = excursion.getBookedSeats() != null ? excursion.getBookedSeats() : 0;
        Integer newCapacity = request.getTotalCapacity() != null ? request.getTotalCapacity() : excursion.getTotalCapacity();
        if (newCapacity != null && currentBooked > newCapacity) {
            throw new BusinessException(ErrorCode.EXCURSION_CAPACITY_TOO_LOW);
        }
        ExcursionMapper.applyUpdates(excursion, request);
        if (excursion.getBookedSeats() == null) {
            excursion.setBookedSeats(0);
        }
        if (excursion.getIsEnabled() == null) {
            excursion.setIsEnabled(Boolean.TRUE);
        }
        ensureCapacityValid(excursion.getTotalCapacity(), excursion.getBookedSeats());
        return ExcursionMapper.toResponse(excursionRepository.save(excursion));
    }

    @Override
    public ExcursionResponse getById(final String excursionId) {
        Excursion excursion = excursionRepository.findById(excursionId)
                .orElseThrow(() -> new EntityNotFoundException("Excursion not found"));
        return ExcursionMapper.toResponse(excursion);
    }

    @Override
    public ExcursionResponse getPublicById(final String excursionId) {
        Excursion excursion = excursionRepository.findByIdAndIsEnabledTrue(excursionId)
                .orElseThrow(() -> new EntityNotFoundException("Excursion not found"));
        return ExcursionMapper.toResponse(excursion);
    }

    @Override
    public void delete(final String excursionId) {
        if (!excursionRepository.existsById(excursionId)) {
            throw new EntityNotFoundException("Excursion not found");
        }
        excursionRepository.deleteById(excursionId);
    }

    @Override
    public List<ExcursionResponse> listPublic(final ExcursionSearchCriteria criteria) {
        criteria.setEnabledOnly(Boolean.TRUE);
        Page<Excursion> page = excursionRepository.search(criteria, Pageable.unpaged());
        return page.getContent()
                .stream()
                .map(ExcursionMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Page<ExcursionResponse> listAdmin(final ExcursionSearchCriteria criteria, final Pageable pageable) {
        return excursionRepository.search(criteria, pageable).map(ExcursionMapper::toResponse);
    }

    @Override
    public ExcursionResponse setEnabled(final String excursionId, final boolean enabled) {
        Excursion excursion = excursionRepository.findById(excursionId)
                .orElseThrow(() -> new EntityNotFoundException("Excursion not found"));
        excursion.setIsEnabled(enabled);
        return ExcursionMapper.toResponse(excursionRepository.save(excursion));
    }

    private void ensureCapacityValid(final Integer totalCapacity, final Integer bookedSeats) {
        if (totalCapacity == null || totalCapacity < 0) {
            throw new BusinessException(ErrorCode.EXCURSION_CAPACITY_INVALID);
        }
        int booked = bookedSeats != null ? bookedSeats : 0;
        if (booked < 0 || booked > totalCapacity) {
            throw new BusinessException(ErrorCode.EXCURSION_CAPACITY_INVALID);
        }
    }

    @Override
    public ExcursionResponse uploadImages(final String excursionId,
                                          final List<MultipartFile> images) throws IOException {

        if (images == null || images.isEmpty()) {
            throw new BusinessException(ErrorCode.FILE_REQUIRED);
        }

        Excursion excursion = excursionRepository.findById(excursionId)
                .orElseThrow(() -> new EntityNotFoundException("Excursion not found"));

        List<String> imageUrls =
                excursion.getImages() != null ? new ArrayList<>(excursion.getImages()) : new ArrayList<>();

        for (MultipartFile file : images) {
            UploadResponse upload = uploadService.uploadImage(file);
            String url = resolveUrl(upload);
            if (url != null) {
                imageUrls.add(url);
            }
        }

        excursion.setImages(imageUrls);

        return ExcursionMapper.toResponse(excursionRepository.save(excursion));
    }
    private String resolveUrl(UploadResponse upload) {
        if (upload == null) return null;
        return upload.getUrl(); // adjust if your UploadResponse differs
    }
}
