package brama.pressing_api.auth.impl;

import brama.pressing_api.auth.AuthenticationService;
import brama.pressing_api.auth.request.AuthenticationRequest;
import brama.pressing_api.auth.request.RefreshRequest;
import brama.pressing_api.auth.request.RegistrationRequest;
import brama.pressing_api.auth.response.AuthenticationResponse;
import brama.pressing_api.exception.BusinessException;
import brama.pressing_api.exception.EntityNotFoundException;
import brama.pressing_api.exception.ErrorCode;
import brama.pressing_api.role.Role;
import brama.pressing_api.role.RoleRepository;
import brama.pressing_api.security.JwtService;
import brama.pressing_api.token.TokenService;
import brama.pressing_api.user.User;
import brama.pressing_api.user.UserMapper;
import brama.pressing_api.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j

public class AuthenticationServiceImpl implements AuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final TokenService tokenService;


    @Override
    public AuthenticationResponse login(AuthenticationRequest request) {
        final Authentication auth = this.authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        final User user = (User) auth.getPrincipal();
        final String accessToken = this.jwtService.generateAccessToken(user);
        final String refreshToken=this.jwtService.generateRefreshToken(user);
        LocalDateTime expirationTime = this.jwtService.extractExpiration(refreshToken)
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        tokenService.storeRefreshToken(user.getId(), refreshToken, expirationTime);

        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .build();
    }

    @Override
    @Transactional
    public void register(RegistrationRequest request) {
        checkUserEmail(request.getEmail());
        checkUserPhoneNumber(request.getPhoneNumber());
        checkPassword(request.getPassword(), request.getConfirmPassword());

        final Role userRole = this.roleRepository.findByName("USER")
                .orElseThrow(() -> new EntityNotFoundException("Role USER does not exist"));

        final List<String> roleNames = new ArrayList<>();
        roleNames.add(userRole.getName());

        final User user = this.userMapper.toUser(request);
        user.setRoles(roleNames);

        log.debug("Saving user {}", user);
        this.userRepository.save(user);

    }

    @Override
    public AuthenticationResponse refreshToken(RefreshRequest req) {
        final String accessToken = this.jwtService.refreshAccessToken(req.getRefreshToken());
        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(req.getRefreshToken())
                .tokenType("Bearer")
                .build();

    }
    private void checkPassword(String password, String confirmPassword) {
        if(password == null || !password.equals(confirmPassword))
        {
            throw new BusinessException(ErrorCode.PASSWORD_MISMATCH);
        }
    }

    private void checkUserPhoneNumber(String phoneNumber) {
        final boolean phoneNumberExists = this.userRepository.existsByPhoneNumber(phoneNumber);
        if (phoneNumberExists) {
            throw new BusinessException(ErrorCode.PHONE_ALREADY_EXISTS);
        }
    }

    private void checkUserEmail(String email) {
        final boolean emailExists = this.userRepository.existsByEmailIgnoreCase(email);
        if (emailExists) {
            throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

    }
}
