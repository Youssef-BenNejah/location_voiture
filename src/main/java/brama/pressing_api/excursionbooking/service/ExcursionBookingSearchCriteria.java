package brama.pressing_api.excursionbooking.service;

import brama.pressing_api.excursionbooking.domain.model.ExcursionBookingStatus;
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
public class ExcursionBookingSearchCriteria {
    private String query;
    private ExcursionBookingStatus status;
    private String excursionId;
    private LocalDate selectedDate;
}
