package brama.pressing_api.user.dto.response;

import brama.pressing_api.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private LocalDate dateOfBirth;
    private boolean enabled;
    private boolean locked;
    private boolean emailVerified;
    private boolean phoneVerified;
    private List<String> roles;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;

    public static UserProfileResponse from(final User user) {
        if (user == null) {
            return null;
        }
        return UserProfileResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .dateOfBirth(user.getDateOfBirth())
                .enabled(user.isEnabled())
                .locked(user.isLocked())
                .emailVerified(user.isEmailVerified())
                .phoneVerified(user.isPhoneVerified())
                .roles(user.getRoles())
                .createdDate(user.getCreatedDate())
                .lastModifiedDate(user.getLastModifiedDate())
                .build();
    }
}
