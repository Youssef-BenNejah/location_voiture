package brama.pressing_api.excursionbooking.dto.response;

import brama.pressing_api.excursionbooking.domain.model.ExcursionBookingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExcursionBookingResponse {
    private String id;
    private String excursionId;
    private String excursionTitle;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private LocalDate selectedDate;
    private Integer numberOfSeats;
    private BigDecimal totalPrice;
    private ExcursionBookingStatus status;
    private LocalDateTime bookedAt;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;
    private List<String> excursionImages;
}
