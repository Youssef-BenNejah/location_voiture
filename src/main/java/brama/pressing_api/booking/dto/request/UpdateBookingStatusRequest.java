package brama.pressing_api.booking.dto.request;

import brama.pressing_api.booking.domain.model.BookingStatus;
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
public class UpdateBookingStatusRequest {
    @NotNull
    private BookingStatus status;
}
