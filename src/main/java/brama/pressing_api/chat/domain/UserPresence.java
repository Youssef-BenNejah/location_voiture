package brama.pressing_api.chat.domain;

import brama.pressing_api.common.BaseDocument;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Document(collection = "user_presence")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class UserPresence extends BaseDocument {
    @Field("user_id")
    @Indexed(unique = true)
    private String userId;

    @Field("online")
    private boolean online;

    @Field("last_seen_at")
    private LocalDateTime lastSeenAt;

    @Field("last_active_at")
    private LocalDateTime lastActiveAt;

}
