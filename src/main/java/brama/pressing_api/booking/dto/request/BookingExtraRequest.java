package brama.pressing_api.booking.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingExtraRequest {
    @NotBlank
    private String name;

    @Positive
    private BigDecimal pricePerDay;

    @Positive
    private Integer quantity;
}
