package brama.pressing_api.booking.domain.model;

import brama.pressing_api.common.BaseDocument;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Document(collection = "bookings")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Booking extends BaseDocument {
    @Field("user_id")
    @Indexed
    private String userId;

    @Field("customer_name")
    private String customerName;

    @Field("customer_email")
    private String customerEmail;

    @Field("customer_phone")
    private String customerPhone;

    @Field("vehicle_id")
    @Indexed
    private String vehicleId;

    @Field("vehicle_name")
    private String vehicleName;

    @Field("pickup_location_id")
    private String pickupLocationId;

    @Field("dropoff_location_id")
    private String dropoffLocationId;

    @Field("start_date")
    @Indexed
    private LocalDate startDate;

    @Field("end_date")
    @Indexed
    private LocalDate endDate;

    @Field("status")
    @Indexed
    private BookingStatus status;

    @Field("payment_status")
    private BookingPaymentStatus paymentStatus;

    @Field("created_by")
    private BookingCreatedBy createdBy;

    @Field("paid_amount")
    private BigDecimal paidAmount;

    @Field("payment_history")
    private List<BookingPaymentEntry> paymentHistory;

    @Field("pricing")
    private BookingPricing pricing;

    @Field("extras")
    private List<BookingExtra> extras;

    @Field("driver")
    private DriverDetails driver;

    @Field("notes")
    private String notes;

    @Field("promo_code")
    private String promoCode;
}
