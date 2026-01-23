package brama.pressing_api.circuit;

import brama.pressing_api.circuit.domain.Circuit;
import brama.pressing_api.circuit.domain.CircuitBooking;
import brama.pressing_api.circuit.dto.request.CircuitRequest;
import brama.pressing_api.circuit.dto.response.CircuitBookingResponse;
import brama.pressing_api.circuit.dto.response.CircuitResponse;

public final class CircuitMapper {
    private CircuitMapper() {
    }

    public static CircuitResponse toResponse(final Circuit circuit) {
        if (circuit == null) {
            return null;
        }
        return CircuitResponse.builder()
                .id(circuit.getId())
                .title(circuit.getTitle())
                .description(circuit.getDescription())
                .originCity(circuit.getOriginCity())
                .destinationCity(circuit.getDestinationCity())
                .distance(circuit.getDistance())
                .estimatedDuration(circuit.getEstimatedDuration())
                .price(circuit.getPrice())
                .currency(circuit.getCurrency())
                .vehicleType(circuit.getVehicleType())
                .maxPassengers(circuit.getMaxPassengers())
                .status(circuit.getStatus())
                .images(circuit.getImages())
                .bookingsCount(null)
                .createdDate(circuit.getCreatedDate())
                .lastModifiedDate(circuit.getLastModifiedDate())
                .build();
    }

    public static CircuitBookingResponse toBookingResponse(final CircuitBooking booking) {
        if (booking == null) {
            return null;
        }
        return CircuitBookingResponse.builder()
                .id(booking.getId())
                .circuitId(booking.getCircuitId())
                .userId(booking.getUserId())
                .circuitTitle(booking.getCircuitTitle())
                .customerName(booking.getCustomerName())
                .customerEmail(booking.getCustomerEmail())
                .customerPhone(booking.getCustomerPhone())
                .selectedDate(booking.getSelectedDate())
                .selectedTime(booking.getSelectedTime())
                .numberOfPassengers(booking.getNumberOfPassengers())
                .totalPrice(booking.getTotalPrice())
                .pickupAddress(booking.getPickupAddress())
                .dropoffAddress(booking.getDropoffAddress())
                .notes(booking.getNotes())
                .status(booking.getStatus())
                .bookedAt(booking.getBookedAt())
                .createdDate(booking.getCreatedDate())
                .lastModifiedDate(booking.getLastModifiedDate())
                .build();
    }

    public static void applyRequest(final Circuit circuit, final CircuitRequest request) {
        circuit.setTitle(request.getTitle());
        circuit.setDescription(request.getDescription());
        circuit.setOriginCity(request.getOriginCity());
        circuit.setDestinationCity(request.getDestinationCity());
        circuit.setDistance(request.getDistance());
        circuit.setEstimatedDuration(request.getEstimatedDuration());
        circuit.setPrice(request.getPrice());
        circuit.setCurrency(request.getCurrency());
        circuit.setVehicleType(request.getVehicleType());
        circuit.setMaxPassengers(request.getMaxPassengers());
        circuit.setStatus(request.getStatus());
        circuit.setImages(request.getImages());
    }
}
