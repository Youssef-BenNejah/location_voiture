package brama.pressing_api.excursionbooking;

import brama.pressing_api.excursion.domain.model.Excursion;
import brama.pressing_api.excursionbooking.domain.model.ExcursionBooking;
import brama.pressing_api.excursionbooking.dto.response.ExcursionBookingResponse;
import brama.pressing_api.excursionbooking.dto.response.ExcursionBookingTicketResponse;

public final class ExcursionBookingMapper {
    private ExcursionBookingMapper() {
    }

    public static ExcursionBookingResponse toResponse(final ExcursionBooking booking, final Excursion excursion) {
        if (booking == null) {
            return null;
        }

        ExcursionBookingResponse.ExcursionBookingResponseBuilder builder = ExcursionBookingResponse.builder()
                .id(booking.getId())
                .excursionId(booking.getExcursionId())
                .excursionTitle(booking.getExcursionTitle())
                .customerName(booking.getCustomerName())
                .customerEmail(booking.getCustomerEmail())
                .customerPhone(booking.getCustomerPhone())
                .selectedDate(booking.getSelectedDate())
                .numberOfSeats(booking.getNumberOfSeats())
                .totalPrice(booking.getTotalPrice())
                .status(booking.getStatus())
                .bookedAt(booking.getBookedAt())
                .createdDate(booking.getCreatedDate())
                .lastModifiedDate(booking.getLastModifiedDate());

        // Add excursion images if excursion is provided
        if (excursion != null) {
            builder.excursionImages(excursion.getImages());
        }

        return builder.build();
    }

    // Overload for backward compatibility
    public static ExcursionBookingResponse toResponse(final ExcursionBooking booking) {
        return toResponse(booking, null);
    }

    public static ExcursionBookingTicketResponse toTicketResponse(final ExcursionBooking booking, final Excursion excursion) {
        return ExcursionBookingTicketResponse.builder()
                .bookingId(booking.getId())
                .excursionId(booking.getExcursionId())
                .excursionTitle(booking.getExcursionTitle())
                .customerName(booking.getCustomerName())
                .customerEmail(booking.getCustomerEmail())
                .customerPhone(booking.getCustomerPhone())
                .selectedDate(booking.getSelectedDate())
                .numberOfSeats(booking.getNumberOfSeats())
                .totalPrice(booking.getTotalPrice())
                .status(booking.getStatus())
                .bookedAt(booking.getBookedAt())
                .startLocation(excursion.getStartLocation())
                .endLocation(excursion.getEndLocation())
                .duration(excursion.getDuration())
                .durationType(excursion.getDurationType())
                .images(excursion.getImages())
                .build();
    }
}