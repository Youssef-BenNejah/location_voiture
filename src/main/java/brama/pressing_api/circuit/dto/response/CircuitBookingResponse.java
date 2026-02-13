package brama.pressing_api.circuit.dto.response;

import brama.pressing_api.circuit.domain.CircuitBookingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CircuitBookingResponse {
    private String id;
    private String circuitId;
    private String userId;
    private String circuitTitle;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private LocalDate selectedDate;
    private String selectedTime;
    private Integer numberOfPassengers;
    private BigDecimal totalPrice;
    private String pickupAddress;
    private String dropoffAddress;
    private String notes;
    private CircuitBookingStatus status;
    private LocalDateTime bookedAt;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;

}
