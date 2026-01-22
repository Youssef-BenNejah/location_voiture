package brama.pressing_api.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.math.BigDecimal;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.pricing")
public class PricingProperties {
    private BigDecimal taxRate = BigDecimal.ZERO;
    private BigDecimal fees = BigDecimal.ZERO;
    private String currency = "USD";
}
