package brama.pressing_api.booking.dto.response;

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
public class BookingPricingResponse {
    private BigDecimal dailyRate;
    private Integer days;
    private BigDecimal extrasTotal;
    private BigDecimal subtotal;
    private BigDecimal discount;
    private BigDecimal taxes;
    private BigDecimal fees;
    private BigDecimal total;
    private BigDecimal deposit;
    private String currency;
}
