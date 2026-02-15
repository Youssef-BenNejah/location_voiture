package brama.pressing_api.auth;

import brama.pressing_api.auth.request.*;
import brama.pressing_api.auth.response.AuthenticationResponse;
import jakarta.validation.Valid;

public interface AuthenticationService {
    AuthenticationResponse login(AuthenticationRequest request);

    void register(RegistrationRequest request);

    AuthenticationResponse refreshToken(RefreshRequest req);

    void resetPassword(ResetPasswordRequest request);



    void verifyEmailByUserId(String userId, String code);
}
