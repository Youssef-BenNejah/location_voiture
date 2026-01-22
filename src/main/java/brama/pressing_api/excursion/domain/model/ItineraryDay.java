package brama.pressing_api.excursion.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItineraryDay {
    private Integer day;
    private String title;
    private String description;
    private List<ExcursionStop> stops;
}
