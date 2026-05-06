package ng.ikigai.trackspensev2.service;

import ng.ikigai.trackspensev2.exception.EmailSendingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.List;
import java.util.Map;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final String apiKey;
    private final String secretKey;
    private final String senderEmail;
    private final String senderName;
    private final RestTemplate restTemplate = new RestTemplate();

    public EmailService(
            @Value("${mailjet.api.key}") String apiKey,
            @Value("${mailjet.secret.key}") String secretKey,
            @Value("${mailjet.sender.email}") String senderEmail,
            @Value("${mailjet.sender.name:TrackSpense}") String senderName
    ) {
        this.apiKey = apiKey;
        this.secretKey = secretKey;
        this.senderEmail = senderEmail;
        this.senderName = senderName;
    }

    public void sendEmail(String to, String subject, String body) {
        try {
            String credentials = Base64.getEncoder().encodeToString((apiKey + ":" + secretKey).getBytes());

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Basic " + credentials);
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));

            Map<String, Object> payload = Map.of(
                    "Messages", List.of(Map.of(
                            "From", Map.of("Email", senderEmail, "Name", senderName),
                            "To", List.of(Map.of("Email", to)),
                            "Subject", subject,
                            "HTMLPart", body
                    ))
            );

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);
            restTemplate.postForEntity("https://api.mailjet.com/v3.1/send", request, String.class);

        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage(), e);
            throw new EmailSendingException("Failed to send activation email to " + to + ". Please check your email address and try again.", e);
        }
    }
}