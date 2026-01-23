package brama.pressing_api.circuit;

import brama.pressing_api.circuit.dto.response.CircuitBookingResponse;
import brama.pressing_api.circuit.domain.CircuitBookingStatus;
import brama.pressing_api.circuit.service.CircuitBookingService;
import brama.pressing_api.exception.BusinessException;
import brama.pressing_api.exception.ErrorCode;
import brama.pressing_api.utils.SecurityUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/circuits/bookings")
@RequiredArgsConstructor
@Tag(name = "Circuit Bookings", description = "Customer circuit bookings")
public class CircuitBookingController {
    private final CircuitBookingService bookingService;

    @GetMapping
    public Page<CircuitBookingResponse> listMyBookings(Pageable pageable,
                                                       @RequestParam(required = false) CircuitBookingStatus status) {
        String userId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        Page<CircuitBookingResponse> page = bookingService.listMyBookings(userId, pageable);
        if (status == null) {
            return page;
        }
        var filtered = page.getContent().stream()
                .filter(booking -> booking.getStatus() == status)
                .toList();
        return new org.springframework.data.domain.PageImpl<>(filtered, pageable, filtered.size());
    }

    @GetMapping("/{id}")
    public CircuitBookingResponse getMyBooking(@PathVariable String id) {
        String userId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        return bookingService.getMyBooking(id, userId);
    }

    @PostMapping("/{id}/cancel")
    public CircuitBookingResponse cancelMyBooking(@PathVariable String id) {
        String userId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        return bookingService.cancelMyBooking(id, userId);
    }

    @GetMapping("/{id}/ticket")
    public CircuitBookingResponse getTicket(@PathVariable String id) {
        String userId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        return bookingService.getMyTicket(id, userId);
    }
}
