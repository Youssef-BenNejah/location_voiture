package brama.pressing_api.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;
    @Async
    public void sendOtpEmail(String to, String username, String otpCode, String purpose,String expiryMinutes) throws MessagingException {
        Map<String, Object> properties = new HashMap<>();
        properties.put("username", username);
        properties.put("otpCode", otpCode);
        properties.put("purpose", purpose);
        properties.put("expiryMinutes", expiryMinutes);

        String subject = getSubjectForPurpose(purpose);
        sendEmail(to, subject, EmailTemplateName.OTP_VERIFICATION, properties);
    }
    @Async
    public void sendEmail(String to, String subject, EmailTemplateName emailTemplate, Map<String, Object> properties) throws MessagingException {
        String templateName = emailTemplate.getName();

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(
                mimeMessage,
                MimeMessageHelper.MULTIPART_MODE_MIXED,
                StandardCharsets.UTF_8.name()
        );

        Context context = new Context();
        context.setVariables(properties);

        helper.setFrom("contact@attia.com");
        helper.setTo(to);
        helper.setSubject(subject);

        String template = templateEngine.process(templateName, context);
        helper.setText(template, true);

        mailSender.send(mimeMessage);
        log.info("Email sent successfully to: {} with template: {}", to, templateName);
    }

    private String getSubjectForPurpose(String purpose) {
        return switch (purpose) {
            case "EMAIL_VERIFICATION" -> "Verify Your Email Address";
            case "PHONE_VERIFICATION" -> "Verify Your Phone Number";
            case "PASSWORD_RESET" -> "Reset Your Password";
            case "TWO_FACTOR_AUTH" -> "Two-Factor Authentication Code";
            case "ACCOUNT_ACTIVATION" -> "Activate Your Account";
            default -> "Verification Code";
        };
    }
}
