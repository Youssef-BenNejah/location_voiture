package brama.pressing_api.booking.dto.response;

import brama.pressing_api.booking.domain.model.BookingCreatedBy;
import brama.pressing_api.booking.domain.model.BookingPaymentEntry;
import brama.pressing_api.booking.domain.model.BookingPaymentStatus;
import brama.pressing_api.booking.domain.model.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponse {
    private String id;
    private String userId;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private String vehicleId;
    private String vehicleName;
    private String pickupLocationId;
    private String dropoffLocationId;
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
