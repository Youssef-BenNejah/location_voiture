package brama.pressing_api.config;

import brama.pressing_api.chat.websocket.ChatPrincipal;
import brama.pressing_api.user.User;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class ApplicationAuditorAware implements AuditorAware<String> {
    @Override
    public Optional<String> getCurrentAuditor() {
        final Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() ||
                authentication instanceof AnonymousAuthenticationToken) {
            return Optional.empty();
        }

        final Object principal = authentication.getPrincipal();
        if (principal instanceof User user) {
            return Optional.ofNullable(user.getId());
        }
        if (principal instanceof ChatPrincipal chatPrincipal) {
            return Optional.ofNullable(chatPrincipal.getName());
        }
        return Optional.empty();
    }
}
