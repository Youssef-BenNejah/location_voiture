package brama.pressing_api.security;

import brama.pressing_api.exception.ErrorCode;
import brama.pressing_api.handler.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final ObjectMapper objectMapper;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    private final List<String> publicPaths = Arrays.asList(SecurityPaths.PUBLIC_URLS);

    @Override
    protected boolean shouldNotFilter(@NonNull final HttpServletRequest request) {
        if (HttpMethod.OPTIONS.matches(request.getMethod())) {
            return true;
        }
        final String path = request.getServletPath();
        return this.publicPaths.stream().anyMatch(pattern -> this.pathMatcher.match(pattern, path));
    }
    @Override
    protected void doFilterInternal(
            @NonNull
            final HttpServletRequest request,
            @NonNull
            final HttpServletResponse response,
            @NonNull
            final FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String jwt;
        final String username;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            writeUnauthorized(response, ErrorCode.ACCESS_TOKEN_MISSING);
            return;
        }

        jwt = authHeader.substring(7);
        try {
            username = this.jwtService.extractUsername(jwt);
        } catch (final RuntimeException ex) {
            writeUnauthorized(response, ErrorCode.ACCESS_TOKEN_INVALID);
            return;
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            final UserDetails userDetails;
            try {
                userDetails = this.userDetailsService.loadUserByUsername(username);
            } catch (final RuntimeException ex) {
                writeUnauthorized(response, ErrorCode.ACCESS_TOKEN_INVALID);
                return;
            }

            try {
                if (!this.jwtService.isTokenValid(jwt, userDetails.getUsername())) {
                    writeUnauthorized(response, ErrorCode.ACCESS_TOKEN_INVALID);
                    return;
                }
            } catch (final RuntimeException ex) {
                writeUnauthorized(response, ErrorCode.ACCESS_TOKEN_INVALID);
                return;
            }

            final UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities());

            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authToken);
        }

        filterChain.doFilter(request, response);
    }

    private void writeUnauthorized(final HttpServletResponse response, final ErrorCode errorCode)
            throws IOException {
        response.setStatus(errorCode.getStatus().value());
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        final ErrorResponse body = ErrorResponse.builder()
                .code(errorCode.getCode())
                .message(errorCode.getDefaultMessage())
                .build();
        response.getWriter().write(this.objectMapper.writeValueAsString(body));
    }
}
