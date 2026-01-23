package brama.pressing_api.chat.websocket;

import brama.pressing_api.security.JwtService;
import brama.pressing_api.user.User;
import brama.pressing_api.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketAuthChannelInterceptor implements ChannelInterceptor {
    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    public Message<?> preSend(final Message<?> message, final MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null) {
            return message;
        }
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String token = resolveToken(accessor);
            if (StringUtils.isBlank(token)) {
                throw new IllegalArgumentException("Missing Authorization token");
            }
            String username = jwtService.extractUsername(token);
            User user = userRepository.findByEmailIgnoreCase(username)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid token"));
            ChatPrincipal principal = new ChatPrincipal(user.getId(), user.getEmail());
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    principal,
                    null,
                    user.getAuthorities()
            );
            accessor.setUser(authentication);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.debug("WebSocket authenticated for user {}", user.getId());
        }
        return message;
    }

    private String resolveToken(final StompHeaderAccessor accessor) {
        String authHeader = getFirstNativeHeader(accessor, "Authorization");
        if (StringUtils.isBlank(authHeader)) {
            authHeader = getFirstNativeHeader(accessor, "authorization");
        }
        if (StringUtils.isBlank(authHeader)) {
            authHeader = getFirstNativeHeader(accessor, "token");
        }
        if (StringUtils.isBlank(authHeader)) {
            return null;
        }
        if (StringUtils.startsWithIgnoreCase(authHeader, "Bearer ")) {
            return authHeader.substring(7);
        }
        return authHeader;
    }

    private String getFirstNativeHeader(final StompHeaderAccessor accessor, final String name) {
        if (accessor.getNativeHeader(name) == null || accessor.getNativeHeader(name).isEmpty()) {
            return null;
        }
        return accessor.getNativeHeader(name).get(0);
    }
}
