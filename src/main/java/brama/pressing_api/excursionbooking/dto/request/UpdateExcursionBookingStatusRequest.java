package brama.pressing_api.excursionbooking.dto.request;

import brama.pressing_api.excursionbooking.domain.model.ExcursionBookingStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateExcursionBookingStatusRequest {
    @NotNull
    private ExcursionBookingStatus status;
}
