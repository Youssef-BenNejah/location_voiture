package brama.pressing_api.user;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.CollectionUtils;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Document(collection = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User implements UserDetails {
    @Id
    private String id;

    @Field("first_name")
    @NotNull
    private String firstName;

    @Field("last_name")
    @NotNull
    private String lastName;

    @Field("email")
    @Indexed(unique = true)
    @NotNull
    private String email;

    @Field("phone_number")
    @Indexed(unique = true)
    @NotNull
    private String phoneNumber;

    @Field("fcm_token")
    private String fcmToken;

    @Field("password")
    @NotNull
    private String password;

    @Field("date_of_birth")
    private LocalDate dateOfBirth;

    @Field("is_enabled")
    private boolean enabled;

    @Field("is_account_locked")
    private boolean locked;

    @Field("credentials_expired")
    private boolean credentialsExpired;

    @Field("is_email_verified")
    private boolean emailVerified;

    @Field("is_phone_verified")
    private boolean phoneVerified;

    @Field("created_date")
    private LocalDateTime createdDate;

    @Field("last_modified_date")
    private LocalDateTime lastModifiedDate;
    @Field("role_ids")
    private List<String> roles;
    @Field("banned_at")
    private LocalDateTime bannedAt;

    @Field("ban_reason")
    private String banReason;
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (CollectionUtils.isEmpty(this.roles)) {
            return List.of();
        }

        return this.roles.stream()
                .map(role -> {
                    String normalized = role.toUpperCase();
                    if (normalized.startsWith("ROLE_")) {
                        return new SimpleGrantedAuthority(normalized);
                    }
                    return new SimpleGrantedAuthority("ROLE_" + normalized);
                })
                .collect(Collectors.toList());

    }
    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    @Override
    public boolean isAccountNonLocked() {
        return !this.locked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return !this.credentialsExpired;
    }
}
