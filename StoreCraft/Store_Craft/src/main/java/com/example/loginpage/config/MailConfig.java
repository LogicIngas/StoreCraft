package com.example.loginpage.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

/**
 * B1 — Mail / Brevo configuration.
 *
 * Builds a {@link JavaMailSender} from the BREVO_SMTP_* environment variables.
 * The bean is only created when {@code spring.mail.host} is non-blank, so tests
 * that don't set BREVO_SMTP_HOST won't fail trying to connect to a mail server.
 *
 * Primary sending is done via the Brevo HTTP API in {@link com.example.loginpage.service.impl.BrevoEmailService}.
 * SMTP is used as a fallback when the HTTP API key is absent or the call fails.
 */
@Configuration
public class MailConfig {

    @Value("${spring.mail.host:}")
    private String mailHost;

    @Value("${spring.mail.port:587}")
    private int mailPort;

    @Value("${spring.mail.username:}")
    private String mailUsername;

    @Value("${spring.mail.password:}")
    private String mailPassword;

    /**
     * Only register the JavaMailSender bean when BREVO_SMTP_HOST is configured.
     * This prevents Spring Boot's auto-configured mail health indicator from
     * reporting the app as DOWN when running without SMTP credentials.
     */
    @Bean
    @ConditionalOnProperty(name = "spring.mail.host", havingValue = "", matchIfMissing = false)
    public JavaMailSender javaMailSender() {
        return buildSender();
    }

    /**
     * Fallback bean registration when the SMTP host IS configured (non-empty).
     * Named differently to avoid ambiguity with Spring Boot's auto-configured bean.
     */
    @Bean("brevoMailSender")
    @ConditionalOnProperty(name = "spring.mail.host", matchIfMissing = false)
    public JavaMailSender brevoMailSender() {
        if (mailHost == null || mailHost.isBlank()) {
            return null; // No-op — host not configured
        }
        return buildSender();
    }

    private JavaMailSender buildSender() {
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost(mailHost);
        sender.setPort(mailPort);
        sender.setUsername(mailUsername);
        sender.setPassword(mailPassword);

        Properties props = sender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.starttls.required", "true");
        props.put("mail.smtp.connectiontimeout", "5000");
        props.put("mail.smtp.timeout", "5000");
        props.put("mail.smtp.writetimeout", "5000");

        return sender;
    }
}
