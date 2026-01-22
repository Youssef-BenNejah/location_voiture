package brama.pressing_api.excursion.dto.request;

import brama.pressing_api.excursion.domain.model.ExcursionDurationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class CreateExcursionRequest {
    @NotBlank
    private String title;

    @NotBlank
    private String description;

    @NotBlank
    private String shortDescription;

    private List<ItineraryDayRequest> itinerary;
    private List<String> placesToVisit;
    private List<ExcursionStopRequest> stops;

    @NotBlank
    private String startLocation;

    @NotBlank
    private String endLocation;

    @NotBlank
    private String duration;

    @NotNull
    private ExcursionDurationType durationType;

    @NotNull
    private BigDecimal pricePerPerson;

    @NotNull
    private Integer totalCapacity;

    private Integer bookedSeats;
    private List<LocalDate> availableDates;
    private List<String> images;
    private Boolean isEnabled;

    @NotBlank
    private String category;

    private List<String> highlights;
}
