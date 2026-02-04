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

    @Override
    public LocationResponse create(final CreateLocationRequest request) {
        if (locationRepository.existsByCodeIgnoreCase(request.getCode())) {
            throw new BusinessException(ErrorCode.LOCATION_CODE_EXISTS);
        }
        Location location = LocationMapper.toEntity(request);
        return LocationMapper.toResponse(locationRepository.save(location));
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
        return LocationMapper.toResponse(locationRepository.save(location));
    }

    @Override
    public LocationResponse getById(final String locationId) {
        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new EntityNotFoundException("Location not found"));
        return LocationMapper.toResponse(location);
    }

    @Override
    public void delete(final String locationId) {
        if (!locationRepository.existsById(locationId)) {
            throw new EntityNotFoundException("Location not found");
        }
        locationRepository.deleteById(locationId);
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
