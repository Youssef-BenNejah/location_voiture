package brama.pressing_api.excursion.dto.response;

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
public class ItineraryDayResponse {
    private Integer day;
    private String title;
    private String description;
    private List<ExcursionStopResponse> stops;
}
