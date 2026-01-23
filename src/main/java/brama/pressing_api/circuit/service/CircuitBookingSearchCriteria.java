package brama.pressing_api.circuit.service;

import brama.pressing_api.circuit.domain.CircuitBookingStatus;
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
public class CircuitBookingSearchCriteria {
    private String query;
    private CircuitBookingStatus status;
    private String circuitId;
    private LocalDate selectedDate;
}
