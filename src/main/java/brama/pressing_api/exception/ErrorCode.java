package brama.pressing_api.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
public enum ErrorCode {
    EMAIL_ALREADY_EXISTS("ERR_EMAIL_EXISTS", "Email already exists", CONFLICT),
    PHONE_ALREADY_EXISTS("ERR_PHONE_EXISTS", "An account with this phone number already exists", CONFLICT),
    PASSWORD_MISMATCH("ERR_PASSWORD_MISMATCH", "The password and confirmation do not match", BAD_REQUEST),
    CHANGE_PASSWORD_MISMATCH("ERR_PASSWORD_MISMATCH", "New password and confirmation do not match", BAD_REQUEST),
    ERR_SENDING_ACTIVATION_EMAIL("ERR_SENDING_ACTIVATION_EMAIL",
            "An error occurred while sending the activation email",
            HttpStatus.INTERNAL_SERVER_ERROR),

    ERR_USER_DISABLED("ERR_USER_DISABLED",
            "User account is disabled, please activate your account or contact the administrator",
            UNAUTHORIZED),
    EMAIL_NOT_VERIFIED("EMAIL_NOT_VERIFIED", "Email address is not verified", UNAUTHORIZED),
    INVALID_CURRENT_PASSWORD("INVALID_CURRENT_PASSWORD", "The current password is incorrect", BAD_REQUEST),
    USER_NOT_FOUND("USER_NOT_FOUND", "User not found", NOT_FOUND),
    ACCOUNT_ALREADY_DEACTIVATED("ACCOUNT_ALREADY_DEACTIVATED", "Account has been deactivated", BAD_REQUEST),
    BAD_CREDENTIALS("BAD_CREDENTIALS", "Username and / or password is incorrect", UNAUTHORIZED),
    INTERNAL_EXCEPTION("INTERNAL_EXCEPTION",
            "An internal exception occurred, please try again or contact the admin",
            HttpStatus.INTERNAL_SERVER_ERROR),
    USERNAME_NOT_FOUND("USERNAME_NOT_FOUND", "Cannot find user with the provided username", NOT_FOUND),
    CATEGORY_ALREADY_EXISTS_FOR_USER("CATEGORY_ALREADY_EXISTS_FOR_USER", "Category already exists for this user", CONFLICT),
    INVALID_REFRESH_TOKEN("INVALID_REFRESH_TOKEN", "The refresh token is invalid or expired", UNAUTHORIZED),
    REFRESH_TOKEN_REVOKED("REFRESH_TOKEN_REVOKED", "The refresh token has been revoked", UNAUTHORIZED),
    INVALID_OR_EXPIRED_OTP("INVALID_OR_EXPIRED_OTP", "Invalid or expired OTP", BAD_REQUEST),
    INVALID_OTP_CODE("INVALID_OTP_CODE", "Invalid OTP code", BAD_REQUEST),
    OTP_MAX_ATTEMPTS_EXCEEDED("OTP_MAX_ATTEMPTS_EXCEEDED", "Maximum OTP attempts exceeded", TOO_MANY_REQUESTS),
    OTP_RATE_LIMIT_EXCEEDED("OTP_RATE_LIMIT_EXCEEDED", "OTP generation rate limit exceeded", TOO_MANY_REQUESTS),

    INVALID_DATE_RANGE("INVALID_DATE_RANGE", "Start date must be before end date", BAD_REQUEST),
    VEHICLE_NOT_AVAILABLE("VEHICLE_NOT_AVAILABLE", "Vehicle is not available for the selected dates", CONFLICT),
    LOCATION_CODE_EXISTS("LOCATION_CODE_EXISTS", "Location code already exists", CONFLICT),
    PROMO_CODE_EXISTS("PROMO_CODE_EXISTS", "Promo code already exists", CONFLICT),
    PROMO_CODE_INVALID("PROMO_CODE_INVALID", "Promo code is invalid or expired", BAD_REQUEST),
    BOOKING_STATUS_NOT_ALLOWED("BOOKING_STATUS_NOT_ALLOWED", "Booking status change is not allowed", BAD_REQUEST),
    BOOKING_NOT_FOUND("BOOKING_NOT_FOUND", "Booking not found", NOT_FOUND),
    PAYMENT_ALREADY_PROCESSED("PAYMENT_ALREADY_PROCESSED", "Payment already processed", CONFLICT),
    EXCURSION_DISABLED("EXCURSION_DISABLED", "Excursion is disabled", CONFLICT),
    EXCURSION_DATE_NOT_AVAILABLE("EXCURSION_DATE_NOT_AVAILABLE", "Selected date is not available", CONFLICT),
    EXCURSION_FULL("EXCURSION_FULL", "Excursion is fully booked", CONFLICT),
    EXCURSION_SEATS_LIMIT("EXCURSION_SEATS_LIMIT", "Requested seats exceed the allowed limit", BAD_REQUEST),
    EXCURSION_CAPACITY_INVALID("EXCURSION_CAPACITY_INVALID", "Excursion capacity is invalid", BAD_REQUEST),
    EXCURSION_CAPACITY_TOO_LOW("EXCURSION_CAPACITY_TOO_LOW", "Capacity is lower than booked seats", CONFLICT),
    EXCURSION_BOOKING_STATUS_NOT_ALLOWED("EXCURSION_BOOKING_STATUS_NOT_ALLOWED", "Excursion booking status change is not allowed", BAD_REQUEST),
    FILE_REQUIRED("FILE_REQUIRED", "File is required", BAD_REQUEST),
    FILE_TOO_LARGE("FILE_TOO_LARGE", "File exceeds maximum size", BAD_REQUEST),
    FILE_TYPE_NOT_ALLOWED("FILE_TYPE_NOT_ALLOWED", "File type is not allowed", BAD_REQUEST),
    CHAT_INVALID_REQUEST("CHAT_INVALID_REQUEST", "Invalid chat request", BAD_REQUEST),
    CHAT_MESSAGE_EMPTY("CHAT_MESSAGE_EMPTY", "Message content or attachments required", BAD_REQUEST),
    CHAT_ACCESS_DENIED("CHAT_ACCESS_DENIED", "You do not have access to this conversation", FORBIDDEN),
    CHAT_MESSAGE_EDIT_NOT_ALLOWED("CHAT_MESSAGE_EDIT_NOT_ALLOWED", "You cannot edit this message", FORBIDDEN),
    CHAT_MESSAGE_DELETE_NOT_ALLOWED("CHAT_MESSAGE_DELETE_NOT_ALLOWED", "You cannot delete this message", FORBIDDEN),
    CHAT_MESSAGE_DELETED("CHAT_MESSAGE_DELETED", "Message has been deleted", BAD_REQUEST),
    BOOKING_INVALID_REQUEST("BOOKING_INVALID_REQUEST", "Invalid booking request", BAD_REQUEST),
    PAYMENT_AMOUNT_INVALID("PAYMENT_AMOUNT_INVALID", "Payment amount is invalid", BAD_REQUEST),
    CIRCUIT_NOT_FOUND("CIRCUIT_NOT_FOUND", "Circuit not found", NOT_FOUND),
    CIRCUIT_INACTIVE("CIRCUIT_INACTIVE", "Circuit is inactive", BAD_REQUEST),
    CIRCUIT_BOOKING_NOT_FOUND("CIRCUIT_BOOKING_NOT_FOUND", "Circuit booking not found", NOT_FOUND),
    CIRCUIT_PASSENGERS_LIMIT("CIRCUIT_PASSENGERS_LIMIT", "Requested passengers exceed the allowed limit", BAD_REQUEST),
    CIRCUIT_BOOKING_STATUS_NOT_ALLOWED("CIRCUIT_BOOKING_STATUS_NOT_ALLOWED", "Circuit booking status change not allowed", BAD_REQUEST),
    CIRCUIT_DATE_INVALID("CIRCUIT_DATE_INVALID", "Selected date must be today or later", BAD_REQUEST),
    CIRCUIT_ACCESS_DENIED("CIRCUIT_ACCESS_DENIED", "You do not have access to this circuit booking", FORBIDDEN),
    USER_ALREADY_BANNED("USER_ALREADY_BANNED","User account is already banned",HttpStatus.BAD_REQUEST),
    USER_NOT_BANNED("USER_NOT_BANNED","User account is not banned",HttpStatus.BAD_REQUEST),
    USER_BANNED("USER_BANNED", "Your account has been banned. Please contact support.", HttpStatus.FORBIDDEN);
    private final String code;
    private final String defaultMessage;
    private final HttpStatus status;

    ErrorCode(final String code,
              final String defaultMessage,
              final HttpStatus status) {
        this.code = code;
        this.defaultMessage = defaultMessage;
        this.status = status;
    }
}
