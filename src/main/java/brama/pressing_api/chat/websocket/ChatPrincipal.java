package brama.pressing_api.chat.websocket;

import java.security.Principal;

public class ChatPrincipal implements Principal {
    private final String userId;
    private final String email;

    public ChatPrincipal(final String userId, final String email) {
        this.userId = userId;
        this.email = email;
    }

    @Override
    public String getName() {
        return userId;
    }

    public String getEmail() {
        return email;
    }
}
