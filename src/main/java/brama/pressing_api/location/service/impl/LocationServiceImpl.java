package brama.pressing_api.location.service.impl;

import brama.pressing_api.exception.BusinessException;
import brama.pressing_api.exception.EntityNotFoundException;
import brama.pressing_api.exception.ErrorCode;
import brama.pressing_api.location.LocationMapper;
import brama.pressing_api.location.domain.model.Location;
import brama.pressing_api.location.dto.request.CreateLocationRequest;
import brama.pressing_api.location.dto.request.UpdateLocationRequest;
import brama.pressing_api.location.dto.response.LocationResponse;
import brama.pressing_api.location.repo.LocationRepository;
import brama.pressing_api.location.service.LocationService;
import brama.pressing_api.notification.domain.NotificationImportance;
import brama.pressing_api.notification.dto.NotificationRequest;
import brama.pressing_api.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {
    private final LocationRepository locationRepository;
    private final NotificationService notificationService;

    @Override
    public LocationResponse create(final CreateLocationRequest request) {
        if (locationRepository.existsByCodeIgnoreCase(request.getCode())) {
            throw new BusinessException(ErrorCode.LOCATION_CODE_EXISTS);
        }
        Location location = LocationMapper.toEntity(request);
        Location saved = locationRepository.save(location);
        notificationService.notifyAdmins(NotificationRequest.builder()
                .type("LOCATION_CREATED")
                .title("Location created")
                .body("Location " + saved.getCode() + " has been created")
                .importance(NotificationImportance.LOW)
                .data(java.util.Map.of("locationId", saved.getId(), "code", saved.getCode()))
                .build());
        return LocationMapper.toResponse(saved);
    }

    @Override
    public LocationResponse update(final String locationId, final UpdateLocationRequest request) {
        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new EntityNotFoundException("Location not found"));
        if (request.getCode() != null && !request.getCode().equalsIgnoreCase(location.getCode())) {
            if (locationRepository.existsByCodeIgnoreCase(request.getCode())) {
                throw new BusinessException(ErrorCode.LOCATION_CODE_EXISTS);
            }
        }
        LocationMapper.applyUpdates(location, request);
        Location saved = locationRepository.save(location);
        notificationService.notifyAdmins(NotificationRequest.builder()
                .type("LOCATION_UPDATED")
                .title("Location updated")
                .body("Location " + saved.getCode() + " has been updated")
                .importance(NotificationImportance.LOW)
                .data(java.util.Map.of("locationId", saved.getId(), "code", saved.getCode()))
                .build());
        return LocationMapper.toResponse(saved);
    }

    @Override
    public LocationResponse getById(final String locationId) {
        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new EntityNotFoundException("Location not found"));
        return LocationMapper.toResponse(location);
    }

    @Override
    public void delete(final String locationId) {
        Location existing = locationRepository.findById(locationId)
                .orElseThrow(() -> new EntityNotFoundException("Location not found"));
        locationRepository.deleteById(locationId);
        notificationService.notifyAdmins(NotificationRequest.builder()
                .type("LOCATION_DELETED")
                .title("Location deleted")
                .body("Location " + existing.getCode() + " has been deleted")
                .importance(NotificationImportance.NORMAL)
                .data(java.util.Map.of("locationId", existing.getId(), "code", existing.getCode()))
                .build());
    }

    @Override
    public List<LocationResponse> listPublic() {
        return locationRepository.findAll()
                .stream()
                .filter(location -> Boolean.TRUE.equals(location.getActive()))
                .map(LocationMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Page<LocationResponse> listAdmin(final Pageable pageable) {
        return locationRepository.findAll(pageable).map(LocationMapper::toResponse);
    }
}
