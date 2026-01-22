package brama.pressing_api.booking.dto.response;

import brama.pressing_api.booking.domain.model.BookingPaymentStatus;
import brama.pressing_api.booking.domain.model.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponse {
    private String id;
    private String userId;
    private String vehicleId;
    private String pickupLocationId;
    private String dropoffLocationId;
    private LocalDate startDate;
    private LocalDate endDate;
    private BookingStatus status;
    private BookingPaymentStatus paymentStatus;
    private BookingPricingResponse pricing;
    private List<BookingExtraResponse> extras;
    private DriverDetailsResponse driver;
    private String notes;
    private String promoCode;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;
}
