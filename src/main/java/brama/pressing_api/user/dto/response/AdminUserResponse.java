package brama.pressing_api.user.dto.response;

import brama.pressing_api.user.User;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class AdminUserResponse {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private boolean enabled;
    private boolean locked;
    private boolean emailVerified;
    private boolean phoneVerified;
    private LocalDateTime createdDate;
    private List<String> roles;

    public static AdminUserResponse from(User user) {
        return AdminUserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .enabled(user.isEnabled())
                .locked(user.isLocked())
                .emailVerified(user.isEmailVerified())
                .phoneVerified(user.isPhoneVerified())
                .createdDate(user.getCreatedDate())
                .roles(user.getRoles())
                .build();
    }
}
