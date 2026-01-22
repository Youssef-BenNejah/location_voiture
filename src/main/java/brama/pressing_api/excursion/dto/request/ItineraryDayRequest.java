package brama.pressing_api.excursion.dto.request;

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
public class ItineraryDayRequest {
    private Integer day;
    private String title;
    private String description;
    private List<ExcursionStopRequest> stops;
}
