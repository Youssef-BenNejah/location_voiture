package brama.pressing_api.circuit.service.impl;

import brama.pressing_api.circuit.CircuitMapper;
import brama.pressing_api.circuit.domain.Circuit;
import brama.pressing_api.circuit.domain.CircuitStatus;
import brama.pressing_api.circuit.dto.request.CircuitRequest;
import brama.pressing_api.circuit.dto.response.CircuitAdminStatsResponse;
import brama.pressing_api.circuit.dto.response.CircuitResponse;
import brama.pressing_api.circuit.repo.CircuitBookingRepository;
import brama.pressing_api.circuit.repo.CircuitRepository;
import brama.pressing_api.circuit.service.CircuitSearchCriteria;
import brama.pressing_api.circuit.service.CircuitService;
import brama.pressing_api.exception.BusinessException;
import brama.pressing_api.exception.EntityNotFoundException;
import brama.pressing_api.exception.ErrorCode;
import brama.pressing_api.notification.domain.NotificationImportance;
import brama.pressing_api.notification.dto.NotificationRequest;
import brama.pressing_api.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class CircuitServiceImpl implements CircuitService {
    private final CircuitRepository circuitRepository;
    private final CircuitBookingRepository bookingRepository;
    private final NotificationService notificationService;

    @Override
    public Page<CircuitResponse> listPublic(final CircuitSearchCriteria criteria, final Pageable pageable) {
        CircuitSearchCriteria effective = criteria != null ? criteria : new CircuitSearchCriteria();
        effective.setStatus(CircuitStatus.ACTIVE);
        return circuitRepository.search(effective, pageable)
                .map(CircuitMapper::toResponse);
    }

    @Override
    public CircuitResponse getPublic(final String id) {
        Circuit circuit = circuitRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.CIRCUIT_NOT_FOUND));
        if (circuit.getStatus() != CircuitStatus.ACTIVE) {
            throw new BusinessException(ErrorCode.CIRCUIT_INACTIVE);
        }
        return CircuitMapper.toResponse(circuit);
    }

    @Override
    public Page<CircuitResponse> listAdmin(final CircuitSearchCriteria criteria, final Pageable pageable) {
        return circuitRepository.search(criteria, pageable)
                .map(circuit -> {
                    CircuitResponse response = CircuitMapper.toResponse(circuit);
                    response.setBookingsCount(bookingRepository.countByCircuitId(circuit.getId()));
                    return response;
                });
    }

    @Override
    public CircuitResponse create(final CircuitRequest request) {
        Circuit circuit = Circuit.builder().build();
        CircuitMapper.applyRequest(circuit, request);
        Circuit saved = circuitRepository.save(circuit);
        notificationService.notifyAdmins(NotificationRequest.builder()
                .type("CIRCUIT_CREATED")
                .title("Circuit created")
                .body("Circuit " + saved.getTitle() + " has been created")
                .importance(NotificationImportance.LOW)
                .data(java.util.Map.of("circuitId", saved.getId()))
                .build());
        return CircuitMapper.toResponse(saved);
    }

    @Override
    public CircuitResponse update(final String id, final CircuitRequest request) {
        Circuit circuit = circuitRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Circuit not found"));
        CircuitMapper.applyRequest(circuit, request);
        Circuit saved = circuitRepository.save(circuit);
        notificationService.notifyAdmins(NotificationRequest.builder()
                .type("CIRCUIT_UPDATED")
                .title("Circuit updated")
                .body("Circuit " + saved.getTitle() + " has been updated")
                .importance(NotificationImportance.LOW)
                .data(java.util.Map.of("circuitId", saved.getId()))
                .build());
        return CircuitMapper.toResponse(saved);
    }

    @Override
    public void delete(final String id) {
        if (!circuitRepository.existsById(id)) {
            throw new EntityNotFoundException("Circuit not found");
        }
        circuitRepository.deleteById(id);
        notificationService.notifyAdmins(NotificationRequest.builder()
                .type("CIRCUIT_DELETED")
                .title("Circuit deleted")
                .body("Circuit " + id + " has been deleted")
                .importance(NotificationImportance.NORMAL)
                .data(java.util.Map.of("circuitId", id))
                .build());
    }

    @Override
    public CircuitAdminStatsResponse getAdminStats() {
        long totalCircuits = circuitRepository.count();
        long activeCircuits = circuitRepository.search(CircuitSearchCriteria.builder()
                .status(CircuitStatus.ACTIVE)
                .build(), Pageable.unpaged()).getTotalElements();

        var bookings = bookingRepository.findAll();
        BigDecimal revenue = BigDecimal.ZERO;
        for (var booking : bookings) {
            if (booking.getTotalPrice() != null) {
                revenue = revenue.add(booking.getTotalPrice());
            }
        }

        return CircuitAdminStatsResponse.builder()
                .totalCircuits(totalCircuits)
                .activeCircuits(activeCircuits)
                .totalBookings(bookings.size())
                .revenue(revenue)
                .build();
    }
}
