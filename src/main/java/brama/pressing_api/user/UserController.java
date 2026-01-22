package brama.pressing_api.user;

import brama.pressing_api.user.request.ChangePasswordRequest;
import brama.pressing_api.user.request.ProfileUpdateRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
}
