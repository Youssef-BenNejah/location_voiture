package brama.pressing_api.location;

import brama.pressing_api.location.domain.model.Location;
import brama.pressing_api.location.dto.request.CreateLocationRequest;
import brama.pressing_api.location.dto.request.UpdateLocationRequest;
import brama.pressing_api.location.dto.response.LocationResponse;

public final class LocationMapper {
    private LocationMapper() {
    }

    public static Location toEntity(final CreateLocationRequest request) {
        return Location.builder()
                .name(request.getName())
                .code(request.getCode())
                .addressLine1(request.getAddressLine1())
                .addressLine2(request.getAddressLine2())
                .city(request.getCity())
                .state(request.getState())
                .postalCode(request.getPostalCode())
                .country(request.getCountry())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .phone(request.getPhone())
                .email(request.getEmail())
                .timezone(request.getTimezone())
                .active(request.getActive() != null ? request.getActive() : Boolean.TRUE)
                .pickupSupported(request.getPickupSupported() != null ? request.getPickupSupported() : Boolean.TRUE)
                .dropoffSupported(request.getDropoffSupported() != null ? request.getDropoffSupported() : Boolean.TRUE)
                .openingHours(request.getOpeningHours())
                .build();
    }

    public static void applyUpdates(final Location location, final UpdateLocationRequest request) {
        if (request.getName() != null) {
            location.setName(request.getName());
        }
        if (request.getCode() != null) {
            location.setCode(request.getCode());
        }
        if (request.getAddressLine1() != null) {
            location.setAddressLine1(request.getAddressLine1());
        }
        if (request.getAddressLine2() != null) {
            location.setAddressLine2(request.getAddressLine2());
        }
        if (request.getCity() != null) {
            location.setCity(request.getCity());
        }
        if (request.getState() != null) {
            location.setState(request.getState());
        }
        if (request.getPostalCode() != null) {
            location.setPostalCode(request.getPostalCode());
        }
        if (request.getCountry() != null) {
            location.setCountry(request.getCountry());
        }
        if (request.getLatitude() != null) {
            location.setLatitude(request.getLatitude());
        }
        if (request.getLongitude() != null) {
            location.setLongitude(request.getLongitude());
        }
        if (request.getPhone() != null) {
            location.setPhone(request.getPhone());
        }
        if (request.getEmail() != null) {
            location.setEmail(request.getEmail());
        }
        if (request.getTimezone() != null) {
            location.setTimezone(request.getTimezone());
        }
        if (request.getActive() != null) {
            location.setActive(request.getActive());
        }
        if (request.getPickupSupported() != null) {
            location.setPickupSupported(request.getPickupSupported());
        }
        if (request.getDropoffSupported() != null) {
            location.setDropoffSupported(request.getDropoffSupported());
        }
        if (request.getOpeningHours() != null) {
            location.setOpeningHours(request.getOpeningHours());
        }
    }

    public static LocationResponse toResponse(final Location location) {
        return LocationResponse.builder()
                .id(location.getId())
                .name(location.getName())
                .code(location.getCode())
                .addressLine1(location.getAddressLine1())
                .addressLine2(location.getAddressLine2())
                .city(location.getCity())
                .state(location.getState())
                .postalCode(location.getPostalCode())
                .country(location.getCountry())
                .latitude(location.getLatitude())
                .longitude(location.getLongitude())
                .phone(location.getPhone())
                .email(location.getEmail())
                .timezone(location.getTimezone())
                .active(location.getActive())
                .pickupSupported(location.getPickupSupported())
                .dropoffSupported(location.getDropoffSupported())
                .openingHours(location.getOpeningHours())
                .createdDate(location.getCreatedDate())
                .lastModifiedDate(location.getLastModifiedDate())
                .build();
    }
}
