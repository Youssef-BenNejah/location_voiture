package brama.pressing_api.user.impl;

import brama.pressing_api.exception.BusinessException;
import brama.pressing_api.exception.ErrorCode;
import brama.pressing_api.user.User;
import brama.pressing_api.user.UserMapper;
import brama.pressing_api.user.UserRepository;
import brama.pressing_api.user.UserService;
import brama.pressing_api.notification.domain.NotificationImportance;
import brama.pressing_api.notification.dto.NotificationRequest;
import brama.pressing_api.notification.service.NotificationService;
import brama.pressing_api.user.request.ChangePasswordRequest;
import brama.pressing_api.user.request.ProfileUpdateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import brama.pressing_api.user.dto.response.AdminUserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;

import static brama.pressing_api.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final NotificationService notificationService;
    @Override
    public UserDetails loadUserByUsername(final String userEmail) throws UsernameNotFoundException {
        return this.userRepository.findByEmailIgnoreCase(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with user name: " + userEmail));
    }

    @Override
    public void updateProfileInfo(ProfileUpdateRequest request, String userId) {
        final User savedUser = this.userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USERNAME_NOT_FOUND,userId));
        this.userMapper.mergeUserInfo(savedUser, request);
        this.userRepository.save(savedUser);
        notificationService.notifyUser(userId, NotificationRequest.builder()
                .type("PROFILE_UPDATED")
                .title("Profile updated")
                .body("Your profile information has been updated")
                .importance(NotificationImportance.NORMAL)
                .data(java.util.Map.of("userId", userId))
                .build());

    }

    @Override
    public void changePassword(ChangePasswordRequest request, String userId) {
        if (!request.getNewPassword()
                .equals(request.getConfirmNewPassword())) {
            throw new BusinessException(CHANGE_PASSWORD_MISMATCH);
        }

        final User savedUser = this.userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(USER_NOT_FOUND));

        if (!this.passwordEncoder.matches(request.getCurrentPassword(),
                savedUser.getPassword())) {
            throw new BusinessException(INVALID_CURRENT_PASSWORD);
        }

        final String encoded = this.passwordEncoder.encode(request.getNewPassword());
        savedUser.setPassword(encoded);
        this.userRepository.save(savedUser);
        notificationService.notifyUser(userId, NotificationRequest.builder()
                .type("PASSWORD_CHANGED")
                .title("Password changed")
                .body("Your password was changed successfully")
                .importance(NotificationImportance.HIGH)
                .data(java.util.Map.of("userId", userId))
                .build());

    }

    @Override
    public void deactivateAccount(String userId) {
        final User user= this.userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(USER_NOT_FOUND));
        if(!user.isEnabled()){
            throw new BusinessException(ACCOUNT_ALREADY_DEACTIVATED);
        }
        user.setEnabled(false);
        this.userRepository.save(user);
        notificationService.notifyUser(userId, NotificationRequest.builder()
                .type("ACCOUNT_DEACTIVATED")
                .title("Account deactivated")
                .body("Your account has been deactivated")
                .importance(NotificationImportance.HIGH)
                .data(java.util.Map.of("userId", userId))
                .build());

    }

    @Override
    public void reactivateAccount(String userId) {
        final User user= this.userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(USER_NOT_FOUND));
        if(user.isEnabled()){
            throw new BusinessException(ACCOUNT_ALREADY_DEACTIVATED);
        }
        user.setEnabled(true);
        this.userRepository.save(user);
        notificationService.notifyUser(userId, NotificationRequest.builder()
                .type("ACCOUNT_REACTIVATED")
                .title("Account reactivated")
                .body("Your account has been reactivated")
                .importance(NotificationImportance.HIGH)
                .data(java.util.Map.of("userId", userId))
                .build());

    }

    @Override
    public void deleteAccount(String userId) {

    }

    @Override
    public Page<AdminUserResponse> getAllUsers(Pageable pageable) {
        return userRepository.findAllByOrderByCreatedDateDesc(pageable)
                .map(AdminUserResponse::from);
    }


    @Override
    public AdminUserResponse getUserById(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(USER_NOT_FOUND));

        return AdminUserResponse.from(user);
    }
    @Override
    public void banUser(String id, String reason) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(USER_NOT_FOUND));

        if (user.isLocked()) {
            throw new BusinessException(USER_ALREADY_BANNED);
        }

        user.setLocked(true);
        user.setEnabled(false);
        user.setBannedAt(LocalDateTime.now());
        user.setBanReason(reason);

        userRepository.save(user);
        notificationService.notifyUser(user.getId(), NotificationRequest.builder()
                .type("ACCOUNT_BANNED")
                .title("Account banned")
                .body("Your account has been banned. Reason: " + (reason == null ? "Not specified" : reason))
                .importance(NotificationImportance.CRITICAL)
                .data(java.util.Map.of("userId", user.getId()))
                .build());
    }
    @Override
    public void unbanUser(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(USER_NOT_FOUND));

        if (!user.isLocked()) {
            throw new BusinessException(USER_NOT_BANNED);
        }

        user.setLocked(false);
        user.setEnabled(true);
        user.setBanReason(null);
        user.setBannedAt(null);

        userRepository.save(user);
        notificationService.notifyUser(user.getId(), NotificationRequest.builder()
                .type("ACCOUNT_UNBANNED")
                .title("Account unbanned")
                .body("Your account access has been restored")
                .importance(NotificationImportance.HIGH)
                .data(java.util.Map.of("userId", user.getId()))
                .build());
    }


}
