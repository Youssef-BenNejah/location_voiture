package brama.pressing_api.vehicle.service.impl;

import brama.pressing_api.booking.domain.model.BookingStatus;
import brama.pressing_api.booking.repo.BookingRepository;
import brama.pressing_api.exception.BusinessException;
import brama.pressing_api.exception.EntityNotFoundException;
import brama.pressing_api.exception.ErrorCode;
import brama.pressing_api.upload.dto.response.UploadResponse;
import brama.pressing_api.upload.service.UploadService;
import brama.pressing_api.vehicle.VehicleMapper;
import brama.pressing_api.vehicle.domain.model.Vehicle;
import brama.pressing_api.vehicle.domain.model.VehicleStatus;
import brama.pressing_api.vehicle.dto.request.CreateVehicleRequest;
import brama.pressing_api.vehicle.dto.request.UpdateVehicleRequest;
import brama.pressing_api.vehicle.dto.response.VehicleResponse;
import brama.pressing_api.vehicle.repo.VehicleRepository;
import brama.pressing_api.vehicle.service.VehicleSearchCriteria;
import brama.pressing_api.vehicle.service.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.time.LocalDate;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VehicleServiceImpl implements VehicleService {
    private final VehicleRepository vehicleRepository;
    private final BookingRepository bookingRepository;
    private final UploadService uploadService;

    @Override
    public VehicleResponse create(final CreateVehicleRequest request) {
        Vehicle vehicle = VehicleMapper.toEntity(request);
        return VehicleMapper.toResponse(vehicleRepository.save(vehicle));
    }

    @Override
    public VehicleResponse update(final String vehicleId, final UpdateVehicleRequest request) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new EntityNotFoundException("Vehicle not found"));
        VehicleMapper.applyUpdates(vehicle, request);
        return VehicleMapper.toResponse(vehicleRepository.save(vehicle));
    }

    @Override
    public VehicleResponse getById(final String vehicleId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new EntityNotFoundException("Vehicle not found"));
        return VehicleMapper.toResponse(vehicle);
    }

    @Override
    public void delete(final String vehicleId) {
        if (!vehicleRepository.existsById(vehicleId)) {
            throw new EntityNotFoundException("Vehicle not found");
        }
        vehicleRepository.deleteById(vehicleId);
    }

    @Override
    public Page<VehicleResponse> searchPublic(final VehicleSearchCriteria criteria, final Pageable pageable) {
        VehicleSearchCriteria effective = criteria;
        if (criteria.getStartDate() != null || criteria.getEndDate() != null) {
            validateDateRange(criteria.getStartDate(), criteria.getEndDate());
        }

        if (criteria.getStatuses() == null || criteria.getStatuses().isEmpty()) {
            effective = VehicleSearchCriteria.builder()
                    .search(criteria.getSearch())
                    .locationId(criteria.getLocationId())
                    .startDate(criteria.getStartDate())
                    .endDate(criteria.getEndDate())
                    .category(criteria.getCategory())
                    .transmission(criteria.getTransmission())
                    .fuelType(criteria.getFuelType())
                    .minSeats(criteria.getMinSeats())
                    .minPrice(criteria.getMinPrice())
                    .maxPrice(criteria.getMaxPrice())
                    .statuses(EnumSet.of(VehicleStatus.AVAILABLE))
                    .excludeVehicleIds(criteria.getExcludeVehicleIds())
                    .build();
        }

        if (effective.getStartDate() != null && effective.getEndDate() != null) {
            Set<String> reservedVehicleIds = getReservedVehicleIds(effective.getStartDate(), effective.getEndDate());
            effective = VehicleSearchCriteria.builder()
                    .search(effective.getSearch())
                    .locationId(effective.getLocationId())
                    .startDate(effective.getStartDate())
                    .endDate(effective.getEndDate())
                    .category(effective.getCategory())
                    .transmission(effective.getTransmission())
                    .fuelType(effective.getFuelType())
                    .minSeats(effective.getMinSeats())
                    .minPrice(effective.getMinPrice())
                    .maxPrice(effective.getMaxPrice())
                    .statuses(effective.getStatuses())
                    .excludeVehicleIds(reservedVehicleIds)
                    .build();
        }

        return vehicleRepository.search(effective, pageable)
                .map(VehicleMapper::toResponse);
    }

    @Override
    public Page<VehicleResponse> listAdmin(final VehicleSearchCriteria criteria, final Pageable pageable) {
        VehicleSearchCriteria effective = criteria != null ? criteria : VehicleSearchCriteria.builder().build();
        return vehicleRepository.search(effective, pageable).map(VehicleMapper::toResponse);
    }

    @Override
    public VehicleResponse uploadMedia(final String vehicleId,
                                       final List<MultipartFile> images,
                                       final List<MultipartFile> documents) throws IOException {
        if ((images == null || images.isEmpty()) && (documents == null || documents.isEmpty())) {
            throw new BusinessException(ErrorCode.FILE_REQUIRED);
        }

        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new EntityNotFoundException("Vehicle not found"));

        List<String> imageUrls = vehicle.getImages() != null ? new ArrayList<>(vehicle.getImages()) : null;
        if (images != null && !images.isEmpty()) {
            if (imageUrls == null) {
                imageUrls = new ArrayList<>();
            }
            for (MultipartFile file : images) {
                UploadResponse upload = uploadService.uploadImage(file);
                String url = resolveUrl(upload);
                if (url != null) {
                    imageUrls.add(url);
                }
            }
        }
        if (imageUrls != null) {
            vehicle.setImages(imageUrls);
        }

        List<String> documentUrls = vehicle.getDocuments() != null ? new ArrayList<>(vehicle.getDocuments()) : null;
        if (documents != null && !documents.isEmpty()) {
            if (documentUrls == null) {
                documentUrls = new ArrayList<>();
            }
            for (MultipartFile file : documents) {
                UploadResponse upload = uploadService.uploadDocument(file);
                String url = resolveUrl(upload);
                if (url != null) {
                    documentUrls.add(url);
                }
            }
        }
        if (documentUrls != null) {
            vehicle.setDocuments(documentUrls);
        }

        return VehicleMapper.toResponse(vehicleRepository.save(vehicle));
    }

    private void validateDateRange(final LocalDate startDate, final LocalDate endDate) {
        if (startDate == null || endDate == null || !startDate.isBefore(endDate)) {
            throw new BusinessException(ErrorCode.INVALID_DATE_RANGE);
        }
    }

    private Set<String> getReservedVehicleIds(final LocalDate startDate, final LocalDate endDate) {
        List<BookingStatus> activeStatuses = List.of(BookingStatus.CONFIRMED, BookingStatus.ACTIVE);
        return bookingRepository
                .findByOverlappingDates(startDate, endDate, activeStatuses)
                .stream()
                .map(booking -> booking.getVehicleId())
                .collect(Collectors.toSet());
    }

    private String resolveUrl(final UploadResponse upload) {
        if (upload == null) {
            return null;
        }
        if (upload.getSecureUrl() != null && !upload.getSecureUrl().isBlank()) {
            return upload.getSecureUrl();
        }
        return upload.getUrl();
    }
}
