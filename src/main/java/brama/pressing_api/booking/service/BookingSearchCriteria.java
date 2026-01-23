package brama.pressing_api.booking.service;

import brama.pressing_api.booking.domain.model.BookingPaymentStatus;
import brama.pressing_api.booking.domain.model.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingSearchCriteria {
    private String query;
    private BookingStatus status;
    private BookingPaymentStatus paymentStatus;
    private LocalDate startDate;
    private LocalDate endDate;
}
