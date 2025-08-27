package com.example.spring_security_asymetric_encrytion.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class EmailDomainValidator implements ConstraintValidator<NonDisposableEmail, String> {
    private final Set<String> blocked;
    public EmailDomainValidator(@Value("${app.security.disposable-email}")
                                final List<String> domains) {
        this.blocked = domains.stream().map(String::toLowerCase).collect(Collectors.toSet());

    }
    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        if(email == null || !email.contains("@"))
        {
            return true;
        }
        final int atIndex = email.indexOf("@")+1;
        final int dotIndex = email.indexOf(".", atIndex);
        final String domain = email.substring(atIndex, dotIndex);
        return !this.blocked.contains(domain);
    }
}
