package brama.pressing_api.handler;

import brama.pressing_api.exception.BusinessException;
import brama.pressing_api.exception.EntityNotFoundException;
import brama.pressing_api.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class ApplicationExceptionHandler {

    private final MessageSource messageSource;

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(final BusinessException ex) {
        final ErrorResponse body = ErrorResponse.builder()
                .code(ex.getErrorCode().getCode())
                .message(ex.getMessage())
                .build();
        log.info("Business exception {}", ex.getMessage());
        log.debug(ex.getMessage(), ex);
        HttpStatus status = ex.getErrorCode().getStatus();
        if (ex.getErrorCode() == ErrorCode.OTP_RATE_LIMIT_EXCEEDED
                || ex.getErrorCode() == ErrorCode.OTP_MAX_ATTEMPTS_EXCEEDED) {
            status = HttpStatus.TOO_MANY_REQUESTS;
        }
        return ResponseEntity.status(status != null ? status : HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ErrorResponse> handleDisabledException(final DisabledException ex) {
        final ErrorResponse body = ErrorResponse.builder()
                .code(ErrorCode.ERR_USER_DISABLED.getCode())
                .message(ErrorCode.ERR_USER_DISABLED.getDefaultMessage())
                .build();
        log.info("Disabled exception {}", ex.getMessage());
        log.debug(ex.getMessage(), ex);
        return ResponseEntity.status(ErrorCode.ERR_USER_DISABLED.getStatus()).body(body);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentialsException(final BadCredentialsException ex) {
        log.info("Bad credentials exception {}", ex.getMessage());
        log.debug(ex.getMessage(), ex);
        final ErrorResponse body = ErrorResponse.builder()
                .code(ErrorCode.BAD_CREDENTIALS.getCode())
                .message(ErrorCode.BAD_CREDENTIALS.getDefaultMessage())
                .build();
        return ResponseEntity.status(ErrorCode.BAD_CREDENTIALS.getStatus()).body(body);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUsernameNotFoundException(final UsernameNotFoundException ex) {
        final ErrorResponse body = ErrorResponse.builder()
                .code(ErrorCode.USERNAME_NOT_FOUND.getCode())
                .message(ErrorCode.USERNAME_NOT_FOUND.getDefaultMessage())
                .build();
        log.info("Username not found exception {}", ex.getMessage());
        log.debug(ex.getMessage(), ex);
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(final EntityNotFoundException ex) {
        log.info("Entity not found exception {}", ex.getMessage());
        log.debug(ex.getMessage(), ex);
        final ErrorResponse body = ErrorResponse.builder()
                .code("ENTITY_NOT_FOUND")
                .message(ex.getMessage())
                .build();
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(final Exception ex) {
        // ⚠️ LOG THE FULL STACK TRACE
        log.error("❌❌❌ INTERNAL EXCEPTION - FULL DETAILS ❌❌❌", ex);
        log.error("Exception type: {}", ex.getClass().getName());
        log.error("Exception message: {}", ex.getMessage());

        // Print stack trace to console
        ex.printStackTrace();

        final ErrorResponse body = ErrorResponse.builder()
                .code(ErrorCode.INTERNAL_EXCEPTION.getCode())
                .message(ex.getMessage()) // ⚠️ Changed: show actual error message instead of generic one
                .build();
        return ResponseEntity.status(ErrorCode.INTERNAL_EXCEPTION.getStatus()).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(final MethodArgumentNotValidException ex) {
        final List<ErrorResponse.ValidationError> validationErrors = new ArrayList<>();

        ex.getBindingResult().getFieldErrors().forEach(error -> {
            final String fieldName = ((FieldError) error).getField();
            final String errorCode = error.getDefaultMessage();

            // Resolve the message from MessageSource
            String resolvedMessage = null;
            try {
                resolvedMessage = messageSource.getMessage(
                        errorCode,
                        error.getArguments(),
                        LocaleContextHolder.getLocale()
                );
            } catch (Exception e) {
                log.warn("Could not resolve message for code: {}", errorCode);
                // If message resolution fails, use the code as fallback
                resolvedMessage = errorCode;
            }

            validationErrors.add(
                    ErrorResponse.ValidationError.builder()
                            .field(fieldName)
                            .code(errorCode)
                            .message(resolvedMessage)
                            .build()
            );
        });

        final ErrorResponse body = ErrorResponse.builder()
                .validationErrors(validationErrors)
                .build();

        log.info("Validation exception {}", ex.getMessage());
        log.debug(ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }
}
