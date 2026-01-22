package brama.pressing_api.excursion;

import brama.pressing_api.excursion.domain.model.Excursion;
import brama.pressing_api.excursion.domain.model.ExcursionStop;
import brama.pressing_api.excursion.domain.model.ItineraryDay;
import brama.pressing_api.excursion.dto.request.CreateExcursionRequest;
import brama.pressing_api.excursion.dto.request.ExcursionStopRequest;
import brama.pressing_api.excursion.dto.request.ItineraryDayRequest;
import brama.pressing_api.excursion.dto.request.UpdateExcursionRequest;
import brama.pressing_api.excursion.dto.response.ExcursionResponse;
import brama.pressing_api.excursion.dto.response.ExcursionStopResponse;
import brama.pressing_api.excursion.dto.response.ItineraryDayResponse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public final class ExcursionMapper {
    private ExcursionMapper() {
    }

    public static Excursion toEntity(final CreateExcursionRequest request) {
        return Excursion.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .shortDescription(request.getShortDescription())
                .itinerary(toItinerary(request.getItinerary()))
                .placesToVisit(defaultList(request.getPlacesToVisit()))
                .stops(toStops(request.getStops()))
                .startLocation(request.getStartLocation())
                .endLocation(request.getEndLocation())
                .duration(request.getDuration())
                .durationType(request.getDurationType())
                .pricePerPerson(request.getPricePerPerson())
                .totalCapacity(request.getTotalCapacity())
                .bookedSeats(request.getBookedSeats() != null ? request.getBookedSeats() : 0)
                .availableDates(defaultList(request.getAvailableDates()))
                .images(defaultList(request.getImages()))
                .isEnabled(request.getIsEnabled() != null ? request.getIsEnabled() : Boolean.TRUE)
                .category(request.getCategory())
                .highlights(defaultList(request.getHighlights()))
                .build();
    }

    public static void applyUpdates(final Excursion excursion, final UpdateExcursionRequest request) {
        if (request.getTitle() != null) {
            excursion.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            excursion.setDescription(request.getDescription());
        }
        if (request.getShortDescription() != null) {
            excursion.setShortDescription(request.getShortDescription());
        }
        if (request.getItinerary() != null) {
            excursion.setItinerary(toItinerary(request.getItinerary()));
        }
        if (request.getPlacesToVisit() != null) {
            excursion.setPlacesToVisit(request.getPlacesToVisit());
        }
        if (request.getStops() != null) {
            excursion.setStops(toStops(request.getStops()));
        }
        if (request.getStartLocation() != null) {
            excursion.setStartLocation(request.getStartLocation());
        }
        if (request.getEndLocation() != null) {
            excursion.setEndLocation(request.getEndLocation());
        }
        if (request.getDuration() != null) {
            excursion.setDuration(request.getDuration());
        }
        if (request.getDurationType() != null) {
            excursion.setDurationType(request.getDurationType());
        }
        if (request.getPricePerPerson() != null) {
            excursion.setPricePerPerson(request.getPricePerPerson());
        }
        if (request.getTotalCapacity() != null) {
            excursion.setTotalCapacity(request.getTotalCapacity());
        }
        if (request.getBookedSeats() != null) {
            excursion.setBookedSeats(request.getBookedSeats());
        }
        if (request.getAvailableDates() != null) {
            excursion.setAvailableDates(request.getAvailableDates());
        }
        if (request.getImages() != null) {
            excursion.setImages(request.getImages());
        }
        if (request.getIsEnabled() != null) {
            excursion.setIsEnabled(request.getIsEnabled());
        }
        if (request.getCategory() != null) {
            excursion.setCategory(request.getCategory());
        }
        if (request.getHighlights() != null) {
            excursion.setHighlights(request.getHighlights());
        }
    }

    public static ExcursionResponse toResponse(final Excursion excursion) {
        return ExcursionResponse.builder()
                .id(excursion.getId())
                .title(excursion.getTitle())
                .description(excursion.getDescription())
                .shortDescription(excursion.getShortDescription())
                .itinerary(toItineraryResponse(excursion.getItinerary()))
                .placesToVisit(defaultList(excursion.getPlacesToVisit()))
                .stops(toStopsResponse(excursion.getStops()))
                .startLocation(excursion.getStartLocation())
                .endLocation(excursion.getEndLocation())
                .duration(excursion.getDuration())
                .durationType(excursion.getDurationType())
                .pricePerPerson(excursion.getPricePerPerson())
                .totalCapacity(excursion.getTotalCapacity())
                .bookedSeats(excursion.getBookedSeats())
                .availableDates(defaultList(excursion.getAvailableDates()))
                .images(defaultList(excursion.getImages()))
                .isEnabled(excursion.getIsEnabled())
                .category(excursion.getCategory())
                .highlights(defaultList(excursion.getHighlights()))
                .createdDate(excursion.getCreatedDate())
                .lastModifiedDate(excursion.getLastModifiedDate())
                .build();
    }

    private static List<ExcursionStop> toStops(final List<ExcursionStopRequest> requests) {
        return Optional.ofNullable(requests)
                .orElse(Collections.emptyList())
                .stream()
                .map(stop -> ExcursionStop.builder()
                        .name(stop.getName())
                        .description(stop.getDescription())
                        .duration(stop.getDuration())
                        .build())
                .collect(Collectors.toList());
    }

    private static List<ExcursionStopResponse> toStopsResponse(final List<ExcursionStop> stops) {
        return Optional.ofNullable(stops)
                .orElse(Collections.emptyList())
                .stream()
                .map(stop -> ExcursionStopResponse.builder()
                        .name(stop.getName())
                        .description(stop.getDescription())
                        .duration(stop.getDuration())
                        .build())
                .collect(Collectors.toList());
    }

    private static List<ItineraryDay> toItinerary(final List<ItineraryDayRequest> requests) {
        return Optional.ofNullable(requests)
                .orElse(Collections.emptyList())
                .stream()
                .map(day -> ItineraryDay.builder()
                        .day(day.getDay())
                        .title(day.getTitle())
                        .description(day.getDescription())
                        .stops(toStops(day.getStops()))
                        .build())
                .collect(Collectors.toList());
    }

    private static List<ItineraryDayResponse> toItineraryResponse(final List<ItineraryDay> days) {
        return Optional.ofNullable(days)
                .orElse(Collections.emptyList())
                .stream()
                .map(day -> ItineraryDayResponse.builder()
                        .day(day.getDay())
                        .title(day.getTitle())
                        .description(day.getDescription())
                        .stops(toStopsResponse(day.getStops()))
                        .build())
                .collect(Collectors.toList());
    }

    private static <T> List<T> defaultList(final List<T> list) {
        return list == null ? new ArrayList<>() : list;
    }
}
