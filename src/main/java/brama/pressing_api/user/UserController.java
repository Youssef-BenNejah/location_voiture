package brama.pressing_api.user;

import brama.pressing_api.user.dto.request.BanUserRequest;
import brama.pressing_api.user.dto.response.AdminUserResponse;
import brama.pressing_api.user.request.ChangePasswordRequest;
import brama.pressing_api.user.request.FcmTokenRequest;
import brama.pressing_api.user.request.ProfileUpdateRequest;
import brama.pressing_api.user.dto.response.UserProfileResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * User profile endpoints for authenticated users.
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User", description = "User API")
public class UserController {
    private final UserService service;

    /**
     * Returns the current user's profile information.
     */
    @GetMapping("/me")
    public UserProfileResponse getProfile(final Authentication principal) {
        return UserProfileResponse.from((User) principal.getPrincipal());
    }

    /**
     * Updates the current user's profile fields.
     */
    @PatchMapping("/me")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void updateProfile(
            @RequestBody
            @Valid
            final ProfileUpdateRequest request,
            final Authentication principal) {
        this.service.updateProfileInfo(request, getUserId(principal));
    }

    /**
     * Changes the current user's password.
     */
    @PostMapping("/me/password")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void changePassword(
            @RequestBody
            @Valid
            final ChangePasswordRequest request,
            final Authentication principal) {
        this.service.changePassword(request, getUserId(principal));
    }

    /**
     * Registers the current user's FCM token (subscribes to their topic).
     */
    @PostMapping("/me/fcm-token")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void registerFcmToken(
            @RequestBody
            @Valid
            final FcmTokenRequest request,
            final Authentication principal) {
        this.service.registerFcmToken(getUserId(principal), request.getToken());
    }

    /**
     * Deactivates the current user's account.
     */
    @PatchMapping("/me/deactivate")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deactivateAccount(final Authentication principal) {
        this.service.deactivateAccount(getUserId(principal));
    }

    /**
     * Reactivates the current user's account.
     */
    @PatchMapping("/me/reactivate")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void reactivateAccount(final Authentication principal) {
        this.service.reactivateAccount(getUserId(principal));
    }

    /**
     * Deletes the current user's account (placeholder implementation).
     */
    @DeleteMapping("/me")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deleteAccount(final Authentication principal) {
        this.service.deleteAccount(getUserId(principal));
    }

    private String getUserId(final Authentication authentication) {
        return ((User) authentication.getPrincipal()).getId();
    }


    // GET ALL USERS (pagination)
    @GetMapping
    public Page<AdminUserResponse> getAllUsers(Pageable pageable) {
        return service.getAllUsers(pageable);
    }

    // GET USER BY ID
    @GetMapping("/{id}")
    public AdminUserResponse getUserById(@PathVariable String id) {
        return service.getUserById(id);
    }

    // BAN USER
    @PatchMapping("/{id}/ban")
    public void banUser(
            @PathVariable String id,
            @RequestBody BanUserRequest request
    ) {
        service.banUser(id, request.getReason());
    }

    // UNBAN USER
    @PatchMapping("/{id}/unban")
    public void unbanUser(@PathVariable String id) {
        service.unbanUser(id);
    }
}
