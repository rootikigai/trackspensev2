package ng.ikigai.trackspensev2.service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import ng.ikigai.trackspensev2.exception.EmailSendingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;
    private final org.thymeleaf.TemplateEngine templateEngine;

//    @Value("${spring.mail.username}")
    @Value("${mail.from.address:noreply@trackspense.local}")
    private String fromEmail;

    public void sendEmail(String to, String subject, String body) {
        try {
            //            3. Send Email
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true);
            mailSender.send(mimeMessage);
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage(), e);
            throw new EmailSendingException("Failed to send activation email to " + to + ". Please check your email address and try again.", e);
        }
    }

    public void sendActivationEmail(String to, String name, String activationUrl) {
        try {
//            1. Prepare Thymeleaf Context
            org.thymeleaf.context.Context context = new org.thymeleaf.context.Context();
            context.setVariable("name", name);
            context.setVariable("activationUrl", activationUrl);

//            2. Process Template
            String htmlContent = templateEngine.process("activation-email", context);
            sendEmail(to, "Activate your account", htmlContent);
        }catch (Exception e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage(), e);
            throw new EmailSendingException("Failed to send activation email to " + to + ". Please check your email address and try again.", e);
        }
    }
}