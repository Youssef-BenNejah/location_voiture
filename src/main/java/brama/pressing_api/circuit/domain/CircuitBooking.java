package brama.pressing_api.circuit.domain;

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
import java.time.LocalDateTime;

@Document(collection = "circuit_bookings")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class CircuitBooking extends BaseDocument {
    @Field("circuit_id")
    @Indexed
    private String circuitId;

    @Field("user_id")
    @Indexed
    private String userId;

    @Field("circuit_title")
    private String circuitTitle;

    @Field("customer_name")
    private String customerName;

    @Field("customer_email")
    private String customerEmail;

    @Field("customer_phone")
    private String customerPhone;

    @Field("selected_date")
    @Indexed
    private LocalDate selectedDate;

    @Field("selected_time")
    private String selectedTime;

    @Field("number_of_passengers")
    private Integer numberOfPassengers;

    @Field("total_price")
    private BigDecimal totalPrice;

    @Field("pickup_address")
    private String pickupAddress;

    @Field("dropoff_address")
    private String dropoffAddress;

    @Field("notes")
    private String notes;

    @Field("status")
    @Indexed
    private CircuitBookingStatus status;

    @Field("booked_at")
    private LocalDateTime bookedAt;
}
