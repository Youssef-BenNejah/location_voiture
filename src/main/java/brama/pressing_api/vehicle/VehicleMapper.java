package brama.pressing_api.vehicle;

import brama.pressing_api.vehicle.domain.model.Vehicle;
import brama.pressing_api.vehicle.dto.request.CreateVehicleRequest;
import brama.pressing_api.vehicle.dto.request.UpdateVehicleRequest;
import brama.pressing_api.vehicle.dto.response.VehicleResponse;

public final class VehicleMapper {
    private VehicleMapper() {
    }

    public static Vehicle toEntity(final CreateVehicleRequest request) {
        return Vehicle.builder()
                .make(request.getMake())
                .model(request.getModel())
                .year(request.getYear())
                .trim(request.getTrim())
                .category(request.getCategory())
                .transmission(request.getTransmission())
                .fuelType(request.getFuelType())
                .seats(request.getSeats())
                .doors(request.getDoors())
                .luggageCapacity(request.getLuggageCapacity())
                .color(request.getColor())
                .licensePlate(request.getLicensePlate())
                .vin(request.getVin())
                .locationId(request.getLocationId())
                .dailyRate(request.getDailyRate())
                .weeklyRate(request.getWeeklyRate())
                .monthlyRate(request.getMonthlyRate())
                .deposit(request.getDeposit())
                .mileageLimitPerDay(request.getMileageLimitPerDay())
                .status(request.getStatus())
                .description(request.getDescription())
                .features(request.getFeatures())
                .images(request.getImages())
                .documents(request.getDocuments())
                .ratingAverage(0.0)
                .ratingCount(0)
                .build();
    }

    public static void applyUpdates(final Vehicle vehicle, final UpdateVehicleRequest request) {
        if (request.getMake() != null) {
            vehicle.setMake(request.getMake());
        }
        if (request.getModel() != null) {
            vehicle.setModel(request.getModel());
        }
        if (request.getYear() != null) {
            vehicle.setYear(request.getYear());
        }
        if (request.getTrim() != null) {
            vehicle.setTrim(request.getTrim());
        }
        if (request.getCategory() != null) {
            vehicle.setCategory(request.getCategory());
        }
        if (request.getTransmission() != null) {
            vehicle.setTransmission(request.getTransmission());
        }
        if (request.getFuelType() != null) {
            vehicle.setFuelType(request.getFuelType());
        }
        if (request.getSeats() != null) {
            vehicle.setSeats(request.getSeats());
        }
        if (request.getDoors() != null) {
            vehicle.setDoors(request.getDoors());
        }
        if (request.getLuggageCapacity() != null) {
            vehicle.setLuggageCapacity(request.getLuggageCapacity());
        }
        if (request.getColor() != null) {
            vehicle.setColor(request.getColor());
        }
        if (request.getLicensePlate() != null) {
            vehicle.setLicensePlate(request.getLicensePlate());
        }
        if (request.getVin() != null) {
            vehicle.setVin(request.getVin());
        }
        if (request.getLocationId() != null) {
            vehicle.setLocationId(request.getLocationId());
        }
        if (request.getDailyRate() != null) {
            vehicle.setDailyRate(request.getDailyRate());
        }
        if (request.getWeeklyRate() != null) {
            vehicle.setWeeklyRate(request.getWeeklyRate());
        }
        if (request.getMonthlyRate() != null) {
            vehicle.setMonthlyRate(request.getMonthlyRate());
        }
        if (request.getDeposit() != null) {
            vehicle.setDeposit(request.getDeposit());
        }
        if (request.getMileageLimitPerDay() != null) {
            vehicle.setMileageLimitPerDay(request.getMileageLimitPerDay());
        }
        if (request.getStatus() != null) {
            vehicle.setStatus(request.getStatus());
        }
        if (request.getDescription() != null) {
            vehicle.setDescription(request.getDescription());
        }
        if (request.getFeatures() != null) {
            vehicle.setFeatures(request.getFeatures());
        }
        if (request.getImages() != null) {
            vehicle.setImages(request.getImages());
        }
        if (request.getDocuments() != null) {
            vehicle.setDocuments(request.getDocuments());
        }
    }

    public static VehicleResponse toResponse(final Vehicle vehicle) {
        return VehicleResponse.builder()
                .id(vehicle.getId())
                .make(vehicle.getMake())
                .model(vehicle.getModel())
                .year(vehicle.getYear())
                .trim(vehicle.getTrim())
                .category(vehicle.getCategory())
                .transmission(vehicle.getTransmission())
                .fuelType(vehicle.getFuelType())
                .seats(vehicle.getSeats())
                .doors(vehicle.getDoors())
                .luggageCapacity(vehicle.getLuggageCapacity())
                .color(vehicle.getColor())
                .licensePlate(vehicle.getLicensePlate())
                .vin(vehicle.getVin())
                .locationId(vehicle.getLocationId())
                .dailyRate(vehicle.getDailyRate())
                .weeklyRate(vehicle.getWeeklyRate())
                .monthlyRate(vehicle.getMonthlyRate())
                .deposit(vehicle.getDeposit())
                .mileageLimitPerDay(vehicle.getMileageLimitPerDay())
                .status(vehicle.getStatus())
                .description(vehicle.getDescription())
                .features(vehicle.getFeatures())
                .images(vehicle.getImages())
                .documents(vehicle.getDocuments())
                .ratingAverage(vehicle.getRatingAverage())
                .ratingCount(vehicle.getRatingCount())
                .createdDate(vehicle.getCreatedDate())
                .lastModifiedDate(vehicle.getLastModifiedDate())
                .build();
    }
}
