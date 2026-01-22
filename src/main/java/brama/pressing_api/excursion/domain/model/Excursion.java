package brama.pressing_api.excursion.domain.model;

import brama.pressing_api.common.BaseDocument;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Document(collection = "excursions")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Excursion extends BaseDocument {
    @Field("title")
    @Indexed
    private String title;

    @Field("description")
    private String description;

    @Field("short_description")
    private String shortDescription;

    @Field("itinerary")
    private List<ItineraryDay> itinerary;

    @Field("places_to_visit")
    private List<String> placesToVisit;

    @Field("stops")
    private List<ExcursionStop> stops;

    @Field("start_location")
    private String startLocation;

    @Field("end_location")
    private String endLocation;

    @Field("duration")
    private String duration;

    @Field("duration_type")
    @Indexed
    private ExcursionDurationType durationType;

    @Field("price_per_person")
    @Indexed
    private BigDecimal pricePerPerson;

    @Field("total_capacity")
    private Integer totalCapacity;

    @Field("booked_seats")
    private Integer bookedSeats;

    @Field("available_dates")
    private List<LocalDate> availableDates;

    @Field("images")
    private List<String> images;

    @Field("is_enabled")
    @Indexed
    private Boolean isEnabled;

    @Field("category")
    @Indexed
    private String category;

    @Field("highlights")
    private List<String> highlights;
}
