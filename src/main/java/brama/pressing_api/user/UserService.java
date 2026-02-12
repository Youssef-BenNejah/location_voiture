package brama.pressing_api.user;

import brama.pressing_api.user.dto.response.AdminUserResponse;
import brama.pressing_api.user.request.ChangePasswordRequest;
import brama.pressing_api.user.request.ProfileUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    void updateProfileInfo(ProfileUpdateRequest request, String userId);

    void changePassword(ChangePasswordRequest request, String userId);
    void deactivateAccount(String userId);

    void reactivateAccount(String userId);

    void deleteAccount(String userId);

    Page<AdminUserResponse> getAllUsers(Pageable pageable);
    AdminUserResponse getUserById(String id);
    void banUser(String id, String reason);
    void unbanUser(String id);
}
