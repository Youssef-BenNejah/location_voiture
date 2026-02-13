package brama.pressing_api.booking.dto.response;

import brama.pressing_api.booking.domain.model.BookingCreatedBy;
import brama.pressing_api.booking.domain.model.BookingPaymentEntry;
import brama.pressing_api.booking.domain.model.BookingPaymentStatus;
import brama.pressing_api.booking.domain.model.BookingStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingClientResponse {
    private String id;
    private String userId;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private String vehicleId;
    private String vehicleName;
    private String vehicleImage;  // ADD THIS - vehicle image URL
    private String pickupLocationId;
    private String pickupLocationName;  // ADD THIS - pickup location name
    private String dropoffLocationId;
    private String dropoffLocationName;  // ADD THIS - dropoff location name
    private LocalDate startDate;
    private LocalDate endDate;
    private BookingStatus status;
    private BookingPaymentStatus paymentStatus;
    private BookingCreatedBy createdBy;
    private BigDecimal paidAmount;
    private List<BookingPaymentEntry> paymentHistory;
    private BookingPricingResponse pricing;
    private List<BookingExtraResponse> extras;
    private DriverDetailsResponse driver;
    private String notes;
    private String promoCode;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;
}
