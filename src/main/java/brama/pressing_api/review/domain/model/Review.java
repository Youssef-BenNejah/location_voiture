package brama.pressing_api.review.domain.model;

import brama.pressing_api.common.BaseDocument;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "reviews")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Review extends BaseDocument {
    @Field("user_id")
    @Indexed
    private String userId;

    @Field("vehicle_id")
    @Indexed
    private String vehicleId;

    @Field("rating")
    private Integer rating;

    @Field("comment")
    private String comment;

    @Field("status")
    @Indexed
    private ReviewStatus status;
}
