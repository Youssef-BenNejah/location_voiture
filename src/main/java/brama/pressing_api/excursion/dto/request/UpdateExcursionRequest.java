package brama.pressing_api.excursion.dto.request;

import brama.pressing_api.excursion.domain.model.ExcursionDurationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateExcursionRequest {
    private String title;
    private String description;
    private String shortDescription;
    private List<ItineraryDayRequest> itinerary;
    private List<String> placesToVisit;
    private List<ExcursionStopRequest> stops;
    private String startLocation;
    private String endLocation;
    private String duration;
    private ExcursionDurationType durationType;
    private BigDecimal pricePerPerson;
    private Integer totalCapacity;
    private Integer bookedSeats;
    private List<LocalDate> availableDates;
    private List<String> images;
    private Boolean isEnabled;
    private String category;
    private List<String> highlights;
}
