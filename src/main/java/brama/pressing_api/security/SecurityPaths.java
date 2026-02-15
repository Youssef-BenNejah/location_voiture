package brama.pressing_api.security;

public final class SecurityPaths {
    public static final String[] PUBLIC_URLS = {
            "/api/v1/auth/login",
            "/api/v1/auth/verify-email/**",
            "/api/v1/auth/register",
            "/api/v1/auth/refresh",
            "/api/v1/auth/reset-password",
            "/api/v1/public/**",
            "/api/v1/payments/stripe/webhook",
            "/ws/**",
            "/v2/api-docs",
            "/v3/api-docs",
            "/v3/api-docs/**",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-ui/**",
            "/webjars/**",
            "/swagger-ui.html",
            "/api/v1/otp/**",
            "/api/v1/ws/**"
    };

    private SecurityPaths() {
    }
}
