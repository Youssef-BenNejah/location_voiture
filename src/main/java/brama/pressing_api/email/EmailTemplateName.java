package brama.pressing_api.email;

import lombok.Getter;

@Getter
public enum EmailTemplateName {
    OTP_VERIFICATION("otp-verification"),
    ACTIVATE_ACCOUNT("activate-account"),
    PASSWORD_RESET("password-reset"),
    WELCOME_EMAIL("welcome-email"),
    ORDER_CONFIRMATION("order-confirmation");

    private final String name;

    EmailTemplateName(String name) {
        this.name = name;
    }
}
