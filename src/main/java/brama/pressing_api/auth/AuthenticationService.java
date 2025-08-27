package brama.pressing_api.auth;

import brama.pressing_api.auth.request.AuthenticationRequest;
import brama.pressing_api.auth.request.RefreshRequest;
import brama.pressing_api.auth.request.RegistrationRequest;
import brama.pressing_api.auth.response.AuthenticationResponse;

public interface AuthenticationService {
    AuthenticationResponse login(AuthenticationRequest request);

    void register(RegistrationRequest request);

    AuthenticationResponse refreshToken(RefreshRequest req);
}
