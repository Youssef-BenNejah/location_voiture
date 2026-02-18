package brama.pressing_api.firebase.dto.request;

import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class PushNotificationRequest {
    private String title;
    private String body;
    private Map<String, String> data;  // optional extra payload

    // For single user
    private String token;

    // For multiple users
    private List<String> tokens;

    // For topic-based broadcast
    private String topic;
}
