package brama.pressing_api.booking;

import brama.pressing_api.booking.domain.model.Booking;
import brama.pressing_api.booking.domain.model.BookingExtra;
import brama.pressing_api.booking.domain.model.BookingPricing;
import brama.pressing_api.booking.domain.model.DriverDetails;
import brama.pressing_api.booking.dto.request.BookingExtraRequest;
import brama.pressing_api.booking.dto.request.DriverDetailsRequest;
import brama.pressing_api.booking.dto.response.BookingExtraResponse;
import brama.pressing_api.booking.dto.response.BookingPricingResponse;
import brama.pressing_api.booking.dto.response.BookingResponse;
import brama.pressing_api.booking.dto.response.DriverDetailsResponse;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public final class BookingMapper {
    private BookingMapper() {
    }

    public static List<BookingExtra> toExtras(final List<BookingExtraRequest> extras) {
        if (extras == null) {
            return Collections.emptyList();
        }
        return extras.stream()
                .map(extra -> BookingExtra.builder()
                        .name(extra.getName())
                        .pricePerDay(extra.getPricePerDay())
                        .quantity(extra.getQuantity())
                        .build())
                .collect(Collectors.toList());
    }

    public static DriverDetails toDriverDetails(final DriverDetailsRequest request) {
        if (request == null) {
            return null;
        }
        return DriverDetails.builder()
                .fullName(request.getFullName())
                .licenseNumber(request.getLicenseNumber())
                .licenseCountry(request.getLicenseCountry())
                .dateOfBirth(request.getDateOfBirth())
                .phone(request.getPhone())
                .email(request.getEmail())
                .build();
    }

    public static BookingResponse toResponse(final Booking booking) {
        return BookingResponse.builder()
                .id(booking.getId())
                .userId(booking.getUserId())
                .vehicleId(booking.getVehicleId())
                .pickupLocationId(booking.getPickupLocationId())
                .dropoffLocationId(booking.getDropoffLocationId())
                .startDate(booking.getStartDate())
                .endDate(booking.getEndDate())
                .status(booking.getStatus())
                .paymentStatus(booking.getPaymentStatus())
                .pricing(toPricingResponse(booking.getPricing()))
                .extras(toExtraResponses(booking.getExtras()))
                .driver(toDriverDetailsResponse(booking.getDriver()))
                .notes(booking.getNotes())
                .promoCode(booking.getPromoCode())
                .createdDate(booking.getCreatedDate())
                .lastModifiedDate(booking.getLastModifiedDate())
                .build();
    }

    private static BookingPricingResponse toPricingResponse(final BookingPricing pricing) {
        if (pricing == null) {
            return null;
        }
        return BookingPricingResponse.builder()
                .dailyRate(pricing.getDailyRate())
                .days(pricing.getDays())
                .extrasTotal(pricing.getExtrasTotal())
                .subtotal(pricing.getSubtotal())
                .discount(pricing.getDiscount())
                .taxes(pricing.getTaxes())
                .fees(pricing.getFees())
                .total(pricing.getTotal())
                .deposit(pricing.getDeposit())
                .currency(pricing.getCurrency())
                .build();
    }

    private static List<BookingExtraResponse> toExtraResponses(final List<BookingExtra> extras) {
        if (extras == null) {
            return Collections.emptyList();
        }
        return extras.stream()
                .map(extra -> BookingExtraResponse.builder()
                        .name(extra.getName())
                        .pricePerDay(extra.getPricePerDay())
                        .quantity(extra.getQuantity())
                        .build())
                .collect(Collectors.toList());
    }

    private static DriverDetailsResponse toDriverDetailsResponse(final DriverDetails driver) {
        if (driver == null) {
            return null;
        }
        return DriverDetailsResponse.builder()
                .fullName(driver.getFullName())
                .licenseNumber(driver.getLicenseNumber())
                .licenseCountry(driver.getLicenseCountry())
                .dateOfBirth(driver.getDateOfBirth())
                .phone(driver.getPhone())
                .email(driver.getEmail())
                .build();
    }
}
