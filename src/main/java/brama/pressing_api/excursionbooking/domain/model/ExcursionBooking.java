package brama.pressing_api.excursionbooking.domain.model;

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

@Document(collection = "excursion_bookings")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class ExcursionBooking extends BaseDocument {
    @Field("excursion_id")
    @Indexed
    private String excursionId;

    @Field("excursion_title")
    private String excursionTitle;

    @Field("user_id")
    @Indexed
    private String userId;

    @Field("customer_name")
    private String customerName;

    @Field("customer_email")
    @Indexed
    private String customerEmail;

    @Field("customer_phone")
    private String customerPhone;

    @Field("selected_date")
    @Indexed
    private LocalDate selectedDate;

    @Field("number_of_seats")
    private Integer numberOfSeats;

    @Field("total_price")
    private BigDecimal totalPrice;

    @Field("status")
    @Indexed
    private ExcursionBookingStatus status;

    @Field("booked_at")
    private LocalDateTime bookedAt;
}
